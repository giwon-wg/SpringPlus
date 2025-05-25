package com.example.springplusteamproject.domain.coupon.repository;

import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscountCouponRepository extends JpaRepository<DiscountCoupon, Long> {

    @Query("""
SELECT dc FROM DiscountCoupon dc
WHERE dc.store.id = :storeId
AND dc.isDeleted = false
AND NOT EXISTS (
    SELECT 1 FROM UserCoupon uc
    WHERE uc.discountCoupon.id = dc.id
    AND uc.user.id = :userId
    AND uc.isUsed = false
)
""")
    List<DiscountCoupon> findIssuableCouponList(@Param("userId") Long userId,
                                                 @Param("storeId") Long storeId);

    @Query("SELECT dc FROM DiscountCoupon dc WHERE dc.id = :couponId AND dc.store.id = :storeId AND dc.isDeleted = false")
    Optional<DiscountCoupon> findIssuableCoupon(@Param("storeId") Long storeId, @Param("couponId") Long couponId);
}
