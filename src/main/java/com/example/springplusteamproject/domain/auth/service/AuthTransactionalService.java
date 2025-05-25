package com.example.springplusteamproject.domain.auth.service;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.auth.dto.request.SignupRequestDto;
import com.example.springplusteamproject.domain.auth.dto.response.SignupResponseDto;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j(topic = "AuthTransactionalService")
@Service
@RequiredArgsConstructor
public class AuthTransactionalService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponseDto signup(SignupRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            log.warn("중복 이메일 요청 감지: {}", requestDto.getEmail());
            throw new ApiException(ErrorStatus.USER_EXIST);
        }

        if (userRepository.existsByNickname(requestDto.getNickname())) {
            log.warn("중복 닉네임 요청 감지: {}", requestDto.getNickname());
            throw new ApiException(ErrorStatus.USER_EXIST);
        }

        if ("OWNER".equalsIgnoreCase(requestDto.getUserRole())) {
            if (requestDto.getBrn() == null || requestDto.getBrn().trim().isEmpty()) {
                log.warn("사업자 등록번호 누락: UserRole = {}", requestDto.getUserRole());
                throw new ApiException(ErrorStatus.USER_OWNER_BRN_REQUIRED);
            }
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        UserRole userRole = UserRole.of(requestDto.getUserRole());
        User newUser = buildUser(requestDto, encodedPassword, userRole); //buildUser는 아래에 메서드로 구현함

        User savedUser = userRepository.save(newUser);
        userRepository.flush();
        log.info("{} 회원가입 성공: {}", Thread.currentThread().getName(), savedUser.getEmail());
        return SignupResponseDto.from(savedUser);
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
