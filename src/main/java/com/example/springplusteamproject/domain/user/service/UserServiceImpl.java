package com.example.springplusteamproject.domain.user.service;

import com.example.springplusteamproject.common.exception.ErrorCode;
import com.example.springplusteamproject.common.exception.GlobalException;
import com.example.springplusteamproject.domain.user.dto.request.UpdatePasswordRequestDto;
import com.example.springplusteamproject.domain.user.dto.response.UserResponseDto;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void updateMyPassword(UpdatePasswordRequestDto requestDto, CustomUserPrincipal principal) {
        // 기존 비밀번호와 같은 비밀번호로 요청이 들어오는 경우 예외 발생
        if (Objects.equals(requestDto.getOldPassword(), requestDto.getNewPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_DUPLICATED);
        }

        User user = userRepository.findByEmail(principal.getUsername())
            .orElseThrow(()-> new GlobalException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getOldPassword(), principal.getPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_NOT_MATCHED);
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());
        user.updatePassword(encodedPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        return UserResponseDto.from(user);
    }
}
