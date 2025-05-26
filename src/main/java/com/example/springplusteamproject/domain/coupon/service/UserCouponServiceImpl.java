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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService {

    private final UserCouponTransactionalService userCouponTransactionalService;
    private final DiscountCouponRepository discountCouponRepository;
    private final UserCouponRepository userCouponRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;

    @Override
    @Transactional(readOnly = true)
    public List<IssuableUserCouponResponseDto> findIssuableUserCoupons(Long storeId, CustomUserPrincipal principal) {

        User user = validateActivateUser(principal.getUsername());

        validateActivateStore(storeId);

        return discountCouponRepository.findIssuableCouponDtoList(user.getId(), storeId);
    }

    @Override
    @Transactional(readOnly = true)
    public IssuableUserCouponResponseDto findIssuableUserCoupon(Long storeId, Long couponId,
                                                                CustomUserPrincipal principal) {

        User user = validateActivateUser(principal.getUsername());
        validateCouponNotIssued(user.getId(), couponId);

        validateActivateStore(storeId);
        DiscountCoupon issuableCoupon = validateIssuableCoupon(storeId, couponId);

        return IssuableUserCouponResponseDto.from(issuableCoupon);
    }

    @Override
    public UserCouponIssueResponseDto issueUserCoupon(Long storeId, Long couponId, CustomUserPrincipal principal) {

        User user = validateActivateUser(principal.getUsername());
        Long discountCouponId = discountCouponRepository.findById(couponId)
            .orElseThrow(() -> new ApiException(ErrorStatus.COUPON_NOT_FOUND)).getId();
        String lockKey = "coupon-lock:id: " + discountCouponId;
        log.info("lockKey = {}", lockKey);
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked;

        try {
            isLocked = lock.tryLock(3000, 3000, TimeUnit.MILLISECONDS);

            if (!isLocked) {
                log.warn("[Coupon - 쿠폰 발급] 락 획득 실패, couponId: {}", discountCouponId);
                throw new ApiException(ErrorStatus.COUPON_BAD_REQUEST);
            }

            return userCouponTransactionalService.issueUserCoupon(storeId, couponId, user);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[Coupon - 쿠폰 발급] 락 인터럽트", e);
            throw new ApiException(ErrorStatus.STORE_BAD_REQUEST);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.error("[Coupon - 쿠폰 발급] 락 해제 실패", e);
                }
            }
        }
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

    private DiscountCoupon validateIssuableCoupon(Long storeId, Long couponId) {

        DiscountCoupon issuableCoupon = discountCouponRepository.findIssuableCoupon(storeId, couponId)
            .orElseThrow(() -> new ApiException(ErrorStatus.COUPON_NOT_FOUND));

        return issuableCoupon;
    }

    private void validateActivateStore(Long storeId) {

        storeRepository.findByIdAndDeletedFalse(storeId)
            .orElseThrow(() -> new ApiException(ErrorStatus.STORE_NOT_FOUND));
    }

    private void validateCouponNotIssued(Long userId, Long couponId) {

        boolean alreadyIssued = userCouponRepository.existsByUser_IdAndDiscountCoupon_Id(userId, couponId);
        if (alreadyIssued) {
            throw new ApiException(ErrorStatus.COUPON_ALREADY_ISSUED);
        }
    }
}
