package com.expis.manage.dto;

import lombok.Data;

/**
 * [IETM]Parser 시 파라미터로 값 전달할 DTO Class
 */
@Data
public class RegisterDto {

    private String biz;					//프로젝트명

    /* Register  관련 변수 */
    private String bizIetmdata;		    //프로젝트별_IETM_저장경로
    private String bizSyspath;		    //프로젝트별_IETM_저장시스템경로

    private String toKey;				//교범키
    private String tocoId;				//교범목차ID
    //private String tocoName;			//교범목차명
    private String userId;				//사용자ID

    private String sysFilePath;		    //계통트리(SysTree) 파일 경로
    private String verFilePath;		    //버전(oldversion/UUID.xml) 파일 경로
    private String isSysDel;			//계통트리(SysTree) 삭제 여부 (1:계통, 2:TO, 4:전체)
    private String dbType;

    private String toDirPath;			//TO 디렉토리 경로

}
