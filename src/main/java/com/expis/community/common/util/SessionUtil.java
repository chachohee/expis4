package com.expis.community.common.util;

import com.expis.common.CommonConstants;
import com.expis.community.common.exception.CmntException;
import com.expis.community.common.exception.ErrorCode;
import com.expis.user.dto.UserDetailInfoDto;
import com.expis.user.dto.UserDto;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    /**
     * Session ID 조회
     * session 매개변수로 LOGIN_MEMBER 조회
     *
     * @param session 사용자 세션 (작성자 ID 추출)
     */
    public static String getUserId(HttpSession session){
        UserDto loginUser = (UserDto) session.getAttribute(CommonConstants.LOGIN_MEMBER);
        if(loginUser == null){
            throw new CmntException(ErrorCode.SESSION_NOT_FOUND);
        }
        return loginUser.getUserId();
    }

    /**
     * Session ID 조회
     * session 매개변수로 BOARD_M_SEQ 조회
     *
     * @param session 사용자 세션 (작성자 ID 추출)
     */
    public static int getBoardMSeq(HttpSession session){
        int boardMSeq = (int) session.getAttribute(CommonConstants.BOARD_M_SEQ);

        if(boardMSeq == 0){
            boardMSeq = 1;
        }
        return boardMSeq;
    }

    /**
     * Session ID 조회
     * session 매개변수로 RoleCode 조회
     *
     * @param session 사용자 세션 (작성자 ID 추출)
     */
    public static String getRoleCode(HttpSession session){
       UserDto userRoleCode = (UserDto) session.getAttribute(CommonConstants.LOGIN_MEMBER);
       if(userRoleCode == null){
           throw new CmntException(ErrorCode.SESSION_NOT_FOUND);
       }
       return userRoleCode.getUserRoleCode();
    }

    /**
     * User 정보조회
     * session 매개변수로 LOGIN_MEMBER 조회
     *
     * @param session 사용자 세션 (작성자 ID 추출)
     */
    public static UserDetailInfoDto getUserDetailInfo(HttpSession session){
        UserDetailInfoDto userDetailInfoDto = new UserDetailInfoDto();

        UserDto loginUser = (UserDto) session.getAttribute(CommonConstants.LOGIN_MEMBER);
        if(loginUser == null){
            throw new CmntException(ErrorCode.SESSION_NOT_FOUND);
        }

        userDetailInfoDto.setUserName(loginUser.getUserName());
        userDetailInfoDto.setUserSn(loginUser.getUserSn());
        userDetailInfoDto.setUserTalent(loginUser.getUserTalent());
        userDetailInfoDto.setUserId(loginUser.getUserId());

        return userDetailInfoDto;
    }
}
