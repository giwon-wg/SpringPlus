package com.example.springplusteamproject.domain.auth.service;

import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.domain.auth.dto.request.LoginRequestDto;
import com.example.springplusteamproject.domain.auth.dto.request.SignupRequestDto;
import com.example.springplusteamproject.domain.auth.dto.response.LoginResponseDto;
import com.example.springplusteamproject.domain.auth.dto.response.SignupResponseDto;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j(topic = "AuthService")
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedissonClient redissonClient;
    private final AuthTransactionalService authTransactionalService;


    @Override
    public SignupResponseDto signup(SignupRequestDto requestDto) {

        String emailLockKey = "email-lock:id : " + requestDto.getEmail();
        String nicknameLockKey = "nickname-lock: id: " + requestDto.getNickname();

        RLock emailLock = redissonClient.getLock(emailLockKey);
        RLock nicknameLock = redissonClient.getLock(nicknameLockKey);

        boolean emailLocked = false;
        boolean nicknameLocked = false;

        try {
            emailLocked = emailLock.tryLock(300, 1000, TimeUnit.MILLISECONDS);
            if (!emailLocked) throw new IllegalStateException("이메일 락 획득 실패");

            nicknameLocked = nicknameLock.tryLock(300, 1000, TimeUnit.MILLISECONDS);
            if (!nicknameLocked) throw new IllegalStateException("닉네임 락 획득 실패");

            // 이메일, 닉네임 둘 다 락 잡은 상태에서 비즈니스 로직(회원가입) 실행
            return authTransactionalService.signup(requestDto);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("락 획득 실패: {}, {}", emailLockKey, nicknameLockKey);
            throw new ApiException(ErrorStatus.FORBIDDEN);
        } finally {
            // 개별 락 해제(락 보유 시에만)
            if (nicknameLocked && nicknameLock.isHeldByCurrentThread()) {
                nicknameLock.unlock();
            }
            if (emailLocked && emailLock.isHeldByCurrentThread()) {
                emailLock.unlock();
            }
        }
    };


    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
            .orElseThrow( ()-> {
                log.warn("입력한 이메일에 해당하는 유저 없음: User Email: {}", requestDto.getEmail());
                return new ApiException(ErrorStatus.USER_NOT_FOUND);
            });

        user.validateDelete();

        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            log.warn("입력 비밀번호 미일치");
            throw new ApiException(ErrorStatus.PASSWORD_NOT_MATCHED);
        }

        String bearerToken = jwtUtil.generateToken(user);
        return new LoginResponseDto(bearerToken);
    }

    private User buildUser(SignupRequestDto requestDto, String encodedPassword, UserRole userRole) {
        User.UserBuilder builder = User.builder()
            .email(requestDto.getEmail())
            .nickname(requestDto.getNickname())
            .password(encodedPassword)
            .phone(requestDto.getPhone())
            .address(requestDto.getAddress())
            .userRole(userRole);

        // OWNER인 경우 brn 설정
        if (userRole == UserRole.OWNER) {
            builder.brn(requestDto.getBrn());
        }

        return builder.build();
    }

}
