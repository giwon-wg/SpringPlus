package com.example.springplusteamproject.domain.coupon;

import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.repository.DiscountCouponRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest
public class DiscountCouponBulkInsertTest {

    @Autowired
    private DiscountCouponRepository discountCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    @DisplayName("1만개 쿠폰 데이터 삽입")
    @Transactional
    @Rollback(false)
    void insertMillionCoupons() {
        Store testStore = createTestStore();

        int totalCoupons = 10000;
        int batchSize = 100;
        List<DiscountCoupon> buffer = new ArrayList<>(batchSize);
        String targetCouponName = "target_coupon_special";

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= totalCoupons; i++) {
            String couponName;

            if (i == 7777) {
                couponName = targetCouponName;
            } else {
                couponName = "Coupon_" + UUID.randomUUID().toString().substring(0, 8);
            }

            DiscountCoupon coupon = createCoupon(testStore, couponName, i);
            buffer.add(coupon);

            if (i % batchSize == 0) {
                discountCouponRepository.saveAll(buffer);
                discountCouponRepository.flush();
                buffer.clear();
                System.out.println(i + " coupons insert success");
            }
        }

        if (!buffer.isEmpty()) {
            discountCouponRepository.saveAll(buffer);
            discountCouponRepository.flush();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Million coupons insert success");
        System.out.println("Total time taken: " + (endTime - startTime) / 1000 + " seconds");
    }

    private Store createTestStore() {
        User user = User.builder()
            .nickname("testuser")
            .email("test@example.com")
            .password("password123")
            .address("주소")
            .phone("010-1234-5678")
            .userRole(UserRole.OWNER)
            .build();
        userRepository.save(user);
        Store store = Store.builder()
            .name("Test Store for Bulk Insert")
            .address("서울특별시 강남구 테스트로 123")
            .phoneNumber("02-1234-5678")
            .closeTime(LocalTime.parse("23:00"))
            .openTime(LocalTime.parse("08:00"))
            .deleted(false)
            .minOrderPrice(10000L)
            .user(user)
            .build();
        return storeRepository.save(store);
    }

    private DiscountCoupon createCoupon(Store store, String couponName, int index) {
        Random random = new Random();
        LocalDate now = LocalDate.now();

        return DiscountCoupon.builder()
            .store(store)
            .couponName(couponName)
            .discount(1000L + random.nextInt(9000))
            .issuedAt(now)
            .expiresAt(now.plusMonths(3))
            .quantity(100L)
            .stock(100L)
            .isDeleted(false)
            .build();
    }
}
