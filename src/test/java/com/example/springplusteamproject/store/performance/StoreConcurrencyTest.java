package com.example.springplusteamproject.store.performance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.example.springplusteamproject.domain.store.dto.request.StoreRequestDto;
import com.example.springplusteamproject.domain.store.entity.Store;
import com.example.springplusteamproject.domain.store.repository.StoreRepository;
import com.example.springplusteamproject.domain.store.service.StoreService;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;

import io.github.cdimascio.dotenv.Dotenv;

@Sql(statements = "ALTER TABLE user AUTO_INCREMENT = 1", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest
@ActiveProfiles("test")
class StoreConcurrencyTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String STORE_NAME = "동시성가게이름-" + System.currentTimeMillis();

    @BeforeAll
    static void loadDotenv() {
        System.out.println("loadDotenv 시작");
        Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .ignoreIfMissing()
            .ignoreIfMalformed()
            .load();

        // 시스템 속성에 등록
        System.setProperty("MYSQL_URL", dotenv.get("MYSQL_URL"));
        System.setProperty("MYSQL_USERNAME", dotenv.get("MYSQL_USERNAME"));
        System.setProperty("MYSQL_PASSWORD", dotenv.get("MYSQL_PASSWORD"));

        System.setProperty("REDIS_HOST", dotenv.get("REDIS_HOST"));
        System.setProperty("REDIS_PORT", dotenv.get("REDIS_PORT"));

        System.setProperty("SECRET_KEY", dotenv.get("SECRET_KEY"));
    }

    @AfterEach
    void tearDown() {
        storeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        System.out.println("setup 시작");

        storeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        for (int i = 1; i <= 10; i++) {
            System.out.println("user 빌딩, userId: " + i);
            User user = User.builder()
                .email("test" + i + "@example.com")
                .password("dummy")
                .userRole(UserRole.CUSTOMER)
                .address("서울시 테스트구")
                .phone("010-1234-5678")
                .nickname("테스트유저" + i)
                .image("profile.jpg")
                .isDeleted(false)
                .build();
            userRepository.save(user);
        }
    }

    @Test
    void 가게_이름_중복_동시성() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 1; i <= threadCount; i++) {
            long userId = i;
            System.out.println("Thread-" + userId + " 시작");
            executorService.submit(() -> {
                try {
                    StoreRequestDto dto = new StoreRequestDto(
                        STORE_NAME,
                        "서을 특별시",
                        "이미지",
                        "010-1234-5678",
                        10000L,
                        "09:00",
                        "18:00"
                    );

                    CustomUserPrincipal principal = new CustomUserPrincipal(
                        User.builder()
                            .id(userId)
                            .email("test" + userId + "@example.com")
                            .password("dummy")
                            .userRole(UserRole.CUSTOMER)
                            .isDeleted(false)
                            .build()
                    );

                    storeService.createStore(dto, principal);
                    System.out.println("생성 성공 - Thread " + userId);
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
            System.out.println("Thread-" + userId + " 종료");
        }
        latch.await();
        executorService.shutdown();
        System.out.println("총 예외 발생: " + errorCount.get());

        List<Store> stores = storeRepository.findAll();
        assertThat(stores)
            .hasSize(1)
            .extracting(Store::getName)
            .containsExactly(STORE_NAME);
    }
}
