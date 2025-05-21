package com.example.springplusteamproject.domain.coupon.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.coupon.dto.request.DiscountCouponRequestDto;
import com.example.springplusteamproject.domain.coupon.dto.response.DiscountCouponResponseDto;
import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.repository.DiscountCouponRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DiscountCouponServiceTest {

    @Mock
    private DiscountCouponRepository discountCouponRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DiscountCouponServiceImpl discountCouponService;

    private DiscountCouponRequestDto requestDto = new DiscountCouponRequestDto("5000원 할인", 5000L, LocalDate.now(), LocalDate.now(), 100L);
    private DiscountCoupon discountCoupon;
    private CustomUserPrincipal principal;
    private Long storeId;
    private Store store;
    private User user;

    @BeforeEach
    void setUp() {
        discountCoupon = DiscountCoupon.builder()
            .id(1L)
            .couponName("5000원 할인")
            .discount(5000L)
            .stock(500L)
            .build();
        storeId = 1L;
        user = User.builder()
            .id(1L)
            .nickname("가게 주인")
            .email("1@gmail.com")
            .userRole(UserRole.OWNER)
            .build();
        store = Store.builder()
            .id(1L)
            .name("가게 이름")
            .user(user)
            .build();
        principal = new CustomUserPrincipal(user);
    }

    @Test
    void 쿠폰_등록에_성공한다() {

        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));
        given(storeRepository.findByIdAndDeletedFalse(storeId)).willReturn(Optional.of(store));
        given(discountCouponRepository.save(any(DiscountCoupon.class))).willReturn(discountCoupon);

        DiscountCouponResponseDto responseDto = discountCouponService.createCoupon(storeId, requestDto, principal);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(discountCoupon.getId());
        assertThat(responseDto.getCouponName()).isEqualTo(discountCoupon.getCouponName());
        assertThat(responseDto.getDiscount()).isEqualTo(discountCoupon.getDiscount());
    }

    @Test
    void 존재하지_않는_가게일_경우_예외가_발생한다() {

        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));
        given(storeRepository.findByIdAndDeletedFalse(storeId)).willReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> discountCouponService.createCoupon(storeId, requestDto, principal));

        verify(discountCouponRepository, never()).save(discountCoupon);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorStatus.STORE_NOT_FOUND);
    }

    @Test
    void 가게_소유주가_아닌_사용자는_쿠폰_등록이_불가능하다() {

        User anotherUser = User.builder()
            .id(2L)
            .nickname("다른 가게 주인")
            .email("2@gmail.com")
            .userRole(UserRole.OWNER)
            .build();

        principal = new CustomUserPrincipal(anotherUser);

        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(anotherUser));
        given(storeRepository.findByIdAndDeletedFalse(storeId)).willReturn(Optional.of(store));

        ApiException exception = assertThrows(ApiException.class, () -> discountCouponService.createCoupon(storeId, requestDto, principal));

        verify(discountCouponRepository, never()).save(discountCoupon);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorStatus.ROLE_OWNER_FORBIDDEN);
    }
}
