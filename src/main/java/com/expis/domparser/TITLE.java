package com.expis.domparser;

/**
 * [공통모듈]내용 표시시 타이틀 값 정의 Class
 */
public class TITLE {

	/**
	 * <input> 내 태그들
	 */
	public static final String	INPUT					= "준비사항";
	public static final String	IN_TYPE					= "적용 범위";
	public static final String	IN_REQCOND				= "요구 조건";
	public static final String	IN_PERSON				= "소요 인원";
	public static final String	IN_EQUIP				= "지원 장비";
	public static final String	IN_CONSUM				= "필수교환 품목 및 소모성 물자";
	public static final String	IN_ALERT				= "안전 요건";
	public static final String	IN_OTHERCOND			= "기타 요건";
	
	public static final String	NONE					= "해당 사항 없음.";
	public static final String	COLON					= " : ";
	public static final String	MEN						= "명";
	
	public static final String	IN_CON_NAME				= "품명";
	public static final String	IN_CON_GOVSTD			= "규격";
	public static final String	IN_CON_PARTNUM			= "부품번호 (생산자부호)";
	public static final String	IN_CON_REPART			= "대체품";
	public static final String	IN_CON_QUANTITY			= "수량";
	
	public static final String	IN_OTH_ITEM				= "항목";
	public static final String	IN_OTH_DESC				= "목적";
	

	/* EN */
	public static final String	INPUT_EN				= "INPUT CONDITIONS";
	public static final String	IN_TYPE_EN				= "Applicability";
	public static final String	IN_REQCOND_EN			= "Required Conditions";
	public static final String	IN_PERSON_EN			= "Personnel Recommended";
	public static final String	IN_EQUIP_EN				= "Support Equipment";
	public static final String	IN_CONSUM_EN			= "Consumables";
	public static final String	IN_ALERT_EN				= "Safety Conditions";
	public static final String	IN_OTHERCOND_EN			= "Additional Data";
	
	public static final String	NONE_EN					= "None.";
	public static final String	COLON_EN				= " : ";
	public static final String	MEN_EN					= "";
	
	public static final String	IN_CON_NAME_EN			= "Nomenclature";
	public static final String	IN_CON_GOVSTD_EN		= "Specification/PN";
	public static final String	IN_CON_PARTNUM_EN		= "CAGE Code";
	public static final String	IN_CON_REPART_EN		= "Reference";
	public static final String	IN_CON_QUANTITY_EN		= "Use";
	
	public static final String	IN_OTH_ITEM_EN			= "Reference";
	public static final String	IN_OTH_DESC_EN			= "Purpose";
	
	/**
	 * <text type="link"> 내 타이틀명
	 */
	public static final String	LINK_GRPH_KO			= "[도해]";
	public static final String	LINK_TABLE_KO			= "[표]";
	public static final String	LINK_GRPH				= "[GRAPHIC]";
	public static final String	LINK_TABLE				= "[TABLE]";
	
	/**
	 * 내용 시현시에 메모 표시시 타이틀 
	 */
	public static final String	MEMO_USER				= "작성정보";
	/* 2022 10 13 jysi ADD 영문화 작업 */
	public static final String	MEMO_USER_EN			= "Memo Info";
	
	public static final String 	NO_DATA					= "해당 목차의 정보가 존재하지 않습니다.";
	public static final String 	NO_DATA_EN				= "The information in the table of contents does not exist.";
	
	/**
	 * FI 교범 내 각 항목 타이틀
	 */
	public static final String	FI_AP_TITLE				= "접근 및 위치자료";
	public static final String	FI_AP_TITLE_EN			= "Access and location";
	
	/**
	 * 버전정보 시현시에 문구
	 */
	public static final String	VER_APPEND			= "추가된 항목임";
	public static final String	VER_UPDATE			= "수정된 항목임";
	public static final String	VER_UPDATE_2		= "변경된 항목임";
	
	public static final String	VER_APPEND_EN		= "This is an added item";
	public static final String	VER_UPDATE_EN		= "This item has been modified";
	public static final String	VER_UPDATE_2_EN		= "This item has been changed";
	
}
