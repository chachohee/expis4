package com.expis.community.facade;

import com.expis.common.file.FileMaster;
import com.expis.common.paging.PageDTO;
import com.expis.common.paging.PagingDTO;
import com.expis.community.common.exception.CmntException;
import com.expis.community.common.exception.ErrorCode;
import com.expis.community.common.util.SessionUtil;
import com.expis.community.dto.*;
import com.expis.community.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class RelatedSiteFacade {
    private final BoardMasterService boardMasterService;
    private final BoardDetailService boardDetailService;
    private final FileMasterService fileMasterService;
    private final RelatedSiteService relatedSiteService;

    /**
     * 관련 사이트 리스트 페이지 데이터를 조회.
     * 전체 관련 사이트 목록과 게시판 메뉴를 조회하고,
     * 페이징 처리 데이터를 포함하여 페이지 구성을 위한 데이터를 반환
     *
     * @param cmntViewDTO 페이지 번호 등 관련 사이트 조회 파라미터
     * @return 관련 사이트 리스트 페이지용 CmntViewDTO
     */
    public CmntViewDTO getRelatedList(CmntViewDTO cmntViewDTO, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);
        RelatedDTO relatedDTO = CmntViewDTO.ofRelatedSite(cmntViewDTO);
        PageDTO pageDTO = new PageDTO();

        try {
            int totCnt = relatedSiteService.relatedCount();
            pageDTO.setNowPage(cmntViewDTO.getNPage());
            pageDTO.setTotalCount(totCnt);

            // 2. 전체 관련사이트 조회 및 페이징 처리
            PagingDTO pagingData = relatedSiteService.createPaging(relatedDTO);

            List<RelatedDTO> list = relatedSiteService.relatedCmnt(relatedDTO);
            if (list == null) {
                throw new CmntException(ErrorCode.RELATED_SITE_LIST_EMPTY);
            }

            List<BoardMasterDTO> boardMList = boardMasterService.selectBoardMasterList();
            if (boardMList == null) {
                throw new CmntException(ErrorCode.RELATED_SITE_MENU_NOT_FOUND);
            }

            // 3. 응답 객체 구성
            return CmntViewDTO.builder()
                    .nPage(relatedDTO.getNPage())
                    .boardMList(boardMList)
                    .relatedList(list)
                    .pagingDTO(pagingData)
                    .userId(userId)
                    .build();

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.RELATED_SITE_LIST_FAIL, e);
        }
    }

    /**
     * 관련 사이트 등록을 처리
     * 관련 사이트 정보(제목, URL, 내용 등)를 등록하며,
     * 첨부파일이 있을 경우 파일 업로드 후 파일 정보를 저장
     *
     * @param cmntViewDTO 관련 사이트 등록 데이터
     * @param session 사용자 세션 (작성자/수정자 ID 추출)
     * @param mult 첨부파일 정보가 포함된 Multipart 요청 객체
     */
    public void getRelatedInsert(CmntViewDTO cmntViewDTO, HttpSession session, MultipartHttpServletRequest mult) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);
        RelatedDTO relatedDTO = CmntViewDTO.ofRelatedSite(cmntViewDTO);
        relatedDTO.setCreateUserId(userId);
        relatedDTO.setModifyUserId(userId);

        try {
            // 2. 파일 업로드
            fileMasterService.handleFileUpload(mult, relatedDTO::setFileSeq);

            // 3. 관련사이트 등록
            relatedSiteService.relatedInsert(relatedDTO);

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.RELATED_SITE_CREATE_FAIL);
        }
    }

    /**
     * 관련 사이트 수정을 처리
     * 관련 사이트 정보(제목, URL, 내용 등)를 수정하며,
     * 첨부파일이 변경될 경우 새 파일을 업로드 후 파일 정보를 수정
     *
     * @param cmntViewDTO 수정할 관련 사이트 데이터
     * @param session 사용자 세션 (작성자/수정자 ID 추출)
     * @param mult 첨부파일 정보가 포함된 Multipart 요청 객체
     */
    public void getRelatedUpdate(CmntViewDTO cmntViewDTO, HttpSession session, MultipartHttpServletRequest mult) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session);
        RelatedDTO relatedDTO = CmntViewDTO.ofRelatedSite(cmntViewDTO);
        relatedDTO.setCreateUserId(userId);
        relatedDTO.setModifyUserId(userId);

        try {
            // 2. 파일 유무 확인
            fileMasterService.handleFileUpload(mult, relatedDTO::setFileSeq);

            // 3. 관련사이트 수정
            relatedSiteService.updateRelated(relatedDTO);

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.RELATED_SITE_MODIFY_FAIL);
        }
    }

    /**
     * 관련 사이트 상세 정보를 조회
     * 관련 사이트 번호를 기반으로 상세 정보를 조회하며,
     * 첨부파일이 있을 경우 파일 정보도 함께 조회하여 반환
     *
     * @param cmntViewDTO 관련 사이트 식별 정보 (relSeq)
     * @return 상세보기용 CmntViewDTO (관련 사이트 정보 및 파일 정보 포함)
     */
    public CmntViewDTO getRelatedDetail(CmntViewDTO cmntViewDTO) {
        int relSeq = cmntViewDTO.getRelSeq();
        CmntViewDTO result;

        try {
            // 1. 요청 파라미터 값 로컬 변수화
            result = new CmntViewDTO();

            // 2. 관련사이트 상세보기
            RelatedDTO relatedDto = relatedSiteService.relatedDetail(relSeq);

            FileMasterDTO fileDto = new FileMasterDTO();

            if (relatedDto.getFileSeq() != 0) {
                fileDto = boardDetailService.fileSelect(relatedDto.getFileSeq());
            }

            // 3. 응답 객체 구성
            result.setFile(fileDto);
            result.setRelatedDto(relatedDto);

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.RELATED_SITE_NOT_FOUND);
        }
        return result;
    }

    /**
     * 관련 사이트 삭제
     * 관련 사이트 식별 번호를 기반으로 관련 사이트를 삭제하며,
     * 첨부파일이 존재할 경우 파일도 함께 삭제
     *
     * @param cmntViewDTO 삭제할 관련 사이트 정보 (relSeq, 파일 시퀀스 등)
     * @param session 사용자 세션 (작성자/수정자 ID 추출)
     * @return 삭제된 갯수
     */
    public int getRelatedDelete(CmntViewDTO cmntViewDTO, HttpSession session) {
        int boardFSeq = cmntViewDTO.getBoardFSeq();
        RelatedDTO relatedDTO = CmntViewDTO.ofRelatedSite(cmntViewDTO);
        String userId = SessionUtil.getUserId(session);

        // 1. 요청 파라미터 값 로컬 변수화

        FileMaster fileMethod = new FileMaster();
        relatedDTO.setCreateUserId(userId);
        relatedDTO.setModifyUserId(userId);

        try {
            // 2. 파일 유무 확인
            if (boardFSeq != 0) {
                fileMethod.fileDelete(fileMasterService.fileSelect(boardFSeq));
            }

            // 3. 관련사이트 삭제
            return relatedSiteService.deleteRelated(relatedDTO);

        } catch (CmntException e) {
            throw new CmntException(ErrorCode.RELATED_SITE_DELETE_FAIL);
        }
    }
}
