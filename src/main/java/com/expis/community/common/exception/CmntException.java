package com.expis.community.common.exception;

import lombok.Getter;

@Getter
public class CmntException extends RuntimeException {

    private final ErrorCode errorCode;

    public CmntException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CmntException(ErrorCode errorCode, Throwable cause){
        super(errorCode.getMessage(), cause); //예외 원인도 같이 저장
        this.errorCode = errorCode;
    }
}
