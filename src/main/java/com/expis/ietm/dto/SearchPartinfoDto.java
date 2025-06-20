package com.expis.ietm.dto;


import lombok.Data;

/**
 * [IETM]Search IPB Part Info DTO Class
 */

@Data
public class SearchPartinfoDto {

	/* DB Table 매핑 변수 선언 */
	private String toKey;				//교범_KEY
	private String tocoId;				//목차_ID
	private String indexNo;				//색인_번호
	private String grphNo;				//그래픽_번호
	private String ipbCode;				//IPB_코드
	private String partNo;				//부품_번호
	private String partName;			//부품_명
	private String nsn;					//국가재고번호
	private String cage;				//생산자부호
	private String rdn;					//참조지시번호
	private String upa;					//단위당구성수량(UPA)
	private String smr;					//근원정비복구성부호
	private String stdMngt;				//규격_관리도면
	private String wuc;					//2023.12.07 - 작업단위부호 추가 - jingi.kim
	
	private String createUserId;	//등록자ID
	private String createDate;		//등록일자
	private String modifyUserId;	//수정자ID
	private String modifyDate;		//수정일자
}
