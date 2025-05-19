package com.example.springplusteamproject.domain.auth.service;

import com.example.springplusteamproject.domain.auth.dto.request.LoginRequestDto;
import com.example.springplusteamproject.domain.auth.dto.request.SignupRequestDto;
import com.example.springplusteamproject.domain.auth.dto.response.LoginResponseDto;
import com.example.springplusteamproject.domain.auth.dto.response.SignupResponseDto;

public interface AuthService {
    SignupResponseDto signup(SignupRequestDto requestDto);
    LoginResponseDto login(LoginRequestDto requestDto);
}
