package com.example.springplusteamproject.domain.flower.service;

import static com.example.springplusteamproject.domain.flower.enums.Color.PINK;
import static com.example.springplusteamproject.domain.flower.enums.Season.ALL;
import static com.example.springplusteamproject.domain.flower.enums.Type.ROSE;

import com.example.springplusteamproject.domain.flower.dto.request.FlowerRequestDto;
import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.flower.repository.FlowerRepository;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlowerServiceTest {

    @InjectMocks
    private FlowerServiceImpl flowerService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private FlowerRepository flowerRepository;

    private Flower baseFlower;

    @BeforeEach
    void setUp() {

        baseFlower = Flower.builder()
            .id(1L)
            .name("자나장미")
            .description("영원한 사랑, 끝없는 사랑, 행복한 사랑")
            .type(ROSE)
            .color(PINK)
            .season(ALL)
            .price(700)
            .stock(100)
            .expirationDate(LocalDate.parse("2025-05-19"))
            .build();
    }

    @Nested
    @DisplayName("꽃 상품 등록")
    class CreateFlower {

        @Test
        @DisplayName("상품 등록 성공")
        void createFlowerSuccess() {

            // given
            Flower flower = baseFlower.toBuilder()
                .deleted(false)
                .build();

            // when

            // then
        }

        @Test
        @DisplayName("가게가 없을 경우 예외 발생")
        void createFlowerFail1() {

            // given

            // when

            // then
        }

        @Test
        @DisplayName("본인 가게가 아닐 경우 예외 발생")
        void createFlowerFail_() {

            // given

            // when

            // then
        }
    }

    @Nested
    @DisplayName("꽃 상품 수정 / 삭제 권한 확인")
    class checkAuth {

        @Nested
        @DisplayName("상품 등록 가능한 가게인지 확인")
        class checkStoreAuth {

            @Test
            @DisplayName("권한 확인 성공")
            void checkStoreAuthSuccess() {

                // given

                // when

                // then
            }

            @Test
            @DisplayName("존재하지 않는 가게인 경우 예외 발생")
            void checkStoreAuthFail0() {

                // given

                // when

                // then
            }

            @Test
            @DisplayName("본인 가게가 아닌 경우 예외 발생")
            void checkStoreAuthFail1() {

                // given

                // when

                // then
            }
        }

        @Nested
        @DisplayName("등록 가능한 상품인지 확인")
        class checkFlowerAuth {

            @Test
            @DisplayName("권한 확인 성공")
            void checkFlowerAuthSuccess() {

                // given

                // when

                // then
            }

            @Test
            @DisplayName("상품이 없을 경우 예외 발생")
            void checkFlowerAuthFail0() {

                // given

                // when

                // then
            }

            @Test
            @DisplayName("가게에 등록된 상품이 아닌 경우 예외 발생")
            void checkFlowerAuthFail2() {

                // given

                // when

                // then
            }
        }
    }

    @Nested
    @DisplayName("꽃 상품 수정")
    class updateFlower {

        @Test
        @DisplayName("상품 수정 성공")
        void updateFlowerSuccess() {

            // given

            // when

            // then
        }

        @Test
        @DisplayName("가게 권한 오류로 수정 실패")
        void updateFlowerFail() {

            // given

            // when

            // then
        }

        @Test
        @DisplayName("상품 권한 오류로 수정 실패")
        void updateFlowerFail2() {

            // given

            // when

            // then
        }
    }

    @Nested
    @DisplayName("꽃 상품 삭제")
    class deleteFlower {

        @Test
        @DisplayName("상품 삭제 성공")
        void deleteFlowerSuccess() {

            // given

            // when

            // then
        }

        @Test
        @DisplayName("가게 권한 오류로 삭제 실패")
        void deleteFlowerFail() {

            // given

            // when

            // then
        }

        @Test
        @DisplayName("상품 권한 오류로 삭제 실패")
        void deleteFlowerFail2() {

            // given

            // when

            // then
        }
    }

    @Nested
    @DisplayName("꽃 상품 목록 조회 (본인 가게)")
    class getMyFlowers {

        @Test
        @DisplayName("조회 성공")
        void getMyFlowersSuccess() {

            // given

            // when

            // then
        }
    }

    @Nested
    @DisplayName("꽃 상품 단건 조회")
    class getFlowerDetails {

        @Test
        @DisplayName("조회 성공")
        void getFlowerDetailsSuccess() {

            // given

            // when

            // then
        }

        @Test
        @DisplayName("상품이 없을 경우 에러 발생")
        void getFlowerDetailsFail() {

            // given

            // when

            // then
        }
    }
}
