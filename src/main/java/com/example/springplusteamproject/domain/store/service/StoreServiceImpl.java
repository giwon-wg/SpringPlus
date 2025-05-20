package com.example.springplusteamproject.domain.store.service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.store.dto.request.StoreRequestDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreListResponseDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.temp.UserDummy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public StoreResponseDto createStore(StoreRequestDto dto) {

        if (storeRepository.existsByNameAndDeletedFalse(dto.getName())) {
            throw new ApiException(ErrorStatus.STORE_BAD_REQUEST);
        }

        Store store = Store.builder()
            .name(dto.getName())
            .address(dto.getAddress())
            .image(dto.getImage())
            .phoneNumber(dto.getPhoneNumber())
            .minOrderPrice(dto.getMinOrderPrice())
            .openTime(LocalTime.parse(dto.getOpenTime()))
            .closeTime(LocalTime.parse(dto.getCloseTime()))
            .deleted(false)
            .user(UserDummy.INSTANCE)
            .build();

        Store saved = storeRepository.save(store);
        return toResponseDto(saved);
    }

    @Transactional
    public void deleteStore(Long id) {

        Store store = storeRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ApiException(ErrorStatus.STORE_NOT_FOUND));

        store.setDeleted();
        storeRepository.save(store);
    }

    @Transactional(readOnly=true)
    public List<StoreListResponseDto> getAllStores() {

        return storeRepository.findByDeletedFalse().stream()
            .map(this::toListResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly=true)
    public StoreResponseDto getStoreById(Long id) {

        Store store = storeRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ApiException(ErrorStatus.STORE_NOT_FOUND));

        return toResponseDto(store);
    }

    private StoreResponseDto toResponseDto(Store store) {
        return StoreResponseDto.builder()
            .id(store.getId())
            .name(store.getName())
            .address(store.getAddress())
            .phoneNumber(store.getPhoneNumber())
            .image(store.getImage())
            .minOrderPrice(store.getMinOrderPrice())
            .openTime(store.getOpenTime())
            .closeTime(store.getCloseTime())
            .build();
    }

    private StoreListResponseDto toListResponseDto(Store store) {
        return StoreListResponseDto.builder()
            .id(store.getId())
            .name(store.getName())
            .image(store.getImage())
            .minOrderPrice(store.getMinOrderPrice())
            .openTime(store.getOpenTime())
            .closeTime(store.getCloseTime())
            .build();
    }
}
