package com.expis.ietm.dto;

import lombok.Data;

import java.util.Date;


/**
 * [IETM]WorkCard XML Contents DTO Class
 */
@Data
public class XContWCDto {

	/* DB Table 매핑 변수 선언 */
	private String toKey;				//교범_KEY
	private String tocoId;				//목차_ID
	private String cardNo;				//카드_번호
	private String wcType;				//작업카드_타입
	private long xth;					//데이터_순서
	private String xcont;				//데이터_내용
	
	private String createUserId;	//등록자ID
	private Date createDate;		//등록일자
	private String modifyUserId;	//수정자ID
	private Date modifyDate;		//수정일자
	
	/* 사용자 정의 변수 선언 */
	
	/* 2023 01 13 Park.J.S. ADD : WC 카드 내 계통별 링크 구현 위해 추가 */
	private String name			= "";//구분
	private String steptime		= "";//인시분
	private String steparea		= "";//작업구역
	private String system		= "";//계통
	private String subsystem	= "";//하부계통
	private String content		= "";//검사내용
	
}
