package com.example.springplusteamproject.domain.coupon.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.coupon.dto.response.IssuableUserCouponResponseDto;
import com.example.springplusteamproject.domain.coupon.dto.response.UserCouponIssueResponseDto;
import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.entity.UserCoupon;
import com.example.springplusteamproject.domain.coupon.repository.DiscountCouponRepository;
import com.example.springplusteamproject.domain.coupon.repository.UserCouponRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceTest {

    @Mock
    private DiscountCouponRepository discountCouponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserCouponServiceImpl userCouponService;

    private DiscountCoupon issuedDiscountCoupon;
    private DiscountCoupon discountCoupon;
    private CustomUserPrincipal principal;
    private UserCoupon userCoupon;
    private Long storeId;
    private Store store;
    private User user;

    @BeforeEach
    void setUp() {
        issuedDiscountCoupon = DiscountCoupon.builder()
            .id(2L)
            .couponName("5000원 할인")
            .discount(5000L)
            .stock(500L)
            .build();
        discountCoupon = DiscountCoupon.builder()
            .id(1L)
            .couponName("5000원 할인")
            .discount(5000L)
            .stock(500L)
            .build();
        userCoupon = UserCoupon.builder()
            .id(1L)
            .discountCoupon(discountCoupon)
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
    void 발급_가능한_쿠폰_목록_조회에_성공한다() {

        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));
        given(storeRepository.findByIdAndDeletedFalse(storeId)).willReturn(Optional.of(store));
        given(userCouponRepository.findHavingCouponIds(user.getId(), storeId)).willReturn(List.of(2L));
        given(discountCouponRepository.findIssuableCouponList(List.of(2L), storeId)).willReturn(
            List.of(discountCoupon));

        List<IssuableUserCouponResponseDto> responseDtos = userCouponService.findIssuableUserCoupons(storeId,
            principal);

        assertThat(responseDtos).hasSize(1);
        assertThat(responseDtos.get(0).getCouponName()).isEqualTo(discountCoupon.getCouponName());
        assertThat(responseDtos.get(0).getDiscount()).isEqualTo(discountCoupon.getDiscount());
    }

    @Test
    void 발급_가능한_쿠폰_상세_조회에_성공한다() {

        Long couponId = 1L;

        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));
        given(userCouponRepository.existsByUser_IdAndDiscountCoupon_Id(user.getId(), couponId)).willReturn(false);
        given(storeRepository.findByIdAndDeletedFalse(storeId)).willReturn(Optional.of(store));
        given(discountCouponRepository.findIssuableCoupon(storeId, couponId)).willReturn(Optional.of(discountCoupon));

        IssuableUserCouponResponseDto responseDto = userCouponService.findIssuableUserCoupon(storeId, couponId,
            principal);

        assertThat(responseDto.getCouponName()).isEqualTo(discountCoupon.getCouponName());
        assertThat(responseDto.getDiscount()).isEqualTo(discountCoupon.getDiscount());
    }

    @Test
    void 이미_발급_받은_쿠폰은_상세_조회에_실패한다() {

        Long issuedCouponId = 2L;

        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));
        given(userCouponRepository.existsByUser_IdAndDiscountCoupon_Id(user.getId(), issuedCouponId)).willReturn(true);

        ApiException exception = assertThrows(ApiException.class,
            () -> userCouponService.findIssuableUserCoupon(storeId, issuedCouponId, principal));

        verify(discountCouponRepository, never()).findIssuableCoupon(storeId, issuedCouponId);

        assertThat(exception.getErrorCode()).isEqualTo(ErrorStatus.COUPON_ALREADY_ISSUED);
    }

    @Test
    void 쿠폰_발급에_성공한다() {

        Long couponId = 1L;

        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));
        given(userCouponRepository.existsByUser_IdAndDiscountCoupon_Id(user.getId(), couponId)).willReturn(false);
        given(storeRepository.findByIdAndDeletedFalse(storeId)).willReturn(Optional.of(store));
        given(discountCouponRepository.findIssuableCoupon(storeId, couponId)).willReturn(Optional.of(discountCoupon));
        given(userCouponRepository.save(any(UserCoupon.class))).willReturn(userCoupon);

        UserCouponIssueResponseDto responseDto = userCouponService.issueUserCoupon(storeId, couponId, principal);

        assertThat(discountCoupon.getStock()).isEqualTo(499L);
        assertThat(responseDto.getCouponName()).isEqualTo(discountCoupon.getCouponName());
        assertThat(responseDto.getDiscount()).isEqualTo(discountCoupon.getDiscount());
    }

    @Test
    void 기존에_발급받은_쿠폰은_쿠폰_발급에_실패한다() {

        Long issuedCouponId = 2L;

        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));
        given(userCouponRepository.existsByUser_IdAndDiscountCoupon_Id(user.getId(), issuedCouponId)).willReturn(true);

        ApiException exception = assertThrows(ApiException.class,
            () -> userCouponService.issueUserCoupon(storeId, issuedCouponId, principal));

        verify(storeRepository, never()).findByIdAndDeletedFalse(storeId);
        verify(discountCouponRepository, never()).findIssuableCoupon(storeId, issuedCouponId);
        verify(userCouponRepository, never()).save(any(UserCoupon.class));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorStatus.COUPON_ALREADY_ISSUED);
    }

    @Test
    void 발급받은_쿠폰_목록_조회에_성공한다() {

        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));
        given(userCouponRepository.findAllByUser_idAndIsUsedFalse(user.getId())).willReturn(List.of(userCoupon));

        List<UserCouponIssueResponseDto> responseDtos = userCouponService.findMyUserCoupons(principal);

        assertThat(responseDtos.size()).isEqualTo(1);
        assertThat(responseDtos.get(0).getCouponName()).isEqualTo(userCoupon.getDiscountCoupon().getCouponName());
        assertThat(responseDtos.get(0).getDiscount()).isEqualTo(userCoupon.getDiscountCoupon().getDiscount());
    }
}
