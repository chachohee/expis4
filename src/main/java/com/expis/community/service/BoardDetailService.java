package com.expis.community.service;

import com.expis.common.paging.PageDTO;
import com.expis.common.paging.PagingDTO;
import com.expis.community.common.exception.CmntException;
import com.expis.community.common.exception.ErrorCode;
import com.expis.community.dao.BoardDetailMapper;
import com.expis.community.dto.BoardMasterDTO;
import com.expis.community.dto.FileMasterDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardDetailService {

    private final BoardDetailMapper boardMapper;

    @Value("${app.expis.recordCnt}")
    private int recordCnt;
    @Value("${app.expis.pagingCnt}")
    private int pagingCnt;

    public PagingDTO createPaging(BoardMasterDTO boardMasterDTO) {
        int nPage = boardMasterDTO.getNPage() > 0 ? boardMasterDTO.getNPage() : 1;

        int totCnt = boardTotalCount(boardMasterDTO);
        totCnt = (totCnt == 0 ? 1 : totCnt);

        PageDTO pageDTO = PageDTO.builder()
                .boardMSeq(boardMasterDTO.getBoardMSeq())
                .nowPage(nPage)
                .totalCount(totCnt)
                .searchCate(boardMasterDTO.getSearchCate())
                .searchValue(boardMasterDTO.getSearchValue())
                .build();

        PagingDTO pagingDTO = new PagingDTO(pageDTO, recordCnt, pagingCnt);

        boardMasterDTO.setStartRow(pagingDTO.getStartRow());
        boardMasterDTO.setEndRow(pagingDTO.getEndRow());

        return pagingDTO;
    }

    public Map<String, Object> introBoardList(List<BoardMasterDTO> menuList) {
        Map<String, Object> map = new HashMap<>();
        List<List<BoardMasterDTO>> result = new ArrayList<>();
        List<BoardMasterDTO> boardName = new ArrayList<>();

        if (menuList == null || menuList.isEmpty()) {
            throw new CmntException(ErrorCode.INVALID_INPUT);
        }

        int menuSize = menuList.size();

        for (int i = 0; i < menuSize; i++) {
            BoardMasterDTO dto = new BoardMasterDTO();
            List<BoardMasterDTO> list;
            dto.setBoardMSeq(menuList.get(i).getBoardMSeq());
            dto.setBoardName(menuList.get(i).getBoardName());
            dto.setClassName(menuList.get(i).getClassName());

            list = introBoardList(dto);
            log.info("list =" + list);
            boardName.add(dto);
            result.add(list);
        }

        map.put("detailList", result);
        map.put("introBoardName", boardName);

        return map;
    }

    public List<BoardMasterDTO> selectBoardDetailList(BoardMasterDTO dto) {
        List<BoardMasterDTO> boardMasterDTOS = boardMapper.selectBoardDetailList(dto);
        log.info("boardMasterDTOS = " + boardMasterDTOS);

        if (boardMasterDTOS == null) {
            throw new CmntException(ErrorCode.DB_ERROR);
        }
        return boardMasterDTOS;
    }

    public BoardMasterDTO selectXTHBoardDetail(int boardDSeq) {

        ArrayList<BoardMasterDTO> bMDtoList = boardMapper.selectXTHBoardDetail(boardDSeq);
        if(bMDtoList.isEmpty()){
            throw new CmntException(ErrorCode.BOARD_NOT_FOUND);
        }

        StringBuffer rtSB = new StringBuffer();
         for (BoardMasterDTO rsDto : bMDtoList) {
                rtSB.append(rsDto.getBoardContents());
            }

        BoardMasterDTO boardMasterDto = bMDtoList.get(0);
        boardMasterDto.setBoardContents(rtSB.toString());

        return boardMasterDto;
    }


    public int insertXTHBoardDetail(BoardMasterDTO boardMasterDto) {
        StringBuffer brdContSB = new StringBuffer(boardMasterDto.getBoardContents());
        int rtnInt = -1;
        int insertCnt = 1;
        int maxStrLength = 1500;

        int dSeq = boardMapper.selectNextBoardDSeq();
        boardMasterDto.setBoardDSeq(dSeq);
        int maxLoop = brdContSB.length() / maxStrLength + 1;

        for (int i = 0; i < maxLoop; i++) {
            if (brdContSB.length() < maxStrLength) {
                maxStrLength = brdContSB.length();
            }
            String xcont = brdContSB.substring(0, maxStrLength);
            if (xcont.equals("")) {
                continue;
            }

            BoardMasterDTO brdDto = new BoardMasterDTO();
            brdDto.setBoardDSeq(boardMasterDto.getBoardDSeq());
            brdDto.setBoardMSeq(boardMasterDto.getBoardMSeq());
            brdDto.setBoardTitle(boardMasterDto.getBoardTitle());
            brdDto.setBoardContents(xcont);
            brdDto.setBoardPSeq(boardMasterDto.getBoardPSeq());
            brdDto.setBoardFSeq(boardMasterDto.getBoardFSeq());
            brdDto.setBoardTopYn(boardMasterDto.getBoardTopYn());
            brdDto.setCreateUserId(boardMasterDto.getCreateUserId());
            brdDto.setXth(insertCnt);

            rtnInt = boardMapper.insertXTHBoardDetail(brdDto);
            if(rtnInt == 0){
                throw new CmntException(ErrorCode.DB_ERROR);
            }

            brdContSB = new StringBuffer(brdContSB.substring(maxStrLength, brdContSB.length()));
            insertCnt++;
        }

        if (rtnInt > 0) {
            rtnInt = insertCnt;
        }

        return rtnInt;
    }


    public int updateXTHBoardDetail(BoardMasterDTO boardMasterDto) {
        StringBuffer brdContSB = new StringBuffer(boardMasterDto.getBoardContents());
        int rtnInt = -1;
        int insertCnt = 1;
        int maxStrLength = 1500;

        ArrayList<BoardMasterDTO> bMDtoList = boardMapper.selectXTHBoardDetail(boardMasterDto.getBoardDSeq());
        if(bMDtoList == null || bMDtoList.isEmpty()){
            throw new CmntException(ErrorCode.BOARD_NOT_FOUND);
        }

        BoardMasterDTO prevBMDto = bMDtoList.get(0);

        int maxLoop = brdContSB.length() / maxStrLength + 1;

        for (int i = 0; i < maxLoop; i++) {
            if (brdContSB.length() < maxStrLength) {
                maxStrLength = brdContSB.length();
            }
            String xcont = brdContSB.substring(0, maxStrLength);

            BoardMasterDTO brdDto = new BoardMasterDTO();
            brdDto.setBoardDSeq(prevBMDto.getBoardDSeq());
            brdDto.setBoardTitle(boardMasterDto.getBoardTitle());
            brdDto.setBoardContents(xcont);
            brdDto.setBoardFSeq(boardMasterDto.getBoardFSeq());
            brdDto.setBoardTopYn(boardMasterDto.getBoardTopYn());
            brdDto.setModifyUserId(boardMasterDto.getModifyUserId());
            brdDto.setXth(insertCnt);

            if (i < bMDtoList.size()) {
                rtnInt = boardMapper.updateXTHBoardDetail(brdDto);
            } else {
                brdDto.setBoardMSeq(prevBMDto.getBoardMSeq());
                brdDto.setBoardPSeq(prevBMDto.getBoardPSeq());
                brdDto.setBoardCount(prevBMDto.getBoardCount());
                brdDto.setCreateUserId(prevBMDto.getCreateUserId());
                brdDto.setCreateDate(prevBMDto.getCreateDate());

                rtnInt = boardMapper.appendXTHBoardDetail(brdDto);
            }

            brdContSB = new StringBuffer(brdContSB.substring(maxStrLength, brdContSB.length()));
            insertCnt++;
        }

        if (bMDtoList.size() >= maxLoop) {
            for (int i = maxLoop; i < bMDtoList.size(); i++) {
                BoardMasterDTO brdDto = new BoardMasterDTO();
                brdDto.setBoardDSeq(prevBMDto.getBoardDSeq());
                brdDto.setBoardTitle(boardMasterDto.getBoardTitle());
                brdDto.setBoardContents(" ");
                brdDto.setBoardFSeq(boardMasterDto.getBoardFSeq());
                brdDto.setBoardTopYn(boardMasterDto.getBoardTopYn());
                brdDto.setModifyUserId(boardMasterDto.getModifyUserId());
                brdDto.setXth(i + 1);

                rtnInt = boardMapper.updateXTHBoardDetail(brdDto);
            }
        }
        if (rtnInt > 0) {
            rtnInt = insertCnt;
        }

        return rtnInt;
    }

    public BoardMasterDTO selectBoardDetail(int boardDSeq) {
        return boardMapper.selectBoardDetail(boardDSeq);
    }

    public List<BoardMasterDTO> selectBoardTopList(int boardMSeq) {
        return boardMapper.selectBoardTopList(boardMSeq);
    }

    public int boardTotalCount(BoardMasterDTO dto) {
        return boardMapper.boardTotalCount(dto);
    }

    public List<BoardMasterDTO> introBoardList(BoardMasterDTO dto) {
        return boardMapper.introBoardList(dto);
    }

    public int deleteBoardDetail(BoardMasterDTO dto) {
        return boardMapper.deleteBoardDetail(dto);
    }

    public int updateBoardDetailHitCount(int boardDSeq) {
        return boardMapper.updateBoardDetailHitCount(boardDSeq);
    }

    public FileMasterDTO fileSelect(int boardFSeq) {
        return boardMapper.fileSelect(boardFSeq);
    }

    public List<BoardMasterDTO> selectBoardDetailComments(int boardDSeq) {
        return boardMapper.selectBoardDetailComments(boardDSeq);
    }

    public int insertComment(BoardMasterDTO dto) {
        return boardMapper.insertComment(dto);
    }

    public int updateComment(BoardMasterDTO dto) {
        return boardMapper.updateComment(dto);
    }

    public int deleteComment(BoardMasterDTO dto) {
        return boardMapper.deleteComment(dto);
    }
}


