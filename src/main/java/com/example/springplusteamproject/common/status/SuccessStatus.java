//package com.example.springplusteamproject.common.status;
//
//import com.example.springplusteamproject.common.response.ErrorReasonDto;
//import com.example.springplusteamproject.common.response.ReasonDto;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//
//@Getter
//@RequiredArgsConstructor
//public enum SuccessStatus implements BaseCode {
//
//    CUSTOM_SUCCESS_STATUS(HttpStatus.OK, "S1001", "Custom Success"),
//
//    ;
//
//    private final HttpStatus httpStatus;
//    private final String code;
//    private final String message;
//
//    private final ReasonDto cachedReasonDto = ReasonDto.builder()
//        .isSuccess(true)
//        .httpStatus(httpStatus)
//        .code(code)
//        .message(message)
//        .build();
//
//    @Override
//    public ReasonDto getReasonHttpStatus() {
//        return cachedReasonDto;
//    }
//}
