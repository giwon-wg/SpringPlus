package com.example.springplusteamproject.domain.coupon.service;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.coupon.dto.request.DiscountCouponRequestDto;
import com.example.springplusteamproject.domain.coupon.dto.response.DiscountCouponResponseDto;
import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.repository.DiscountCouponRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscountCouponServiceImpl implements DiscountCouponService {

    private final DiscountCouponRepository discountCouponRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public DiscountCouponResponseDto createCoupon(Long storeId, DiscountCouponRequestDto requestDto,
                                                  CustomUserPrincipal principal) {

        User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new ApiException(ErrorStatus.USER_NOT_FOUND));
        user.validateDelete();
        user.validateOwner();

        Store store = storeRepository.findByIdAndDeletedFalse(storeId)
            .orElseThrow(() -> new ApiException(ErrorStatus.STORE_NOT_FOUND));
        if (!Objects.equals(user.getId(), store.getUser().getId())) {
            throw new ApiException(ErrorStatus.ROLE_ADMIN_FORBIDDEN);
        }

        DiscountCoupon discountCoupon = createDiscountCoupon(store, requestDto);
        DiscountCoupon savedDiscountCoupon = discountCouponRepository.save(discountCoupon);

        return DiscountCouponResponseDto.from(savedDiscountCoupon);
    }

    private DiscountCoupon createDiscountCoupon(Store store, DiscountCouponRequestDto requestDto) {
        return DiscountCoupon.builder()
            .store(store)
            .couponName(requestDto.getCouponName())
            .discount(requestDto.getDiscount())
            .issuedAt(requestDto.getIssuedAt())
            .expiresAt(requestDto.getExpiresAt())
            .quantity(requestDto.getQuantity())
            .stock(requestDto.getQuantity())
            .isDeleted(false)
            .build();
    }
}
