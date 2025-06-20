package com.expis.community.service;

import com.expis.community.dao.BoardMasterMapper;
import com.expis.community.dto.BoardMasterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardMasterService {
    private final BoardMasterMapper boardMasterMapper;

    public List<BoardMasterDTO> selectBoardMasterList(){
        return boardMasterMapper.selectBoardMasterList();
    }

    public BoardMasterDTO boardMOption(BoardMasterDTO dto){
        return boardMasterMapper.boardMOption(dto);
    }

    public int boardMCount(BoardMasterDTO dto) {return boardMasterMapper.boardMCount(dto);}

    public List<BoardMasterDTO> selectAllBoardMaster(BoardMasterDTO dto) {return boardMasterMapper.selectAllBoardMaster(dto);}

    public int insertBoardMaster(BoardMasterDTO boardMasterDto) {
//        AdminLogDTO logDto =  new AdminLogDTO();

//        logDto.setCreateUserId(boardMasterDto.getCreateUserId());
//        logDto.setCodeType("3201");
//        glossaryMapper.insertLog(logDto);
        boardMasterDto.setMaxOrderNum(boardMasterMapper.maxOrderNum());

        return boardMasterMapper.insertBoardMaster(boardMasterDto);
    }
//
public int updateBoardMaster(BoardMasterDTO boardMasterDto) {
//    AdminLogDTO logDto =  new AdminLogDTO();
//
//    logDto.setCreateUserId(boardMasterDto.getCreateUserId());
//    logDto.setCodeType("3202");
//    glossaryMapper.insertLog(logDto);
    return boardMasterMapper.updateBoardMaster(boardMasterDto);
}
    public int deleteBoardMaster(BoardMasterDTO boardMasterDto) {
//        AdminLogDTO logDto =  new AdminLogDTO();
//
//        logDto.setCreateUserId(boardMasterDto.getCreateUserId());
//        logDto.setCodeType("3203");
//        glossaryMapper.insertLog(logDto);
        return boardMasterMapper.deleteBoardMaster(boardMasterDto);
    }

    public List<BoardMasterDTO> selectBoardMaster(BoardMasterDTO dto) {
        return boardMasterMapper.selectBoardMaster(dto);
    }

    public int updateBoardNum(BoardMasterDTO dto) {
        int result = 0;

        result += boardMasterMapper.nUpdateBoardNum(dto);

//        AdminLogDTO logDto =  new AdminLogDTO();
//
//        logDto.setCreateUserId(dto.getCreateUserId());
//        logDto.setCodeType("3204");
//        glossaryMapper.insertLog(logDto);
        result += boardMasterMapper.updateBoardNum(dto);

        return result;
    }

    public List<BoardMasterDTO> boardMasterSelect(String statusKind) {
        return boardMasterMapper.boardMasterSelect(statusKind);
    }
}
