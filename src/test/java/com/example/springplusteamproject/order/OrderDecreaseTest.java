package com.example.springplusteamproject.order;

import com.example.springplusteamproject.domain.flower.entity.Flower;
import com.example.springplusteamproject.domain.flower.repository.FlowerRepository;
import com.example.springplusteamproject.domain.order.dto.request.OrderItemRequestDTO;
import com.example.springplusteamproject.domain.order.dto.request.OrderRequestDTO;
import com.example.springplusteamproject.domain.order.service.OrderService;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.springplusteamproject.domain.flower.enums.Color.PINK;
import static com.example.springplusteamproject.domain.flower.enums.Season.ALL;
import static com.example.springplusteamproject.domain.flower.enums.Type.ROSE;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Slf4j
public class OrderDecreaseTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private FlowerRepository flowerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;

    private Long flowerId;

    @Rollback(value = true)
    @BeforeEach
    void setup() {

        flowerRepository.deleteAll();
        userRepository.deleteAll();

        //가게사장
        User owner = userRepository.save(
            User.builder().email("owner@test.com")
                .password("password1!")
                .nickname("정상유저")
                .phone("010-1111-2222")
                .address("아무런 주소")
                .userRole(UserRole.OWNER)
                .isDeleted(false).build());

        //가게
        Store store = storeRepository.save(
            Store.builder().name("이쁜화원").user(owner).address("서울특별시").phoneNumber("010-1234-1234")
                .minOrderPrice(1000L).deleted(false).openTime(LocalTime.now()).closeTime(LocalTime.now()).build());

        //꽃
        Flower flower = Flower.builder().store(store).name("자나장미").description("영원한 사랑, 끝없는 사랑, 행복한 사랑").type(ROSE)
            .color(PINK).season(ALL).price(1000).stock(500).expirationDate(LocalDate.parse("2025-05-19")).build();

        flower = flowerRepository.save(flower);
        flowerId = flower.getId();

        //유저 저장
        for (int i = 1; i <= 400; i++) {
            userRepository.save(
                User.builder()
                    .email("user" + i + "@test.com")
                    .password("password" + i + "!")
                    .nickname("테스트유저" + i)
                    .phone("010-0000-000" + i)
                    .address("테스트주소" + i)
                    .userRole(UserRole.CUSTOMER)
                    .isDeleted(false)
                    .build()
            );
        }

    }

    @Test
    void 동시성_재고차감_테스트() throws InterruptedException {
        int threadCount = 400;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        List<CustomUserPrincipal> userPrincipals = new ArrayList<>();
        for (int i = 1; i <= 400; i++) {
            User user = userRepository.findByEmail("user" + i + "@test.com").orElseThrow();
            userPrincipals.add(new CustomUserPrincipal(user));
        }

        long start = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    OrderRequestDTO request = OrderRequestDTO.builder()
                        .items(List.of(new OrderItemRequestDTO(flowerId, 1)))
                        .build();
                    orderService.createOrder(request, userPrincipals.get(idx));
                    log.info("[SUCCESS] user{} 주문 성공!", idx + 1);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.info("[FAIL] user{} 주문 실패: {}", idx + 1, e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        long end = System.currentTimeMillis();

        Flower flower = flowerRepository.findById(flowerId).orElseThrow();

        log.info("=== 동시성 재고차감 테스트 결과 ===");
        log.info("총 주문 시도: {}", threadCount);
        log.info("주문 성공: {}", successCount.get());
        log.info("주문 실패: {}", failCount.get());
        log.info("최종 재고: {}", flower.getStock());
        log.info("총 소요 시간: {}ms", (end - start));

        assertEquals(400, successCount.get(), "400 성공");
        assertEquals(0, failCount.get(), "0 실패");
        assertEquals(100, flower.getStock(), "재고는 100");
    }
}
