package com.example.springplusteamproject.domain.flower.service;

import static com.example.springplusteamproject.common.status.ErrorStatus.CUSTOM_ERROR_STATUS;
import static com.example.springplusteamproject.common.status.ErrorStatus.FLOWER_ACCESS_DENIED;
import static com.example.springplusteamproject.common.status.ErrorStatus.FLOWER_NOT_FOUND;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto;
import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto.Update;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto.Create;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto.Get;
import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.flower.repository.FlowerRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlowerServiceImpl implements FlowerService {

    private final StoreRepository storeRepository;
    private final FlowerRepository flowerRepository;

    @Override
    @Transactional
    public Create createFlower(Long storeId, FlowerRequestDto.Create request) {

        // 가게 조회
        Store store = storeRepository.findByIdAndDeletedFalse(storeId)
            .orElseThrow(() -> new ApiException(CUSTOM_ERROR_STATUS)); // 수정하기

        // 본인 가게에서만 상품 등록 가능
        if (!store.getUser().getId().equals(1L)) { // 1L -> AuthUser 수정하기
            throw new ApiException(FLOWER_ACCESS_DENIED);
        }

        // 상품 등록
        Flower flower = Flower.from(store, request);
        flowerRepository.save(flower);

        return new FlowerResponseDto.Create(flower.getId(), flower.getName());
    }

    @Override
    @Transactional
    public void updateFlower(Long storeId, Long flowerId, Update request) {

        // 상품 조회
        Flower flower = flowerRepository.findByIdAndDeletedFalse(flowerId)
            .orElseThrow(() -> new ApiException(FLOWER_NOT_FOUND));

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
    public void deleteFlower(Long storeId, Long flowerId) {

        // 가게 조회
        Store store = storeRepository.findByIdAndDeletedFalse(storeId)
            .orElseThrow(() -> new ApiException(CUSTOM_ERROR_STATUS)); // 수정하기

        // 본인 가게의 상품만 삭제 가능
        if (!store.getUser().getId().equals(1L)) { // 1L -> AuthUser 수정하기
            throw new ApiException(FLOWER_ACCESS_DENIED);
        }

        // 상품 조회
        Flower flower = flowerRepository.findByIdAndDeletedFalse(flowerId)
            .orElseThrow(() -> new ApiException(CUSTOM_ERROR_STATUS));

        // soft delete 처리
        flower.setDeleted();
    }
}
