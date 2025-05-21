package com.example.springplusteamproject.domain.coupon.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.domain.coupon.dto.request.DiscountCouponRequestDto;
import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.repository.DiscountCouponRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.entity.User;
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
    private String email;
    private Store store;
    private User user;

    @BeforeEach
    void setUp() {
        discountCoupon = DiscountCoupon.builder()
            .id(1L)
            .build();
        storeId = 1L;
        principal = mock(CustomUserPrincipal.class);
        user = mock(User.class);
        store = mock(Store.class);
        email = "test@gmail.com";
    }

    @Test
    void 쿠폰_등록에_성공한다() {

        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));
        given(storeRepository.findByIdAndDeletedFalse(storeId)).willReturn(Optional.of(store));
        given(store.getUser()).willReturn(user);
        given(discountCouponRepository.save(any(DiscountCoupon.class))).willReturn(discountCoupon);

        discountCouponService.createCoupon(storeId, requestDto, principal);

        verify(userRepository).findByEmail(principal.getUsername());
        verify(storeRepository).findByIdAndDeletedFalse(storeId);
        verify(discountCouponRepository).save(any(DiscountCoupon.class));
    }

    @Test
    void 존재하지_않는_가게일_경우_예외가_발생한다() {

        given(principal.getUsername()).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(mock(User.class)));
        given(storeRepository.findByIdAndDeletedFalse(storeId)).willReturn(Optional.empty());

        assertThrows(ApiException.class, () -> discountCouponService.createCoupon(storeId, requestDto, principal));

        verify(userRepository).findByEmail(email);
        verify(storeRepository).findByIdAndDeletedFalse(storeId);
        verify(discountCouponRepository, never()).save(discountCoupon);
    }

    @Test
    void 가게_소유주가_아닌_사용자는_쿠폰_등록이_불가능하다() {

        User anotherUser = mock(User.class);

        given(principal.getUsername()).willReturn(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(storeRepository.findByIdAndDeletedFalse(storeId)).willReturn(Optional.of(store));
        given(user.getId()).willReturn(1L);
        given(anotherUser.getId()).willReturn(2L);
        given(store.getUser()).willReturn(anotherUser);

        assertThrows(ApiException.class, () -> discountCouponService.createCoupon(storeId, requestDto, principal));

        verify(userRepository).findByEmail(email);
        verify(storeRepository).findByIdAndDeletedFalse(storeId);
        verify(discountCouponRepository, never()).save(discountCoupon);
    }
}
