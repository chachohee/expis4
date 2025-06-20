package com.expis.domparser;

/**
 * [공통모듈]XML DOM 태그 명 정의 Class
 */
public class DTD {

	/**
	 * 노드(태그) 명 정의
	 */
	public static final String	EXPISROOT	= "eXPISInfo";
	public static final String	SYSTEM		= "system";
	
	public static final String	DESC			= "descinfo";
	public static final String	PARASEQ		= "para-seq";
	public static final String	PARA				= "para";
	
	public static final String	TASK				= "task";
	public static final String	STEPSEQ		= "step-seq";
	public static final String	STEP				= "step";
	
	public static final String	TEXT				= "text";
	
	
	/**
	 * 버전
	 */
	public static final String	VERSION		= "version";
	
	
	/**
	 * 경고(Alert)
	 */
	public static final String	ALERT			= "alert";
	
	
	/**
	 * 그래픽
	 */
	public static final String	GRPH			= "grphprim";
	
	
	/**
	 * 표(테이블)
	 */
	public static final String	TABLE			= "table";
	public static final String	TB_ROWH		= "rowhddef";
	public static final String	TB_ENTRY		= "entry";
	
	
	/**
	 * 아이콘
	 */
	public static final String	ICON				= "icon";
	
	
	/**
	 * JG교범의 준비사항
	 */
	public static final String	INPUT					= "input";
	public static final String	IN_REQCOND		= "reqcond";
	public static final String	IN_PERSON			= "person";
	public static final String	IN_EQUIP				= "equip";
	public static final String	IN_CONSUM			= "consum";
	public static final String	IN_ALERT				= "alert";
	public static final String	IN_OTHERCOND	= "othercond";
	public static final String	IN_PARTBASE		= "partbase";
	public static final String	IN_LINKINFO	= "linkinfo";
	
	
	/**
	 * JG교범의 후속정비
	 */
	public static final String	FOLLOWON			= "follow-on";
	
	
	/**
	 * IPB교범
	 */
	public static final String	IPB_PARTINFO		= "partinfo";
	public static final String	IPB_PARTBASE		= "partbase";
	public static final String	IPB_REFPARTINFO		= "p_d_refid";
	
	
	/**
	 * FI교범
	 */
	public static final String	FI_FAULTINF			= "faultinf";
	public static final String	FI_NODE				= "node";
	
	
	/**
	 * WC교범
	 */
	public static final String	WC_WCS				= "workcards";
	public static final String	WC_WORKCARD	= "workcard";
	public static final String	WC_WSTEP			= "wstep";
	public static final String	WC_WIMG			= "wimg";
	
	
}
