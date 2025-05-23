package com.example.springplusteamproject.domain.store.service;

import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springplusteamproject.common.config.ForbiddenWordUtil;
import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.store.dto.request.StoreRequestDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreTransactionalService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;


    @Transactional
    public StoreResponseDto saveStore(StoreRequestDto dto, CustomUserPrincipal principal) {

        String storeName = dto.getName();

        if (!userRepository.existsByEmail(principal.getUsername())){
            log.warn("[Store - 가게 생성] 유저 없음, userEmail: {}", principal.getUsername());
            throw new ApiException(ErrorStatus.USER_NOT_FOUND);
        }

        if (storeRepository.existsByUserIdAndDeletedFalse(principal.getId())) {
            log.warn("[Store - 가게 생성] 보유 가게 수 제약, userId: {}", principal.getId());
            throw new ApiException(ErrorStatus.STORE_BAD_REQUEST);
        }

        if (ForbiddenWordUtil.containsForbiddenWord(storeName)) {
            log.warn("[Store - 가게 생성] 가게 이름에 금지어 포함, storeName: {}", storeName);
            throw new ApiException(ErrorStatus.STORE_BAD_REQUEST);
        }

        if (storeRepository.existsByNameAndDeletedFalse(storeName)) {
            log.warn("[Store - 가게 생성] 가게 이름 중복, storeName: {}", storeName);
            throw new ApiException(ErrorStatus.STORE_BAD_REQUEST);
        }

        Store store = Store.builder()
            .name(storeName)
            .address(dto.getAddress())
            .image(dto.getImage())
            .phoneNumber(dto.getPhoneNumber())
            .minOrderPrice(dto.getMinOrderPrice())
            .openTime(LocalTime.parse(dto.getOpenTime()))
            .closeTime(LocalTime.parse(dto.getCloseTime()))
            .deleted(false)
            .user(User.builder().id(principal.getId()).build())
            .build();

        Store saved = storeRepository.save(store);
        storeRepository.flush();
        log.info("[Store - 가게 생성] 가게 생성 성공, userId={} storeId={}", principal.getId(), saved.getId());
        return toResponseDto(saved);
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
}
