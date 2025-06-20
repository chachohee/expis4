package com.expis.ietm.parser;

import com.expis.ietm.dto.MemoDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * [IETM]Parser 시 파라미터로 값 전달할 DTO Class
 */
@Data
@NoArgsConstructor
public class ParserDto {

	/**
	 * Private 한 클래스 변수 선언
	 */
	private String biz;								//프로젝트명

	/* 파서 관련 변수 */
	private String bizIetmdata;						//프로젝트별_IETM_저장경로
	private String bizSyspath;						//프로젝트별_IETM_저장시스템경로
	private String searchWord;						//검색어
	private String toKey;							//교범키
	private String tocoId;							//교범목차ID
	private String userId;							//사용자ID

	private NodeList iconList = null;				//아이콘 노드리스트
	/* 2024.06.03 - <eXPISInfo> 노드가 여러개 일 경우 iconList 문제 보완용 == TO탐색범위 전체이동일 경우 아이콘 잘못 표시되는 문제 보완용 - jingi.kim */
	private boolean exactIcon = false;				//정확한 아이콘 노드 목록 여부
	private ArrayList<MemoDto> memoList = null;		//메모 리스트

	private String tocoType;						//교범목차타입(IPB일 경우 이미지 구분하기 위함)
	private String fiMode;							//FI보기유형(전체,단계별)
	private String arrIPBCols;						//IPB시현시 선택된 항목들(|로 구분하여 문자열 배열)

	private String vehicleType;						//IETM 시현시 사용자가 선택한 형상 타입 정보
	private String lastVersionId;					//IETM 시현시 최종 변경 버전 정보

	private String browserWidth;					//브라우저 창 가로 크기
	private String browserHeight;					//브라우저 창 세로 크기
	private String fitWidth;						//지정한 가로 사이즈에 맞춤 여부 ( yes: 가로 크기 맞춤 )
	private String contentWidth;					//main_contents 가로 크기
	private String contentHeight;					//main_contents 세로 크기
	private String figureWidth;						//main_contents 영역내 이미지의 가로 크기
	private String figureHeight;					//main_contents 영역내 이미지의 세로 크기

	private String webMobileKind;					//클라이언트 뷰어 구분(웹,모바일)
	private String versionStatus;
	private int frameClassNum;						//add 남대식or김권식

	/* 2022 06 15 Park.J.S. ADD : 언어설정값 전달 위해 추가 */
	private String languageType;						// 언어설정값 en,ko

}
