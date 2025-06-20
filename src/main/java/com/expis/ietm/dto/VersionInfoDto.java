package com.expis.ietm.dto;

import lombok.Data;

import java.util.Date;

@Data
public class VersionInfoDto {

    /* DB Table 매핑 변수 선언 */
    private String toKey;				//교범_KEY
    private String tocoId;				// 2023.08.25 - TOCO_ID 추가 - jingi.kim
    private String contId;
    private String verId;					//버전_ID
    private String chgNo;				//변경판_번호
    private String chgDate;			//변경_일자
    private String revNo;				//개정판_번호
    private String revDate;			//개정_일자
    private String statusKind;		//상태_구분
    private String createUserId;	//등록자ID
    private Date createDate;			//등록일자
    private String modifyUserId;	//수정자ID
    private Date modifyDate;			//수정일자

    /* PagingDto에 포함된 항목 */
    private int firstRecordIndex;
}
