package com.expis.ietm.dto;

import lombok.Data;

/**
 * Paging DTO Class
 */
@Data
public class PagingDto {

    /** 현재페이지 */
    private int pageIndex = 1;

    /** 페이지갯수 */
    private int pageUnit;

    /** 페이지사이즈 */
    private int pageSize;

    /** firstIndex */
    private int firstIndex = 1;

    /** lastIndex */
    private int lastIndex = 1;

    /** recordCountPerPage */
    private int recordCountPerPage;

    /** 페이징 SQL의 조건절에 사용되는 시작 rownum. */
    private int firstRecordIndex;

    /** 서치 값과 구분자 */
    private String searchValue;
    private String searchKeyword;

    /** 구분자(N:본문,R:reply) */
    private String guBun;

    /** 조회시 보이는 번호 */
    private long rowSeq;

}
