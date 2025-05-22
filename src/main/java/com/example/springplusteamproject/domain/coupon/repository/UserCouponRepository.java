package com.example.springplusteamproject.domain.coupon.repository;

import com.example.springplusteamproject.domain.coupon.entity.UserCoupon;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    @Query("SELECT uc.discountCoupon.id FROM UserCoupon uc WHERE uc.discountCoupon.store.id = :storeId AND uc.user.id = :userId AND uc.isUsed = false")
    List<Long> findHavingCouponIds(@Param("userId") Long userId, @Param("storeId") Long storeId);

    boolean existsByUser_IdAndDiscountCoupon_Id(Long userId, Long couponId);

    @EntityGraph(attributePaths = "discountCoupon")
    List<UserCoupon> findAllByUser_idAndIsUsedFalse(Long userId);
}
