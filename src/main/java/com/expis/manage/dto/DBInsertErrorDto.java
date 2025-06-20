package com.expis.manage.dto;

import lombok.Data;

/**
 * 디비 입력 실패 할경우 해당 이력 등록 용
 * 테이블 명 : temp_db_update_result
 */
@Data
public class DBInsertErrorDto {
    /* Register  관련 변수 */
    private String toKey;		//교범키
    private String tocoId;		//교범목차ID
    private String type;		//입력 타입 Thread 고정
    private String errMsg;		//에러 메세지
}
