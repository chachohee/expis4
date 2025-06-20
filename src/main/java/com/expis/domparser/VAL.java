package com.expis.domparser;

/**
 * [공통모듈]노드 내 속성 값 정의 Class (저작데이터에서 구성한 속성값들임)
 */
public class VAL {

	/* 노드 내 타입 정의 */
	
	/**
	 * 교범목록(SysTree.xml) 에 대한 <system type=''> 값
	 * @date 20160907
	 */
	public static final String	SYS_TYPE_SYS				= "SYSTEM";
	public static final String	SYS_TYPE_SUBSYS		= "SUBSYSTEM";
	public static final String	SYS_TYPE_ASS				= "ASSEMBLY";
	public static final String	SYS_TYPE_SUBASS		= "SUBASSEMBLY";
	public static final String	SYS_TYPE_COMP			= "COMPONENT";
	public static final String	SYS_TYPE_TO				= "TO";
	
	
	/**
	 * 교범목차(eXPIS.xml) 에 대한 <system type=''> 값
	 */
	public static final String	CHAPTER			= "CHAPTER";
	public static final String	SECTION			= "SECTION";
	public static final String	TOPIC			= "TOPIC";
	public static final String	SUBTOPIC		= "SUBTOPIC";
	public static final String	INTRO			= "INTRODUCTION";
	public static final String	TASK			= "TASK";
	
	public static final String	TYPE_PARA		= "para";
	public static final String	TYPE_STEP		= "step";
	public static final String	TYPE_TITLE		= "title";
	
	public static final String	WC_UNIT			= "WC";
	public static final String	WC_FIELD		= "F_WC";
	
	public static final String	IPB_UNIT		= "IPB";
	public static final String	IPB_FIELD		= "IPB2";
	public static final String	IPB_ROOT		= "IPBROOT";
	
	public static final String	FI_DI			= "DI";
	public static final String	FI_LR			= "LR";
	public static final String	FI_AP			= "AP";
	public static final String	FI_DDS			= "DDS";
	public static final String	FI_DI_DESC		= "DI_DESC";
	public static final String	FI_LR_DESC		= "LR_DESC";
	
	public static final String	FI_FIA			= "FI_A";		//미사용
	public static final String	FI_FIB			= "FI_B";		//미사용
	public static final String	FI_FIPIC		= "FI_PIC";		//미사용
	public static final String	FI_UNIT			= "FI";			//부대급
	public static final String	FI_FIELD		= "FI_FIELD";	//야전급
	
	
	/**
	 * <faultinf faulttype=''> 내 고장탐구 타입 값
	 */
	public static final String	FI_FAULTTYPE_A		= "A";
	public static final String	FI_FAULTTYPE_B		= "B";
	
	
	/**
	 * <table tabletype=''> 내 테이블 타입 값
	 * CB			: 고장탐구에서 결함식별및설명
	 * CODE		: 고장탐구에서 결함식별및설명
	 * LOGBOOK	: 고장탐구에서 로그북리포트
	 * FITABLE	: 고장배제표 (HUSS에서 고장배제표 추가) //20191204 add LYM
	 */
	public static final String	TB_TBTYPE_CB			= "CB";
	public static final String	TB_TBTYPE_CODE			= "CODE";
	public static final String	TB_TBTYPE_LOGBOOK		= "LOGBOOK";
	public static final String	TB_TBTYPE_FITABLE		= "FaultIsolationTable";
	
	
	/**
	 * <grphprim external-ptr=''> 내 폴더명 값
	 */
	public static final String	IMG_FD_IPBIMAGE		= "IPBImage";
	public static final String	IMG_FD_IMAGE		= "Image";
	public static final String	IMG_FD_FOLDER		= "\\";
	
	/**
	 * <grphprim external-ptr=''> 내 그래픽 파일 값 (확장자 종류)
	 */
	public static final String	IMG_EXT_SWF			= "SWF";
	public static final String	IMG_EXT_JPG			= "JPG";
	public static final String	IMG_EXT_GIF			= "GIF";
	public static final String	IMG_EXT_PNG			= "PNG";
	public static final String	IMG_EXT_BMP			= "BMP";
	public static final String	IMG_EXT_AVI			= "AVI";
	public static final String	IMG_EXT_WMV			= "WMV";
	public static final String	IMG_EXT_MOV			= "MOV";
	public static final String	IMG_EXT_MP4			= "MP4";
	public static final String	IMG_EXT_MP3			= "MP3";
	public static final String	IMG_EXT_WRL			= "WRL";
	public static final String	IMG_EXT_TIF			= "TIF";
	public static final String  IMG_EXT_SVG 		= "SVG";//2022 11 07 ADD
	
	/**
	 * <grphprim widthcheck='1'> 내 그래픽 파일이 큰 이미지인지 체크
	 */
	public static final String	IMG_WIDTHCHECK	= "1";
	public static final String	IMG_VERSION		= "2";	//버전정보 팝업에서의 이미지
	
	
	/**
	 * <alert type=''> 내 경고 종류 값
	 */
	public static final String	ALT_ALERT					= "alert";
	public static final String	ALT_WARNING					= "warning";
	public static final String	ALT_CAUTION					= "caution";
	public static final String	ALT_NOTE					= "note";
	
	public static final String	ALT_ALERT_CL				= "CL_alert";
	public static final String	ALT_WARNING_CL			= "CL_warning";
	public static final String	ALT_CAUTION_CL			= "CL_caution";
	public static final String	ALT_NOTE_CL				= "CL_note";
	public static final String	ALT_WARNING_NF		= "warning_NF";
	public static final String	ALT_WARNING_CLNF	= "CL_warning_NF";
	
	
	/**
	 * <table align=''> 정렬값
	 */
	public static final String	ALIGN_LEFT				= "left";
	public static final String	ALIGN_RIGHT			= "right";
	public static final String	ALIGN_CENTER			= "center";
	public static final String	ALIGN_JUST				= "justify";
	
	/**
	 * <table valign='' bold=''> vertical 정렬값, 볼드 여부 
	 */
	public static final String	VALIGN_0					= "0";
	public static final String	VALIGN_1					= "1";
	public static final String	BOLD_1					= "1";
	
	
	/**
	 * <icon filename=''> 내 폴더명 값
	 */
	public static final String	ICON_FD_LOWER		= "icon";
	public static final String	ICON_FD_UPPER		= "Icon";
	
	
	/**
	 * <text linktype="link"> 링크 여부 값 
	 */
	public static final String	LINK_TYPE			= "link";
	
	/**
	 * <text revelationtype=""> 링크 타입 값 
	 */
	public static final String	LINK_TO				= "TO";
	public static final String	LINK_TOCO			= "목차";
	public static final String	LINK_GRPH			= "도해";
	public static final String	LINK_TABLE			= "테이블";
	public static final String	LINK_IPB				= "IPB";
	public static final String	LINK_RDN				= "RDN";
	public static final String	LINK_WC				= "WC"; //필요한지 검사
	public static final String	LINK_PDF				= "PDF";
	public static final String	LINK_FILE			= "OUTSIDEFILE";
	
	/**
	 * <text viewtype=""> 링크 뷰타입 값 
	 */
	public static final String	LINK_VTYPE_SELF	= "SELF";
	public static final String	LINK_VTYPE_WIN		= "WINDOW";
	
	
	/**
	 * expis.xml에서 <system type=''> 그림/표 목차 목록 구분
	 */
	public static final String	TOCOLIST_GRPH		= "Grph";
	public static final String	TOCOLIST_TABLE		= "Table";
	public static final String	TOCOLIST_VIDEO		= "Video";
	
	public static final String	TOCOLIST_IPB				= "IPB";
	public static final String	TOCOLIST_VIDEO_EXT	= "|AVI|WMV|MOV|MP4|";
	
	/**
	 * 교범 목차에서 그림/동영상/표 목차 생성하기 위한 문구
	 * 0번째 : ID, 1번째: Name, 2번째 : Type
	 */
	public static final String[]	TOCOLIST_GRPH_GROUP		= {"GrphToco", "그림목차", "GrphToco"};
	public static final String[]	TOCOLIST_VIDEO_GROUP		= {"VideoToco", "동영상목차", "VideoToco"};
	public static final String[]	TOCOLIST_TABLE_GROUP		= {"TableToco", "표목차", "TableToco"};
	
	
	/**
	 * FI 교범의 테이블에서 <table><entry style=""> 텍스트 스타일 값
	 */
	public static final String	FI_TB_TD_ULINE		= "1";
	public static final String	FI_TB_TD_NUM			= "1";
	public static final String	FI_TB_TD_CODE		= "2";
	
	
	/**
	 * WC 교범의 테이블에서 <workcard type=""> 텍스트 스타일 값
	 */
	public static final String	WC_TYPE_A				= "typea";
	public static final String	WC_TYPE_B				= "typeb";
	//2022 06 24 Park.J.S. : WC Type ADD(KTA)
	public static final String	WC_TYPE_B_2				= "typeb_2";
	public static final String	WC_TYPE_C				= "typec";
	public static final String	WC_TYPE_C_2				= "typec_2";
	public static final String	WC_TYPE_A_IMG			= "typea_img";
	public static final String	WC_TYPE_B_IMG			= "typeb_img";
	public static final String	WC_TYPE_B_2_IMG			= "typeb_2_img";
	public static final String	WC_TYPE_D				= "typed";
	public static final String	WC_TYPE_E				= "typee";
	public static final String	WC_TYPE_F				= "typef";
	public static final String	WC_SEQNUM_ALT1	= "경고";
	public static final String	WC_SEQNUM_ALT2	= "주의";
	public static final String	WC_SEQNUM_ALT3	= "주기";
	
	public static final char	WC_SEQNUM_ALPHABET	= 'A';
	public static final String	WC_SHAPE			= "ABCD12";
	public static final String	WC_REITER_2			= "2";
	public static final String	WC_REITER_3			= "3";
	
	
	/**
	 * 버전 정보에서 oldversiondata 폴더의 파일들(UUID_C32.xml)에 지정된 접두어
	 * (<version changeno='32'>와 비교할때 사용 
	 */
	public static final String	VER_PREFIX				= "C";
	
	
	/**
	 * IPB 교범의 부품정보에서 <partinfo type=""> 텍스트 스타일 값
	 * 부대급			airpartinfo
	 * 야전급			""
	 * 부대급피규어	airpartinfo_figure
	 * 부대급KF16	PAGE_airpartinfo
	 */
	public static final String	PINFO_TYPE_UNIT		= "AIRPARTINFO";
	public static final String	PINFO_TYPE_FIELD		= "";
	public static final String	PINFO_TYPE_FIG			= "AIRPARTINFO_FIGURE";
	public static final String	PINFO_TYPE_KF16		= "PAGE_AIRPARTINFO";
	
	
	/**
	 * 교범 본문 내용에서 각 컨텐츠 엘리먼트 마다 가지는 버전 정보 속성 값
	 * @date 2016.10.28
	 * 추가(append)	: a
	 * 변경(update)	: b
	 */
	public static final String	CONT_STATUS_APPEND		= "a";
	public static final String	CONT_STATUS_UPDATE		= "u";
	
	
	/**
	 * 교범 본문 내용에서 각 컨텐츠 엘리먼트 마다 가지는 형상 정보 속성 값
	 */
	public static final String	VEHICLE_PRIFIX			= "TYPE";
	public static final String	VEHICLE_TYPE_NONE	= "NONE";
	public static final String	VEHICLE_TYPE_A			= "A";
	public static final String	VEHICLE_TYPE_B			= "B";
	public static final String	VEHICLE_TYPE_C			= "C";
	public static final String	VEHICLE_TYPE_D			= "D";
	
	
	/**
	 * 특수교범의 내용 타입
	 * <step-seq type='CL'>	CL교범의 체크리스트 항목
	 * <table type='WDW'>	WD교범의 표(테이블) -  연결목록(WDC->6), 배선목록(WDW->12)
	 */
	public static final String	STEP_TYPE_CL		= "CL";
	public static final String	TB_TYPE_WDW			= "WDW";
	public static final String	TB_TYPE_WDC			= "WDC";
	
}
