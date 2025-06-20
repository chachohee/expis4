package com.expis.manage.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AdminGlossaryDto {
    private int glsSeq;
    private String glsAbbr;
    private String glsMark;
    private String glsDesc;
    private String createUserId;
    private Date createDate;
    private String modifyUserId;
    private Date modifyDate;
    private String errorType;

    private int lsaSeq;
    private String lsaAssembly;
    private String lsaLcn;
    private String lsaAlc;
    private String lsaPdt;
    private String lsaRefNum;
    private String lsaFgc;
    private String lsaCount;
    private String lsaUnit;
    private String lsaSmr;
    private String lsaMtbf;
    private String lsaPrice;

    private int rNum;
    private int turnNum;
    private int startRow;
    private int endRow;

    private String searchCate;
    private String searchValue;
    private String sortBy;
    private String sortKind;

    private String logVal;
    private String dbType;
    private int recordCnt;
    private int nowPage;
}
