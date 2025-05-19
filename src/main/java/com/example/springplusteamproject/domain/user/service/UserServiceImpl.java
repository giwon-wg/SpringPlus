package com.example.springplusteamproject.domain.user.service;

import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.domain.user.dto.request.UpdatePasswordRequestDto;
import com.example.springplusteamproject.domain.user.dto.response.UserResponseDto;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j(topic = "UserService")
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
            log.warn("기존 비밀번호와 동일한 요청: 기존 비밀번호 입력 = {}, 신규 비밀번호 입력 = {}", requestDto.getOldPassword(), requestDto.getNewPassword());
            throw new ApiException(ErrorStatus.PASSWORD_DUPLICATED);
        }

        User user = userRepository.findByEmail(principal.getUsername())
            .orElseThrow(()-> new ApiException(ErrorStatus.USER_NOT_FOUND));

        user.validateDelete();

        if (!passwordEncoder.matches(requestDto.getOldPassword(), principal.getPassword())) {
            log.warn("비밀번호 불일치: 비밀번호 확인 입력 = {}, 기존 비밀번호 = {}", requestDto.getOldPassword(), principal.getPassword());
            throw new ApiException(ErrorStatus.PASSWORD_NOT_MATCHED);
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());
        user.updatePassword(encodedPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(ErrorStatus.USER_NOT_FOUND));

        user.validateDelete();

        return UserResponseDto.from(user);
    }

    @Override
    @Transactional
    public void deleteMyAccount(CustomUserPrincipal principal) {
        User user = userRepository.findByEmail(principal.getUsername())
            .orElseThrow(()-> new ApiException(ErrorStatus.USER_NOT_FOUND));

        user.validateDelete(); // 유저 삭제 여부 검증
        user.delete(); // 유저 삭제(Soft Delete)
    }
}
