package com.example.springplusteamproject.domain.flower.service;

import static com.example.springplusteamproject.common.status.ErrorStatus.FLOWER_ACCESS_DENIED;
import static com.example.springplusteamproject.common.status.ErrorStatus.FLOWER_NOT_FOUND;
import static com.example.springplusteamproject.common.status.ErrorStatus.STORE_NOT_FOUND;
import static com.example.springplusteamproject.domain.flower.enums.Color.PINK;
import static com.example.springplusteamproject.domain.flower.enums.Color.YELLOW;
import static com.example.springplusteamproject.domain.flower.enums.Season.ALL;
import static com.example.springplusteamproject.domain.flower.enums.Season.SUMMER;
import static com.example.springplusteamproject.domain.flower.enums.Type.ROSE;
import static com.example.springplusteamproject.domain.flower.enums.Type.SUNFLOWER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto;
import com.example.springplusteamproject.domain.flower.dto.response.FlowerResponseDto.Get;
import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.flower.repository.FlowerRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.entity.User;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class FlowerServiceTest {

    @InjectMocks
    private FlowerServiceImpl flowerService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private FlowerRepository flowerRepository;

    private Flower flower;

    @BeforeEach
    void setUp() {

        User user = User.builder().id(1L).build();

        Store store = Store.builder().id(1L).user(user).build();

        flower = Flower.builder().id(1L).store(store).name("자나장미").description("영원한 사랑, 끝없는 사랑, 행복한 사랑").type(ROSE)
            .color(PINK).season(ALL).price(700).stock(100).expirationDate(LocalDate.parse("2025-05-19")).build();
    }

    @Nested
    @DisplayName("꽃 상품 등록")
    class CreateFlower {

        private FlowerRequestDto.Create requestDto;

        @BeforeEach
        void setUpCreateRequestDto() {
            requestDto = new FlowerRequestDto.Create(
                "자나장미",
                "영원한 사랑, 끝없는 사랑, 행복한 사랑",
                ROSE, PINK, ALL, 700, 100, "2025-05-19"
            );
        }

        @Test
        @DisplayName("상품 등록 성공")
        void createFlower_success() {

            // given
            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(flower.getStore()));
            given(flowerRepository.save(any(Flower.class))).willReturn(flower);

            // when
            FlowerResponseDto.Create createFlower = flowerService.createFlower(1L, requestDto, 1L);

            // then
            assertThat(createFlower.getName()).isEqualTo(flower.getName());
            verify(flowerRepository).save(any(Flower.class));
        }

        @Test
        @DisplayName("가게가 없을 경우 예외 발생")
        void createFlower_fail_storeNotFound() {

            // given
            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.empty());

            // when & then
            ApiException ex = assertThrows(ApiException.class, () -> flowerService.createFlower(1L, requestDto, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(STORE_NOT_FOUND);
        }

        @Test
        @DisplayName("본인 가게가 아닐 경우 예외 발생")
        void createFlower_fail_notMyStore() {

            // given
            User otherUser = User.builder().id(999L).build();
            Store otherStore = Store.builder().id(1L).user(otherUser).build();
            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(otherStore));

            // when & then
            ApiException ex = assertThrows(ApiException.class, () -> flowerService.createFlower(1L, requestDto, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(FLOWER_ACCESS_DENIED);
        }
    }

    @Nested
    @DisplayName("꽃 상품 수정")
    class updateFlower {

        private FlowerRequestDto.Update requestDto;

        @BeforeEach
        void setUpUpdateRequestDto() {
            requestDto = new FlowerRequestDto.Update(
                "해바라기",
                "숭배, 존경, 기다림, 당신만을 바라봅니다",
                SUNFLOWER, YELLOW, SUMMER, 2000, 50
            );
        }

        @Test
        @DisplayName("상품 수정 성공")
        void updateFlowerSuccess() {

            // given
            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(flower.getStore()));
            given(flowerRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(flower));

            // when
            flowerService.updateFlower(1L, 1L, requestDto, 1L);

            // then
            assertThat(flower.getName()).isEqualTo(requestDto.getName());
        }

        @Test
        @DisplayName("가게가 없을 경우 예외 발생")
        void updateFlower_fail_storeNotFound() {

            // given
            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.empty());

            // when & then
            ApiException ex = assertThrows(ApiException.class, () -> flowerService.updateFlower(1L, 1L, requestDto, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(STORE_NOT_FOUND);
        }

        @Test
        @DisplayName("본인 가게가 아닐 경우 예외 발생")
        void updateFlower_fail_notMyStore() {

            // given
            User otherUser = User.builder().id(999L).build();
            Store otherStore = Store.builder().id(1L).user(otherUser).build();
            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(otherStore));

            // when & then
            ApiException ex = assertThrows(ApiException.class, () -> flowerService.updateFlower(1L, 1L, requestDto, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(FLOWER_ACCESS_DENIED);
        }

        @Test
        @DisplayName("상품이 없을 경우 예외 발생")
        void updateFlower_fail_flowerNotFound() {

            // given
            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(flower.getStore()));
            given(flowerRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.empty());

            // when & then
            ApiException ex = assertThrows(ApiException.class, () -> flowerService.updateFlower(1L, 1L, requestDto, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(FLOWER_NOT_FOUND);
        }

        @Test
        @DisplayName("가게에 등록된 상품이 아닐 경우 예외 발생")
        void updateFlower_fail_notMyFlower() {

            // given
            Store otherStore = Store.builder().id(999L).build();
            Flower otherFlower = Flower.builder().id(1L).store(otherStore).build();

            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(flower.getStore()));
            given(flowerRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(otherFlower));

            // when & then
            ApiException ex = assertThrows(ApiException.class, () -> flowerService.updateFlower(1L, 1L, requestDto, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(FLOWER_ACCESS_DENIED);
        }
    }

    @Nested
    @DisplayName("꽃 상품 삭제")
    class deleteFlower {

        @Test
        @DisplayName("상품 삭제 성공")
        void deleteFlower_success() {

            // given
            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(flower.getStore()));
            given(flowerRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(flower));

            // when
            flowerService.deleteFlower(1L, 1L, 1L);

            // then
            assertThat(flower.getDeleted()).isEqualTo(true);
        }

        @Test
        @DisplayName("가게가 없을 경우 예외 발생")
        void deleteFlower_fail_storeNotFound() {

            // given
            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.empty());

            // when & then
            ApiException ex = assertThrows(ApiException.class, () -> flowerService.deleteFlower(1L, 1L, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(STORE_NOT_FOUND);
        }

        @Test
        @DisplayName("본인 가게가 아닐 경우 예외 발생")
        void deleteFlower_fail_notMyStore() {

            // given
            User otherUser = User.builder().id(999L).build();
            Store otherStore = Store.builder().id(1L).user(otherUser).build();
            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(otherStore));

            // when & then
            ApiException ex = assertThrows(ApiException.class, () -> flowerService.deleteFlower(1L, 1L, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(FLOWER_ACCESS_DENIED);
        }

        @Test
        @DisplayName("상품이 없을 경우 예외 발생")
        void deleteFlower_fail_flowerNotFound() {

            // given
            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(flower.getStore()));
            given(flowerRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.empty());

            // when & then
            ApiException ex = assertThrows(ApiException.class, () -> flowerService.deleteFlower(1L, 1L, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(FLOWER_NOT_FOUND);
        }

        @Test
        @DisplayName("가게에 등록된 상품이 아닐 경우 예외 발생")
        void deleteFlower_fail_notMyFlower() {

            // given
            Store otherStore = Store.builder().id(999L).build();
            Flower otherFlower = Flower.builder().id(1L).store(otherStore).build();

            given(storeRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(flower.getStore()));
            given(flowerRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(otherFlower));

            // when & then
            ApiException ex = assertThrows(ApiException.class, () -> flowerService.deleteFlower(1L, 1L, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(FLOWER_ACCESS_DENIED);
        }
    }

    @Nested
    @DisplayName("꽃 상품 목록 조회 (본인 가게)")
    class getMyFlowers {

        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        @DisplayName("조회 성공 (데이터 O)")
        void getMyFlowers_success_withData() {

            // given
            List<Flower> flowers = List.of(flower);
            Page<Flower> flowerPage = new PageImpl<>(flowers, pageable, flowers.size());

            given(flowerRepository.findByStore_IdAndDeletedFalse(anyLong(), any(Pageable.class))).willReturn(flowerPage);

            // when
            Page<FlowerResponseDto.Get> getFlowers = flowerService.getMyFlowers(1L, 1, 10);

            // then
            assertThat(getFlowers.getContent()).hasSize(1);
            assertThat(getFlowers.getContent().get(0).getName()).isEqualTo(flower.getName());
            verify(flowerRepository).findByStore_IdAndDeletedFalse(anyLong(), any(Pageable.class));
        }

        @Test
        @DisplayName("조회 성공 (데이터 X)")
        void getMyFlowers_success_empty() {

            // given
            Page<Flower> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            given(flowerRepository.findByStore_IdAndDeletedFalse(anyLong(), any(Pageable.class))).willReturn(emptyPage);

            // when
            Page<FlowerResponseDto.Get> getFlowers = flowerService.getMyFlowers(1L, 1, 10);

            // then
            assertThat(getFlowers.getContent()).isEmpty();
            assertThat(getFlowers.getTotalElements()).isZero();
            verify(flowerRepository).findByStore_IdAndDeletedFalse(anyLong(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("꽃 상품 단건 조회")
    class getFlowerDetails {

        @Test
        @DisplayName("조회 성공")
        void getFlowerDetails_success() {

            // given
            given(flowerRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.of(flower));

            // when
            FlowerResponseDto.Get getFlower = flowerService.getFlowerDetails(1L, 1L);

            // then
            assertThat(getFlower.getName()).isEqualTo(flower.getName());
            verify(flowerRepository).findByIdAndDeletedFalse(anyLong());
        }

        @Test
        @DisplayName("상품이 없을 경우 에러 발생")
        void getFlowerDetails_fail() {

            // given
            given(flowerRepository.findByIdAndDeletedFalse(anyLong())).willReturn(Optional.empty());

            // when & then
            ApiException ex = assertThrows(ApiException.class, () -> flowerService.getFlowerDetails(1L, 1L));
            assertThat(ex.getErrorCode()).isEqualTo(FLOWER_NOT_FOUND);
        }
    }
}
