package com.example.springplusteamproject.auth.service;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.auth.dto.request.LoginRequestDto;
import com.example.springplusteamproject.domain.auth.dto.request.SignupRequestDto;
import com.example.springplusteamproject.domain.auth.dto.response.LoginResponseDto;
import com.example.springplusteamproject.domain.auth.service.AuthServiceImpl;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplUnitTest {
    public static final SignupRequestDto SIGNUP_REQUEST_DTO = new SignupRequestDto(
    "email@email.com",
    "password123",
    "닉네임",
    "서울특별시",
    "010-1111-2222",
    "OWNER",
    null,
    null);

    public static final LoginRequestDto LOGIN_REQUEST_DTO = new LoginRequestDto(
    "email@email.com",
    "password123"
);

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    AuthServiceImpl authService;

    @Test
    void 회원가입시_이메일_중복_발생시_예외발생_에러코드_USER_EXIST() {
        // given
        given(userRepository.existsByEmail(SIGNUP_REQUEST_DTO.getEmail())).willReturn(true);

        // when
        ApiException apiException = assertThrows(ApiException.class, () -> authService.signup(SIGNUP_REQUEST_DTO));

        // then
        assertEquals(ErrorStatus.USER_EXIST, apiException.getErrorCode());
        verify(userRepository).existsByEmail(SIGNUP_REQUEST_DTO.getEmail());
    }

    @Test
    void 회원가입시_닉네임_중복_발생시_예외발생_에러코드_USER_EXIST() {
        // given
        given(userRepository.existsByNickname(SIGNUP_REQUEST_DTO.getNickname())).willReturn(true);

        // when
        ApiException apiException = assertThrows(ApiException.class, () -> authService.signup(SIGNUP_REQUEST_DTO));

        // then
        assertEquals(ErrorStatus.USER_EXIST, apiException.getErrorCode());
        verify(userRepository).existsByNickname(SIGNUP_REQUEST_DTO.getNickname());
    }

    @Test
    void 회원가입시_OWNER가_사업자등록번호_누락시_예외발생_에러코드_USER_OWNER_BRN_REQUIRED() {
        // given & when
        ApiException apiException = assertThrows(ApiException.class, () -> authService.signup(SIGNUP_REQUEST_DTO));

        // then
        assertEquals(ErrorStatus.USER_OWNER_BRN_REQUIRED, apiException.getErrorCode());
    }

    @Test
    void 로그인_성공시_토큰이_발급된다() {
        // given
        User mockUser = User.builder()
            .email(SIGNUP_REQUEST_DTO.getEmail())
            .nickname(SIGNUP_REQUEST_DTO.getNickname())
            .password(SIGNUP_REQUEST_DTO.getPassword())
            .phone(SIGNUP_REQUEST_DTO.getPhone())
            .address(SIGNUP_REQUEST_DTO.getAddress())
            .userRole(UserRole.of(SIGNUP_REQUEST_DTO.getUserRole()))
            .brn("123-12-12313")
            .isDeleted(false)
            .build();

        given(userRepository.findByEmail(mockUser.getEmail())).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(LOGIN_REQUEST_DTO.getPassword(), mockUser.getPassword())).willReturn(true);
        given(jwtUtil.generateToken(mockUser)).willReturn("mockToken");

        // when
        LoginResponseDto loginResponseDto = authService.login(LOGIN_REQUEST_DTO);

        // then
        assertEquals("mockToken", loginResponseDto.getBearerToken());
        verify(userRepository).findByEmail(mockUser.getEmail());
        verify(passwordEncoder).matches(LOGIN_REQUEST_DTO.getPassword(), mockUser.getPassword());
        verify(jwtUtil).generateToken(mockUser);
    }

    @Test
    void 로그인_유저가_존재하지_않으면_에러코드_USER_NOT_FOUND() {
        // given
        given(userRepository.findByEmail(LOGIN_REQUEST_DTO.getEmail())).willReturn(Optional.empty());

        // when
        ApiException apiException = assertThrows(ApiException.class, () -> authService.login(LOGIN_REQUEST_DTO));

        // then
        assertEquals(ErrorStatus.USER_NOT_FOUND, apiException.getErrorCode());
        verify(userRepository).findByEmail(LOGIN_REQUEST_DTO.getEmail());
    }

    @Test
    void 로그인_삭제된_유저가_시도하면_에러코드_DELETED_USER() {
        // given
        User deletedUser = User.builder()
            .email("email@email.com")
            .password("password123")
            .nickname("닉네임")
            .isDeleted(true)
            .build();

        given(userRepository.findByEmail(LOGIN_REQUEST_DTO.getEmail())).willReturn(Optional.of(deletedUser));

        // when
        ApiException apiException = assertThrows(ApiException.class, () -> authService.login(LOGIN_REQUEST_DTO));

        // then
        assertEquals(ErrorStatus.DELETED_USER, apiException.getErrorCode());
        verify(userRepository).findByEmail(LOGIN_REQUEST_DTO.getEmail());

    }

    @Test
    void 로그인_비밀번호가_다르면_에러코드_PASSWORD_NOT_MATCHED() {
        // given
        User user = User.builder()
            .email(SIGNUP_REQUEST_DTO.getEmail())
            .password("rightpassword123")
            .isDeleted(false)
            .build();

        given(userRepository.findByEmail(LOGIN_REQUEST_DTO.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(LOGIN_REQUEST_DTO.getPassword(), user.getPassword())).willReturn(false);

        // when
        ApiException apiException = assertThrows(ApiException.class, () -> authService.login(LOGIN_REQUEST_DTO));

        // then
        assertEquals(ErrorStatus.PASSWORD_NOT_MATCHED, apiException.getErrorCode());
        verify(userRepository).findByEmail(LOGIN_REQUEST_DTO.getEmail());
        verify(passwordEncoder).matches(LOGIN_REQUEST_DTO.getPassword(), user.getPassword());
    }
}
