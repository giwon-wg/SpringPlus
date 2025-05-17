package com.example.springplusteamproject.domain.user.controller;

import com.example.springplusteamproject.common.exception.ErrorCode;
import com.example.springplusteamproject.common.exception.GlobalException;
import com.example.springplusteamproject.domain.user.dto.request.UpdatePasswordRequestDto;
import com.example.springplusteamproject.domain.user.dto.response.UserResponseDto;
import com.example.springplusteamproject.domain.user.entity.User;
import com.example.springplusteamproject.domain.user.service.UserService;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        userService.updateMyPassword(requestDto, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> findUser(@AuthenticationPrincipal CustomUserPrincipal principal) {

        UserResponseDto responseDto = userService.findUserByEmail(principal.getUsername());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
