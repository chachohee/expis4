package com.expis.ietm.dto;

import lombok.Data;

import java.util.Date;

/**
 * [IETM]TO Contents Info DTO Class
 */
@Data
public class TocoInfoDto {

	/* DB Table 매핑 변수 선언 */
	private String toKey = "";					//교범_KEY
	private String tocoId = "";					//목차_ID
	private String pTocoId = "";					//부모_목차_ID
	private long tocoOrd = 0;					//목차_순서
	private String tocoName = "";				//목차_이름
	private String tocoType = "";				//목차_구분
	private String tocoVehicleType = "";	//목차_형상_구분
	private String tocoRefId = "";				//목차_참조_ID
	private String tocoSecurityCode;			//목차_보안_코드
	private String tocoStatusCode;			//목차_상태_코드
	private String tocoSssnNo = "";			//목차_SSSN_번호
	private String tocoDummy = "";			//목차_
	private String tocoVerId = "";				//목차_버전_ID
	private String tocoChgNo = "";			//목차_변경판_번호
	private String tocoRevNo = "";			//목차_개정판_번호
	private String toType = "";

	private String statusKind = "";			//상태_구분
	private String createUserId = "";		//등록자ID
	private Date createDate = null;			//등록일자
	private String modifyUserId = "";		//수정자ID
	private Date modifyDate = null;			//수정일자

	//jysi
	private String uuid = "";				//목차_정렬_ID
	private String contentsSortName = "";   //목차_정렬_기준

	private String param;

	/* PagingDto에 포함된 항목 */
	private int firstRecordIndex;

	/* 사용자 정의 변수 선언 */
	private String rdn;
	
}
