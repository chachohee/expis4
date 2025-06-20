package com.expis.user.service;

import com.expis.ietm.dto.MainDto;
import com.expis.user.dao.UserMapper;
import com.expis.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public UserDto getUserInfo(String loginId) {

        UserDto userDto = new UserDto();
        UserDto findDto = new UserDto();
        findDto.setUserId(loginId);

        try {
            userDto = userMapper.selectUserByUserId(findDto);
            log.info("userDto={}", userDto);
        } catch (Exception e) {
            log.info("exception userDto={}", userDto);
            e.printStackTrace();
        }

        if (userDto != null) {
            return userDto;
        }

        log.info("로그인 실패");
        return null;
    }

    // 로그인 성공 기록
    public void saveLoginConnHistory(UserDto userDto, HttpServletRequest request) {

        try {
            MainDto mainDto = new MainDto();

            String sessionId = request.getSession().getId();

            InetAddress inetAddress = null;
            inetAddress = InetAddress.getLocalHost();

            String strIpAdress = inetAddress.getHostAddress();
            mainDto.setSessionId(sessionId);
            mainDto.setUserId(userDto.getUserId());
            mainDto.setIpAddress(strIpAdress);
            mainDto.setUserPw(userDto.getUserPw());

            // 접속정보를 저장
            userMapper.insertUserId(mainDto);

            // 이전 접속의 로그아웃 타임 저장
            userMapper.updateLogoutTime(mainDto);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }

    // 로그인 실패 기록
    public void saveFailConnHistory(String loginId, HttpServletRequest request) {
        int rInt = 0;

        // 클라이언트 IP 주소
        String connIp = request.getHeader("X-Forwarded-For");
        if (connIp != null && connIp.contains(",")) {
            connIp = connIp.split(",")[0].trim(); // 첫 번째 IP만 가져오기
        }
        if (connIp == null || connIp.isEmpty()) {
            connIp = request.getRemoteAddr();
        }

        MainDto mainDto = new MainDto();
        mainDto.setUserId(loginId);
        mainDto.setIpAddress(connIp);

        try {
            // 접속 실패 기록
            userMapper.insertFailConnHistory(mainDto);

            // 로그인 실패 횟수 확인
            rInt = userMapper.selectCountFailConnHistoryByUserId(mainDto);
            //- log.info("fail count : {}", rInt);

            // 5회 이상일 경우 비활성화 상태로
            if ( rInt >= 5 ) {
                UserDto chkUserDto = new UserDto();
                chkUserDto.setUserId(loginId);
                UserDto userDto = userMapper.selectUserByUserId(chkUserDto);

                if ( userDto != null ) {
                    if ( !"S".equalsIgnoreCase(userDto.getUserRoleCode()) ) {
                        if ( "10".equalsIgnoreCase(userDto.getStatusKind()) ) {
                            userDto.setModifyUserId("SYSTEM");
                            userDto.setStatusKind("20"); 			// 비활성화
                            userMapper.userActivate(userDto);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDisconnDate(String sessionId) {
//        LoginConnHistory loginConnHistory = loginConnHistoryRepository.findBySessionId(sessionId);
//        log.info("loginConnHistory sessionId : " + loginConnHistory.getSessionId());
//        if (loginConnHistory != null) {
//            loginConnHistory.setDisConnDate(LocalDateTime.now());
//            loginConnHistoryRepository.save(loginConnHistory);
//        }
//        log.info("로그인 로그아웃 시간 업뎃 완료");
    }

//    public void loginOut(MainDto dto) {
//        // TODO Auto-generated method stub
//
//        try {
//            //insert log
//            AdminLogDTO logDto =  new AdminLogDTO();
//            logDto.setCreateUserId(dto.getUserId());
//            logDto.setCodeType("1201");
//            glossaryMapper.insertLog(logDto);
//
//            // 편법 방법아님
//            userMapper.loginOutUpdateTest(dto);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public MainDto getDisConnDate(MainDto dto) {

        MainDto mainDto = new MainDto();
        MainDto resultDto = new MainDto();
        List<MainDto> mainList = null;
        int num = 0;
        try {

            String connDate = "";
            String dissConnDate = "";

            mainDto = userMapper.getSLoginIngoTest(dto);
            if(mainDto != null) {
                if(mainDto.getConnDate().isEmpty()) {
                    connDate = "-";
                    dissConnDate = "-";
                } else {
                    connDate = mainDto.getConnDate();
                    log.info("mainDto.getDisconnDate() : " + mainDto.getDisconnDate());
                    dissConnDate = mainDto.getDisconnDate();
                }
            } else {
                connDate = "-";
                dissConnDate = "-";
            }

            log.info("dissConnDate : " + dissConnDate);

            resultDto.setConnDate(connDate);

            if(dissConnDate == null) {
                resultDto.setDisconnDate("");
            } else {
                resultDto.setDisconnDate(dissConnDate);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultDto;
    }

    /**
     * 중복체크
     */
    public UserDto userInfo(UserDto param) {
        log.info("userInfo param={}", param.toString());
        return userMapper.selectUserInfo(param);
    }

    /**
     * 사용자 체크 - 등록대기 중이 아닌 경우 조회 == 비활성화 상태 사용자도 조회 - jingi.kim
     */
    public UserDto userInfoByUserId(UserDto param) {
        return userMapper.selectUserByUserId(param);
    }

    /**
     * 로그인 실패 기록 - jingi.kim
     */
    public void insertFailConnHistory(MainDto dto) {
        try {
            userMapper.insertFailConnHistory(dto);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 로그인 실패 횟수 조회 - jingi.kim
     */
    public int selectCountFailConnHistoryByUserId(MainDto dto) {
        int rint = -1;
        try {
            rint = userMapper.selectCountFailConnHistoryByUserId(dto);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rint;
    }

    /**
     * (정상로그인시) 로그인 실패 횟수 리셋 - jingi.kim
     */
    public void updateFailConnHistoryStatusByUserId(UserDto userDto, HttpServletRequest request) {
        try {
            String connIp = (String) request.getHeader("X-forwarded-For");
            if ( null == connIp || "".equalsIgnoreCase(connIp) ) {
                connIp = request.getRemoteAddr();
            }

            log.info("connIp={}", connIp);
            log.info("request.getRemoteAddr={}", request.getRemoteAddr());

            MainDto fcMainDto = new MainDto();
            fcMainDto.setUserId(userDto.getUserId());
            fcMainDto.setIpAddress(connIp);

            userMapper.updateFailConnHistoryStatusByUserId(fcMainDto);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public List<UserDTO> selectListUserInfo(UserDTO dto) {
//        logger.info("1.ejoo test ------------------------> ");
//        if(!dto.getSearchValue().equals("none")) {
//            AdminLogDTO logDto =  new AdminLogDTO();
//
//            logDto.setCreateUserId(dto.getCreateUserId());
//            logDto.setCodeType("3105");
//            glossaryMapper.insertLog(logDto);
//        }
//        return userMapper.selectListUserInfo(dto);
//    }
//
//    public int insertUserProfile(UserDTO dto) {
//
//        AdminLogDTO logDto =  new AdminLogDTO();
//
//        logDto.setCreateUserId(dto.getCreateUserId());
//        logDto.setCodeType("3101");
//        glossaryMapper.insertLog(logDto);
//        OptionDto oDto = new OptionDto();
//        int count = 0;
//        oDto.setUserId(dto.getCreateUserId());
//        count = optionMapper.selectUserIdCount(oDto);
//
//        if(count == 0) {
//            oDto.setUserId(dto.getUserId());
//            oDto.setFontSize(IConstants.OPT_FONTSIZE_M);
//            oDto.setPrintWordMode(IConstants.OPT_PRINTWORD_DOC);
//            oDto.setExploreMode(IConstants.OPT_EPMODE_ALL);
//            oDto.setOutputMode(IConstants.OPT_OUMODE_MULTI);
//            oDto.setViewMode(IConstants.OPT_VMODE_PAGE);
//            oDto.setFiMode(IConstants.OPT_FIMODE_ALL);
//            oDto.setMobileMenuMode(IConstants.OPT_MOBILE_MENU_CMTO);
//            oDto.setFontFamily(IConstants.OPT_FONT_FAMILY_1);
//            oDto.setCoverType(IConstants.OPT_COVER_1);
//
//            optionMapper.insertOption(oDto);
//        }
//
//        logger.info("oDto : " + oDto.toString());
//        logger.info("dto : " + dto.toString());
//
//        return userMapper.insertUserProfile(dto);
//    }
//
//    public int updateUser(UserDTO dto) {
//        AdminLogDTO logDto =  new AdminLogDTO();
//        logDto.setCreateUserId(dto.getUserId());
//        logDto.setCodeType("2201");
//        dto.setStatusKind("10");
//        glossaryMapper.insertLog(logDto);
//        return userMapper.updateUser(dto);
//    }
//
//    public int userCount(UserDTO userDto) {
//        return userMapper.userCount(userDto);
//    }
//
//    public int userOverlap(String userId) {
//        return userMapper.userOverlap(userId);
//    }
//
//    public int userSnOverlap(UserDTO userDto) {
//        return userMapper.userSnOverlap(userDto);
//    }
//
//    public UserDTO userDetailInfo(String userId) {
//        return userMapper.userDetailInfo(userId);
//    }
//
//    public int userActivate(UserDTO userDto) {
//        AdminLogDTO logDto =  new AdminLogDTO();
//        logDto.setCreateUserId(userDto.getModifyUserId());
//
//        if(userDto.getStatusKind().equals("40")) {
//            logDto.setCodeType("3103");
//
//            //유저이력관리
//            userDto.setApproStatus("30");
//            userMapper.insertApprovalHistory(userDto);
//            userMapper.userDelete(userDto.getUserId());
//        } else {
//
//            logDto.setCodeType("3104");
//        }
//        glossaryMapper.insertLog(logDto);
//
//        return userMapper.userActivate(userDto);
//    }
//
//    public String userSelectEmail(UserDTO dto) {
//        return userMapper.userSelectEmail(dto);
//    }
//
//    public int changePw(UserDTO userDto) {
//        return userMapper.changePw(userDto);
//    }
//
//    public String userIdSearch(UserDTO userDto) {
//        return userMapper.userIdSearch(userDto);
//    }
//
//    public int manageCount(UserDTO userDto) {
//        return userMapper.manageCount(userDto);
//    }
//
//    public List<UserDTO> manageSelectList(UserDTO userDto) {
//        int recordCnt = userDto.getRecordCnt();
//        int startRow = userDto.getStartRow();
//
//        userDto.setPrevRecordCnt(recordCnt * startRow);
//
//        return userMapper.manageSelectList(userDto);
//    }
//
//    public int updateRoleCode(UserDTO userDto) {
//        String roleCode = userDto.getUserRoleCode();
//
//        if(roleCode.equals("Z")) {
//            userDto.setApproStatus("20");
//        } else {
//            userDto.setApproStatus("10");
//        }
//
//        userMapper.insertApprovalHistory(userDto);
//
//        return userMapper.updateRoleCode(userDto);
//    }
//
//    public int approvalHistoryCount(UserDTO userDto) {
//        return userMapper.approvalHistoryCount(userDto);
//    }
//
//    public List<UserDTO> approvalHistorySelect(UserDTO userDto) {
//        return userMapper.approvalHistorySelect(userDto);
//    }
//
//    //비밀번호 관리
//    public void pwValidationModify(SystemOptionDTO systemOptionDto) {
//        userMapper.pwValidationModify(systemOptionDto);
//    }
//
//    //계정 유효기간 설정
//    public UserDTO userEndDate(String userId) {
//        return userMapper.userEndDate(userId);
//    }
//
//    //미접속 사용자 수
//    public int userNotconnManageCount(UserDTO userDto) {
//        return userMapper.userNotconnManageCount(userDto);
//    }
//
//    //미접속자 사용자 목록
//    public List<UserDTO> selectNotconnManageList(UserDTO userDto) {
//        return userMapper.selectNotconnManageList(userDto);
//    }
//
//    //비활성화 사용자 목록
//    public int userLockedManageCount(UserDTO userDto) {
//        return userMapper.userLockedManageCount(userDto);
//    }
//
//    //비활성화 사용자 목록
//    public List<UserDTO> selectLockedManageList(UserDTO userDto) {
//        return userMapper.selectLockedManageList(userDto);
//    }
}
