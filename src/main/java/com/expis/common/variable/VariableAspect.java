package com.expis.common.variable;

import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;


/**
 * [공통]
 * @FileName		: VariableAspect.java
 */
@Slf4j
@Component
public class VariableAspect {

	/**
	 * 클래스 변수 선언
	 */
	public static String GV_URL				= "";
	public static String GV_BIZ				= "";
	public static String GV_CSS				= "";
	public static String GV_IMG				= "";
	public static String GV_JS				= "";
	public static String GV_JSP				= "";
	public static String GV_IETMDATA		= "";
	public static String GV_SYSPATH			= "";
	public static String GV_WEBDATA_SYSPATH	= "";
	public static String GV_MODEL_IMG		= "";	//20201118 add LYM
	public static String GV_BIZ_CODE		= "";	//20201123 add LYM
	public static String GV_TYPE			= "";	//20211221 add Park Js
	public static String GV_bizCode            = "";   //20221121 add jysi
	public static String GV_DBTYPE            = "";   //20230103 add jysi
	public static String GV_SHAPE_TYPE            = "";
	
	private static String setLang;
	@Value("${app.expis.language}")
	public void setLang(String value) {
		setLang = value;
	}

	/**
	 * View 영역에서 사용될 파일 및 이미지 경로 셋팅
	 * IConstants.BIZ_(*) 변수와 webpath.properties 의 (*)_IMG 변수와 매칭
	 * 
	 * @MethodName	: setting
	 * @param request
	 * @throws Exception
	 */
	public static void setting(HttpServletRequest request) throws Exception {
		
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	    ExternalFileEx ext = ctx.getBean("extConf",ExternalFileEx.class);

		try {
			HttpSession hs = request.getSession();
			String bizName = StringUtil.checkNull((String) hs.getAttribute("SS_BUSINESS"));
			String css = "";
			String img = "";
			String lang = (String) hs.getAttribute("lang");
			bizName = "DF"; //default
			log.info("lang A : "+lang);
			if(lang == null) {
				lang = setLang;
			}
			if(lang == null) {
				lang = "ko";
			}
			log.info("lang B : "+lang);
			if(lang.equals("ko")) {
				css = ext.getDF_CSS();
				img = ext.getDF_IMG();
			} else {
				css = ext.getDF_EN_CSS();
				img = ext.getDF_EN_IMG();
			}
			
			String project 			= ext.getPROJECT();
			String business 		= ext.getDF_BIZ();
			String js 				= ext.getDF_JS();
			String jsp 				= ext.getDF_JSP();
			String ietmdata 		= ext.getDF_IETMDATA();
			String syspath 			= ext.getDF_SYSPATH();
			String webdataSyspath 	= ext.getDF_WEBDATA_SYSPATH();
			String cover 			= ext.getDF_COVER();
			String modelImg 		= ext.getModelImg();
			String bizCode 			= ext.getBizCode();
			//2022 11 21 jysi ADD : commonValidate에 있는 bizCode프로퍼티 세팅추가
			String commonBizCode    = ext.getCommonBizCode();
			//2023 01 03 jysi ADD : webpath에 있는 DBTYPE프로퍼티 세팅추가
			String dbtype           = ext.getDBTYPE();
			
			String shapetype 		= ext.getSHAPE_TYPE();
			
			log.info("ext : "+ext.toString());
			/**
			 * 2021 12 21 Add Park Js
			 * 오류날경우 대비해서 기본값 추가
			 */
			String type 			= ext.getType();
			if(type == null || "".equals(type)){type = "T.M.";}
			hs.setAttribute("GV_COVER"			, cover);
			hs.setAttribute("GV_URL"			, project);
			hs.setAttribute("GV_BIZ"			, business);
			hs.setAttribute("GV_CSS"			, css);
			hs.setAttribute("GV_IMG"			, img);
			hs.setAttribute("GV_JS"				, js);
			hs.setAttribute("GV_JSP"			, jsp);
			hs.setAttribute("GV_IETMDATA"		, ietmdata);
			hs.setAttribute("GV_SYSPATH"		, syspath);
			hs.setAttribute("GV_WEBDATA_SYSPATH", webdataSyspath);
			hs.setAttribute("GV_MODEL_IMG"		, modelImg);
			hs.setAttribute("GV_BIZ_CODE"		, bizCode);
			hs.setAttribute("GV_TYPE"			, type);
			hs.setAttribute("GV_bizCode"           , commonBizCode);
			hs.setAttribute("GV_DBTYPE"           , dbtype);
			hs.setAttribute("GV_SHAPE_TYPE"           , shapetype);
			
			GV_URL				= project;
			GV_BIZ				= business;
			GV_CSS				= css;
			GV_IMG				= img;
			GV_JS				= js;
			GV_JSP				= jsp;
			GV_IETMDATA			= ietmdata;
			GV_SYSPATH			= syspath;
			GV_WEBDATA_SYSPATH	= webdataSyspath;
			GV_MODEL_IMG		= modelImg;
			GV_BIZ_CODE			= bizCode;
			GV_TYPE				= type;
			GV_bizCode          = commonBizCode;
			GV_DBTYPE           = dbtype;
			GV_SHAPE_TYPE		= shapetype;
			
		} catch (Throwable ta) {
			ta.printStackTrace();
			log.info("VariableAspect setting error!!"+ta.toString());
		}
	}

}
