package com.expis.common;

public class eXPIS3Constants {

    private eXPIS3Constants() {
        //인스턴스화 방지
        throw new UnsupportedOperationException("This is a utility class and cannot be Instantiated.");
    }


    /**
     * DB 구분 값 정의
     * ORACLE	: ORACLE DB
     * MSSQL		: MS-SQL DB
     * MDB			: MS-Access DB
     */
    public static final String DB_ORACLE		= "ORACLE";
    public static final String DB_MARIADB	    = "MARIADB";
    public static final String DB_MSSQL		    = "MSSQL";
    public static final String DB_MDB			= "MDB";


    /**
     * 목록 트리 시현시 계통/TO/MY TO 구분 값 정의
     * 01	: SYS
     * 02	: TO
     * 03	: MY
     */
    public static final String TOINDEX_SYS		= "01";
    public static final String TOINDEX_TO		= "02";
    public static final String TOINDEX_MY		= "03";


    /**
     * Business 구분 값 정의
     * DF	: Default Web
     * T50	: T50 IETM
     * KT1	: KT1 IETM
     */
    public static final String BIZ_DF		= "DF";
    public static final String BIZ_T50		= "T50";
    public static final String BIZ_KT1		= "KT1";


    /**
     * View영역에서 리소스 경로 추출위한 폴더명 지정
     * PROP는 프로퍼티 내 이름에 사용되고, VI는 코어부분에서 폴더명으로 사용됨
     * biz		: 비즈니스
     * css		: 스타일시트
     * image	: 그래픽
     * js			: 자바스크립트
     * jsp		: JSP
     * ietmdata	: 아이콘 및 그래픽 등 컨텐츠 데이터
     * syspath	: ietmdata들의 절대경로
     */
    public static final String PROP_BIZ			= "BIZ";
    public static final String PROP_CSS			= "CSS";
    public static final String PROP_IMG			= "IMG";
    public static final String PROP_JS			= "JS";
    public static final String PROP_JSP			= "JSP";
    public static final String PROP_IETMDATA	= "IETMDATA";
    public static final String PROP_SYSPATH	= "SYSPATH";

    public static final String M_PROP_BIZ		= "M_BIZ";
    public static final String M_PROP_CSS		= "M_CSS";
    public static final String M_PROP_IMG		= "M_IMG";
    public static final String M_PROP_JS		= "M_JS";
    public static final String M_PROP_JSP		= "M_JSP";
    public static final String M_PROP_IETMDATA	= "M_IETMDATA";
    public static final String M_PROP_SYSPATH	= "M_SYSPATH";

    public static final String VI_CSS				= "css";
    public static final String VI_IMG				= "image";
    public static final String VI_JS					= "js";
    public static final String VI_JSP					= "jsp";
    public static final String VI_IETMDATA		= "ietmdata";


    /**
     * IETM 컨텐츠 그래픽(일반 image, ipb image) 경로(폴더명)
     */
    public static final String GRPH_PATH_ICON		= "icon";
    public static final String GRPH_PATH_IMAGE		= "image";
    public static final String GRPH_PATH_AUDIO		= "audio";
    public static final String GRPH_PATH_VIDEO		= "video";
    public static final String GRPH_PATH_VR			= "vr";


    /**
     * 이미지 정보 DB에 저장시 일반이미지인지 IPB이미지인지 구분
     */
    public static final String GRPH_TYPE_COMM		= "01";
    public static final String GRPH_TYPE_IPB			= "02";


    /**
     * 저작데이터 프로젝트에서 파일들 구성하는 디렉토리명 설정
     */
    public static final String AUTH_VERSION			= "oldversiondata";


    /**
     * CSS Style을 정의 위해 depth와 매핑
     * 1 : Chapter
     * 2 : Section
     * 3 : Topic
     * 4 : SubTopic
     * 5 : Introduction
     */
//	public static final String STYLE_1		= "1";
//	public static final String STYLE_2		= "2";
//	public static final String STYLE_3		= "3";
//	public static final String STYLE_4		= "4";
//	public static final String STYLE_5		= "5";

//	public static final String HEAD_ALERT		= "1";


    /**
     * 내용 시현시 목차 및 컨텐츠 종류 분류(view contents kind)
     * 01	: 내용 - 일반목차
     * 02	: 경고 - 프레임보기에서 경고창
     * 03	: 그래픽 - 그림목차에서, 그림팝업
     * 04	: 표(테이블) - 표목차에서, 표팝업
     * 05	: IPB(IPB목차)
     * 06	: WC(WC목차들)
     * 07	: FI(FI목차들)
     */
    public static final String VCONT_KIND_CONT		= "01";
    public static final String VCONT_KIND_ALERT	= "02";
    public static final String VCONT_KIND_GRPH		= "03";
    public static final String VCONT_KIND_TABLE	= "04";
    public static final String VCONT_KIND_IPB		= "05";
    public static final String VCONT_KIND_WC		= "06";
    public static final String VCONT_KIND_FI			= "07";


    /**
     * TM_TOCO_INFO 테이블에서 TOCO_TYPE 속성 값
     * 01 : Introduction (소개)
     * 02 : Chapter (장)
     * 03 : Section (절)
     * 04 : Topic (항)
     * 05 : SubTopic (항-2)
     * 06 : Task (절차)
     * 11 : IPBROOT (IPB장)
     * 12 : ipb (IPB)
     * 13 : WC (작업카드)
     * 14 : F_WC (해당데이터 찾아봐야함)
     *
     * 21 : DI				(FI_1.결함식별및설명)
     * 22 : DI_DESC	(FI_1.sub.조종사탐지결함)
     * 23 : FI_A			(FI_1.sub.(1)설명)
     * 24 : LR				(FI_2.로그북리포트)
     * 25 : LR_DESC	(FI_2.sub.조종사탐지결함)
     * 26 : AP				(FI_3.접근및위치자료)
     * 27 : FI_PIC		(FI_3.sub.접근및위치자료 밑의 그래픽)
     * 28 : DDS			(FI_4.고장탐구절차)
     * 29 : FI_B			(FI_4.sub.AA)
     *
     * 41 : GrphToco (그림목차)
     * 42 : TableToco (표목차)
     * 43 : Grph (그림)
     * 44 : Table (표)
     * 45 : AudioToco (동영상목차)
     * 46 : Audio (동영상)
     */
	/*
	public static final String TOCO_TYPE_INTRO		= "01";
	public static final String TOCO_TYPE_CHA		= "02";
	public static final String TOCO_TYPE_SEC		= "03";
	public static final String TOCO_TYPE_TOP		= "04";
	public static final String TOCO_TYPE_SUB		= "05";
	public static final String TOCO_TYPE_TASK		= "06";
	*/
    public static final String TOCO_TYPE_NONE		= "00";
    public static final String TOCO_TYPE_WC			= "11";
    public static final String TOCO_TYPE_IPB		= "16";
    public static final String TOCO_TYPE_FIELD		= "17";
    public static final String TOCO_TYPE_ROOT		= "18";
    public static final String TOCO_TYPE_FI			= "21";
    public static final String TOCO_TYPE_FI_FIA		= "23";
	/*
	public static final String TOCO_TYPE_IPB		= "01";
	public static final String TOCO_TYPE_FI			= "02";
	public static final String TOCO_TYPE_WC			= "03";
	public static final String TOCO_TYPE_NONE		= "00";
	*/


    /**
     * 목차 타입 (목차 트리에서 그림/표목차 믈릭시)
     * 01	: 일반목차
     * 02	: 그림목차
     * 03	: 표목차
     * 04	: 음성목차
     * 05	: 동영상목차
     */
    public static final String TOCOLIST_KIND_CONT		= "01";
    public static final String TOCOLIST_KIND_GRPH		= "02";
    public static final String TOCOLIST_KIND_TABLE		= "03";
    public static final String TOCOLIST_KIND_AUDIO		= "04";
    public static final String TOCOLIST_KIND_VIDEO		= "05";


    /**
     * 검색시 조건 분류
     * 01 : 현재교범
     * 02 : 현재계통
     * 03 : 부품정보
     * 04 : 결함코드
     * 05 : 작업카드
     * 06 : 작업단위부호
     * 07 : 용어검색
     */
    public static final String SC_COND_TO				= "01";
    public static final String SC_COND_SYS				= "02";
    public static final String SC_COND_IPBPART		= "03";
    public static final String SC_COND_FICODE		= "04";
    public static final String SC_COND_WC				= "05";
    public static final String SC_COND_WUC			= "06";
    public static final String SC_COND_GLOSSARY	= "07";

    /**
     * 검색시 조건 중 부품정보 일 경우 하위 분류
     * 31	: 부품명
     * 32	: 부품번호/재고번호(NSN)
     * 33	: RDN
     */
    public static final String SC_COND_IPB_PNM		= "01";
    public static final String SC_COND_IPB_PNO		= "02";
    public static final String SC_COND_IPB_RDN		= "03";

    /**
     * 검색시 결과 형태 분류
     * 01	: 목차
     * 02	: 내용
     * 03	: 그래픽
     * 04	: 표(테이블)
     * 05	: 경고
     * 06	: IPB
     * 07	: FI
     * 08	: WC
     * 09	: WUC
     * 10	: 용어
     */
    public static final String SC_RT_TOC					= "01";
    public static final String SC_RT_CONT				= "02";
    public static final String SC_RT_GRPH				= "03";
    public static final String SC_RT_TABLE				= "04";
    public static final String SC_RT_ALERT				= "05";
    public static final String SC_RT_IPB					= "06";
    public static final String SC_RT_FI					= "07";
    public static final String SC_RT_WC					= "08";
    public static final String SC_RT_WUC				= "09";
    public static final String SC_RT_GLOSSARY		= "10";


    /**
     * 옵션 설정을 세션으로 저장하고 변수값 지정
     * 탐색모드				SS_OPT_EXPLORE_MODE		(01 : TO전체이동,  02 : TO선택이동)
     * 탐색범위				SS_OPT_OUTPUT_MODE		(01 : 다중목차범위,  02 : 단일목차범위)
     * 내용보기 유형			SS_OPT_VIEW_MODE			(01 : 페이지,  02 : 프레임)
     * FI 디스플레이 유형	SS_OPT_FI_MODE				(01 : 전체,  02 : 단계별)
     * 폰트 조정				SS_OPT_FONT_SIZE				(01 : 작게,  02 : 원래크기, 03 : 크게)
     * 출력프로그램 유형		SS_OPT_PRINT_WORD			(01 : MS-Word,  02 : 한글)
     * 모바일 메뉴 유형		SS_OPT_MOBILE_MENU		(01 : 공통TO, 02 : MY TO)
     */
    public static final String OPT_EPMODE_ALL				= "01";
    public static final String OPT_EPMODE_CHOICE			= "02";

    public static final String OPT_OUMODE_MULTI			= "01";
    public static final String OPT_OUMODE_SINGLE		= "02";

    public static final String OPT_VMODE_PAGE				= "01";
    public static final String OPT_VMODE_FRAME			= "02";

    public static final String OPT_FIMODE_ALL				= "01";
    public static final String OPT_FIMODE_LEVEL			= "02";

    public static final String OPT_FONTSIZE_S				= "01";
    public static final String OPT_FONTSIZE_M				= "02";
    public static final String OPT_FONTSIZE_L				= "03";

    public static final String OPT_PRINTWORD_DOC		= "01";
    public static final String OPT_PRINTWORD_HWP		= "02";

    public static final String OPT_MOBILE_MENU_CMTO	= "01";
    public static final String OPT_MOBLIE_MENU_MYTO	= "02";

    public static final String OPT_FONT_FAMILY_1	= "01";
    public static final String OPT_FONT_FAMILY_2	= "02";
    public static final String OPT_FONT_FAMILY_3	= "03";

    public static final String OPT_COVER_1	= "01";
    public static final String OPT_COVER_2	= "02";
    public static final String OPT_COVER_3	= "03";



    /**
     * 출력 여부
     * Y	: 출력 YES
     * N	: 출력 NO
     */
    public static final String PRINT_YN_YES		= "Y";
    public static final String PRINT_YN_NO		= "N";


    /**
     * 프레임보기 여부
     * Y	: 프레임보기 YES
     * N	: 프레임보기 NO
     */
    public static final String FVIEW_YN_YES		= "Y";
    public static final String FVIEW_YN_NO		= "N";


    /**
     * IPB교범에서 항목 선택 여부 값 정의
     * Y	: 항목표시
     * N	: 항목미표시
     */
    public static final String IPB_COLS_YES		= "Y";
    public static final String IPB_COLS_NO		= "N";


    /**
     * IPB교범에서 내용 보기 옵션 설정 값 정의
     * 01 : 도해도
     * 02 : 표(테이블)
     * 03 : 전체보기
     */
    public static final String IPB_CONTROL_GRPH			= "01";
    public static final String IPB_CONTROL_TABLE		= "02";
    public static final String IPB_CONTROL_ALL			= "03";


    /**
     * IPB교범에서 부대급/야전급 타입 설정 값 정의
     */
    public static final int IPB_TYPE_UNIT			= 1;
    public static final int IPB_TYPE_FIELD			= 2;


    /**
     * IPB 도해 핫스팟 선택시 테이블 로우 반전 기능 위해 ID 부여 값 정의
     */
    public static final String IPB_TABLE_TR_ID			= "part_";


    /**
     * WC 에서 관련 ID 값 넣은 문자열 구분자
     */
    public static final String WC_REFID_REGEX			= ",";
    public static final String WC_ICON_REITER			= "/ietm/wc/wc_reiter_";
    public static final String WC_ICON_REITER3			= "/ietm/wc/wc_reiter3_";
    public static final String WC_ICON_SHAPE			= "/ietm/wc/wc_shape_";
    public static final String WC_ICON_EXT_JPG			= ".jpg";


    /**
     * 상태 구분자 정의 - 정상, 중지, 삭제
     */
    public static final String STATUS_KIND_VALID	= "10";
    public static final String STATUS_KIND_STOP		= "30";
    public static final String STATUS_KIND_DELETE	= "40";


    /**
     * 불변의 파일명 정의 - 계통트리 파일, TO별 목차 파일
     */
    public static final String FILE_NAME_SYS		= "SysTree.xml";
    public static final String FILE_NAME_SYSTO		= "System_ToInfo.xml";
    public static final String FILE_NAME_TO			= "eXPIS.xml";
    public static final String FILE_EXTNAME			= "xml";
    //2022 06 27 Park.J.S ADD
    public static final String FILE_NAME_SYSTO_KTA	= "ToInfo.xml";


    /**
     * TREE XML 데이터 저장시 계통트리인지, 교범인지, MYTO인지 구분
     */
    public static final String TREE_KIND_SYSTEM		= "01";
    public static final String TREE_KIND_TO			= "02";
    public static final String TREE_KIND_MYTO		= "03";


    /**
     * 계통트리(System Tree) 타입 구분
     * SYSTEM			01
     * SUBSYSTEM		02
     * ASSEMBLY		03
     * SUBASSEMBLY	04
     * COMPONENT	05
     * TO					06
     */
    public static final String SYS_TYPE_SYS			= "01";
    public static final String SYS_TYPE_SUBSYS		= "02";
    public static final String SYS_TYPE_ASS			= "03";
    public static final String SYS_TYPE_SUBASS		= "04";
    public static final String SYS_TYPE_COMP			= "05";
    public static final String SYS_TYPE_TO				= "06";


    /**
     * TO목차(eXPIS Tree) 타입 구분
     * TO				""
     * INTRODUCTION	01
     * CHAPTER		02
     * SECTION		03
     * TOPIC			04
     * SUBTOPIC	05
     * TASK				06
     * WC				11	WC - 부대급
     * F_WC			12	WC - 야전급
     * IPB				16	IPB - 부대급
     * IPB2				17	IPB - 야전급
     * IPBROOT		18
     * DI					21	FI - 결함식별 및 설명
     * DI_DESC		22	FI -
     * FI_A				23	FI -
     * LR					24	FI - 로그북리포트
     * LR_DESC		25	FI -
     * AP				26	FI -
     * FI_PIC			27	FI -
     * DDS				28	FI -
     * FI_B				29	FI -
     */
    public static final String	STOCO_TYPE_TO				="";
    public static final String	STOCO_TYPE_INTRO			= "01";
    public static final String	STOCO_TYPE_CHAPTER		= "02";
    public static final String	STOCO_TYPE_SECTION		= "03";
    public static final String	STOCO_TYPE_TOPIC			= "04";
    public static final String	STOCO_TYPE_SUBTOPIC	= "05";
    public static final String	STOCO_TYPE_TASK			= "06";

    public static final String	STOCO_TYPE_WC_UNIT	= "11";
    public static final String	STOCO_TYPE_WC_FIELD	= "12";

    public static final String	STOCO_TYPE_IPB_UNIT	= "16";
    public static final String	STOCO_TYPE_IPB_FIELD	= "17";
    public static final String	STOCO_TYPE_IPB_ROOT	= "18";

    public static final String	STOCO_TYPE_FI_DI			= "21";
    public static final String	STOCO_TYPE_FI_DIDESC	= "22";
    public static final String	STOCO_TYPE_FI_FIA			= "23";
    public static final String	STOCO_TYPE_FI_LR			= "24";
    public static final String	STOCO_TYPE_FI_LRDESC	= "25";
    public static final String	STOCO_TYPE_FI_AP			= "26";
    public static final String	STOCO_TYPE_FI_FIPIC		= "27";
    public static final String	STOCO_TYPE_FI_DDS		= "28";
    public static final String	STOCO_TYPE_FI_FIB			= "29";


    /**
     * 작업카드(WC) 타입
     */
	/*
	public static final String	WC_TYPE_A				= "01";
	public static final String	WC_TYPE_AIMG		= "02";
	public static final String	WC_TYPE_B				= "03";
	public static final String	WC_TYPE_BIMG		= "04";
	public static final String	WC_TYPE_B2			= "05";
	public static final String	WC_TYPE_C				= "06";
	public static final String	WC_TYPE_C2			= "07";
	public static final String	WC_TYPE_D				= "08";
	public static final String	WC_TYPE_E				= "09";
	public static final String	WC_TYPE_F				= "10";
	*/


    /**
     * TO DB 저장 여부
     * Y	: DB 저장 YES
     * N	: DB 저장 NO
     */
    public static final String TOSAVE_YN_YES			= "Y";
    public static final String TOSAVE_YN_NO			= "N";


    /**
     * TO 등록시 어떤 종류 업데이트인지 속성 설정 구분
     * 01 : TO 등록 여부
     * 02 : TO 프레임보기 여부
     */
    public static final String TOPROP_YN_SAVE		= "01";
    public static final String TOPROP_YN_FVIEW		= "02";


    /**
     * TO 등록시 FI/IPB처럼 템플릿 형태의 컨텐츠는 직접적인 하위 내용 없는 목차에도 가상 태그 등록위함
     */
    public static final int REG_TITLE_DESC		= 1;
    public static final int REG_TITLE_FI			= 2;
    public static final int REG_TITLE_IPB			= 3;
    public static final int REG_TITLE_FIPIC		= 4;


    /**
     * TO 등록시 FI는 fi_code 구하는 공식이 DI(결함식별및설명)/LR(로그북리포트) 과 DDS(고장탐구절차)가 다름
     */
    public static final int REG_FI_DILR			= 1;
    public static final int REG_FI_DDS				= 2;


    /**
     * DB에 컨텐츠 넣을때 Insert All로 일괄등록시 list(Connection 연관) 한계 지정
     */
    //public static final int REG_MAX_LIST_LARGE		= 300; //500;
    public static final int REG_MAX_LIST_LARGE		= 100; //500;
    public static final int REG_MAX_LIST_SMALL		= 50;

    //2023 01 03 jysi EDIT : MSSQL IN 구문 사용 시 매개변수 최대값(2100) 에러 발생하여 1000=>250으로 변경
    //                    >> 후에 교범등록 속도이슈 있을 시 ContRegister에서 DBTYPE=MSSQL만 분기처리 필요
    public static final int REG_MAXIMUM				= 250; //1000;


    /**
     * DB에 컨텐츠 넣을때 텍스트 크기 지정(문자열 자를때 사용)
     */
    public static final int REG_MAX_STR_LEN			= 1500;

    public static final int REG_CHECK_STR_LEN		= REG_MAX_LIST_LARGE / 100 * REG_MAX_STR_LEN;


    /**
     * 표(테이블)의 TD(entry) 항목이 제목 항목인지, 아닌지 구분
     */
    public static final String TB_ENTRY_CAPTION_YES			= "1";
    public static final String TB_ENTRY_CAPTION_NO			= "";


    /**
     * 표(테이블) 제목 표시시, 상단인지 하단에 표시인지 구분
     */
    public static final String TB_CAPTION_TOP				= "01";
    public static final String TB_CAPTION_BOTTOM			= "02";
    public static final String TB_CAPTION_FOCUS			= TB_CAPTION_TOP;


    /**
     * 메모 공유 여부
     * Y	: 공유 YES
     * N	: 공유 NO
     */
    public static final String MEMOSHARE_YN_YES			= "Y";
    public static final String MEMOSHARE_YN_NO			= "N";


    /**
     * 메모 본문에 아이콘 표시시 타입 구분
     * 1 : 내가 쓴 비공유 메모
     * 2 : 내가 쓴 공유 메모
     * 3 : 다른 사람이 쓴 공유 메모
     */
    public static final String MEMOSHARE_TYPE1			= "1";
    public static final String MEMOSHARE_TYPE2			= "2";
    public static final String MEMOSHARE_TYPE3			= "3";


    /**
     * 검색시 AND/OR 다중검색 할 경우 식별 기호
     */
    public static final String SEARCH_WORD_MARK			= ",";


    /**
     * 변경바 표시시 변경정보 상태에 따라 다른 스타일로 표시
     */
    public static final String	VER_STATUS_APPEND		= "01";
    public static final String	VER_STATUS_UPDATE		= "02";
    public static final String	VER_STATUS_LATEST		= "03";
    public static final String	VER_STATUS_TP			= "04";


    /**
     * 클라이언트 뷰어가 웹인지 모바일인지 구분
     */
    public static final String	WEBMB_KIND_WEB			= "01";
    public static final String	WEBMB_KIND_MOBILE		= "02";


    /**
     * IPB 타입 정의
     * 20200309 add LYM
     * 1	: 공군(T50처럼)
     * 2	: 해군(HUSS처럼)
     * 3	: 육군()
     */
    public static final int MILIT_AIR		= 1;
    public static final int MILIT_NAVY		= 2;
    public static final int MILIT_ARMY		= 3;


    /**
     * 2023.05.22 - KTA, 특수문자 아이콘 치환 image Tag - jingi.kim
     */
    public static final String TEMPLATE_ICON_TAG_1			= "<img src='%IMGPATH%ietm/icons/template_icon_1.jpg' />";
    public static final String TEMPLATE_ICON_TAG_2			= "<img src='%IMGPATH%ietm/icons/template_icon_2.jpg' />";
    public static final String TEMPLATE_ICON_TAG_3			= "<img src='%IMGPATH%ietm/icons/template_icon_3.jpg' />";
    public static final String TEMPLATE_ICON_TAG_4			= "<img src='%IMGPATH%ietm/icons/template_icon_4.jpg' />";
    public static final String TEMPLATE_ICON_TAG_5			= "<img src='%IMGPATH%ietm/icons/template_icon_5.jpg' />";

    /**
     * 세션 몇 정의
     */
    public static final String SS_ARR_IPB_COLS		= "SS_ARR_IPB_COLS";

}
