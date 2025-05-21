package com.example.springplusteamproject.domain.user.entity;

import com.example.springplusteamproject.common.entity.BaseEntity;
import com.example.springplusteamproject.common.exception.ApiException;
import com.example.springplusteamproject.common.status.ErrorStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name= "user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Builder.Default
    private Boolean isDeleted = false;

    private String image;

    private String brn;

    // 비밀번호 변경
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // 유저 삭제
    public void delete() {
        this.isDeleted = true;
    }

    // 삭제 검증 (삭제 되었다면 예외 발생)
    public void validateDelete() {
        if (this.isDeleted) {
            throw new ApiException(ErrorStatus.DELETED_USER);
        }
    }

    // 접근 권한 검증 (로그인한 유저의 id와 id가 다르면 예외 발생)
    public void validateAccess(long userId) {
        if (userId != this.id) {
            throw new ApiException(ErrorStatus.FORBIDDEN);
        }
    }

    // 오너 여부 검증
//    public void validateOwner() {
//        if (userRole != UserRole.OWNER) {
//            throw new ApiException(ErrorStatus.ROLE_OWNER_FORBIDDEN);
//        }
//    }

    // 일반 고객 여부 검증
//    public void validateCustomer() {
//        if (userRole != UserRole.CUSTOMER) {
//            throw new ApiException(ErrorStatus.ROLE_CUSTOMER_FORBIDDEN);
//        }
//    }
}
