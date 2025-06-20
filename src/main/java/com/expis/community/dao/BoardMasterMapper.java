package com.expis.community.dao;

import com.expis.community.dto.BoardMasterDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BoardMasterMapper {
    public List<BoardMasterDTO> selectBoardMasterList();
    public List<BoardMasterDTO> selectBoardMaster(BoardMasterDTO dto);
    public List<BoardMasterDTO> selectAllBoardMaster(BoardMasterDTO dto);
    public int insertBoardMaster(BoardMasterDTO boardMasterDto);
    public int updateBoardMaster(BoardMasterDTO boardMasterDto);
    public int deleteBoardMaster(BoardMasterDTO boardMasterDto);
    public int nUpdateBoardNum(BoardMasterDTO dto);
    public int updateBoardNum(BoardMasterDTO dto);
    public int boardMCount(BoardMasterDTO dto);
    public List<BoardMasterDTO> boardMasterSelect(String statusKind);
    public BoardMasterDTO boardMOption(BoardMasterDTO dto);
    public int maxOrderNum();
}
