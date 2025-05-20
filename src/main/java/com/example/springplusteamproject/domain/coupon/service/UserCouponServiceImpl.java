package com.example.springplusteamproject.domain.coupon.service;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.coupon.dto.response.IssuableUserCouponResponseDto;
import com.example.springplusteamproject.domain.coupon.dto.response.UserCouponIssueResponseDto;
import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.entity.UserCoupon;
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
    public List<IssuableUserCouponResponseDto> findIssuableUserCoupons(Long storeId, CustomUserPrincipal principal) {

        User user = validateActivateUser(principal.getUsername());

        // TODO store의 예외 코드 추가되면 해당 예외 코드로 수정
        validateActivateStore(storeId);

        List<Long> couponIds = userCouponRepository.findHavingCouponIds(user.getId(), storeId);
        List<DiscountCoupon> availableCouponList = discountCouponRepository.findIssuableCouponList(couponIds, storeId);

        return availableCouponList.stream().map(IssuableUserCouponResponseDto::from).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public IssuableUserCouponResponseDto findIssuableUserCoupon(Long storeId, Long couponId,
                                                                CustomUserPrincipal principal) {

        User user = validateActivateUser(principal.getUsername());
        validateCouponNotIssued(user.getId(), couponId);

        validateActivateStore(storeId);
        DiscountCoupon availableCoupon = validateAvailableCoupon(storeId, couponId);

        return IssuableUserCouponResponseDto.from(availableCoupon);
    }

    @Override
    @Transactional
    public UserCouponIssueResponseDto issueUserCoupon(Long storeId, Long couponId, CustomUserPrincipal principal) {

        User user = validateActivateUser(principal.getUsername());
        validateCouponNotIssued(user.getId(), couponId);

        validateActivateStore(storeId);
        DiscountCoupon availableCoupon = validateAvailableCoupon(storeId, couponId);

        UserCoupon issueCoupon = createUserCoupon(user, availableCoupon);
        UserCoupon savedCoupon = userCouponRepository.save(issueCoupon);
        availableCoupon.decreaseStock();

        return UserCouponIssueResponseDto.from(availableCoupon, savedCoupon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCouponIssueResponseDto> findMyUserCoupons(CustomUserPrincipal principal) {

        User user = validateActivateUser(principal.getUsername());

        List<UserCoupon> myUserCouponList = userCouponRepository.findAllByUser_idAndIsUsedFalse(user.getId());

        return myUserCouponList.stream()
            .map(userCoupon -> UserCouponIssueResponseDto.from(userCoupon.getDiscountCoupon(), userCoupon))
            .collect(Collectors.toList());
    }

    private User validateActivateUser(String username) {

        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new ApiException(ErrorStatus.USER_NOT_FOUND));
        user.validateDelete();

        return user;
    }

    private DiscountCoupon validateAvailableCoupon(Long storeId, Long couponId) {

        DiscountCoupon availableCoupon = discountCouponRepository.findAvailableCoupon(storeId, couponId)
            .orElseThrow(() -> new ApiException(ErrorStatus.COUPON_NOT_FOUND));

        return availableCoupon;
    }

    private void validateActivateStore(Long storeId) {

        storeRepository.findByIdAndDeletedFalse(storeId).orElseThrow(() -> new ApiException(ErrorStatus.FORBIDDEN));
    }

    private void validateCouponNotIssued(Long userId, Long couponId) {

        boolean alreadyIssued = userCouponRepository.existsByUser_IdAndDiscountCoupon_Id(userId, couponId);
        if (alreadyIssued) {
            throw new ApiException(ErrorStatus.COUPON_ALREADY_ISSUED);
        }
    }

    private UserCoupon createUserCoupon(User user, DiscountCoupon discountCoupon) {
        return UserCoupon.builder()
            .user(user)
            .discountCoupon(discountCoupon)
            .isUsed(false)
            .build();
    }
}
