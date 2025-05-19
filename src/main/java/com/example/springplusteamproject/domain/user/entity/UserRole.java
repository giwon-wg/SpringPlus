package com.example.springplusteamproject.domain.user.entity;

import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
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
            .orElseThrow(() -> new ApiException(ErrorStatus.INVALID_USER_ROLE));
    }

}
