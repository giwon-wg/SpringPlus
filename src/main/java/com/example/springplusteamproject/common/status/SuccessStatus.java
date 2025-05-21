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
    USER_PASSWORD_UPDATE_SUCCESS(HttpStatus.OK, "U001", "비밀번호 업데이트에 성공했습니다."),
    USER_FIND_SUCCESS(HttpStatus.OK, "U001", "유저 조회에 성공했습니다."),
    USER_DELETE_SUCCESS(HttpStatus.OK, "U001", "유저 삭제에 성공했습니다."),
    USER_RESPONSE_SUCCESS(HttpStatus.OK, "U001", "유저 응답에 성공했습니다."),


























    // Store S000 80 ~ 110
    STORE_SUCCESS(HttpStatus.OK, "S001", "요청을 성공적으로 수행하였습니다."),
    STORE_CREATED_SUCCESS(HttpStatus.CREATED, "S002", "요청한 정보를 작성하였습니다."),




























    // Flower F000
    FLOWER_CREATE_SUCCESS(HttpStatus.CREATED, "F002", "꽃 상품 등록에 성공했습니다."),
    FLOWER_OPERATION_SUCCESS(HttpStatus.OK, "F001", "꽃 상품에 대한 요청이 성공적으로 처리되었습니다."),



























    // Coupon C000
    DISCOUNT_COUPON_CREATE_SUCCESS(HttpStatus.CREATED, "C002", "할인 쿠폰 등록에 성공했습니다."),
    USER_COUPON_FIND_SUCCESS(HttpStatus.OK, "C001", "쿠폰 조회에 성공했습니다."),
    USER_COUPON_ISSUE_SUCCESS(HttpStatus.OK, "C001", "할인 쿠폰 발급에 성공했습니다."),



























    // Order O000
    ORDER_SUCCESS(HttpStatus.CREATED, "O002","주문등록에 성공했습니다.");




























    ;
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
