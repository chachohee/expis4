package com.expis.community.dao;

import com.expis.community.dto.BoardMasterDTO;
import com.expis.community.dto.FileMasterDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface BoardDetailMapper {

    // 게시판 리스트 및 조회
    List<BoardMasterDTO> introBoardList(BoardMasterDTO dto);
    List<BoardMasterDTO> selectBoardDetailList(BoardMasterDTO dto);
    BoardMasterDTO selectBoardDetail(int boardDSeq);
    ArrayList<BoardMasterDTO> selectXTHBoardDetail(int boardDSeq);
    int updateBoardDetailHitCount(int boardDSeq);
    int boardTotalCount(BoardMasterDTO dto);
    List<BoardMasterDTO> selectBoardTopList(int boardMSeq);

    // 게시글 등록,수정,삭제
    int selectNextBoardDSeq();
    int insertXTHBoardDetail(BoardMasterDTO boardMasterDto);
    int updateXTHBoardDetail(BoardMasterDTO boardMasterDto);
    int appendXTHBoardDetail(BoardMasterDTO boardMasterDto);
    int deleteBoardDetail(BoardMasterDTO boardMasterDto);

    // 댓글
    List<BoardMasterDTO> selectBoardDetailComments(int boardDSeq);
    String selectComment(int boardCSeq);
    int insertComment(BoardMasterDTO boardMasterDto);
    int updateComment(BoardMasterDTO boardMasterDto);
    int deleteComment(BoardMasterDTO boardMasterDto);

    // 파일 관련
    FileMasterDTO fileSelect(int boardFSeq);
}