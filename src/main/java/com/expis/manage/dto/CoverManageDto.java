package com.expis.manage.dto;

import lombok.Data;

@Data
public class CoverManageDto {
    private int coverSeq;
    private String tocoId;
    private String toKey;
    private String coverTitle;
    private String coverSubTitle;
    private int coverImg;
    private String coverDistCont;
    private String coverWarnningCont;
    private String coverCont;
    private String coverDate;
    private String coverVerDate;
    private String coverChgNo;      // cover 변경 번호 추가 - osm
    private String modifyUserId;	//교범 팝업 문구 및 계정 정보 표시용으로 사용중
    private String createUserId;
    private String coverVersion;	// 버전명
    private String coverPart; 		// Part
    private String totype; 			// totype
    private String coverdata;		// BLOCK2에서 추가, 표지 개정정보표시 날짜 - jingi.kim
    private String tpinfo;          // KTA계열일때 tpinfo 추가 - JSH
    private String versiondate;     // KTA계열일때 versiondate 추가 - JSH
    private String sysitem;
}
