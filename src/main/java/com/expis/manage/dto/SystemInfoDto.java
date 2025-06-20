package com.expis.manage.dto;

import lombok.Data;

import java.util.Date;

/**
 * [IETM]System Tree Info DTO Class
 */
@Data
public class SystemInfoDto {

    /* DB Table 매핑 변수 선언 */
    private String sysId;				//계통_ID
    private String pSysId;				//부모_계통_ID
    private long sysOrd;				//계통_순서
    private String sysName;			//계통_이름
    private String sysSubname;		//계통_부이름
    private String sysType;			//계통_구분
    private String toKey;				//교범_KEY

    private String statusKind;		//상태_구분
    private String createUserId;	//등록자ID
    private Date createDate;			//등록일자
    private String modifyUserId;	//수정자ID
    private Date modifyDate;			//수정일자

}
