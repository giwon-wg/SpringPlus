package com.example.springplusteamproject.domain.coupon.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import com.example.springplusteamproject.domain.coupon.entity.DiscountCoupon;
import com.example.springplusteamproject.domain.coupon.repository.DiscountCouponRepository;
import com.example.springplusteamproject.domain.coupon.repository.UserCouponRepository;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class CouponDistributedLockTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private DiscountCouponRepository discountCouponRepository;

    @Autowired
    private UserCouponServiceImpl userCouponService;

    private DiscountCoupon discountCoupon;
    private CustomUserPrincipal principal;
    private Store store;
    private User user;

    @BeforeEach
    void setUp() {
        userCouponRepository.deleteAll();
        discountCouponRepository.deleteAll();
        storeRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
            .nickname("가게 주인")
            .email("1@gmail.com")
            .address("주소")
            .password("1234")
            .phone("01012345678")
            .userRole(UserRole.OWNER)
            .build());

        store = storeRepository.save(Store.builder()
            .name("가게 이름")
            .user(user)
            .closeTime(LocalTime.parse("21:00"))
            .openTime(LocalTime.parse("08:00"))
            .phoneNumber("0212345678")
            .minOrderPrice(10000L)
            .address("주소")
            .deleted(false)
            .build());

        discountCoupon = discountCouponRepository.save(DiscountCoupon.builder()
            .couponName("5000원 할인")
            .discount(5000L)
            .store(store)
            .quantity(1000L)
            .stock(1000L)
            .issuedAt(LocalDate.now())
            .expiresAt(LocalDate.now())
            .build());
    }

    @Test
    void 분산락을_이용한_할인_쿠폰_재고_동시성_처리_성공() throws InterruptedException {
        int threadCount = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executor.execute(() -> {
                try {
                    User user = userRepository.save(User.builder()
                        .nickname("유저" + finalI)
                        .email("user" + finalI + "@test.com")
                        .password("1234")
                        .phone("01012345678")
                        .address("주소")
                        .userRole(UserRole.CUSTOMER)
                        .build());

                    CustomUserPrincipal principal = new CustomUserPrincipal(user);

                    userCouponService.issueUserCoupon(store.getId(), discountCoupon.getId(), principal);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        DiscountCoupon saved = discountCouponRepository.findById(discountCoupon.getId()).orElseThrow();
        long issuedCount = userCouponRepository.count();

        System.out.println("남은 재고: " + saved.getStock());
        System.out.println("발급 수: " + issuedCount);

        assertThat(saved.getStock()).isEqualTo(0);
        assertThat(issuedCount).isEqualTo(1000);
    }

    @Test
    void 재고_부족시_동시성_처리_성공() throws InterruptedException {
        int threadCount = 1500;
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executor.execute(() -> {
                try {
                    User user = userRepository.save(User.builder()
                        .nickname("유저" + finalI)
                        .email("user" + finalI + "@test.com")
                        .password("1234")
                        .phone("01012345678")
                        .address("주소")
                        .userRole(UserRole.CUSTOMER)
                        .build());

                    CustomUserPrincipal principal = new CustomUserPrincipal(user);

                    try {
                        userCouponService.issueUserCoupon(store.getId(), discountCoupon.getId(), principal);
                        successCount.getAndIncrement();
                    } catch (Exception e) {
                        failCount.getAndIncrement();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        DiscountCoupon saved = discountCouponRepository.findById(discountCoupon.getId()).orElseThrow();
        long issuedCount = userCouponRepository.count();

        System.out.println("남은 재고: " + saved.getStock());
        System.out.println("발급 수: " + issuedCount);
        System.out.println("성공 수: " + successCount.get());
        System.out.println("실패 수: " + failCount.get());

        assertThat(saved.getStock()).isEqualTo(0);
        assertThat(issuedCount).isEqualTo(1000);
        assertThat(successCount.get()).isEqualTo(1000);
        assertThat(failCount.get()).isEqualTo(500);
    }
}
