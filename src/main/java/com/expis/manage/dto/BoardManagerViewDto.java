package com.expis.manage.dto;

import com.expis.common.paging.PagingDTO;
import com.expis.community.dto.BoardMasterDTO;
import com.expis.community.dto.FileMasterDTO;
import com.expis.community.dto.RelatedDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardManagerViewDto {

    private int nPage = 1;
    private int boardDSeq = 1;
    private int boardMSeq = 0;
    private String statusKind = "10";
    private String active = "board";
    private String sort = "crateDate";
    private String sortKind = "asc";
    private String boardName;
    private String boardType;
    private String boardFileYn;
    private String boardWriteYn;
    private String boardReplyYn;
    private String boardCommentYn;
    private int updateMSeq;
    private int mSeq;
    private int nNum;
    private int updateNum;
    private int totCnt = 0;
    private int updateCnt = 0;
    private int boardOrderNum;
    private PagingDTO pagingDTO;
    private BoardMasterDTO boardOption;
    private BoardMasterDTO boardDetail;
    private FileMasterDTO file;
    private List<RelatedDTO> relatedList;
    private List<BoardMasterDTO> allBoardMList;
    private List<BoardMasterDTO> menuMList;
    private List<BoardMasterDTO> boardList;
    private List<BoardMasterDTO> boardMasterList;
    private List<BoardMasterDTO> commentList;

    // related
    private int relSeq = 0;
    private int fileSeq = 0;
    private String relTitle;
    private String relUrl;
    private String relContents;
    private String fileTranYn = "Y";
    private FileMasterDTO relatedFile;


    public static BoardMasterDTO ofBoardM(BoardManagerViewDto boardManagerViewDto) {
        return BoardMasterDTO.builder()
                .nPage(boardManagerViewDto.getNPage())
                .boardMSeq(boardManagerViewDto.getBoardMSeq())
                .boardDSeq(boardManagerViewDto.getBoardDSeq())
                .boardOrderNum(boardManagerViewDto.getBoardOrderNum())
                .boardName(boardManagerViewDto.getBoardName())
                .statusKind(boardManagerViewDto.getStatusKind())
                .sortKind(boardManagerViewDto.getSortKind())
                .boardType(boardManagerViewDto.getBoardType())
                .boardFileYn(boardManagerViewDto.getBoardFileYn())
                .boardWriteYn(boardManagerViewDto.getBoardWriteYn())
                .boardReplyYn(boardManagerViewDto.getBoardReplyYn())
                .boardCommentYn(boardManagerViewDto.getBoardCommentYn())
                .mSeq(boardManagerViewDto.getMSeq())
                .nNum(boardManagerViewDto.getNNum())
                .updateMSeq(boardManagerViewDto.getUpdateMSeq())
                .updateNum(boardManagerViewDto.getUpdateNum())
                .build();
    }

    public static RelatedDTO ofRelatedM(BoardManagerViewDto boardManagerViewDto) {
        return RelatedDTO.builder()
                .relSeq(boardManagerViewDto.getRelSeq())
                .relTitle(boardManagerViewDto.getRelTitle())
                .relUrl(boardManagerViewDto.getRelUrl())
                .relContents(boardManagerViewDto.getRelContents())
                .relSeq(boardManagerViewDto.getRelSeq())
                .build();
    }
}


