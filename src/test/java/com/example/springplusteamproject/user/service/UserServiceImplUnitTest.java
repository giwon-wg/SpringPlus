package com.example.springplusteamproject.user.service;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import com.example.springplusteamproject.domain.user.dto.request.UpdatePasswordRequestDto;
import com.example.springplusteamproject.domain.user.dto.response.UserResponseDto;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.entity.UserRole;
import com.example.springplusteamproject.domain.user.repository.UserRepository;
import com.example.springplusteamproject.domain.user.service.UserServiceImpl;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
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
public class UserServiceImplUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    private User user;
    private CustomUserPrincipal principal;

    @BeforeEach
    void testInit() {
        user = User.builder()
            .id(1L)
            .email("email@email.com")
            .nickname("정상유저")
            .phone("010-1111-2222")
            .address("아무런 주소")
            .password("encodedPassword")
            .userRole(UserRole.CUSTOMER)
            .isDeleted(false)
            .build();

        principal = new CustomUserPrincipal(user);
    }

    @Test
    void 유저_비밀번호_업데이트_성공() {
        // given
        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));
        given(passwordEncoder.matches("encodedPassword", "encodedPassword")).willReturn(true);
        given(passwordEncoder.encode("newPassword")).willReturn("hashedPassword");

        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("encodedPassword", "newPassword");

        // when
        userService.updateMyPassword(requestDto, principal);

        // then
        assertEquals("hashedPassword", user.getPassword());
        verify(userRepository).findByEmail(principal.getUsername());
        verify(passwordEncoder).matches("encodedPassword", "encodedPassword");
    }

    @Test
    void 유저_비밀번호_업데이트시_기존비밀번호와_동일하면_에러코드_PASSWORD_DUPLICATED() {
        // given
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("encodedPassword", "encodedPassword");

        // when
        ApiException apiException = assertThrows(ApiException.class, () -> userService.updateMyPassword(requestDto, principal));

        // then
        assertEquals(ErrorStatus.PASSWORD_DUPLICATED, apiException.getErrorCode());
    }

    @Test
    void 유저_비밀번호_업데이트시_비밀번호_확인이_틀리면_에러코드_PASSWORD_NOT_MATCHED() {
        // given
        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));
        UpdatePasswordRequestDto requestDto = new UpdatePasswordRequestDto("wrongPassword", "newPassword");

        // when
        ApiException apiException = assertThrows(ApiException.class, () -> userService.updateMyPassword(requestDto, principal));

        // then
        verify(userRepository).findByEmail(principal.getUsername());
        assertEquals(ErrorStatus.PASSWORD_NOT_MATCHED, apiException.getErrorCode());
    }

    @Test
    void 유저_내정보_조회_성공() {
        // given
        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));

        // when
        UserResponseDto findUserDto = userService.findUserByEmail(principal.getUsername());

        // then
        assertEquals(user.getId(), findUserDto.getId());
        assertEquals(user.getEmail(), findUserDto.getEmail());
        assertEquals(user.getNickname(), findUserDto.getNickname());
        verify(userRepository).findByEmail(principal.getUsername());
    }

    @Test
    void 유저_내정보_조회시_정보가_없으면_에러코드_USER_NOT_FOUND() {
        // given
        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.empty());

        // when
        ApiException apiException = assertThrows(ApiException.class, () -> userService.findUserByEmail(principal.getUsername()));

        // then
        verify(userRepository).findByEmail(principal.getUsername());
        assertEquals(ErrorStatus.USER_NOT_FOUND, apiException.getErrorCode());
    }

    @Test
    void 유저_내계정_삭제_성공() {
        // given
        given(userRepository.findByEmail(principal.getUsername())).willReturn(Optional.of(user));

        // when
        userService.deleteMyAccount(principal);

        // then
        assertEquals(user.getIsDeleted(), true);
        verify(userRepository).findByEmail(principal.getUsername());
    }
}
