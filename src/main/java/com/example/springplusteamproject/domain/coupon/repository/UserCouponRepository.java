package com.example.springplusteamproject.domain.coupon.repository;

import com.example.springplusteamproject.domain.coupon.entity.UserCoupon;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    @Query("SELECT uc.id FROM UserCoupon uc WHERE uc.discountCoupon.store.id = :storeId AND uc.user.id = :userId")
    List<Long> findHavingCouponIds(@Param("storeId") Long storeId, @Param("userId") Long userId);

    boolean existsByUser_IdAndDiscountCoupon_Id(Long userId, Long couponId);
}
