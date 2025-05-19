package com.example.springplusteamproject.common.status;

import com.example.springplusteamproject.common.response.ReasonDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessStatus implements BaseCode {

    CUSTOM_SUCCESS_STATUS(HttpStatus.OK, "S1001", "Custom Success"),







    // Auth A000
    AUTH_SIGNUP_SUCCESS(HttpStatus.CREATED, "A002", "회원 가입에 성공했습니다."),
    AUTH_LOGIN_SUCCESS(HttpStatus.OK, "A001", "로그인에 성공했습니다."),



























    // User U000
    USER_RESPONSE_SUCCESS(HttpStatus.OK, "U001", "유저 응답에 성공했습니다.");


























    // Store S000





























    // Flower F000





























    // Coupon C000





























    // Order O000





























    // 본 코드
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final ReasonDto cachedReasonDto;

    SuccessStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
        this.cachedReasonDto = ReasonDto.builder()
            .isSuccess(true)
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
