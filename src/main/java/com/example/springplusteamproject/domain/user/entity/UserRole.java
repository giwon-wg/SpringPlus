package com.example.springplusteamproject.domain.user.entity;

import com.example.springplusteamproject.common.exception.ErrorCode;
import com.example.springplusteamproject.common.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UserRole {
    OWNER("사업자 계정"),
    CUSTOMER("일반 계정");

    private final String role;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
            .filter(r->r.name().equalsIgnoreCase(role))
            .findFirst()
            .orElseThrow(() -> new GlobalException(ErrorCode.INVALID_USER_ROLE));
    }

}
