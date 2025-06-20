package com.expis.community.dto;

import com.expis.community.common.exception.ErrorCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponseDTO {

    private final String errorCode;
    private final String errorMessage;
    private final int status;
    private final LocalDateTime timestamp;

    public ErrorResponseDTO(ErrorCode errorCode) {
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
        this.status = errorCode.getStatus().value();
        this.timestamp = LocalDateTime.now();
    }
}
