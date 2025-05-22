package com.example.springplusteamproject.domain.store.service;


import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.security.CustomUserPrincipal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    @Override
    public StoreResponseDto createStore(StoreRequestDto dto, CustomUserPrincipal principal) {

        if (storeRepository.existsByUserIdAndDeletedFalse(principal.getId())) {
            log.warn("[Store - 가게 생성] 유저 Id 없음, userId: {}", principal.getId());
            throw new ApiException(ErrorStatus.STORE_BAD_REQUEST);
        }

        if (ForbiddenWordUtil.containsForbiddenWord(dto.getName())) {
            log.warn("[Store - 가게 생성] 가게 이름에 금지어 포함, storeName: {}", dto.getName());
            throw new ApiException(ErrorStatus.STORE_BAD_REQUEST);
        }

        if (storeRepository.existsByNameAndDeletedFalse(dto.getName())) {
            log.warn("[Store - 가게 생성] 가게 이름 중복, storeName: {}", dto.getName());
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
            .user(User.builder().id(principal.getId()).build())
            .build();

        Store saved = storeRepository.save(store);
        log.info("[Store - 가게 생성] 가게 생성 성공, userId={} storeId={}", principal.getId(), saved.getId());
        return toResponseDto(saved);
    }

    @Transactional
    @Override
    public void deleteStore(CustomUserPrincipal principal) {

        Store store = storeRepository.findByUserIdAndDeletedFalse(principal.getId())
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
            .map(this::toListResponseDto)
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
        log.info("[Store - 가게 단건 조회] 가게 단건 조회 성공, storeId: {}", id);
        return toResponseDto(store);
    }

    @Transactional(readOnly=true)
    @Override
    public String checkingName(StoreCheckNameRequestDto dto) {

        String name = dto.getName();

        long start = System.nanoTime();

        boolean exists = storeRepository.existsByNameAndDeletedFalse(name);
        long afterExists = System.nanoTime();

        boolean hasForbidden = ForbiddenWordUtil.containsForbiddenWord(name);
        long afterForbidden = System.nanoTime();

        System.out.printf("중복이름 조회: %.2fms, 금지어 확인: %.2fms%n",
            (afterExists - start) / 1_000_000.0,
            (afterForbidden - afterExists) / 1_000_000.0
        );

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
