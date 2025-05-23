package com.example.springplusteamproject.domain.store.service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springplusteamproject.common.config.ForbiddenWordUtil;
import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.request.CursorPageRequest;
import com.example.springplusteamproject.common.response.CursorPageResponse;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.common.util.CursorPaginationUtil;
import com.example.springplusteamproject.domain.store.dto.request.StoreCheckNameRequestDto;
import com.example.springplusteamproject.domain.store.dto.request.StoreRequestDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreListResponseDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final RedissonClient redissonClient;
    private final UserRepository userRepository;
    private final StoreTransactionalService storeTransactionalService;
    private final RedisTemplate<String, Long> redisTemplate;

    @Override
    public StoreResponseDto createStore(StoreRequestDto dto, CustomUserPrincipal principal) {

        String storeName = dto.getName();
        String lockKey = "store-lock:name:" + storeName;
        log.info("lockKey = {}", lockKey);
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(500, -1, TimeUnit.MILLISECONDS);

            if (!isLocked) {
                log.warn("[Store - 가게 생성] 락 획득 실패, storeName: {}", storeName);
                throw new ApiException(ErrorStatus.STORE_BAD_REQUEST);
            }

            return storeTransactionalService.saveStore(dto, principal);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[Store - 가게 생성] 락 인터럽트", e);
            throw new ApiException(ErrorStatus.STORE_BAD_REQUEST);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.error("[Store - 가게 생성] 락 해제 실패", e);
                }
            }
        }
    }

    @Transactional
    @Override
    public void deleteStore(CustomUserPrincipal principal) {

        Store store = storeRepository.findByUserIdAndDeletedFalseForUpdate(principal.getId())
            .orElseThrow(() -> {
                log.warn("[Store - 가게 폐업] userId에 해당하는 가게 없음, userId: {}", principal.getId());
                return new ApiException(ErrorStatus.STORE_NOT_FOUND);
            });

        log.info("[Store - 가게 폐업] 가게 폐업 성공, userId={}", principal.getId());
        store.setDeleted();

    }

    @Transactional(readOnly=true)
    @Override
    public List<StoreListResponseDto> getAllStores() {
        log.info("[Store - 가게 전체 조회] 가게 전체 조회 성공");
        return storeRepository.findByDeletedFalse().stream()
            .map(StoreListResponseDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly=true)
    @Override
    public StoreResponseDto getStoreById(Long id) {

        Store store = storeRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> {
                log.warn("[Store - ID 기반 조회] Id에 해당하는 가게 없음, storeId: {}", id);
                return new ApiException(ErrorStatus.STORE_NOT_FOUND);
            });
        increaseViewCount(id);
        log.info("[Store - 가게 단건 조회] 가게 단건 조회 성공, storeId: {}", id);
        return StoreResponseDto.fromEntity(store);
    }

    @Transactional(readOnly=true)
    @Override
    public String checkingName(StoreCheckNameRequestDto dto) {

        String name = dto.getName();

        boolean exists = storeRepository.existsByNameAndDeletedFalse(name);

        boolean hasForbidden = ForbiddenWordUtil.containsForbiddenWord(name);

        if (exists) {
            log.warn("[Store - 이름 확인] 가게 이름에 금지어 포함, storeName: {}", dto.getName());
            return "이미 존재하는 상호명입니다.";
        }
        if (hasForbidden) {
            log.warn("[Store - 이름 확인] 가게 이름에 금지어 포함, storeName: {}", dto.getName());
            return "부적절한 단어가 포함되어 있습니다.";
        }
        log.info("[Store - 이름 확인] 가게 이름 확인 성공, storeName: {}", dto.getName());
        return "사용 가능한 상호명입니다.";

    }

    @Transactional(readOnly = true)
    @Override
    public CursorPageResponse<StoreListResponseDto> getStoresByCursor(CursorPageRequest cursorPageRequest) {

        Pageable pageable = PageRequest.of(0, cursorPageRequest.getSize());

        List<Store> stores = storeRepository.findByCursor(cursorPageRequest.getCursor(), pageable);

        List<StoreListResponseDto> dtoList = stores.stream()
            .map(StoreListResponseDto::fromEntity).toList();

        log.info("[Store - 커서기반 전체 조회] 커서기반 전체 조회 성공");
        return CursorPaginationUtil.paginate(dtoList, cursorPageRequest.getSize(), StoreListResponseDto::getId);
    }

    private void increaseViewCount(Long storeId) {
        String key = "store:viewcount:" + storeId;
        redisTemplate.opsForValue().increment(key);
    }
}
