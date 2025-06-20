package com.expis.manage.dto;

import lombok.Data;

@Data
public class AdminLogDto {
    //log_info
    private int logSeq;

    //log_code_info
    private int codeSeq;
    private String codeType;
    private String codeName;
    private String codeInfo;
    private String logVal;

    private String dbType;
    private String createUserId;
    private String createDate;
    private String modifyUserId;
    private String modifyDate;

    private int rNum;
    private int turnNum;
    private int startRow;
    private int endRow;

    private int nowPage;
    private int recordCnt;
    private String startDate;
    private String endDate;
}
