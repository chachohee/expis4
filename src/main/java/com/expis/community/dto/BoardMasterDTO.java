package com.expis.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardMasterDTO {

    // cm_board_master
    private int boardMSeq;
    private String boardName;
    private String boardType;
    private String boardFileYn;
    private String boardWriteYn;
    private String boardReplyYn;
    private String boardCommentYn;
    private String boardCountYn;
    private String boardNoticeYn;
    private int noticeDay;
    private int boardOrderNum;
    private String className;
    private String viewUserId;

    // cm_board_detail
    private int boardDSeq;
    private String boardTitle;
    private String boardContents;
    private int boardPSeq;
    private int boardFSeq;
    private int boardRSeq;
    private int boardCount;
    private String boardTopYn;
    private String boardNoticeCheck;
    private int commentsCount;
    private String fileYn;
    private int xth;

    // cm_board_comments
    private int boardCSeq;
    private String boardComments;

    private String statusKind;
    private String createUserId;
    private String createDate;
    private String modifyUserId;
    private String modifyDate;

    private int rNum;
    private int tNum;
    private int startRow;
    private int endRow;
    private String searchCate;
    private String searchValue;
    private int recordCnt;

    private int[] boardDSeqList;

    private int fileSeq;
    private String fileOrgNm;

    private String sort;
    private String sortKind;

    private String paramKeyword;

    private int mSeq;
    private int nNum;
    private int updateMSeq;
    private int updateNum;
    private int nPage;
    private int totalCount;
    private int maxOrderNum;

    private String fileName;
}
