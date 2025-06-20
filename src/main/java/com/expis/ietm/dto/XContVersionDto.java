package com.expis.ietm.dto;

import lombok.Data;

import java.util.Date;

/**
 * [IETM]Version XML Contents DTO Class
 */
@Data
public class XContVersionDto {

	/* DB Table 매핑 변수 선언 */
	private String toKey;				//교범_KEY
	private String tocoId;				//목차_ID
	private String contId;				//내용_ID
	private String verId;					//버전_ID
	private String verName;			//버전_명
	private long xth;						//트리_데이터_순서
	private String xcont;				//트리_데이터

	private String createUserId;	//등록자ID
	private Date createDate;			//등록일자
	private String modifyUserId;	//수정자ID
	private Date modifyDate;			//수정일자

}
