package com.expis.domparser;

/**
 * [공통모듈]DOM 파싱 및 코드 변환 시 사용될 코드값 상수 정의 Class
 */
public class CODE {

	/**
	 * 상수 변수 선언 : 기호 관련, symbol
	 */
	public static final char		SYMBOL_AMP			= '&';
	public static final char		SYMBOL_SHARP		= '#';
	public static final char		SYMBOL_SEMI			= ';';
	
	
	/**
	 * 상수 변수 선언 : 원문자 관련 [20]...
	 */
	public static final char		SYMBOL_CIRCLE_START	= '[';
	public static final char		SYMBOL_CIRCLE_END		= ']';
	
	
	/**
	 * 상수 변수 선언 : XML Entity 관련, entity name
	 */
	public static final String	ENM_AMP			= "&amp;";
	public static final String	ENM_APOS		= "&apos;";
	public static final String	ENM_QUOT		= "&quot;";
	public static final String	ENM_LT			= "&lt;";
	public static final String	ENM_GT			= "&gt;";
	public static final String	ENM_SPACE		= "&nbsp;";
	
	
	/**
	 * 상수 변수 선언 : XML Entity 관련, entity number(code)
	 * 7		MARK	: 머릿기호
	 * 13	BR		: 줄바꿈
	 * X00AE	REG	: ®
	 * 24	ICON	: 아이콘모음 
	 */
	public static final String	ECD_MARK		= "&#7;";
	public static final String	ECD_NLINE		= "&#13;";
	public static final String	ECD_NLINE2		= "&amp;#13;";
	public static final String	ECD_REG			= "&#X00AE;";
	public static final String	ECD_ICON		= "&#24;";
	
	
	/**
	 * 상수 변수 선언 : 검색어 붉은색으로 반전효과 위해 단어 앞뒤에 지정될 스타일
	 * 5000	검색어 붉은 컬러 시작
	 * 6000	검색어 붉은 컬러 종료
	 */
	public static final String	ECD_KW_START	= "&#5000;";
	public static final String	ECD_KW_END		= "&#6000;";
	
	
	
}
