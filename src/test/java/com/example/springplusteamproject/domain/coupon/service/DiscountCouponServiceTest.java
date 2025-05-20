package com.example.springplusteamproject.domain.coupon.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {
        discountCoupon = DiscountCoupon.builder()
            .id(1L)
            .build();
    }

    @Test
    void 쿠폰_등록에_성공한다() {
        Long storeId = 1L;

        CustomUserPrincipal principal = mock(CustomUserPrincipal.class);
        User user = mock(User.class);
        Store store = mock(Store.class);

        when(userRepository.findByEmail(principal.getUsername())).thenReturn(Optional.of(user));
        when(storeRepository.findByIdAndDeletedFalse(storeId)).thenReturn(Optional.of(store));
        when(discountCouponRepository.save(any(DiscountCoupon.class))).thenReturn(discountCoupon);

        discountCouponService.createCoupon(storeId, requestDto, principal);

        verify(userRepository).findByEmail(principal.getUsername());
        verify(storeRepository).findByIdAndDeletedFalse(storeId);
        verify(discountCouponRepository).save(any(DiscountCoupon.class));
    }

    @Test
    void 존재하지_않는_가게일_경우_예외가_발생한다() {
        Long storeId = 1L;
        String email = "1@gmail.com";
        CustomUserPrincipal principal = mock(CustomUserPrincipal.class);

        when(principal.getUsername()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mock(User.class)));
        when(storeRepository.findByIdAndDeletedFalse(storeId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> discountCouponService.createCoupon(storeId, requestDto, principal));

        verify(userRepository).findByEmail(email); verify(storeRepository).findByIdAndDeletedFalse(storeId);
    }

    // TODO 사용자 인가 부분 수정 후 인가 관련 실패 테스트 코드 추가
}
