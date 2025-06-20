package com.expis.domparser;

/**
 * [공통모듈]노드 내 속성 타입명 정의 Class
 */
public class ATTR {

	/**
	 * 공통 속성
	 */
	public static final String	ID				= "id";
	public static final String	NAME			= "name";
	public static final String	TYPE			= "type";
	public static final String	VEHICLETYPE		= "vehicletype";	//20160909 add
	public static final String	ITEMID			= "itemid";			//2022 08 11 ADD
	
	
	/**
	 * <task> 내 속성 <faultinf> 내 속성
	 */
	public static final String	FAULT_TYPE		= "faulttype";
	public static final String	FAULT_REFIMAGE	= "refimage";
	
	
	/**
	 * <Grphprim> 내 속성
	 */
	public static final String	GRPH_REF_ID				= "p_d_refid";
	public static final String	GRPH_PATH				= "external-ptr";
	public static final String	GRPH_REF				= "ref";
	public static final String	GRPH_WIDTHCHECK			= "widthcheck";
	
	
	/**
	 * <Table> 내 속성
	 * TB_TABLETYPE : FI 내에서 사용됨 <system type='DI_DESC'>...<table tabletype=''>
	 * 20191204 참고 <table tabletype='FaultIsolationTable'> 고장배제표에서도 사용하도록 추가 
	 */
	public static final String	TB_ROWCNT			= "rowcnt";
	public static final String	TB_COLCNT			= "colcnt";
	public static final String	TB_CAPTION			= "caption";
	public static final String	TB_COLSPAN			= "colspan";
	public static final String	TB_ROWSPAN			= "rowspan";
	public static final String	TB_ALIGN			= "align";
	public static final String	TB_VALIGN			= "verticaladjusttype";
	public static final String	TB_WIDTH			= "width";
	public static final String	TB_COLUMN			= "column";
	public static final String	TB_BOLD				= "bold";
	public static final String	TB_TABLETYPE		= "tabletype";
	
	
	/**
	 * <Icon> 내 속성
	 */
	public static final String	ICON_ID				= "iconid";
	public static final String	ICON_FILE			= "filename";
	
	
	/**
	 * <input> 의 <person> <consum> 내 속성
	 */
	public static final String	IN_QUANTITY			= "quantity";
	
	public static final String	IN_REPART			= "repart";
	public static final String	IN_GOVSTD			= "govstd";
	public static final String	IN_NAME				= "name";
	public static final String	IN_PARTNUM			= "partnum";
	public static final String	IN_CAGE				= "cage";
	
	public static final String	IN_OTH_ITEM			= "othercond_item";
	public static final String	IN_OTH_DESC			= "othercond_desc";
	
	
	/**
	 * <text linktype="link"> 내 속성
	 */
	public static final String	LINK_TYPE			= "linktype";
	public static final String	LINK_RVL_TP			= "revelationtype";
	public static final String	LINK_TO_KEY			= "tmname";
	public static final String	LINK_TOCO_ID		= "linkid";
	public static final String	LINK_TOCO_NM		= "listname";
	public static final String	LINK_SEC_ID			= "sectionid";
	public static final String	LINK_VIEW_TP		= "viewtype";
	
	
	/**
	 * <system id='' name='' listuuid='' listname=''> TO Tree 에서 그림/표 목차 표시시
	 */
	public static final String	SYS_ID			= "id";
	public static final String	SYS_NAME		= "name";
	public static final String	SYS_TOCO_ID		= "listuuid";
	public static final String	SYS_TOCO_NAME	= "listname";
	public static final String	SYS_TOCO_TYPE	= "listtype";
	
	
	/**
	 * <system id='' name='' > DB등록(Register) - 계통트리(System Tree) 등록 시 속성
	 */
	public static final String	SYS_SUBNAME		= "subname";
	public static final String	SYS_TYPE		= "type";
	public static final String	SYS_ITEMID		= "itemid";
	
	
	/**
	 * <system subname='' part='' 등> System_ToInfo.xml 파일 읽어서 DB에 등록 위함
	 */
	public static final String	SYS_TOTYPE			= "totype";
	public static final String	SYS_PART			= "part";
	public static final String	SYS_REVNO			= "version";
	public static final String	SYS_REVDATE			= "versiondate";
	public static final String	SYS_DESC			= "supplement";
	public static final String	SYS_COVERDATA		= "coverdata";
	
	
	/**
	 * <system ref='' security='' 등> eXPIS.xml 파일 읽어서 DB에 등록 위함
	 */
	public static final String	SYS_TOCO_REFID			= "ref";
	public static final String	SYS_TOCO_SECURITYCD		= "security";
	public static final String	SYS_TOCO_SSSNNO			= "sssn";
	public static final String	SYS_TOCO_STATUSCD		= "status";
	public static final String	SYS_TOCO_TP_STATUSCD	= "tp_status";
	public static final String	SYS_TOCO_DUMMY			= "dummy";
	public static final String	SYS_TOCO_VERID			= "version";
	public static final String  SYS_TOCO_PARENTLEVEL	= "level";
	public static final String  SYS_TOCO_lEVEL			= "level";
	
	/**
	 * <version revision='' changeno='' 등> eXPIS.xml 파일 읽어서 DB에 등록 위함
	 */
	public static final String	SYS_VER_CHGNO			= "changeno";
	public static final String	SYS_VER_CHGDATE			= "chgdate";
	public static final String	SYS_VER_REVNO			= "revision";	
	public static final String	SYS_VER_REVDATE			= "revdate";
	
	
	/**
	 * <system saveyn=''> DB등록(Register) - 계통트리(System Tree) 에 추가되는 속성
	 */
	public static final String	SYS_SAVEYN			= "saveyn";	
	
	
	/**
	 * IPB교범
	 * <partinfo>와 <partbase> 내 속성
	 */
	public static final String	IPB_IPBCODE		= "ipbcode";
	public static final String	IPB_LCN			= "LCN";
	public static final String	IPB_GRPHNUM		= "graphicnum";		//부대IPB
	public static final String	IPB_GRPHNUM2	= "figureno";		//20200210 add LYM 야전IPB
	public static final String	IPB_INDEXNUM	= "indexnum";
	public static final String	IPB_LEVEL		= "level";
	public static final String	IPB_CONTENT		= "name";
	public static final String	IPB_UNITSPER	= "unitsper";
	public static final String	IPB_RETROFIT	= "retrofit";
	public static final String	IPB_UOC			= "usablon";
	public static final String	IPB_RDN			= "RDN";
	public static final String	IPB_WUC			= "workunitcode";
	public static final String	IPB_PARTSOURCE	= "partsource";
	public static final String	IPB_REFDATA		= "refdata";
	public static final String	IPB_REFTONO		= "reftono";
	public static final String	IPB_STD			= "kai_std";
	public static final String	IPB_SSSN		= "sssn";
	public static final String	IPB_CHECKDIV	= "checkdiv";
	public static final String	IPB_CHILDPART	= "childpart";
	public static final String	IPB_PDREFID		= "p_d_refid";		//IPB에서 호기 분리될 경우 해당 Attribute가 생성되며 상위 객체의 UUID
	public static final String	IPB_GRPHREF		= "grphref";
	
	/**
	 * <partbase> 내 속성
	 */
	public static final String	IPB_PARTNUM			= "partnum";
	public static final String	IPB_SMR				= "smr";
	public static final String	IPB_NSN				= "nsn";
	public static final String	IPB_CAGE			= "cage";
	public static final String	IPB_FILENAME		= "filename";
	
	
	/**
	 * FI 테이블에서 <table cols="" rows=""><entry column="" row=""> 내 속성
	 */
	public static final String	FI_TB_ROWS			= "rows";
	public static final String	FI_TB_COLS			= "cols";
	public static final String	FI_TB_TD_ROW		= "row";
	public static final String	FI_TB_TD_COL		= "column";
	public static final String	FI_TB_TD_ULINE		= "underline";
	//public static final String	FI_TB_TD_STYLE	= "style";
	
	
	/**
	 * WC교범
	 */
	public static final String	WC_CARDNO			= "cardno";
	public static final String	WC_TYPE				= "type";
	public static final String	WC_SYSTEM			= "system";//2023 01 12 Park.J.S. ADD : WC 카드 내 계통별 링크 기능구현 위해 추가
	public static final String	WC_SUBSYSTEM		= "subsystem";		// 2023.11.30 - 작업단위부호 ( WUC ) 검색어 구성을 위해서 추가 - jingi.kim
	
}
