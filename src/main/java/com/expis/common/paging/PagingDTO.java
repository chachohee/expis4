package com.expis.common.paging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingDTO {
    private int totalPage;   // 전체 페이지 수 (총 페이지 개수)
    private int startPage;   // 현재 블록의 시작 페이지 번호
    private int endPage;     // 현재 블록의 끝 페이지 번호
    private int lastPage;    // 현재 사용자가 위치한 마지막 페이지 번호
    private int startRow;    // 현재 페이지의 첫 번째 데이터 번호
    private int endRow;      // 현재 페이지의 마지막 데이터 번호
    private int boardMSeq;

    public PagingDTO(PageDTO pageDto, int recordSize, int blockSize){
        int totalCount = pageDto.getTotalCount();
        int nowPage = Math.max(1, pageDto.getNowPage());

        this.totalPage = Math.max(1, (int) Math.ceil((double) totalCount / recordSize));
        this.lastPage = Math.min(nowPage, totalPage);

        this.startRow = calculateStartRow(lastPage, recordSize);
        this.endRow = startRow + recordSize - 1;

        this.startPage = calculateStartPage(lastPage, blockSize);
        this.endPage = Math.min(startPage + blockSize - 1, totalPage);

        this.boardMSeq = pageDto.getBoardMSeq();
    }

    //현재 페이지를 기준으로 몇 번째 데이터부터 가져와야 하는지 1부터 시작하는 번호를 구하는 메서드(1..11..22)
    private int calculateStartRow(int nowPage, int recordSize) {
        return (nowPage - 1) * recordSize + 1;
    }

    private int calculateStartPage(int nowPage, int blockSize){
        return ((nowPage - 1) / blockSize) * blockSize + 1;
    }

}
