package com.example.springplusteamproject.domain.store.status.exception;

import org.springframework.http.HttpStatus;

import com.example.springplusteamproject.common.response.ErrorReasonDto;
import com.example.springplusteamproject.common.status.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements BaseErrorCode {

    STORE_NAME_CONFLICT(HttpStatus.BAD_REQUEST, "S400", "중복된 상표명입니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "S404", "가게를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final ErrorReasonDto cachedErrorReasonDto;

    StoreErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
        this.cachedErrorReasonDto = ErrorReasonDto.builder()
            .isSuccess(false)
            .httpStatus(httpStatus)
            .code(code)
            .message(message)
            .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return cachedErrorReasonDto;
    }
}
