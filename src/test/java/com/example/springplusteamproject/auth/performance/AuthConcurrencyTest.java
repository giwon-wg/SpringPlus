package com.example.springplusteamproject.auth.performance;

import com.example.springplusteamproject.domain.auth.dto.request.SignupRequestDto;
import com.example.springplusteamproject.domain.auth.service.AuthService;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


@SpringBootTest
@ActiveProfiles("test")
public class AuthConcurrencyTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    private static final String USER_EMAIL = "중복이메일-" + System.currentTimeMillis();
    private static final String USER_NICKNAME = "중복닉네임-" + System.currentTimeMillis();

    @BeforeAll
    static void loadDotEnv() {
        System.out.println("loadDotEnv 시작");
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
    void cleanUp() {
        userRepository.deleteAll();
    }

    private void runConCurrentSignupTest(SignupRequestDto baseDto, int threadCount, AtomicInteger errorCount) throws InterruptedException {
        ExecutorService executerService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();

        for (int i=1; i <= threadCount; i++) {
            final int threadId = i;
            System.out.println("Thread-" + threadId + " 시작");
            executerService.submit(() -> {
                try {
                    authService.signup(baseDto);
                    successCount.incrementAndGet();
                    System.out.println("Thread-" + threadId + " 회원가입 성공");
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.out.println("Thread-" + threadId + " 예외: " +
                        (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        System.out.println("성공 수: " + successCount.get() + ", 실패 수: " + errorCount.get());
        executerService.shutdown();
    }

    @Test
    @DisplayName("이메일 중복 회원가입 동시성 테스트")
    void 유저_이메일_동시성_테스트() throws InterruptedException {
        cleanUp();

        int threadCount = 10;
        AtomicInteger errorCount = new AtomicInteger(0);

        SignupRequestDto requestDto = new SignupRequestDto(
            "dup-email@email.com", // 고정된 중복 이메일
            "password",
            USER_NICKNAME, // 유니크 닉네임
            "서울특별시", "010-1111-2222", "CUSTOMER", null, null
        );

        runConCurrentSignupTest(requestDto, threadCount, errorCount);

        List<User> users = userRepository.findAll();
        System.out.println("등록된 유저 수: " + users.size());
        users.forEach(u -> System.out.println("유저: " + u.getEmail() + " / " + u.getNickname()));

        assertThat(users)
            .hasSize(1)
            .extracting(User::getEmail)
            .containsExactly("dup-email@email.com");

        System.out.println("예외 발생 수: " + errorCount.get());
    }

    @Test
    @DisplayName("닉네임 중복 회원가입 동시성 테스트")
    void 유저_닉네임_동시성_테스트() throws InterruptedException {
        cleanUp();

        int threadCount = 10;
        AtomicInteger errorCount = new AtomicInteger(0);

        SignupRequestDto requestDto = new SignupRequestDto(
            USER_EMAIL, // 유니크한 이메일
            "password",
            "중복 닉네임", // 중복 닉네임
            "서울특별시", "010-1111-2222", "CUSTOMER", null, null
        );

        runConCurrentSignupTest(requestDto, threadCount, errorCount);

        List<User> users = userRepository.findAll();
        System.out.println("등록된 유저 수: " + users.size());
        users.forEach(u -> System.out.println("유저: " + u.getEmail() + " / " + u.getNickname()));

        assertThat(users)
            .hasSize(1)
            .extracting(User::getNickname)
            .containsExactly("중복 닉네임");

        System.out.println("예외 발생 수: " + errorCount.get());
    }

    @Test
    @DisplayName("닉네임, 이메일 중복 회원가입 동시성 테스트")
    void 유저_이메일_닉네임_동시_중복_테스트() throws InterruptedException {
        cleanUp();

        int threadCount = 10;
        AtomicInteger errorCount = new AtomicInteger(0);

        SignupRequestDto requestDto = new SignupRequestDto(
            "dup-email@email.com",
            "password",
            "중복 닉네임",
            "서울특별시", "010-1111-2222", "CUSTOMER", null, null
        );

        runConCurrentSignupTest(requestDto, threadCount, errorCount);

        List<User> users = userRepository.findAll();
        System.out.println("등록된 유저 수: " + users.size());
        users.forEach(u -> System.out.println("유저: " + u.getEmail() + " / " + u.getNickname()));

        assertThat(users)
            .hasSize(1)
            .extracting(User::getEmail, User::getNickname)
            .containsExactly(tuple("dup-email@email.com", "중복 닉네임"));

        System.out.println("예외 발생 수: " + errorCount.get());
    }
}
