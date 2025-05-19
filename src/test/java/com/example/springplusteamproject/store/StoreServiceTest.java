package com.example.springplusteamproject.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.domain.store.dto.request.StoreRequestDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreListResponseDto;
import com.example.springplusteamproject.domain.store.dto.response.StoreResponseDto;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.store.service.StoreService;
import com.example.springplusteamproject.temp.UserDummy;
import static org.assertj.core.api.Assertions.assertThat;


class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    private StoreService storeService;

    private Store store;

    @BeforeEach
    void SetUp() {
        MockitoAnnotations.openMocks(this);
        storeService = new StoreService(storeRepository);

        store = Store.builder()
            .id(1L)
            .name("안개꽃 화원")
            .address("대구 광역시")
            .phoneNumber("010-1234-5678")
            .minOrderPrice(15000L)
            .openTime(LocalTime.of(9, 0))
            .closeTime(LocalTime.of(18, 0))
            .deleted(false)
            .user(UserDummy.INSTANCE)
            .build();
    }

    @Test
    void 가게_생성_성공(){
        // given
        StoreRequestDto dto = new StoreRequestDto(
            "장미 화원",
            "대구 광역시",
            "이미지",
            "010-1234-5678",
            15000L,
            "09:00",
            "18:00"
        );
        when(storeRepository.existsByNameAndDeletedFalse(dto.getName()))
            .thenReturn(false);

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

        when(storeRepository.save(any(Store.class))).thenReturn(store);

        // when
        StoreResponseDto response = storeService.createStore(dto);

        // then
        assertThat(response.getName()).isEqualTo("장미 화원");
        assertThat(response.getAddress()).isEqualTo("대구 광역시");
        assertThat(response.getImage()).isEqualTo("이미지");
        assertThat(response.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(response.getMinOrderPrice()).isEqualTo(15000L);
        assertThat(response.getOpenTime()).isEqualTo("09:00");
        assertThat(response.getCloseTime()).isEqualTo("18:00");
    }

    @Test
    void 가게_생성_이름중복_예외() {
        // given
        StoreRequestDto dto = new StoreRequestDto(
            "안개꽃 화원",
            "대구 광역시",
            "이미지",
            "010-1234-5678",
            15000L,
            "09:00",
            "18:00"
        );
        when(storeRepository.existsByNameAndDeletedFalse(dto.getName())).thenReturn(true);

        // when & then
        assertThrows(ApiException.class, () -> storeService.createStore(dto));
    }

    @Test
    void 가게_삭제_성공() {
        // given
        Store store = Store.builder()
            .id(1L)
            .name("삭제할 화원")
            .deleted(false)
            .user(UserDummy.INSTANCE)
            .build();
        when(storeRepository.findByIdAndDeletedFalse(1L))
            .thenReturn(Optional.of(store));

        // when
        storeService.deleteStore(1L);

        // then
        store.setDeleted();
        assertThat(store.getDeleted()).isTrue();
        verify(storeRepository).save(store);
    }

    @Test
    void 가게_삭제_Id없음_예외() {
        // given
        when(storeRepository.findByIdAndDeletedFalse(999L))
            .thenReturn(Optional.empty());

        // when & then
        assertThrows(ApiException.class, () -> storeService.deleteStore(999L));
    }

    @Test
    void 전체_가게_조회_성공() {

        // given
        List<Store> stores = List.of(
            Store.builder()
                .id(1L)
                .name("장미 화원")
                .address("대구 광역시")
                .image("이미지")
                .phoneNumber("010-1234-5678")
                .minOrderPrice(10000L)
                .openTime(LocalTime.of(9, 0))
                .closeTime(LocalTime.of(20, 0))
                .deleted(false)
                .user(UserDummy.INSTANCE)
                .build(),
            Store.builder()
                .id(2L)
                .name("튤립 화원")
                .address("서을 특별시")
                .image("이미지")
                .phoneNumber("010-8765-4321")
                .minOrderPrice(12000L)
                .openTime(LocalTime.of(10, 0))
                .closeTime(LocalTime.of(21, 0))
                .deleted(false)
                .user(UserDummy.INSTANCE)
                .build()
        );

        when(storeRepository.findByDeletedFalse()).thenReturn(stores);

        // when
        List<StoreListResponseDto> response = storeService.getAllStores();

        // then
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getName()).isEqualTo("장미 화원");
        assertThat(response.get(1).getName()).isEqualTo("튤립 화원");
    }

    @Test
    void 단일_가게_조회_성공() {
        // given
        Store store = Store.builder()
            .id(1L)
            .name("장미 화원")
            .address("제주 특별시")
            .image("이미지")
            .phoneNumber("010-1234-5678")
            .minOrderPrice(11000L)
            .openTime(LocalTime.of(9, 0))
            .closeTime(LocalTime.of(18, 0))
            .deleted(false)
            .user(UserDummy.INSTANCE)
            .build();

        when(storeRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(store));

        // when
        StoreResponseDto response = storeService.getStoreById(1L);

        // then
        assertThat(response.getName()).isEqualTo("장미 화원");
        assertThat(response.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(response.getImage()).isEqualTo("이미지");
        assertThat(response.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(response.getMinOrderPrice()).isEqualTo(11000L);
        assertThat(response.getOpenTime()).isEqualTo("09:00");
        assertThat(response.getCloseTime()).isEqualTo("18:00");
    }

    @Test
    void 단일_가게_조회_Id없음_예외() {
        // given
        when(storeRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ApiException.class, () -> storeService.getStoreById(999L));
    }
}
