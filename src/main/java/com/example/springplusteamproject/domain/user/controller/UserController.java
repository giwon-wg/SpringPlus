package com.example.springplusteamproject.domain.user.controller;

import com.example.springplusteamproject.common.exception.ErrorCode;
import com.example.springplusteamproject.common.exception.GlobalException;
import com.example.springplusteamproject.domain.user.dto.request.UpdatePasswordRequestDto;
import com.example.springplusteamproject.domain.user.dto.response.UserResponseDto;
import com.example.springplusteamproject.domain.user.service.UserService;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PatchMapping("/me")
    public ResponseEntity<Void> updateMyPassword(
        @Valid @RequestBody UpdatePasswordRequestDto requestDto,
        @AuthenticationPrincipal CustomUserPrincipal principal)
    {
        log.info("oldPassword: {}, newPassword: {}", requestDto.getOldPassword(), requestDto.getNewPassword());
        userService.updateMyPassword(requestDto, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> findUser(@AuthenticationPrincipal CustomUserPrincipal principal) {

        UserResponseDto responseDto = userService.findUserByEmail(principal.getUsername());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserPrincipal principal) {

        userService.deleteMyAccount(principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
