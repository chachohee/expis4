package com.expis.ietm.dto;

import lombok.Data;

@Data
public class GlossaryDto {

    /* DB Table 매핑 변수 선언 */
    private long glsSeq;			//용어_일련번호
    private String glsAbbr;			//용어_약어
    private String glsMark;			//용어_표시(별표)
    private String glsDesc;			//용어_설명
    private String chkVal;
    private String[] searchArray;

    private String createUserId;	//등록자ID
    private String createDate;		//등록일자
    private String modifyUserId;	//수정자ID
    private String modifyDate;		//수정일자

}
