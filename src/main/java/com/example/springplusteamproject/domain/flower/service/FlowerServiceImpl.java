package com.example.springplusteamproject.domain.flower.service;

import static com.example.springplusteamproject.common.status.ErrorStatus.FLOWER_ACCESS_DENIED;
import static com.example.springplusteamproject.common.status.ErrorStatus.FLOWER_NOT_FOUND;
import static com.example.springplusteamproject.common.status.ErrorStatus.STORE_NOT_FOUND;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto;
import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto.Update;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto.Create;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto.Get;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerSearchResponseDto;
import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.flower.entity.FlowerSearchLog;
import com.example.springplusteamproject.domain.flower.enums.SearchType;
import com.example.springplusteamproject.domain.flower.repository.FlowerRepository;
import com.example.springplusteamproject.domain.flower.repository.FlowerSearchLogRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlowerServiceImpl implements FlowerService {

    private final StoreRepository storeRepository;
    private final FlowerRepository flowerRepository;
    private final FlowerSearchLogRepository flowerSearchLogRepository;

    private final StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public Create createFlower(Long storeId, FlowerRequestDto.Create request, Long userId) {

        // 등록 권한 확인
        Store store = checkStoreAuth(storeId, userId);

        // 상품 등록
        Flower flower = Flower.from(store, request);
        flowerRepository.save(flower);

        return new FlowerResponseDto.Create(flower.getId(), flower.getName());
    }

    @Override
    @Transactional
    public void updateFlower(Long storeId, Long flowerId, Update request, Long userId) {

        // 수정 권한 확인
        checkStoreAuth(storeId, userId);
        Flower flower = checkFlowerAuth(storeId, flowerId);

        // 상품 수정
        flower.update(request);
    }

    @Override
    public Page<FlowerResponseDto.Get> getMyFlowers(Long storeId, int page, int size) {

        // 본인 가게 상품 목록 페이징 조회
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Flower> flowers = flowerRepository.findByStore_IdAndDeletedFalse(storeId, pageable);

        return flowers.map(FlowerResponseDto.Get::toDto);
    }

    @Override
    public Get getFlowerDetails(Long storeId, Long flowerId) {

        // 상품 상세 정보 조회
        Flower flower = flowerRepository.findByIdAndDeletedFalse(flowerId)
            .orElseThrow(() -> new ApiException(FLOWER_NOT_FOUND));

        return FlowerResponseDto.Get.toDto(flower);
    }

    @Override
    @Transactional
    public void deleteFlower(Long storeId, Long flowerId, Long userId) {

        // 삭제 권한 확인
        checkStoreAuth(storeId, userId);
        Flower flower = checkFlowerAuth(storeId, flowerId);

        // soft delete 처리
        flower.setDeleted();
    }

    @Override
    @Transactional
    public Page<Get> searchFlowers(String keyword, Long userId, int page, int size) {

        // 검색
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Flower> flowers = flowerRepository.findByNameContainingAndDeletedFalse(keyword, pageable);

        // 검색 로그 저장
        FlowerSearchLog log = new FlowerSearchLog(userId, keyword);
        flowerSearchLogRepository.save(log);

        return flowers.map(FlowerResponseDto.Get::toDto);
    }

    @Cacheable(
        cacheNames = "searchFlowersResults",
        key = "'search:' + #keyword + ':' + #page + ':' + #size",
        unless = "#result.content.isEmpty()"
    )
    @Override
    @Transactional
    public Page<Get> searchFlowersV2(String keyword, Long userId, int page, int size) {

        // 검색
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Flower> flowers = flowerRepository.findByNameContainingAndDeletedFalse(keyword, pageable);

        // 실제 사용자가 검색할 때만 로그 저장
        if (userId != null) {
            // 검색 로그 저장 - RDB
            FlowerSearchLog log = new FlowerSearchLog(userId, keyword);
            flowerSearchLogRepository.save(log);

            // 검색 로그 저장 - Redis
            increaseKeywordScore(keyword);
        }

        return flowers.map(FlowerResponseDto.Get::toDto);
    }

    @Override
    public List<FlowerSearchResponseDto> getTop10Keywords(SearchType type) {

        // key 생성
        String key = type.getRedisKey();

        // 상위 10개 조회
        Set<TypedTuple<String>> resultSet = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 9);

        // 조회 결과가 없을 경우 빈 배열 반환
        if (resultSet == null || resultSet.isEmpty()) {
            return List.of();
        }

        AtomicInteger rankCounter = new AtomicInteger(1);

        // 응답 DTO로 변환
        return resultSet.stream()
            .map(
                tuple -> new FlowerSearchResponseDto(
                    rankCounter.getAndIncrement(),
                    tuple.getValue(),
                    tuple.getScore().intValue()
                )
            ).collect(Collectors.toList());
    }

    private Store checkStoreAuth(Long storeId, Long userId) {

        // 가게 조회
        Store store = storeRepository.findByIdAndDeletedFalse(storeId)
            .orElseThrow(() -> new ApiException(STORE_NOT_FOUND));

        // 본인 가게가 맞는지 확인
        if (!store.getUser().getId().equals(userId)) {
            throw new ApiException(FLOWER_ACCESS_DENIED);
        }

        return store;
    }

    private Flower checkFlowerAuth(Long storeId, Long flowerId) {

        // 상품 조회
        Flower flower = flowerRepository.findByIdAndDeletedFalse(flowerId)
            .orElseThrow(() -> new ApiException(FLOWER_NOT_FOUND));

        // 선택한 가게에 등록된 상품이 맞는지 확인
        if (!flower.getStore().getId().equals(storeId)) {
            throw new ApiException(FLOWER_ACCESS_DENIED);
        }

        return flower;
    }

    private void increaseKeywordScore(String keyword) {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        int currentYear = today.getYear();

        String dailyKey = "popular:keywords:daily:" + today;
        String monthlyKey = "popular:keywords:monthly:" + currentMonth;
        String yearlyKey = "popular:keywords:yearly:" + currentYear;

        redisTemplate.opsForZSet().incrementScore(dailyKey, keyword, 1);
        redisTemplate.opsForZSet().incrementScore(monthlyKey, keyword, 1);
        redisTemplate.opsForZSet().incrementScore(yearlyKey, keyword, 1);
    }
}
