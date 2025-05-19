package com.example.springplusteamproject.domain.auth.status;

import com.example.springplusteamproject.common.response.ReasonDto;
import com.example.springplusteamproject.common.status.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthSuccessCode implements BaseCode {

    AUTH_SIGNUP_SUCCESS(HttpStatus.CREATED, "A201", "회원 가입에 성공했습니다."),
    AUTH_LOGIN_SUCCESS(HttpStatus.OK, "S202", "로그인에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final ReasonDto cachedReasonDto;

    AuthSuccessCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
        this.cachedReasonDto = ReasonDto.builder()
            .isSuccess(false)
            .httpStatus(httpStatus)
            .code(code)
            .message(message)
            .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return cachedReasonDto;
    }
}
