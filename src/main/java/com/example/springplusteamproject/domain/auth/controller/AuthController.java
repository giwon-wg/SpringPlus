package com.example.springplusteamproject.domain.auth.controller;

import com.example.springplusteamproject.common.response.ApiResponse;
import com.example.springplusteamproject.common.status.SuccessStatus;
import com.example.springplusteamproject.domain.auth.dto.request.LoginRequestDto;
import com.example.springplusteamproject.domain.auth.dto.request.SignupRequestDto;
import com.example.springplusteamproject.domain.auth.dto.response.LoginResponseDto;
import com.example.springplusteamproject.domain.auth.dto.response.SignupResponseDto;
import com.example.springplusteamproject.domain.auth.service.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authService;

    @Operation(
        summary = "회원가입",
        description = "회원가입을 진행합니다."
    )

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponseDto>> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        SignupResponseDto responseDto = authService.signup(requestDto);
        return ApiResponse.onSuccess(SuccessStatus.AUTH_SIGNUP_SUCCESS, responseDto);
    }

    @Operation(
        summary = "로그인",
        description = "로그인을 진행합니다."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = authService.login(requestDto);
        return ApiResponse.onSuccess(SuccessStatus.AUTH_LOGIN_SUCCESS, responseDto);
    }
}
