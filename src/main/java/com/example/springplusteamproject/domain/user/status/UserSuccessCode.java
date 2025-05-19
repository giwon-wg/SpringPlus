package com.example.springplusteamproject.domain.user.status;

import com.example.springplusteamproject.common.response.ReasonDto;
import com.example.springplusteamproject.common.status.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserSuccessCode implements BaseCode {

    USER_PASSWORD_UPDATE_SUCCESS(HttpStatus.OK, "U201", "비밀번호 업데이트에 성공했습니다."),
    USER_FIND_SUCCESS(HttpStatus.OK, "U202", "유저 조회에 성공했습니다."),
    USER_DELETE_SUCCESS(HttpStatus.OK, "U203", "유저 삭제에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final ReasonDto cachedReasonDto;

    UserSuccessCode(HttpStatus httpStatus, String code, String message) {
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
