package com.example.springplusteamproject.domain.user.controller;

import com.example.springplusteamproject.common.response.ApiResponse;
import com.example.springplusteamproject.common.status.SuccessStatus;
import com.example.springplusteamproject.domain.user.dto.request.UpdatePasswordRequestDto;
import com.example.springplusteamproject.domain.user.dto.response.UserResponseDto;
import com.example.springplusteamproject.domain.user.service.UserService;
import com.example.springplusteamproject.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "비밀번호 업데이트",
        description = "비밀번호를 업데이트 합니다.",
        security = {@SecurityRequirement(name= "bearerAuth")}
    )
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMyPassword(
        @Valid @RequestBody UpdatePasswordRequestDto requestDto,
        @AuthenticationPrincipal CustomUserPrincipal principal)
    {
        log.info("oldPassword: {}, newPassword: {}", requestDto.getOldPassword(), requestDto.getNewPassword());
        userService.updateMyPassword(requestDto, principal);
        return ApiResponse.onSuccess(SuccessStatus.USER_PASSWORD_UPDATE_SUCCESS, null);
    }


    @Operation(
        summary = "내 정보 조회",
        description = "내 정보를 조회합니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> findUser(@AuthenticationPrincipal CustomUserPrincipal principal) {

        UserResponseDto responseDto = userService.findUserByEmail(principal.getUsername());
        return ApiResponse.onSuccess(SuccessStatus.USER_FIND_SUCCESS, responseDto);
    }


    @Operation(
        summary = "내 계정 삭제",
        description = "내 계정을 삭제합니다.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal CustomUserPrincipal principal) {

        userService.deleteMyAccount(principal);
        return ApiResponse.onSuccess(SuccessStatus.USER_DELETE_SUCCESS, null);
    }
}
