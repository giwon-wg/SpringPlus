package com.example.springplusteamproject.domain.coupon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.coupon.dto.request.DiscountCouponRequestDto;
import com.example.springplusteamproject.domain.coupon.dto.response.IssuableUserCouponResponseDto;
import com.example.springplusteamproject.domain.coupon.dto.response.UserCouponIssueResponseDto;
import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.entity.UserCoupon;
import com.example.springplusteamproject.domain.coupon.repository.DiscountCouponRepository;
import com.example.springplusteamproject.domain.coupon.repository.UserCouponRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import java.time.LocalDate;
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

    private DiscountCouponRequestDto requestDto = new DiscountCouponRequestDto("5000원 할인", 5000L, LocalDate.now(),
            LocalDate.now(), 100L);
    private DiscountCoupon issuedDiscountCoupon;
    private DiscountCoupon discountCoupon;
    private CustomUserPrincipal principal;
    private UserCoupon userCoupon;
    private Long storeId;
    private String email;
    private Store store;
    private User user;

    @BeforeEach
    void setUp() {
        issuedDiscountCoupon = DiscountCoupon.builder()
                .id(2L)
                .build();
        discountCoupon = DiscountCoupon.builder()
                .id(1L)
                .couponName("쿠폰이름")
                .stock(10L)
                .build();
        userCoupon = UserCoupon.builder()
                .id(1L)
                .discountCoupon(discountCoupon)
                .build();

        storeId = 1L;
        principal = mock(CustomUserPrincipal.class);
        user = mock(User.class);
        store = mock(Store.class);
        email = "test@gmail.com";
    }

    @Test
    void 발급_가능한_쿠폰_목록_조회에_성공한다() {

        given(principal.getUsername()).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(storeRepository.findByIdAndDeletedFalse(storeId)).willReturn(Optional.of(store));
        given(userCouponRepository.findHavingCouponIds(user.getId(), storeId)).willReturn(List.of(2L));
        given(discountCouponRepository.findIssuableCouponList(List.of(2L), storeId)).willReturn(
                List.of(discountCoupon));

        List<IssuableUserCouponResponseDto> responseDtos = userCouponService.findIssuableUserCoupons(storeId,
                principal);
        assertEquals(1, responseDtos.size());

        verify(userRepository).findByEmail(email);
        verify(storeRepository).findByIdAndDeletedFalse(storeId);
        verify(userCouponRepository).findHavingCouponIds(user.getId(), storeId);
        verify(discountCouponRepository).findIssuableCouponList(List.of(2L), storeId);
    }

    @Test
    void 발급_가능한_쿠폰_상세_조회에_성공한다() {

        Long couponId = 1L;

        given(principal.getUsername()).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(userCouponRepository.existsByUser_IdAndDiscountCoupon_Id(user.getId(), couponId)).willReturn(false);
        given(storeRepository.findByIdAndDeletedFalse(storeId)).willReturn(Optional.of(store));
        given(discountCouponRepository.findIssuableCoupon(storeId, couponId)).willReturn(Optional.of(discountCoupon));

        IssuableUserCouponResponseDto responseDto = userCouponService.findIssuableUserCoupon(storeId, couponId,
                principal);

        verify(userRepository).findByEmail(email);
        verify(userCouponRepository).existsByUser_IdAndDiscountCoupon_Id(user.getId(), couponId);
        verify(storeRepository).findByIdAndDeletedFalse(storeId);
        verify(discountCouponRepository).findIssuableCoupon(storeId, couponId);
    }

    @Test
    void 이미_발급_받은_쿠폰은_상세_조회에_실패한다() {

        Long couponId = 1L;

        given(principal.getUsername()).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(userCouponRepository.existsByUser_IdAndDiscountCoupon_Id(user.getId(), couponId)).willReturn(true);

        ApiException exception = assertThrows(ApiException.class, () -> userCouponService.findIssuableUserCoupon(storeId, couponId, principal));

        assertEquals(ErrorStatus.COUPON_ALREADY_ISSUED, exception.getErrorCode());

        verify(userRepository).findByEmail(email);
        verify(userCouponRepository).existsByUser_IdAndDiscountCoupon_Id(user.getId(), couponId);
        verify(storeRepository, never()).findByIdAndDeletedFalse(storeId);
        verify(discountCouponRepository, never()).findIssuableCoupon(storeId, couponId);
    }

    @Test
    void 쿠폰_발급에_성공한다() {

        Long couponId = 1L;

        given(principal.getUsername()).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(userCouponRepository.existsByUser_IdAndDiscountCoupon_Id(user.getId(), couponId)).willReturn(false);
        given(storeRepository.findByIdAndDeletedFalse(storeId)).willReturn(Optional.of(store));
        given(discountCouponRepository.findIssuableCoupon(storeId, couponId)).willReturn(Optional.of(discountCoupon));
        given(userCouponRepository.save(any(UserCoupon.class))).willReturn(userCoupon);

        userCouponService.issueUserCoupon(storeId, couponId, principal);

        verify(userRepository).findByEmail(email);
        verify(userCouponRepository).existsByUser_IdAndDiscountCoupon_Id(user.getId(), couponId);
        verify(storeRepository).findByIdAndDeletedFalse(storeId);
        verify(discountCouponRepository).findIssuableCoupon(storeId, couponId);
        verify(userCouponRepository).save(any(UserCoupon.class));

        assertEquals(discountCoupon.getStock(), 9L);
    }

    @Test
    void 기존에_발급받은_쿠폰은_쿠폰_발급에_실패한다() {

        Long couponId = 1L;

        given(principal.getUsername()).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(userCouponRepository.existsByUser_IdAndDiscountCoupon_Id(user.getId(), couponId)).willReturn(true);

        ApiException exception = assertThrows(ApiException.class, () -> userCouponService.issueUserCoupon(storeId, couponId, principal));

        verify(userRepository).findByEmail(email);
        verify(userCouponRepository).existsByUser_IdAndDiscountCoupon_Id(user.getId(), couponId);
        verify(storeRepository, never()).findByIdAndDeletedFalse(storeId);
        verify(discountCouponRepository, never()).findIssuableCoupon(storeId, couponId);
        verify(userCouponRepository, never()).save(any(UserCoupon.class));

        assertEquals(ErrorStatus.COUPON_ALREADY_ISSUED, exception.getErrorCode());
    }

    @Test
    void 발급받은_쿠폰_목록_조회에_성공한다() {

        given(principal.getUsername()).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(userCouponRepository.findAllByUser_idAndIsUsedFalse(user.getId())).willReturn(List.of(userCoupon));

        List<UserCouponIssueResponseDto> responseDtos = userCouponService.findMyUserCoupons(principal);

        verify(userRepository).findByEmail(email);
        verify(userCouponRepository).findAllByUser_idAndIsUsedFalse(user.getId());

        assertEquals(responseDtos.size(), 1);
    }
}
