package com.example.springplusteamproject.domain.coupon.repository;

import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscountCouponRepository extends JpaRepository<DiscountCoupon, Long> {

    @Query("SELECT dc FROM DiscountCoupon dc WHERE dc.id NOT IN :couponIds AND dc.store.id = :storeId")
    List<DiscountCoupon> findAvailableCouponList(@Param("couponIds") List<Long> couponIds,
                                                 @Param("storeId") Long storeId);
}
