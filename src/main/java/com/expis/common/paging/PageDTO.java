package com.expis.common.paging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {
    private int boardMSeq;
    private int nowPage = 0;
    private int totalCount = 0;
    private String searchCate = "";
    private String searchValue = "";
}
