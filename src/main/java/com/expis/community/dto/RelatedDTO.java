package com.expis.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatedDTO {
    private int relSeq;
    private String relTitle;
    private String relUrl;
    private int fileSeq;
    private String fileStrNm;
    private String relContents;
    private String statusKind;
    private String createUserId;
    private String createDate;
    private String modifyUserId;
    private String modifyDate;
    private String fileOrgNm;
    private String fileType;
    private int rNum;
    private int startRow;
    private int endRow;
    private String searchKeyWord;
    private int nPage;
    private int recordCnt;
}
