package com.expis.ietm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * [IETM]All XML Contents DTO Class
 *   - TM_All_XCONT
 *   - TM_ALERT_XCONT
 *   - TM_GRPH_XCONT
 *   - TM_TABLT_XCONT
 *   - TM_VER_XCONT
 *   - TM_WC_XCONT
 */
@Data
@NoArgsConstructor
public class XContDto {

	/* DB Table 매핑 변수 선언 - 공통 TM_ALL_XCONT */
	private String toKey;						//교범_KEY
	private String tocoId;						//목차_ID
	private long xth;							//데이터_순서
	private String xcont;						//데이터_내용

	private String createUserId;				//등록자ID
	private Date createDate;					//등록일자
	private String modifyUserId;				//수정자ID
	private Date modifyDate;					//수정일자

	/* 경고 TM_ALERT_XCONT */
	private String altId;						//경고_ID

	/* 그래픽 TM_GRPH_XCONT */
	private String grphId;						//그래픽_ID
	private String grphCaption;					//그래픽_제목
	private String grphType;					//그래픽_타입
	private String fileOrgName;					//파일명

	/* WD To WD 링크 */
	private String flick;

	/* 표(테이블) TM_TABLE_XCONT */
	private String tblId;						//표_ID
	private String tblCaption;					//표_제목

	/* 버전 TM_VER_XCONT */
	private String verId;						//버전_ID
	private String verName;						//버전_명
	private String verStatus;					//버전_상태(a:추가, u:수정)
	private String changebasis;					//changebasis 2022 09 15 Park.J.S. ADD 변경 근거 표시용

	/* 내용 */
	private String contId;						//내용_ID

	/* 작업카드(WC) 일 경우 TM_WC_XCONT */
	private String[] arrRefId;					//관련_목차_ID
	private String cardNo;						//카드_번호
	private String wcType;						//작업카드_타입
	//작업카드_계통  [2023 01 12 Park.J.S. ADD : WC 카드 내 계통별 링크 기능구현 위해 추가]
	private String wcSystem;					//계통
	private String wcName;						//구분
	private String wcSteptime;					//인시분
	private String wcSteparea;					//작업구역
	private String wcSubsystem;					//하부계통
	private String wcContent;					//검사내용

	/* 사용자 정의 변수 선언 */
	private String biz;							//웹서비스 클라이언트 비즈니스명
	private String userId;						//(현재접속)사용자_ID
	private String searchWord;					//검색어
	private String outputMode;					//탐색범위(다중목차-하위목차포함,단일목차)
	private String viewMode;					//내용보기유형(페이지보기,프레임보기)
	private String fiMode;						//FI보기유형(전체,단계별)

	private String viewContKind;				//내용종류(내용,경고,그래픽,표)
	private String printYn = "N";				//출력여부(yes,no)
	private String printWordKind;				//출력프로그램종류(MS-워드,한글)
	private String arrIPBCols;					//IPB시현시 선택된 항목들(|로 구분하여 문자열 배열)
	private String vehicleType;					//형상타입
	private String lastVersionId;				//IETM 시현시 최종 변경 버전 정보

	private String browserWidth;				//브라우저 창 가로 크기
	private String browserHeight;				//브라우저 창 세로 크기
	private String contentWidth;				//main_contents 가로 크기
	private String contentHeight;				//main_contents 세로 크기

	private String webMobileKind;				//클라이언트 뷰어 구분(웹,모바일)

	private String tocoVehicleType;
	/* 2022 06 15 Park.J.S. ADD : 언어설정값 전달 위해 추가 */
	private String languageType;					// 언어설정값 en,ko
	private String bizCode;						// 2023.05.03 - BLOCK2 이미지 일 경우 체크를 위해 추가 - jingi.kim

	private String ipbType;						// IPB 테이블 열 너비 조정을 위해 추가 - chohee.cha

}
