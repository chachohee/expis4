package com.expis.user.dto;

import lombok.*;

@Data
public class UserDetailInfoDto {
    private String userId;			//아이디
    private String userPw;			//패스워드
    private String authority;	//권한코드
    private String userName;		//이름
    private String userScopeCode;	//사용자영역
    private String statusKind;		//상태
    private String userEname;
    private String userUnit;
    private String userPart;
    private String userGrade;
    private String userTalent;
    private String userSn;
    private String userPhone;
    private String userCellphone;
    private String userEmail;
    private String userHopeCode;
    private String printRoleCode;
    private String printWordKind;
    private String pwChangeDate;
    private String createUserId;
    private String createDate;
    private String modifyUserId;
    private String modifyDate;
    private String bizMasterCode;
}
