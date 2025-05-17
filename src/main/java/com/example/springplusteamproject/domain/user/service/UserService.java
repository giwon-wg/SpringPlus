package com.example.springplusteamproject.domain.user.service;

import com.example.springplusteamproject.domain.user.dto.request.UpdatePasswordRequestDto;
import com.example.springplusteamproject.domain.user.dto.response.UserResponseDto;
import com.example.springplusteamproject.security.CustomUserPrincipal;

public interface UserService {
    void updateMyPassword(UpdatePasswordRequestDto requestDto, CustomUserPrincipal principal);
    UserResponseDto findUserByEmail(String email);
//    void deleteUser(CustomUserPrincipal principal);
}
