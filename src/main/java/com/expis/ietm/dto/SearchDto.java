package com.expis.ietm.dto;

import lombok.Data;

/**
 * [IETM-SC]Search 관련 DTO Class
 */
@Data
public class SearchDto {

	/* DB Table 매핑 변수 선언 - 공통*/
	private String toKey;					//교범_KEY
	private String tocoId;					//목차_ID
	private String pTocoId;
	
	/* 현재교범/현재계통 본문 검색 TM_SC_CONT 필요 항목 */
	private String cont;						//데이터_내용
	
	/* FI 검색 TM_SC_FI 필요 항목 */
	private String fiId;						//고장탐구_ID
	private String fiCode;					//고장탐구_코드
	
	/* WUC 검색 TM_SC_WUC 필요 항목 */
	private String wucCode;				//작업단위부호_코드
	private String wucName;				//작업단위부호_명
	
	/* 그림/표/경고에서 해당 ID(grph_id, tbl_id, alt_id) 필요 항목 */
	private String contId;					//데이터_내용
	
	private String createUserId;		//등록자ID
	private String createDate;			//등록일자
	private String modifyUserId;		//수정자ID
	private String modifyDate;			//수정일자

	/* 사용자 정의 변수 선언 */
	private String searchCond;			//검색조건
	private String searchSubCond;	//검색조건2
	private String searchWord;			//검색어
	private String resultKind;				//검색 결과 구분(목차,본문,그림,표,경고)
	private String chkVal;
	private String[] searchArray;
	private String searchFirst;
	private String searchSecond;
	
	
	/* 상수로 정의한 값 셋팅 */
	private String[] arrConstCond			= null;
	private String[] arrConstSubCond	= null;
	private String[] arrConstResult		= null;
	
	private String hasFirst;
	private String hasSecond;
	private String hasThird;
	private String hasFourth;
	/* 2022 09 26 ADD */
	private String hasFifth;
	private String hasSixth;
	private String hasSeventh;				//2023.12.07 - 작업단위부호 추가 - jingi.kim
	
	private String xcontTagChk;
	//2022 09 01 Park.J.S. ADD
	private String vehicleType;//항공기 기종 선택했을 경우 타입(A,B,C,D)

	
}
