package com.expis.user.dao;

import com.expis.ietm.dto.MainDto;
import com.expis.manage.dto.SystemOptionDto;
import com.expis.user.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    public MainDto userPwCheck(MainDto dto);
    public int loginCheck(MainDto dto);
    public UserDto selectUserInfo(UserDto dto);

    // 2023.10.23 - 사용자 정보 조회 - jingi.kim
    public UserDto selectUserByUserId(UserDto dto);
    public void insertFailConnHistory(MainDto dto);
    public int selectCountFailConnHistoryByUserId(MainDto dto);
    public void updateFailConnHistoryStatusByUserId(MainDto dto);

    public List<UserDto> selectListUserInfo(UserDto dto);
    public int insertUserProfile(UserDto dto);
    public int updateUser(UserDto dto);
    public void insertUserId(MainDto dto);
    public void updateLogoutTime(MainDto dto);
    // 정상적인 방법아님
    public void loginOutUpdateTest(MainDto dto);
    // 최초 로그인인지, 아닌지 파악하는곳
    public int countConnUser(MainDto dto);
    // 정상적인 방법아님
    public MainDto getSLoginIngoTest(MainDto dto);
    // 로그아웃할때 로그아웃시간 업데이트
    public void loginOutUpdate(MainDto dto);

    public int userCount(UserDto userDto);
    public int userOverlap(String userId);
    public int userSnOverlap(UserDto userDto);
    public UserDto userDetailInfo(String userId);
    public int userActivate(UserDto userDto);
    public void userDelete(String userId);
    public String userSelectEmail(UserDto userDto);
    public int changePw(UserDto userDto);
    public String userIdSearch(UserDto userDto);

    public int manageCount(UserDto userDto);
    public List<UserDto> manageSelectList(UserDto userDto);
    public int updateRoleCode(UserDto userDto);

    public int approvalHistoryCount(UserDto userDto);
    public List<UserDto> approvalHistorySelect(UserDto userDto);
    public int insertApprovalHistory(UserDto userDto);

    // 2023.10.26 - 미접속 사용자 - jingi.kim
    public int userNotconnManageCount(UserDto userDto);
    public List<UserDto> selectNotconnManageList(UserDto userDto);
    // 2023.10.27 - 비활성화 사용자 - jingi.kim
    public int userLockedManageCount(UserDto userDto);
    public List<UserDto> selectLockedManageList(UserDto userDto);


    //비밀번호 관리
    public void pwValidationModify(SystemOptionDto systemOptionDto);
    //계정 유효기간 설정
    public UserDto userEndDate(String userId);
    //마이그레이션 사용자 계정 등록
    public int insertUserProfileMI(UserDto dto);
}
