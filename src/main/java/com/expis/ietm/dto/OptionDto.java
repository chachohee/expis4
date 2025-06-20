package com.expis.ietm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OptionDto {

    private String userId;
    private String exploreMode;         // 탐색 모드
    private String outputMode;          // 탐색 범위
    private String viewMode;            // 내용 보기 유형
    private String fiMode;              // 파일 보기 유형
    private String fontSize;            // 글꼴 크기
    private String fontFamily;          // 글꼴 조정
    private String printWordMode;
    private String mobileMenuMode;
    private String coverType;
    private int count;

}
