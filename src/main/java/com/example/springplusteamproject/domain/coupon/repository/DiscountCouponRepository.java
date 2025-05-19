package com.example.springplusteamproject.domain.coupon.repository;

import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountCouponRepository extends JpaRepository<DiscountCoupon, Long> {
}
