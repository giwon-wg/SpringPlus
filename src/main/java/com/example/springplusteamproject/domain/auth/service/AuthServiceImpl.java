package com.example.springplusteamproject.domain.auth.service;

import com.example.springplusteamproject.common.exception.ErrorCode;
import com.example.springplusteamproject.common.exception.GlobalException;
import com.example.springplusteamproject.domain.auth.dto.request.LoginRequestDto;
import com.example.springplusteamproject.domain.auth.dto.request.SignupRequestDto;
import com.example.springplusteamproject.domain.auth.dto.response.LoginResponseDto;
import com.example.springplusteamproject.domain.auth.dto.response.SignupResponseDto;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new GlobalException(ErrorCode.USER_EXIST_EMAIL);
        }

        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new GlobalException(ErrorCode.USER_EXIST_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        UserRole userRole = UserRole.of(requestDto.getUserRole());
        User newUser = buildUser(requestDto, encodedPassword, userRole); //buildUser는 아래에 메서드로 구현함

        User savedUser = userRepository.save(newUser);
        return SignupResponseDto.from(savedUser);
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
            .orElseThrow( ()-> new GlobalException(ErrorCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_NOT_MATCHED);
        }

        String bearerToken = jwtUtil.generateToken(user);
        return new LoginResponseDto(bearerToken);
    }

    private User buildUser(SignupRequestDto requestDto, String encodedPassword, UserRole userRole) {
        return User.builder()
            .email(requestDto.getEmail())
            .nickname(requestDto.getNickname())
            .password(encodedPassword)
            .phone(requestDto.getPhone())
            .address(requestDto.getAddress())
            .userRole(userRole)
            .build();
    }

}
