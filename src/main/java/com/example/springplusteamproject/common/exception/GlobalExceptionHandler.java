package com.example.springplusteamproject.common.exception;

import com.example.springplusteamproject.common.response.ApiResponse;
import com.example.springplusteamproject.common.response.ErrorReasonDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<ErrorReasonDto>> handleCustomException3(ApiException e) {
        return ApiResponse.onFailure(e.getErrorCode());
    }
}
