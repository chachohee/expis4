package com.expis.ietm.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class MemoDto extends PagingDto {

	private long memoSeq;
	private String toKey;
	private String tocoId;
	private String tocoName;
	private String subject;
	private String cont;
	private String shareYn;
	private String statusKind;
	private String createUserId;
	private String createDate;
	private String createDate2;
	private String modifyUserId;
	private String modifyDate;
	private String sortCode;
	private String memoStatus;

	//PagingDto에 포함된 항목
	private int firstRecordIndex;

	private String createUserInfo;
	private String outputMode;		//탐색범위(단일목차,다중목차-하위목차포함) - 목차마다 메모를 가져오는게아니라 다중목차 일때 일괄 가져옴

}
