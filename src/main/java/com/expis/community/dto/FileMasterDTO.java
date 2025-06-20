package com.expis.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMasterDTO {

    private int fileSeq;
    private String fileOrgNm;
    private String fileStrNm;
    private String filePath;
    private String fileSize;
    private String fileType;
    private String statusKind;
    private String createUserId;
    private String createDate;
    private String modifyUserId;
    private String modifyDate;
}
