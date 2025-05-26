package com.example.springplusteamproject.domain.coupon.repository;

import com.example.springplusteamproject.domain.coupon.entity.UserCoupon;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    boolean existsByUser_IdAndDiscountCoupon_Id(Long userId, Long couponId);

    @EntityGraph(attributePaths = "discountCoupon")
    List<UserCoupon> findAllByUser_idAndIsUsedFalse(Long userId);
}
