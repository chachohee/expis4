package com.expis.login.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
public class LoginHistoryDto {

    private int connSeq;
    private String loginId;
    private String sessionId;
    private String connIp;
    private String statusKind;
    private Date connDate;
    private LocalDateTime disConnDate;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public LoginHistoryDto(String loginId, String sessionId, String connIp, Date connDate, String statusKind) {
        this.loginId = loginId;
        this.sessionId = sessionId;
        this.connIp = connIp;
        this.connDate = connDate;
        this.statusKind = statusKind;
    }

    public LoginHistoryDto(String loginId, String connIp) {
        this.loginId = loginId;
        this.connIp = connIp;
    }
}