package com.example.springplusteamproject.domain.coupon.service;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.coupon.dto.response.AvailableUserCouponResponseDto;
import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.entity.UserCoupon;
import com.example.springplusteamproject.domain.coupon.repository.DiscountCouponRepository;
import com.example.springplusteamproject.domain.coupon.repository.UserCouponRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService {

    private final DiscountCouponRepository discountCouponRepository;
    private final UserCouponRepository userCouponRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Override
    public List<AvailableUserCouponResponseDto> findAvailableUserCoupons(Long storeId, CustomUserPrincipal principal) {

        User user = userRepository.findByEmail(principal.getUsername())
            .orElseThrow(() -> new ApiException(ErrorStatus.USER_NOT_FOUND));
        user.validateDelete();

        // TODO store의 예외 코드 추가되면 해당 예외 코드로 수정
        Store store = storeRepository.findByIdAndDeletedFalse(storeId)
            .orElseThrow(() -> new ApiException(ErrorStatus.FORBIDDEN));

        List<Long> couponIds = userCouponRepository.findHavingCouponIds(user.getId(), storeId);
        List<DiscountCoupon> availableCouponList = discountCouponRepository.findAvailableCouponList(couponIds, storeId);

        return availableCouponList.stream().map(AvailableUserCouponResponseDto::from).collect(Collectors.toList());
    }
}
