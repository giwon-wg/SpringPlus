package com.example.springplusteamproject.common.exception;

import com.example.springplusteamproject.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {

  private final BaseErrorCode errorCode;

}