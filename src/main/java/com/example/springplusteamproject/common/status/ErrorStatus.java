package com.example.springplusteamproject.common.status;

import com.example.springplusteamproject.common.response.ErrorReasonDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    CUSTOM_ERROR_STATUS(HttpStatus.INTERNAL_SERVER_ERROR, "C1001", "Custom Error"),







    // Auth A000
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A005", "로그인 실패, 아이디나 비밀번호를 확인해 주세요."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "A005", "인증되지 않은 접근입니다. 로그인 후 시도해 주세요."),
    USER_EXIST(HttpStatus.CONFLICT, "A008", "이미 존재하는 정보입니다."),
    USER_OWNER_BRN_REQUIRED(HttpStatus.BAD_REQUEST, "A004", "가게 주인은 사업자 등록번호를 반드시 입력해야 합니다."),

    // JWT
    JWT_NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "A007", "JWT 토큰이 없습니다"),






















    // User U000
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U007", "사용자를 찾을 수 없습니다."),
    DELETED_USER(HttpStatus.NOT_FOUND, "U007", "삭제된 사용자 입니다."),
    USER_NOT_FOUND_ROLE(HttpStatus.NOT_FOUND, "U007", "권한 정보가 없습니다."),
    INVALID_USER_ROLE(HttpStatus.FORBIDDEN, "U006", "유효하지 않은 사용자 권한입니다."),
    ROLE_ADMIN_FORBIDDEN(HttpStatus.UNAUTHORIZED, "U005", "관리자 권한이 없습니다."),
    ROLE_CUSTOMER_FORBIDDEN(HttpStatus.UNAUTHORIZED, "U005", "주문 고객이 아닙니다."),
    ROLE_OWNER_FORBIDDEN(HttpStatus.UNAUTHORIZED, "U005", "매장 사장님이 아닙니다."),
    PASSWORD_DUPLICATED(HttpStatus.BAD_REQUEST, "U004", "같은 비밀번호로 중복 요청할 수 없습니다"),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "U004", "비밀번호가 다릅니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "U006", "잘못된 접근입니다."),




















    // Store S000  80 ~ 110
    STORE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "S004", "잘못된 요청입니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "S007", "해당 정보를 찾을 수 없습니다."),



























    // Flower F000
    FLOWER_NOT_FOUND(HttpStatus.NOT_FOUND, "F007", "선택한 꽃 상품을 찾을 수 없습니다."),
    FLOWER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "F006", "선택한 꽃 상품에 대한 권한이 없습니다."),
    FLOWER_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "F004", "선택한 꽃 상품의 재고가 소진되었습니다."),



























    // Coupon C000
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "C007", "해당 쿠폰이 없습니다."),
    COUPON_ALREADY_ISSUED(HttpStatus.CONFLICT, "C008", "이미 발급받은 쿠폰입니다."),
    COUPON_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "C004", "쿠폰의 수량이 소진되었습니다."),
    COUPON_BAD_REQUEST(HttpStatus.BAD_REQUEST, "C004", "잘못된 요청입니다."),



























    // Order O000
    ORDER_BAD_REQUEST(HttpStatus.BAD_REQUEST, "O004", "잘못된 요청입니다."),
    ORDER_FLOWER_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "F004", "선택한 꽃 상품의 재고가 소진되었습니다."),
    ORDER_COUPON_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "O006", "본인 소유 쿠폰이 아닙니다."),
    ORDER_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "O007","해당 유저가 없습니다."),
    ORDER_FLOWER_NOTFOUND(HttpStatus.NOT_FOUND, "O007","해당 꽃 상품이 없습니다."),
    ORDER_COUPON_NOTFOUND(HttpStatus.NOT_FOUND, "O007","해당 쿠폰이 없습니다."),
    ORDER_COUPON_ALREADY_USED(HttpStatus.CONFLICT, "O008","이미 사용된 쿠폰입니다."),




















    ;
    // 본 코드
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final ErrorReasonDto cachedErrorReasonDto;

    ErrorStatus(HttpStatus httpStatus, String code, String message) {
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
