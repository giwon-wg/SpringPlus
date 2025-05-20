package com.example.springplusteamproject.domain.coupon.service;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.coupon.dto.response.AvailableUserCouponResponseDto;
import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.repository.DiscountCouponRepository;
import com.example.springplusteamproject.domain.coupon.repository.UserCouponRepository;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService {

    private final DiscountCouponRepository discountCouponRepository;
    private final UserCouponRepository userCouponRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AvailableUserCouponResponseDto> findAvailableUserCoupons(Long storeId, CustomUserPrincipal principal) {

        User user = validateActivateUser(principal.getUsername());

        // TODO store의 예외 코드 추가되면 해당 예외 코드로 수정
        validateActivateStore(storeId);

        List<Long> couponIds = userCouponRepository.findHavingCouponIds(user.getId(), storeId);
        List<DiscountCoupon> availableCouponList = discountCouponRepository.findAvailableCouponList(couponIds, storeId);

        return availableCouponList.stream().map(AvailableUserCouponResponseDto::from).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AvailableUserCouponResponseDto findAvailableUserCoupon(Long storeId, Long couponId, CustomUserPrincipal principal) {

        User user = validateActivateUser(principal.getUsername());
        isCouponAlreadyIssued(user.getId(), couponId);

        validateActivateStore(storeId);
        DiscountCoupon availableCoupon = discountCouponRepository.findAvailableCoupon(storeId, couponId)
            .orElseThrow(() -> new ApiException(ErrorStatus.COUPON_NOT_FOUND));

        return AvailableUserCouponResponseDto.from(availableCoupon);
    }

    private User validateActivateUser(String username) {

        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new ApiException(ErrorStatus.USER_NOT_FOUND));
        user.validateDelete();

        return user;
    }

    private void validateActivateStore(Long storeId) {

        storeRepository.findByIdAndDeletedFalse(storeId).orElseThrow(() -> new ApiException(ErrorStatus.FORBIDDEN));
    }

    private void isCouponAlreadyIssued(Long userId, Long couponId) {

        boolean alreadyIssued = userCouponRepository.existsByUser_IdAndDiscountCoupon_Id(userId, couponId);
        if (alreadyIssued) {
            throw new ApiException(ErrorStatus.COUPON_ALREADY_ISSUED);
        }
    }
}
