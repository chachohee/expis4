package com.expis.domparser;

/**
 * [공통모듈]DOM 파싱 및 코드 변환 시 사용될 문자&문자열 상수 정의 Class
 */
public class CHAR {

	/**
	 * 상수 변수 선언 : XML Entity 관련, character
	 */
	public static final String	CHAR_AMP		= "&";
	public static final String	CHAR_APOS		= "\'";
	public static final String	CHAR_QUOT		= "\"";
	public static final String	CHAR_LT			= "<";
	public static final String	CHAR_GT			= ">";
	public static final String	CHAR_SPACE	= " ";
	
	/**
	 * 상수 변수 선언 : 태그 시작/종료 관련, tag
	 */
	public static final char		TAG_START			= '<';
	public static final char		TAG_END				= '>';
	public static final String	TAG_SL_START		= TAG_START + "/";
	public static final String	TAG_SL_END		= "/" + TAG_END;
	
	/**
	 * 상수 변수 선언 : 기타 기호 관련, mark
	 */
	//public static final String	MARK_DOT_1		= "●";
	public static final String	MARK_DOT_1		= "•"; //<span style='font-size:8px;'>●</span>";
	public static final String	MARK_DOT_2		= "•"; //"";
	public static final String	MARK_DOT			= "•";
	public static final String	MARK_DOT_ORG	= "●";
	
	public static final String	MARK_LT				= "〈";
	public static final String	MARK_GT				= "〉";
	
	public static final String	MARK_ALERT		= "·";
	//2023.04.26 jysi ADD : ㆍ 추가(·과 다름)
	public static final String	MARK_HEAD_1		= "·,ㆍ,.,-,&quot;,\u25CF,\"";
	public static final char	MARK_HEAD_2		= 0X22;
	
	/**
	 * 상수 변수 선언 : 폰트 형태 - 볼드, 이태릭체, 밑줄
	 */
	public static final String	NAME_B			= "b";
	public static final String	NAME_I			= "i";
	public static final String	NAME_U			= "u";
	
	public static final String	WIDTH_PER		= "%";
	
	/**
	 * 상수 변수 선언 : 줄바꿈 기호를 <br>로 변환
	 */
	public static final String	NEWLINE			= "\n";
	
	/**
	 * 상수 변수 선언 : <text linktype="link" revelationtype="목차,목차,목차"> 구분 식별자 
	 */
	public static final String	LINK_REGEX_COMMA	= ",";
	
	/**
	 * 상수 변수 선언 : IPB교범에서 항목 컬럼 선택 배열로 전달시 구분 식별자 
	 */
	public static final String	IPB_REGEX_COLS		= "|";
	public static final String	IPB_REGEX_YN			= ":";
	
	/**
	 * 상수 변수 선언 : WC교범에서 경고창에 내용 마크 
	 */
	//public static final String	WC_ALERT_MARK		= "";
	
	public static final char	REG_IPB_CODE				= 0X40;
	
	/**
	 * 상수 변수 선언 : IPB교범 내에서 <grphprim ref="ID,ID,ID"> 구분 식별자
	 */
	public static final String	REG_GRPH_COMMA	= ",";
	
	/**
	 * 상수 변수 선언 : 내용 검색 테이블(tm_sc_cont) 에 등록시 목차별로 text 연결해서 넣는데 사용하는 구분 식별자
	 */
	public static final String	SEARCH_TEXT			= "|#";
	
}
