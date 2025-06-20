package com.expis.manage.facade;

import com.expis.common.CommonConstants;
import com.expis.common.file.FileMaster;
import com.expis.common.paging.PagingDTO;
import com.expis.community.common.util.SessionUtil;
import com.expis.community.dto.BoardMasterDTO;
import com.expis.community.dto.FileMasterDTO;
import com.expis.community.dto.RelatedDTO;
import com.expis.community.service.BoardDetailService;
import com.expis.community.service.BoardMasterService;
import com.expis.community.service.FileMasterService;
import com.expis.community.service.RelatedSiteService;
import com.expis.manage.dto.BoardManagerViewDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.util.List;

import static com.expis.common.CommonConstants.YN_YES;

@Slf4j
@RequiredArgsConstructor
@Component
public class BoardManagerFacade {
    private final BoardMasterService boardMasterService;
    private final BoardDetailService boardDetailService;
    private final RelatedSiteService relatedSiteService;
    private final FileMasterService fileMasterService;

    /**
     * 관리자 게시판 목록
     * 게시판 정보를 조회하여 페이지 데이터를 반환
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @return 뷰페이지를 위한 BoardManagerViewDto
     */
    public BoardManagerViewDto getBoardAllBoard(BoardManagerViewDto boardManagerViewDto) {

        // 1. 요청 파라미터 값 로컬 변수화
        BoardMasterDTO boardMasterDTO = BoardManagerViewDto.ofBoardM(boardManagerViewDto);
        PagingDTO pagingData = boardDetailService.createPaging(boardMasterDTO);

        // 2. 전체 게시판 갯수 및 목록 조회
        int totCnt = boardMasterService.boardMCount(boardMasterDTO);
        List<BoardMasterDTO> allBoardMList = boardMasterService.selectAllBoardMaster(boardMasterDTO);

        // 3. 응답 객체 구성
        return BoardManagerViewDto.builder()
                .nPage(boardManagerViewDto.getNPage())
                .active(boardManagerViewDto.getSort())
                .sortKind(boardManagerViewDto.getSortKind())
                .totCnt(totCnt)
                .allBoardMList(allBoardMList)
                .pagingDTO(pagingData)
                .build();
    }

    /**
     * 관리자 게시판 수정
     * 전달받은 게시판 정보 DTO를 기반으로 게시판 수정
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @param session 사용자 세션 (작성자 ID 추출)
     */
    public void getBoardMUpdate(BoardManagerViewDto boardManagerViewDto, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);

        BoardMasterDTO boardMasterDTO = BoardManagerViewDto.ofBoardM(boardManagerViewDto);
        boardMasterDTO.setCreateUserId(userId);
        boardMasterDTO.setModifyUserId(userId);

        if (boardMasterDTO.getClassName() == null) {
            boardMasterDTO.setClassName(boardMasterDTO.getBoardType());
        }

        // 2. 게시판 수정
        boardMasterService.updateBoardMaster(boardMasterDTO);
    }

    /**
     * 관리자 게시판 등록
     * 전달받은 게시판 정보 DTO를 기반으로 게시판 등록
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @param session 사용자 세션 (작성자 ID 추출)
     */
    public void getBoardMInsert(BoardManagerViewDto boardManagerViewDto, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);

        BoardMasterDTO boardMasterDTO = BoardManagerViewDto.ofBoardM(boardManagerViewDto);
        boardMasterDTO.setCreateUserId(userId);
        boardMasterDTO.setModifyUserId(userId);

        if (boardMasterDTO.getClassName() == null) {
            boardMasterDTO.setClassName(boardMasterDTO.getBoardType());
        }

        // 2. 게시글 수정
        boardMasterService.insertBoardMaster(boardMasterDTO);
    }

    /**
     * 관리자 게시판 삭제
     * 전달받은 게시판 정보 DTO를 기반으로 게시판 삭제
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @param session 사용자 세션 (작성자 ID 추출)
     */
    public void getBoardMDelete(BoardManagerViewDto boardManagerViewDto, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);

        BoardMasterDTO boardMasterDTO = BoardManagerViewDto.ofBoardM(boardManagerViewDto);
        boardMasterDTO.setCreateUserId(userId);
        boardMasterDTO.setModifyUserId(userId);

        // 2. 게시글 삭제
        boardMasterService.deleteBoardMaster(boardMasterDTO);
    }

    /**
     * 활성화 된 게시판 데이터를 조회
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @return 뷰페이지를 위한 BoardManagerViewDto
     */
    public BoardManagerViewDto getBoard(BoardManagerViewDto boardManagerViewDto) {

        // 1. 요청 파라미터 값 로컬 변수화
        BoardMasterDTO boardMasterDTO = BoardManagerViewDto.ofBoardM(boardManagerViewDto);

        // 2. 게시글 리스트 조회
        List<BoardMasterDTO> menuMList = boardMasterService.selectBoardMaster(boardMasterDTO);

        // 3. 응답 객체 구성
        return BoardManagerViewDto.builder()
                .menuMList(menuMList)
                .active(boardManagerViewDto.getActive())
                .build();
    }

    /**
     * 게시판 정렬 순서 수정
     * 전달받은 게시판 정보 DTO를 기반으로 게시판 정렬 순서 수정
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @param session 사용자 세션 (작성자 ID 추출)
     */
    public void getIndexUpdate(BoardManagerViewDto boardManagerViewDto, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);

        BoardMasterDTO boardMasterDTO = BoardManagerViewDto.ofBoardM(boardManagerViewDto);
        boardMasterDTO.setCreateUserId(userId);
        boardMasterDTO.setModifyUserId(userId);

        // 2. 정렬 순서 수정
        boardMasterService.updateBoardNum(boardMasterDTO);
    }

    /**
     * 활성화된 게시판의 상세 정보를 조회
     * 전달받은 게시판 마스터 번호와 상태 코드를 기준으로 게시판의 게시글 목록 조회
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @return 뷰페이지를 위한 BoardManagerViewDto
     */
    public BoardManagerViewDto getActivateBoard(BoardManagerViewDto boardManagerViewDto, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        int boardMSeq = boardManagerViewDto.getBoardMSeq();
        int nPage = boardManagerViewDto.getNPage();
        BoardMasterDTO boardMasterDTO = BoardManagerViewDto.ofBoardM(boardManagerViewDto);

        // 3. 게시판 리스트 조회
        List<BoardMasterDTO> boardMasterList = boardMasterService.boardMasterSelect(boardManagerViewDto.getStatusKind());

        // 3. boardMSeq 변수 설정
        if(boardMSeq == 0 && !boardMasterList.isEmpty()) {
            boardMSeq = boardMasterList.getFirst().getBoardMSeq();
            boardMasterDTO.setBoardMSeq(boardMSeq);
            session.setAttribute(CommonConstants.BOARD_M_SEQ, boardMSeq);
        }else {
            session.setAttribute(CommonConstants.BOARD_M_SEQ, boardMSeq);
        }

        // 4. 페이징네이션
        PagingDTO pagingData = boardDetailService.createPaging(boardMasterDTO);

        // 5. 활성화 된 게시판 데이터 조회
        int totCnt = boardDetailService.boardTotalCount(boardMasterDTO);
        List<BoardMasterDTO> boardList = boardDetailService.selectBoardDetailList(boardMasterDTO);
        BoardMasterDTO boardOption = boardMasterService.boardMOption(boardMasterDTO);

        // 6. 응답 객체 구성
        return BoardManagerViewDto.builder()
                .boardMSeq(boardMSeq)
                .nPage(nPage)
                .boardOption(boardOption)
                .boardList(boardList)
                .boardMasterList(boardMasterList)
                .pagingDTO(pagingData)
                .statusKind(boardManagerViewDto.getStatusKind())
                .active(boardManagerViewDto.getActive())
                .totCnt(totCnt)
                .build();
    }

    /**
     * 활성화된 게시판의 특정 게시글 상세 정보를 조회
     * 게시글 번호(boardDSeq)를 기준으로 정보를 조회하여 반환
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @return 뷰페이지를 위한 BoardManagerViewDto
     */
    public BoardManagerViewDto getActivateBoardDetail(BoardManagerViewDto boardManagerViewDto, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        int boardMSeq = SessionUtil.getBoardMSeq(session);
        int nPage = boardManagerViewDto.getNPage();
        BoardMasterDTO boardMasterDTO = BoardManagerViewDto.ofBoardM(boardManagerViewDto);
        FileMasterDTO fileDto = new FileMasterDTO();

        // 2. 게시글 리스트 조회
        BoardMasterDTO boardDetail = boardDetailService.selectBoardDetail(boardMasterDTO.getBoardDSeq());
        if (boardDetail.getBoardFSeq() != 0) {
            fileDto = boardDetailService.fileSelect(boardDetail.getBoardFSeq());
        }
        List<BoardMasterDTO> commentList = boardDetailService.selectBoardDetailComments(boardManagerViewDto.getBoardDSeq());
        BoardMasterDTO boardOption = boardMasterService.boardMOption(boardMasterDTO);

        // 3. 응답 객체 구성
        return BoardManagerViewDto.builder()
                .boardMSeq(boardMSeq)
                .nPage(nPage)
                .file(fileDto)
                .commentList(commentList)
                .boardOption(boardOption)
                .boardDetail(boardDetail)
                .active(boardManagerViewDto.getActive())
                .statusKind(boardManagerViewDto.getStatusKind())
                .build();
    }

    /**
     * 활성화된 게시판의 특정 게시글 삭제
     * 게시글 번호(boardDSeq)를 기준으로 정보를 조회하여 삭제
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @param session 사용자 세션 (작성자 ID 추출)
     */
    public BoardManagerViewDto getBoardDetailMDelete(BoardManagerViewDto boardManagerViewDto, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);

        BoardMasterDTO boardMasterDTO = BoardManagerViewDto.ofBoardM(boardManagerViewDto);
        FileMaster fileMethod = new FileMaster();
        boardMasterDTO.setCreateUserId(userId);
        boardMasterDTO.setModifyUserId(userId);

        // 2. 게시글 조회
        BoardMasterDTO boardDetail = boardDetailService.selectBoardDetail(boardMasterDTO.getBoardDSeq());

        if (boardDetail.getBoardFSeq() != 0) {
            fileMethod.fileDelete(fileMasterService.fileSelect(boardDetail.getBoardFSeq()));
        }

        // 3. 게시글 삭제
        boardDetailService.deleteBoardDetail(boardMasterDTO);

        return BoardManagerViewDto.builder()
                .boardMSeq(boardDetail.getBoardMSeq())
                .build();
    }

    /**
     * 관련 사이트 목록을 조회
     * 관련 사이트 정보를 기반으로 전체 목록을 조회
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @return 뷰페이지를 위한 BoardManagerViewDto               
     */
    public BoardManagerViewDto getRelatedMSites(BoardManagerViewDto boardManagerViewDto) {

        // 1. 요청 파라미터 값 로컬 변수화
         RelatedDTO relatedDTO = BoardManagerViewDto.ofRelatedM(boardManagerViewDto);

         // 2. 관련사이트 리스트 조회
        int totCnt = relatedSiteService.relatedCount();
        relatedSiteService.createPaging(relatedDTO);
        List<RelatedDTO> relatedList = relatedSiteService.relatedSelect(relatedDTO);

        // 3. 응답 객체 구성
        return BoardManagerViewDto.builder()
                .totCnt(totCnt)
                .relatedList(relatedList)
                .build();
    }

    /**
     * 관련 사이트 등록
     * 전달받은 관련사이트 정보를 DTO를 기반으로 관련사이트 등록
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @param session 사용자 세션 (작성자 ID 추출)
     */
    public void getRelatedMInsert(BoardManagerViewDto boardManagerViewDto, HttpSession session, MultipartHttpServletRequest mult) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);

        RelatedDTO relatedDTO = BoardManagerViewDto.ofRelatedM(boardManagerViewDto);
        relatedDTO.setCreateUserId(userId);
        relatedDTO.setModifyUserId(userId);

        // 2. 파일 등록
        fileMasterService.handleFileUpload(mult, relatedDTO::setFileSeq);

        // 3. 관련사이트 등록
        relatedSiteService.relatedInsert(relatedDTO);
    }

    /**
     * 관련 사이트 수정
     * 전달받은 관련사이트 정보를 DTO를 기반으로 관련사이트 수정
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @param session 사용자 세션 (작성자 ID 추출)
     */
    public void getRelatedMUpdate(BoardManagerViewDto boardManagerViewDto, HttpSession session, MultipartHttpServletRequest mult) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);

        RelatedDTO relatedDTO = BoardManagerViewDto.ofRelatedM(boardManagerViewDto);
        relatedDTO.setCreateUserId(userId);
        relatedDTO.setModifyUserId(userId);

        // 2. 파일 수정
        if(boardManagerViewDto.getFileTranYn().equals(YN_YES)) {
            fileMasterService.handleFileUpload(mult,relatedDTO::setFileSeq);
        }

        // 3. 관련사이트 수정
        relatedSiteService.updateRelated(relatedDTO);
    }

    /**
     * 관련 사이트 삭제
     * 전달받은 관련사이트 정보를 DTO를 기반으로 관련사이트 삭제
     *
     * @param boardManagerViewDto 게시판 마스터 번호
     * @param session 사용자 세션 (작성자 ID 추출)
     */
    public int getRelatedMDelete(BoardManagerViewDto boardManagerViewDto, HttpSession session){

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);

        RelatedDTO relatedDTO = BoardManagerViewDto.ofRelatedM(boardManagerViewDto);
        FileMaster fileMethod = new FileMaster();
        relatedDTO.setCreateUserId(userId);
        relatedDTO.setModifyUserId(userId);

        // 2. 관련사이트 파일 삭제
        if (boardManagerViewDto.getFileSeq() != 0) {
            fileMethod.fileDelete(fileMasterService.fileSelect(boardManagerViewDto.getFileSeq()));
        }

        // 3. 관련사이트 삭제
        return relatedSiteService.deleteRelated(relatedDTO);
    }
}
