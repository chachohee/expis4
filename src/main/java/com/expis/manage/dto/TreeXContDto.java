package com.expis.manage.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class TreeXContDto {

    /* DB Table 매핑 변수 선언 */
    private String treeKind;			//트리_구분
    private String refToKey;			//참조_교범_KEY
    private String refUserId;			//참조_사용자_ID
    private long treeXth;				//트리_데이터_순서
    private String treeXcont;			//트리_데이터
    private String statusKind;		//상태_구분
    private String createUserId;	//등록자ID
    private Date createDate;			//등록일자
    private String modifyUserId;	//수정자ID
    private Date modifyDate;			//수정일자

    /* 사용자 정의 변수 선언 */
    private String tocoListKind;		//그림/표목차_목록_구분

}
