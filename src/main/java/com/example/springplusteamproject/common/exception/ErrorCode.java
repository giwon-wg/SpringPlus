package com.example.springplusteamproject.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common -> default 용도
    DUPLICATED(HttpStatus.BAD_REQUEST, "중복 되었습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청값 입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "잘못된 접근입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "대상을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류 발생, 확인 후 조치하겠습니다."),

    // Auth
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인 실패, 아이디나 비밀번호를 확인해 주세요."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증되지 않은 접근입니다. 로그인 후 시도해 주세요."),
    USER_EXIST_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    USER_EXIST_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),

    // password
    PASSWORD_DUPLICATED(HttpStatus.BAD_REQUEST, "같은 비밀번호로 중복 요청할 수 없습니다"),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "비밀번호가 다릅니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DELETED_USER(HttpStatus.NOT_FOUND, "삭제된 사용자 입니다."),
    USER_NOT_FOUND_ROLE(HttpStatus.NOT_FOUND, "권한 정보가 없습니다."),
    INVALID_USER_ROLE(HttpStatus.FORBIDDEN, "유효하지 않은 사용자 권한입니다."),
    ROLE_ADMIN_FORBIDDEN(HttpStatus.UNAUTHORIZED, "관리자 권한이 없습니다."),
    ROLE_CUSTOMER_FORBIDDEN(HttpStatus.UNAUTHORIZED, "주문 고객이 아닙니다."),
    ROLE_OWNER_FORBIDDEN(HttpStatus.UNAUTHORIZED, "매장 사장님이 아닙니다."),

    // JWT
    JWT_REQUIRED(HttpStatus.BAD_REQUEST, "JWT 토큰이 필요합니다."),
    JWT_BAD_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 JWT 토큰입니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    JWT_INVALID_TOKEN( HttpStatus.UNAUTHORIZED, "유효하지 않는 JWT 토큰입니다."),
    JWT_INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "유효하지 않는 JWT 서명입니다."),
    JWT_NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "JWT 토큰이 없습니다");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
