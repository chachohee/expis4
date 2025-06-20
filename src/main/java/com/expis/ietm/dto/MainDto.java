package com.expis.ietm.dto;

import lombok.Data;

@Data
public class MainDto {
    private String toKey;
    private String tocoId;
    private String toName;
    private String tocoName;
    private String vcKind;
    /* Hong */

    private String sessionId;
    private String userId;
    private String userPw;

    private String statusKind;
    private String targetKind;
//    private List<MainDto> mainDto;
    private String ipAddress;
    private int num;
    private String connDate;
    private String disconnDate;
    private int connSeq;

    //표지관리
    private int coverSeq;
    private String coverTitle;
    private String coverSubTitle;
    private int coverImg;
    private String coverDistCont;
    private String coverWarnningCont;
    private String coverCont;
    private String coverDate;
    private String coverVerDate;
    private String modifyUserId;
}
