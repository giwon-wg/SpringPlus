package com.example.springplusteamproject.domain.store.status.success;

import org.springframework.http.HttpStatus;

import com.example.springplusteamproject.common.response.ReasonDto;
import com.example.springplusteamproject.common.status.BaseCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreSuccessCode implements BaseCode {

    STORE_ADD_SUCCESS(HttpStatus.CREATED, "S201", "가게 등록에 성공했습니다."),
    STORE_DELETE_SUCCESS(HttpStatus.OK, "S202", "가게 삭제에 성공했습니다."),
    STORE_LOOKUP_SUCCESS(HttpStatus.OK, "S200", "가게 조회에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final  ReasonDto cachedReasonDto;

    StoreSuccessCode(HttpStatus httpStatus, String code, String message) {
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
