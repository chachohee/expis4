package com.expis.domparser;

/**
 * [공통모듈]XML DOM에서 특정 노드 추출하는 XPath 쿼리 정의 Class
 */
public class XALAN {

	/**
	 * 테이블 내 <colgroup> 생성 위한 XPath 쿼리
	 */
	public static final String	TBXALAN_COLGROUP	= "./" + DTD.TB_ROWH + "[1]/" + DTD.TB_ENTRY;
	
	
	/**
	 * 아이콘 목록 추출 위한 XPath 쿼리
	 */
	public static final String	ICON_LIST					= "//"+DTD.ICON;
	
	
	/**
	 * 그래픽 하위에 캡션과 별개로 표(테이블) 추출 위한 XPath 쿼리
	 */
	public static final String	GRPH_CAPTION_TB		= "./"+DTD.TABLE;
	
	
	/**
	 * 그림/표목차 목록 추출 XPath 쿼리
	 */
	public static final String	TOCOXALAN_GRPH		= "//" + DTD.SYSTEM + "[@" + ATTR.TYPE + "='" + VAL.TOCOLIST_GRPH + "']";
	public static final String	TOCOXALAN_TABLE		= "//" + DTD.SYSTEM + "[@" + ATTR.TYPE + "='" + VAL.TOCOLIST_TABLE + "']";
	//2023.05.10 jysi EDIT : 동영상목차 목록 추출 XPath 쿼리 추가
	public static final String	TOCOXALAN_VIDEO		= "//" + DTD.SYSTEM + "[@" + ATTR.TYPE + "='" + VAL.TOCOLIST_VIDEO + "']";
	
	
	/**
	 * JG교범 준비사항 내 XPath 쿼리
	 */
	public static final String	INXALAN_REQCOND		= "./"+DTD.IN_REQCOND;
	public static final String	INXALAN_PERSON			= "./"+DTD.IN_PERSON;
	public static final String	INXALAN_EQUIP			= "./"+DTD.IN_EQUIP;
	public static final String	INXALAN_CONSUM		= "./"+DTD.IN_CONSUM;
	public static final String	INXALAN_ALERT			= "./"+DTD.IN_ALERT;
	public static final String	INXALAN_OTHERCOND	= "./"+DTD.IN_OTHERCOND;
	public static final String	INXALAN_LINKINFO	= "./"+DTD.IN_LINKINFO;
	
	
	/**
	 * IPB교범 내 XPath 쿼리
	 */
	public static final String	IPB_REF				= "//" + DTD.IPB_PARTINFO + "[@" + ATTR.GRPH_REF_ID + " != '']";
	public static final String	IPB_GRPH				= "//" + DTD.GRPH + "[@" + ATTR.GRPH_PATH + " != '']";
	public static final String	IPB_GRPH_REF				= "//" + DTD.GRPH + "[@" + ATTR.GRPH_REF + " != '']";
	public static final String	IPB_PARTINFO_ALL	= "//" + DTD.IPB_PARTINFO;
	public static final String	IPB_PARTINFO		= "./" + DTD.IPB_PARTINFO;
	public static final String	IPB_PARTBASE		= "./" + DTD.IPB_PARTBASE;
	
	
	/**
	 * FI교범 내 XPath 쿼리
	 * FI_FAULTINF					: 본인과 형제노드 중 <faultinf> 찾기
	 * FI_SUPPLE_PRECED		: 이전 형제 노드에서 <descinfo> 나 <task>
	 * FI_SUPPLE_FOLLOW	: 다음 형제 노드에서 <descinfo> 나 <task>
	 */
	public static final String	FI_FAULTINF					= "./self::faultinf | following-sibling::faultinf";
	public static final String	FI_SUPPLE_PRECED		= "./preceding-sibling::descinfo[@type=''] | ./preceding-sibling::task";
	public static final String	FI_SUPPLE_FOLLOW		= "./following-sibling::descinfo | ./following-sibling::task";
	
	
	/**
	 * WC교범 내 XPath 쿼리
	 * WC_WING	: TypeB 일 경우 이미지 <wimg> 찾기
	 */
	public static final String	WC_WIMG					= ".//wimg";
	
	
	/**
	 * DB등록(Register) - 계통트리(System Tree) 등록 내 XPath 쿼리
	 */
	public static final String	REG_SYSTEM				= ".//" + DTD.SYSTEM;
	public static final String	REG_SYSTEM_TO			= ".//" + DTD.SYSTEM + "[@" + ATTR.TYPE + "='" + VAL.SYS_TYPE_TO + "']";
	public static final String	REG_VERSION				= ".//" + DTD.VERSION;
	public static final String	REG_VERSION_FIRST	= ".//" + DTD.VERSION + "[1]";
	public static final String	REG_VERSION_LAST		= ".//" + DTD.VERSION + "[last()]";
	
	
	/**
	 * DB등록(Register) - 교범 내용 및 버전 내용 등록 내 XPath 쿼리
	 */
	public static final String	REG_ICON					= "//" + DTD.ICON;
	public static final String	REG_ALERT					= "//" + DTD.ALERT;
	public static final String	REG_GRPH					= "//" + DTD.GRPH + "[@external-ptr!='']";
	public static final String	REG_WCS						= "//" + DTD.WC_WCS;
	public static final String	REG_TABLE					= "//" + DTD.TABLE;
	public static final String	REG_TABLE_ROWH		= "//" + DTD.TB_ROWH;	//WUC 구할때 사용
	public static final String	REG_TABLE_CAPTION	= DTD.TB_ROWH + "/" + DTD.TB_ENTRY + "[@" + ATTR.TB_CAPTION + "='1']";
	public static final String	REG_TABLE_ICON			= ".//" + DTD.TEXT + "[contains(text(),'&#24;') or contains(text(),'&amp;#24;')]";
	
	
	/**
	 * DB등록(Register) - 교범 내용 중 검색 관련 내용 등록 내 XPath 쿼리
	 */
	public static final String	REG_FI							= ".//" + DTD.SYSTEM + "[@type='FI' or @type='FI_FIELD']";
	//public static final String	REG_FI_DILR				= "./system/system/" + DTD.SYSTEM + "[@type='DI' or @type='LR']/system[@type='DI_DESC' or @type='LR_DESC']/faultinf/descendant::node[@nodekind='FaultCodeLabel']";
	//public static final String	REG_FI_DDS					= "./system/system/" + DTD.SYSTEM + "[@type='DDS']/faultinf";
	public static final String	REG_FI_DILR				= "./" + DTD.SYSTEM + "[@type='DI' or @type='LR']/system[@type='DI_DESC' or @type='LR_DESC']/faultinf/descendant::node[@nodekind='FaultCodeLabel']";
	public static final String	REG_FI_DDS					= "./" + DTD.SYSTEM + "[@type='DDS']/faultinf";
	public static final String	REG_FI_AP_GRPH			= ".//" + DTD.GRPH + "[@id!='']";
	public static final String	REG_FI_TEXT				= ".//" + DTD.TEXT;
	
	public static final String	REG_IPB						= "./system/" + DTD.IPB_PARTINFO;
	public static final String	REG_IPB_UNIT				= "//" + DTD.IPB_PARTINFO;
	public static final String	REG_IPB_PDREFID		= "//" + DTD.IPB_PARTINFO + "[@p_d_refid!='']";
	public static final String	REG_IPB_PBASE			= "//" + DTD.TEXT + "/parent::partbase";
	public static final String	REG_IPB_PBASE_ALL	= "//" + DTD.IPB_PARTBASE;
	public static final String	REG_IPB_TEXT				= "./" + DTD.TEXT;
	public static final String	REG_IPB_PINFO_CHILD	= "./" + DTD.IPB_PARTINFO;
	public static final String	REG_IPB_PBASE_CHILD	= "./" + DTD.IPB_PARTBASE;
	public static final String	REG_IPB_PBASE_CCHILD	= "./" + DTD.IPB_PARTINFO + "/" + DTD.IPB_PARTBASE;
	public static final String	REG_IPB_GRPH_SIBL		= "./following-sibling::grphprim[@external-ptr!='']";
	public static final String	REG_IPB_GRPH_CHILD		= "./grphprim[@external-ptr='' and @ref!='']";
	
	public static final String	REG_TEXT						= "//" + DTD.TEXT;
	public static final String	REG_DESC					= "./system/" + DTD.DESC;
	
	public static final String	REG_TOCO_COMM			= ".//system[1]";
	
	
	
	public static String getSystemFromToXPathSql(String itemId) {
		
		String rtStr = "";
		rtStr = "//system[@type='TO' and @itemid='" + itemId + "']";
		
		return rtStr;
	}

	
	/**
	 * 그래픽 등록시 IPB의 <grphprim ref=''> 에 대한 처리 쿼리
	 * @MethodName	: getGrphFromIdXPathSql
	 * @AuthorDate		: LIM Y.M. / 2016. 10. 4.
	 * @ModificationHistory	: 
	 * @param refId
	 * @return
	 */
	public static String getGrphFromIdXPathSql(String refId) {
		
		String rtStr = "";
		//rtStr = ".//" + DTD.GRPH + "[@" + ATTR.ID + "='" + id + "']";
		
		String[] arrRefId = refId.split(CHAR.REG_GRPH_COMMA);
		for (int i=0; i<arrRefId.length; i++) {
			if (!rtStr.equals("")) {
				rtStr += " or ";
			}
			rtStr += "@" + ATTR.ID + "='" + arrRefId[i] + "'";	
		}
		
		if (!rtStr.equals("")) {
			rtStr = ".//" + DTD.GRPH + "[" + rtStr + "]";
		}
		
		return rtStr;
	}
	
	
	public static String getGrphFromPartIdXPathSql(String pdRefid) {
		
		String rtStr = "";
		rtStr = "//" + DTD.IPB_PARTINFO + "[@" + ATTR.ID + "='" + pdRefid + "']/" + DTD.GRPH + "[@" + ATTR.GRPH_PATH + "!='']";
		
		return rtStr;
	}
	
	
	public static String getGrphFromRefXPathSql(String arrRef) {
		
		String rtStr = "";
		rtStr = "./following-sibling::grphprim[@external-ptr!='' and (" + arrRef + ")]";
		
		return rtStr;
	}
	

	/**
	 * Table 표 안에 내용중에 아이콘 있을 경우, 아이콘 노드 추출
	 * @MethodName	: getIconFromIdXPathSql
	 * @AuthorDate		: LIM Y.M. / 2016. 9. 22.
	 * @ModificationHistory	: 
	 * @param iconId
	 * @return
	 */
	public static String getIconFromIdXPathSql(String iconId) {
		
		String rtStr = "";
		rtStr = ".//" + DTD.ICON + "[@" + ATTR.ICON_ID + "='" + iconId + "']";
		
		return rtStr;
	}


	/**
	 * eXPIS.xml 파싱중에 Toco 목차안에 <system version=''> 정보를 통해 <version id=''> 찾는 쿼리
	 * @MethodName	: getVerXPathSql
	 * @AuthorDate		: LIM Y.M. / 2016. 9. 10.
	 * @ModificationHistory	: 
	 * @param verId
	 * @return
	 */
	public static String getVerFromIdXPathSql(String verId) {
		
		String rtStr = "";
		rtStr = ".//" + DTD.VERSION + "[@" + ATTR.ID + "='" + verId + "']";
		
		return rtStr;
	}
	
	
	/**
	 * oldversiondata 내 UUID_C32.xml 이전버전 파일명의 버전번호로 eXPIS.xml 의 <version changeno=''> 찾는 쿼리
	 * @MethodName	: getVerNoXPathSql
	 * @AuthorDate		: LIM Y.M. / 2016. 9. 20.
	 * @ModificationHistory	: 
	 * @param verNo
	 * @return
	 */
	public static String getVerFromNoXPathSql(String verNo) {
		
		String rtStr = "";
		rtStr = ".//" + DTD.VERSION + "[@" + ATTR.SYS_VER_CHGNO + "='" + verNo + "']";
		
		return rtStr;
	}
	
}
