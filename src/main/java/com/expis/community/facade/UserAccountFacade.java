package com.expis.community.facade;

import com.expis.community.common.util.SessionUtil;
import com.expis.community.dto.BoardMasterDTO;
import com.expis.community.dto.CmntViewDTO;
import com.expis.community.service.BoardMasterService;
import com.expis.user.dto.UserDetailInfoDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserAccountFacade {
    private final BoardMasterService boardMasterService;

    /**
     * 개인 정보 변경 조회
     *
     * @param cmntViewDTO 페이지 번호 등 관련 사이트 조회 파라미터
     * @param session 사용자 세션 (사용자 정보 추출)
     * @return 개인 정보 변경 조회 페이지용 CmntViewDTO
     */
    public CmntViewDTO getUserAccount(CmntViewDTO cmntViewDTO, HttpSession session) {

        // 1. 요청 파라미터 값 로컬 변수화
        String userId = SessionUtil.getUserId(session); //임시
        UserDetailInfoDto userDetailInfo = SessionUtil.getUserDetailInfo(session);
        List<BoardMasterDTO> menuList = boardMasterService.selectBoardMasterList();

        cmntViewDTO.setUserId(userId);
        cmntViewDTO.setBoardMList(menuList);
        cmntViewDTO.setUserDetailInfo(userDetailInfo);

        // 2. 응답 객체 반환
        return cmntViewDTO;

    }
}
