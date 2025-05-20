package com.example.springplusteamproject.domain.auth.dto.model;

import com.example.springplusteamproject.domain.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUser {
    private Long id;
    private UserRole userRole;

    public static AuthUser dummyOwner() {
        return new AuthUser(999L, UserRole.OWNER);
    }

    public static AuthUser dummyCustomer() {
        return new AuthUser(1L, UserRole.CUSTOMER);
    }

}
