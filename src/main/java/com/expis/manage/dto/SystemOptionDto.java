package com.expis.manage.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SystemOptionDto {
    private int optSysSeq;
    private String optVersion;
    private String optIntro;
    private String optProjectType;
    private String optRemo;
    private String optAirplane;
    private String optUnit;
    private String optPrint;
    private String optCover;
    private String optAlert;
    private int optFontResize;
    private int optIetmImg;
    private int optCmntImg;
    private String optCmntTitle;
    private String optCmntSubTitle;
    private String optIpb;
    private String modifyUserId;
    private String modifyDate;
    private String createUserId;
    private String createDate;
    private String statusKind;
    private String optCoverCont;
    private String optPwValidationInfo;
    private String optPwValidationMsg;
    private String optPwMaxLength;
    private String optPwMinLength;
    private String regexNum;
    private String regexEn;
    private String regexSpecial;
    private String optCtrlKind;
    private String optPrintIp;
    private String optToOutput;
}
