package com.example.springplusteamproject.common.status;

import com.example.springplusteamproject.common.response.ErrorReasonDto;

public interface BaseErrorCode {
    ErrorReasonDto getReasonHttpStatus();
}
