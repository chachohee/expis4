package com.expis.community.facade;

import com.expis.common.file.FileMaster;
import com.expis.common.paging.PagingDTO;
import com.expis.community.common.exception.CmntException;
import com.expis.community.common.exception.ErrorCode;
import com.expis.community.common.util.SessionUtil;
import com.expis.community.dto.*;
import com.expis.community.service.*;
import com.expis.manage.dto.SystemOptionDto;
import com.expis.manage.service.SystemOptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.expis.common.CommonConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class BoardFacade {
    private final BoardMasterService boardMasterService;
    private final BoardDetailService boardDetailService;
    private final FileMasterService fileMasterService;
    private final SystemOptionService systemOptionService;

    @Value("${app.expis.recordCnt}")
    private int recordCnt;
    @Value("${app.expis.filePath}")
    private String filePath;

    /**
     * 게시판 메인 페이지 조회
     * 게시판 옵션, 메뉴 목록, 상단 고정글 목록을 조회하여 메인 페이지 구성 데이터를 반환
     *
     * @param boardMSeq 게시판 마스터 번호
     * @return 뷰페이지를 위한 CmntViewDTO
     */
    public CmntViewDTO getBoardMainPage(int boardMSeq, HttpSession session) {

        try {
            // 1. 요청 파라미터 값 로컬 변수화
            String userId = SessionUtil.getUserId(session);

            // 2. 게시판 정보 조회
            SystemOptionDto systemOptionDto = systemOptionService.selectOptionAll();
            List<BoardMasterDTO> menu = boardMasterService.selectBoardMasterList();
            List<BoardMasterDTO> topList = boardDetailService.selectBoardTopList(boardMSeq);

            // 3. 응답 객체 구성
            return CmntViewDTO.builder()
                    .boardMList(menu)
                    .topList(topList)
                    .option(systemOptionDto)
                    .userId(userId)
                    .build();

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.BOARD_LIST_FAIL);
        }
    }

    /**
     * 게시판 메인 페이지 조회
     * 메뉴 목록을 기반으로 소개글 리스트를 조회하여 Map 형태로 반환
     *
     * @return 메인 리스트가 포함된 Map
     */
    public Map<String, Object> getIntroList() {

        Map<String, Object> map = new HashMap<>();

        try {
            List<BoardMasterDTO> menuList = boardMasterService.selectBoardMasterList();
            map.put("introList", boardDetailService.introBoardList(menuList));
            return map;

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.BOARD_LIST_FAIL, e);
        }
    }

    /**
     * 게시판 리스트 페이지용 데이터 조회
     * 검색 조건, 페이징, 상단 고정글, 게시글 리스트, 게시판 옵션 등을 조회하여 페이지 데이터를 반환
     *
     * @param cmntViewDTO 게시판 파라미터
     * @param session 세션 정보
     * @return 게시판 리스트 페이지 DTO
     */
    public CmntViewDTO getBoardList(CmntViewDTO cmntViewDTO, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        if (cmntViewDTO.getBoardMSeq() == 0) {
            cmntViewDTO.setBoardMSeq(1);

        } else {
            session.setAttribute(BOARD_M_SEQ, cmntViewDTO.getBoardMSeq());
        }

        int boardMSeq = SessionUtil.getBoardMSeq(session);
        int nPage = cmntViewDTO.getNPage();
        String userId = SessionUtil.getUserId(session);
        String roleCode = SessionUtil.getRoleCode(session);

        try {
            // 2. 게시판 검색 조건 DTO 구성
            BoardMasterDTO boardMasterDTO = CmntViewDTO.ofBoard(cmntViewDTO);
            boardMasterDTO.setCreateUserId(userId);
            boardMasterDTO.setStatusKind(ACTIVE);
            boardMasterDTO.setRecordCnt(recordCnt);

            // 3. 페이징네이션 및 검색
            PagingDTO pagingData = boardDetailService.createPaging(boardMasterDTO);

            // 4. 게시글 리스트 조회
            List<BoardMasterDTO> topList = boardDetailService.selectBoardTopList(boardMSeq);
            List<BoardMasterDTO> boardList = boardDetailService.selectBoardDetailList(boardMasterDTO);
            BoardMasterDTO boardOption = boardMasterService.boardMOption(boardMasterDTO);
            List<BoardMasterDTO> boardMList = boardMasterService.selectBoardMasterList();

            // 5. 응답 객체 구성
            return CmntViewDTO.builder()
                    .nPage(nPage)
                    .userId(userId)
                    .roleCode(roleCode)
                    .boardMSeq(boardMSeq)
                    .topList(topList)
                    .boardList(boardList)
                    .boardOption(boardOption)
                    .boardMList(boardMList)
                    .pagingDTO(pagingData)
                    .build();

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.BOARD_LIST_FAIL, e);
        }
    }

    /**
     * 게시판 상세 페이지 데이터를 조회
     * 게시글 상세 정보, 첨부파일, 댓글, 게시판 옵션 및 메뉴 데이터를 통합하여 상세 페이지 구성을 위한 데이터를 반환
     *
     * @param cmntViewDTO 게시판 파라미터
     * @param session 세션 정보
     * @return 게시판 상세 페이지 CmntViewDTO
     */
    public CmntViewDTO getBoardDetail(CmntViewDTO cmntViewDTO, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        CmntViewDTO result = new CmntViewDTO();
        int boardMSeq = SessionUtil.getBoardMSeq(session);
        int nPage = cmntViewDTO.getNPage();
        String roleCode = SessionUtil.getRoleCode(session);
        String userId = SessionUtil.getUserId(session);

        BoardMasterDTO boardMasterDTO = CmntViewDTO.ofBoard(cmntViewDTO);

        try {
            // 2. 게시글 상세 리스트 조회
            BoardMasterDTO detailData = boardDetailService.selectXTHBoardDetail(boardMasterDTO.getBoardDSeq());
            boardDetailService.updateBoardDetailHitCount(boardMasterDTO.getBoardDSeq());

            List<BoardMasterDTO> commentList = boardDetailService.selectBoardDetailComments(boardMasterDTO.getBoardDSeq());

            boardMasterDTO.setBoardMSeq(boardMSeq);
            BoardMasterDTO boardOption = boardMasterService.boardMOption(boardMasterDTO);

            FileMasterDTO fileDto = boardDetailService.fileSelect(detailData.getBoardFSeq());
            if (fileDto != null) {
                result.setFile(fileDto);
            }
            List<BoardMasterDTO> menuList = boardMasterService.selectBoardMasterList();

            // 3. 응답 객체 구성
            return CmntViewDTO.builder()
                    .nPage(nPage)
                    .boardClass(boardOption.getClassName())
                    .boardOption(boardOption)
                    .boardMList(menuList)
                    .boardName(boardOption.getBoardName())
                    .commentList(commentList)
                    .detailData(detailData)
                    .userId(userId)
                    .roleCode(roleCode)
                    .build();

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.BOARD_NOT_FOUND, e);
        }
    }

    /**
     * 게시판 작성 페이지 데이터를 조회
     * 게시글 번호와 게시판 번호를 기반으로 새 글 작성 또는 덧글 작성 여부를 판별하고,
     * 게시판 옵션, 메뉴 리스트, 게시글 타입 등을 조회하여 작성 페이지 구성을 위한 데이터를 반환
     *
     * @param cmntViewDTO 게시판 및 게시글 식별 정보 (게시판 번호, 게시글 번호 등)
     * @return 게시판 작성 페이지용 CmntViewDTO
     */
    public CmntViewDTO getBoardWrite(CmntViewDTO cmntViewDTO, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        int boardMSeq = SessionUtil.getBoardMSeq(session);
        String roleCode = SessionUtil.getRoleCode(session);
        String userId = SessionUtil.getUserId(session);
        String boardClass;
        String kind;

        BoardMasterDTO boardMasterDTO = CmntViewDTO.ofBoard(cmntViewDTO);

        // 2. 게시글 유무에 따른 덧글 작성 여부
        if (boardMasterDTO.getBoardDSeq() != 0) {
            kind = REPLY;
        } else {
            kind = NEW;
        }

        boardMasterDTO.setBoardMSeq(boardMSeq);

        try {
            // 3. 게시글 상세 조회
            BoardMasterDTO boardOption = boardMasterService.boardMOption(boardMasterDTO);
            if (boardOption == null) {
                throw new CmntException(ErrorCode.BOARD_NOT_FOUND);
            }
            List<BoardMasterDTO> menuList;
            menuList = boardMasterService.selectBoardMasterList();

            // 4. 게시글 타입 선택
            if (kind.equals(NEW)) {
                boardClass = boardOption.getClassName();
            } else {
                boardClass = DEFAULT;
            }

            // 5. 응답 객체 구성
            return CmntViewDTO.builder()
                    .boardMList(menuList)
                    .boardOption(boardOption)
                    .boardClass(boardClass)
                    .nPage(boardMasterDTO.getNPage())
                    .boardDSeq(boardMasterDTO.getBoardDSeq())
                    .boardKind(kind)
                    .boardMSeq(boardMSeq)
                    .roleCode(roleCode)
                    .userId(userId)
                    .fileType(cmntViewDTO.isFileType())
                    .boardPSeq(boardMasterDTO.getBoardPSeq())
                    .build();

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.BOARD_CREATE_FAIL, e);
        }
    }

    /**
     * 게시판 글 등록을 처리
     * 게시글 정보 및 첨부파일(선택)을 등록하며, 덧글인 경우 부모 글 번호를 세팅하여 등록
     *
     * @param cmntViewDTO 게시글 등록 정보 (제목, 내용, 게시판 번호 등)
     * @param session 사용자 세션 (작성자 ID 추출)
     * @param mult 첨부파일 정보가 포함된 Multipart 객체
     * @return 등록 결과 DTO (현재 페이지 정보 포함)
     */
    public CmntViewDTO getBoardInsert(CmntViewDTO cmntViewDTO, HttpSession session, MultipartHttpServletRequest mult) {

        // 1. 요청 파라미터 값 로컬 변수화
        CmntViewDTO result = new CmntViewDTO();
        int boardMSeq = SessionUtil.getBoardMSeq(session);
        String userId = SessionUtil.getUserId(session);

        BoardMasterDTO boardMasterDTO = CmntViewDTO.ofBoard(cmntViewDTO);
        boardMasterDTO.setBoardMSeq(boardMSeq);

        // 2. 게시글 상단 유무 여부 판단 및 게시글 설정
        if (cmntViewDTO.getBoardTopYn().equals(YN_YES)) {
            boardMasterDTO.setBoardTopYn(YN_YES);
        }

        boardMasterDTO.setCreateUserId(userId);

        if (boardMasterDTO.getBoardDSeq() != 0) {
            boardMasterDTO.setBoardPSeq(boardMasterDTO.getBoardDSeq());
            boardMasterDTO.setBoardDSeq(0);
        }

        try {
            // 3. 파일 등록
            int fileSeq = fileMasterService.handleFileUpload(mult,boardMasterDTO::setBoardFSeq);
            boardMasterDTO.setBoardFSeq(fileSeq);

            // 4. 게시글 작성
            boardDetailService.insertXTHBoardDetail(boardMasterDTO);
            result.setBoardMSeq(boardMSeq);
            result.setNPage(cmntViewDTO.getNPage());
            result.setUserId(userId);

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.BOARD_CREATE_FAIL, e);
        }

        // 5. 응답 객체 구성
        return result;
    }

    /**
     * 게시판 수정 페이지 데이터를 조회
     * 수정 대상 게시글 상세 정보, 게시판 메뉴, 옵션 데이터를 조회하여
     * 수정 페이지 구성을 위한 데이터를 반환
     *
     * @param cmntViewDTO 게시판 및 게시글 식별 정보
     * @param session 사용자 세션 (수정 모드 플래그 저장)
     * @return 게시판 수정 페이지용 CmntViewDTO
     */
    public CmntViewDTO getBoardModify(CmntViewDTO cmntViewDTO, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        String roleCode = SessionUtil.getRoleCode(session);
        String userId = SessionUtil.getUserId(session);
        boolean fileType = true;
        BoardMasterDTO boardMasterDTO = CmntViewDTO.ofBoard(cmntViewDTO);

        session.setAttribute(BOARD_KIND, MODIFY);

        try {
            // 2. 게시글 상세 조회
            BoardMasterDTO detailData = boardDetailService.selectXTHBoardDetail(cmntViewDTO.getBoardDSeq());
            List<BoardMasterDTO> menuList;
            menuList = boardMasterService.selectBoardMasterList();
            boardMasterDTO.setBoardMSeq(cmntViewDTO.getBoardMSeq());
            BoardMasterDTO boardOption = boardMasterService.boardMOption(boardMasterDTO);

            // 3. 응답 객체 구성
            return CmntViewDTO.builder()
                    .nPage(boardMasterDTO.getNPage())
                    .boardClass(boardMasterDTO.getClassName())
                    .boardOption(boardOption)
                    .boardMList(menuList)
                    .boardKind(MODIFY)
                    .fileType(fileType)
                    .boardPSeq(boardMasterDTO.getBoardPSeq())
                    .detailData(detailData)
                    .boardDSeq(detailData.getBoardDSeq())
                    .roleCode(roleCode)
                    .userId(userId)
                    .build();

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.BOARD_MODIFY_FAIL, e);
        }
    }

    /**
     * 게시판 글 수정을 처리
     *
     * 게시글 정보 및 첨부파일(선택)을 수정하며, 파일이 변경된 경우 기존 파일을 삭제하고 새 파일을 업로드
     * 작성자 ID는 세션에서 가져오며, 게시글 데이터는 CmntViewDTO에서 변환된 BoardMasterDTO를 기반으로 처리
     *
     * @param cmntViewDTO 게시글 수정 데이터
     * @param session 사용자 세션
     * @param mult 첨부파일 정보가 포함된 Multipart 요청 객체
     * @return 수정 결과 DTO (게시글 번호, 게시판 번호, 현재 페이지 정보 포함)
     */
    public CmntViewDTO getBoardUpdate(CmntViewDTO cmntViewDTO, HttpSession session, MultipartHttpServletRequest mult) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);
        BoardMasterDTO boardMasterDTO = CmntViewDTO.ofBoard(cmntViewDTO);

        try {
            // 2. 파일 업로드 처리
            int fileSeq = fileMasterService.handleFileUpload(mult, boardMasterDTO::setBoardFSeq);

            // 3. 게시글 세팅
            boardMasterDTO.setBoardFSeq(fileSeq);
            boardMasterDTO.setModifyUserId(userId);
            boardDetailService.updateXTHBoardDetail(boardMasterDTO);

            // 4. 응답 객체 구성
            return CmntViewDTO.builder()
                    .boardDSeq(boardMasterDTO.getBoardDSeq())
                    .boardMSeq(boardMasterDTO.getBoardMSeq())
                    .nPage(boardMasterDTO.getNPage())
                    .build();

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.BOARD_MODIFY_FAIL, e);
        }
    }

    /**
     * 게시판 글 삭제
     * 게시글 번호를 기반으로 게시글을 삭제하며, 첨부파일이 존재할 경우 파일도 함께 삭제
     *
     * @param cmntViewDTO 삭제할 게시글 정보 (게시글 번호, 첨부파일 번호 등)
     * @param session 사용자 세션 (작성자/수정자 ID 추출)
     */
    public void getBoardDelete(CmntViewDTO cmntViewDTO, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);
        FileMaster fileMethod = new FileMaster();
        BoardMasterDTO boardMasterDTO = CmntViewDTO.ofBoard(cmntViewDTO);
        boardMasterDTO.setCreateUserId(userId);
        boardMasterDTO.setModifyUserId(userId);

        try {
            // 2. 게시글 삭제
            if (boardMasterDTO.getBoardFSeq() != 0) {
                fileMethod.fileDelete(fileMasterService.fileSelect(boardMasterDTO.getBoardFSeq()));
            }
            boardDetailService.deleteBoardDetail(boardMasterDTO);

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.BOARD_DELETE_FAIL, e);
        }
    }

    /**
     * 게시판 댓글 등록을 처리
     * 게시글 번호 및 댓글 내용을 기반으로 댓글을 등록하며,
     * 작성자 ID는 세션에서 가져옴
     *
     * @param cmntViewDTO 댓글 등록 정보 (게시판 번호, 게시글 번호, 댓글 내용 등)
     * @param session 사용자 세션 (작성자 ID 추출)
     * @return 등록 결과 DTO (게시판 번호, 게시글 번호, 현재 페이지 정보 포함)
     */
    public CmntViewDTO getBoardCommentInsert(CmntViewDTO cmntViewDTO, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);
        BoardMasterDTO boardMasterDTO = CmntViewDTO.ofComment(cmntViewDTO);
        boardMasterDTO.setCreateUserId(userId);

        try {
            // 2. 댓글 작성
            boardDetailService.insertComment(boardMasterDTO);

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.COMMENT_CREATE_FAIL, e);
        }

        // 3. 응답 객체 구성
        return CmntViewDTO.builder()
                .boardDSeq(boardMasterDTO.getBoardDSeq())
                .boardMSeq(boardMasterDTO.getBoardMSeq())
                .nPage(boardMasterDTO.getNPage())
                .build();
    }

    public CmntViewDTO getBoardCommentUpdate(CmntViewDTO cmntViewDTO, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);
        BoardMasterDTO boardMasterDTO = CmntViewDTO.ofComment(cmntViewDTO);
        boardMasterDTO.setModifyUserId(userId);

        try {
            // 2. 댓글 수정
            boardDetailService.updateComment(boardMasterDTO);

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.COMMENT_MODIFY_FAIL);
        }

        // 3. 응답 객체 구성
        return CmntViewDTO.builder()
                .boardDSeq(boardMasterDTO.getBoardDSeq())
                .boardMSeq(boardMasterDTO.getBoardMSeq())
                .nPage(boardMasterDTO.getNPage())
                .build();
    }

    /**
     * 게시판 댓글 수정을 처리
     * 댓글 번호 및 수정 내용을 기반으로 댓글을 수정
     *
     * @param cmntViewDTO 댓글 수정 정보 (게시판 번호, 게시글 번호, 댓글 번호, 수정 내용 등)
     * @param session 사용자 세션 (수정자 ID 추출)
     * @return 수정 결과 DTO (게시판 번호, 게시글 번호, 현재 페이지 정보 포함)
     */
    public CmntViewDTO getBoardCommentDelete(CmntViewDTO cmntViewDTO, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);
        BoardMasterDTO boardMasterDTO = CmntViewDTO.ofComment(cmntViewDTO);
        boardMasterDTO.setModifyUserId(userId);

        try {
            // 2. 댓글 삭제
            boardDetailService.deleteComment(boardMasterDTO);

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.COMMENT_DELETE_FAIL, e);
        }

        // 3. 응답 객체 구성
        return CmntViewDTO.builder()
                .boardDSeq(boardMasterDTO.getBoardDSeq())
                .boardMSeq(boardMasterDTO.getBoardMSeq())
                .nPage(boardMasterDTO.getNPage())
                .build();
    }

    /**
     * 게시판 파일 다운로드 처리
     * 파일 식별 번호를 기반으로 파일 정보를 조회하고,
     * 요청 및 응답 객체를 통해 파일 다운로드
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체 (파일 다운로드 스트림 처리)
     * @param fileSeq 다운로드할 파일의 식별 번호
     */
    public void getDownload(HttpServletRequest request, HttpServletResponse response, int fileSeq) {

        // 1. 요청 파라미터 값 로컬 변수화
        FileMaster fileMethod = new FileMaster();

        try {
            // 2. 파일 선택
            FileMasterDTO fileMasterDTO = boardDetailService.fileSelect(fileSeq);

            // 3. 다운로드
            fileMethod.fileDownload(response, request, fileMasterDTO, filePath);

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.FILE_DOWNLOAD_FAIL, e);
        }
    }
}
