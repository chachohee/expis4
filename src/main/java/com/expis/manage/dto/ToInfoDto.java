package com.expis.manage.dto;

import lombok.Data;

import java.util.Date;

/**
 * [IETM]TO Info DTO Class
 *
 */
@Data
public class ToInfoDto {
    /* DB Table 매핑 변수 선언 */
    private String toKey;				//교범_KEY
    private String toId;					//교범_ID
    private String toName;				//교범_이름
    private String toSubname;		//교범_부이름
    private String toType;				//교범_구분
    private String toToType;			//교범_기종구분
    private String toVehicleType;	//교범_형상_구분
    private String toPart;				//교범_
    private String toRevNo;			//교범_개정판_번호
    private String toRevDate;		//교범_개정판_일자
    private String toChgNo;			//교범_변경_번호
    private String toChgDate;		//교범_변경_일자
    //private String toOrgDate;		//교범_원판_일자
    private String toDesc;				//교범_설명
    private String toSaveYn;			//교범_등록_여부
    private String toFviewYn;			//교범_프레임보기_여부

    private String statusKind;		//상태_구분
    private String createUserId;	//등록자ID
    private Date createDate;			//등록일자
    private String modifyUserId;	//수정자ID
    private Date modifyDate;			//수정일자

    /* 사용자 정의 변수 선언 */
    private String propertyKind;		//속성_설정_구분 (01:등록여부, 02:프레임보기여부)
}
