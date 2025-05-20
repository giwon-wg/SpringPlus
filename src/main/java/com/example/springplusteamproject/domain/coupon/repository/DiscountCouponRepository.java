package com.example.springplusteamproject.domain.coupon.repository;

import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscountCouponRepository extends JpaRepository<DiscountCoupon, Long> {

    @Query("SELECT dc FROM DiscountCoupon dc WHERE dc.id NOT IN :couponIds AND dc.store.id = :storeId")
    List<DiscountCoupon> findAvailableCouponList(@Param("couponIds") List<Long> couponIds,
                                                 @Param("storeId") Long storeId);

    @Query("SELECT dc FROM DiscountCoupon dc WHERE dc.id = :couponId AND dc.store.id = :storeId")
    Optional<DiscountCoupon> findAvailableCoupon(@Param("storeId") Long storeId, @Param("couponId") Long couponId);
}
