package com.expis.domparser;

import com.expis.common.IConstants;
import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.ietm.parser.ParserDto;
import com.expis.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * [공통모듈]HTML로 보여줄 CSS 스타일시트 태그 정의 Class
 */
@Slf4j
public class CSS {

	/**
	 * 제목 관련 CSS 및 HTML 태그
	 */
	public static String SECTION_REGEX			= "#SECTION#";
	public static String DIV_CHAPTER			= "<div class='ac-chapter'>";
	public static String DIV_SECTION			= "<div class='ac-section'><a href='javascript:;' class='ac-arrow-open' onclick='javascript:sectionOpen(this);'></a>";
	public static String DIV_INTRO				= "<div class='introduction'>";
	
	/**
	 * 내용(descinfo, task) 관련 CSS 및 HTML 태그 
	 */
	public static String DIV_CONT			= "<div class='ac-content'>";
	public static String REGEX				= "#STYLE#";
	public static String TOPIC_REGEX 		= "#TOPIC#";
	public static String SUB_TOPIC_REGEX 	= "#SUB_TOPIC#";
	private final  static String DIV_TOPIC			= "<div class='ac-topic'><a href='javascript:;' class='ac-arrow-open' onclick='javascript:topicOpen(this);'></a>";
	private final  static String DIV_SUBTOPIC		= "<div class='ac-sub-topic'><a href='javascript:;' class='ac-arrow-open' onclick='javascript:subTopicOpen(this);'></a>";
	private final  static String DIV_DESC			= "<div class='ac-desc' >";
	private final  static String DIV_TASK			= "<div class='ac-task' >";
	public static String SPAN_SUBTOPIC		= "<span class='sub-topic-tit'>";
	
	/**
	 * 경고창 관련 CSS 및 HTML 태그
	 */
	public static String DIV_WARNING	= "<div class='ac-alert-warning alert_class'>";
	public static String DIV_CAUTION	= "<div class='ac-alert-caution alert_class'>";
	public static String DIV_NOTE		= "<div class='ac-alert-note alert_class'>";
	public static String DIV_WARNING_NT	= "<div class='ac-alert-warning_nt alert_class'>";
	/**
	 * 2022 08 11 ADD 경고창 관련 CSS 및 HTML 태그 ENG
	 */
	public static String DIV_WARNING_ENG	= "<div class='ac-alert-warning_eng alert_class'>";
	public static String DIV_CAUTION_ENG	= "<div class='ac-alert-caution_eng alert_class'>";
	public static String DIV_NOTE_ENG		= "<div class='ac-alert-note_eng alert_class'>";
	public static String DIV_WARNING_NT_ENG	= "<div class='ac-alert-warning_nt_eng alert_class'>";
	
	/**
	 * DIV 태그 종료
	 */
	public static String DIV						= "<div>";
	public static String DIV_END				= "</div>";
	
	/**
	 * P 태그
	 */
	public static String P							= "<p>";
	public static String P_END				= "</p>";
	
	/**
	 * 내용 중 descinfo>para 상세 관련 CSS 및 HTML 태그 
	 */
	public static String P_PARA1				= "<span class='para1'>";
	public static String P_PARA				= "<span class='para" + REGEX + "'>";
	public static String P_PARA_ALERT	= "<p class='para1'>";
	
	/**
	 * 내용 중 task>step 상세 관련 CSS 및 HTML 태그 
	 */
	public static String P_STEP1				= "<p class='step1'>";
	public static String P_STEP				= "<p class='step" + REGEX + "'>";
	
	/**
	 * 표(테이블) 관련 CSS 및 HTML 태그 
	 */
	public static String TB_TABLE_NONE			= "<table class='in_table_none'>";
	public static String TB_TABLE_NONE_SUB			= "<table class='in_table_none_sub'>";
	public static String TB_TABLE			= "<table class='in_table'>";
	public static String TB_TABLEEND		= "</table>";
	public static String TB_TR					= "<tr style='border-top:0;'>";
	public static String TB_TREND			= "</tr>";
	private final  static String TB_TDREG			= "<td " + REGEX + ">";
	public static String TB_TD					= "<td>";
	public static String TB_TDEND			= "</td>";
	private final  static String TB_THREG			= "<th " + REGEX + ">";
	public static String TB_TH					= "<th>";
	public static String TB_THEND			= "</th>";
	public static String TB_COLGROUP	= "<colgroup>";
	public static String TB_COLWIDTH	= "<col width='" + REGEX + "'/>";
	public static String TB_COLEND		= "</colgroup>";
	
	/**
	 * 그래픽 관련 CSS 및 HTML 태그
	 * DIV_OBJECT makeFlashString(id, src, width, height, param)
	 * 					makeJpegString(id, src, width, height, attr)
	 * DIV_GRPH_TABLE : 그래픽 내에 캡션(text) 위에 표(table) 있을 경우 처리
	 */
	public static String DIV_OBJECT			= "<div class='ac-object'>";
	public static String DIV_GRPH_TABLE	= "<div class='in_table_size1'>";
	
	/**
	 * CodeConverter 내에서 사용하는 CSS 및 HTML 태그
	 */
	public static final String	BR				= "<br />";
	public static final String	SPAN_CC		= "<span style='font-size:9pt;'>";
	public static final String	FONT_CC		= "<font style='font-weight:bold; color:#F00'>";
	public static final String	FONT_END	= "</font>";
	public static final String	SUP				= "<sup>";
	public static final String	SUP_END		= "</sup>";
	public static final String	SUB				= "<sub>";
	public static final String	SUB_END		= "</sub>";
	
	/**
	 * 데이터가 없거나 이미지가 없을 경우
	 */
	public static final String 	DIV_NO_DATA			= "<div>" + TITLE.NO_DATA + "</div>";
	public static final String 	DIV_NO_IMAGE		= "<div class='ac-noimg'></div>";
	//20191010 edit LYM 원문자 표기시 이미지로 표현안하고 스타일시트로 정의
	public static final String  IMG_REGEX = "IMGCNT";
	public static final String 	DIV_CIRCLE_TEXT	= "<span class='char_circle'>" + IMG_REGEX + "</span>";
	
	public static final String 	NBSP	= "&nbsp;";
	
	
	/**************************************************/
	
	
	/**
	 * JG교범의 준비사항 관련 CSS 및 HTML 태그
	 */
	public static String DIV_INPUT			= "<div class='ac-input'>";
	public static String UL_INPUT			= "<ul class='input-item'>";
	public static String UL_END				= "</ul>";
	public static String LI						= "<li>";
	public static String LI_END				= "</li>";
	public static String UL						= "<ul>";
	public static String SPAN					= "<span>";
	public static String SPAN_END			= "</span>";
	public static String TB_TABLE_JG		= "<table class='in_table_jg'>";
	
	
	/**
	 * IPB교범 관련 CSS 및 HTML 태그
	 */
	public static String DIV_IPB							= "<div id='ipb_main' class='main_container'>";
	public static String DIV_IPB_CONT				= "<div id='ipb_cont' class='ipb_cont'>";
	public static String DIV_IPB_GRPH				= "<div id='ipb_grph' class='cont_l'>";
	public static String DIV_IPB_TABLE_P				= "<div>";
	public static String DIV_IPB_TABLE_P_END				= "</div>";
	public static String DIV_IPB_TABLE				= "<div id='ipb_div_h' class='cont_r'>";
	public static String DIV_IPB_TABLE_SCROLL		= "<div id='ipb_div_h_scroll' class='cont_r'>";
	public static String DIV_IPB_CONTROL			= "<div id='ipb_control' class='control'>";
	public static String DIV_IPB_END					= "</div></div>";
	
	public static String DIV_IPB_GRPH_SHOW	= "<div id='ipb_image' class='ipb_image'>";
	public static String DIV_IPB_PAGINGLIST		= "<div class='ipb_paginglist'>";
	
	public static String TB_TABLE_IPB		= "<table class='in_table_ipb' width='" + REGEX + "'>";
	public static String TB_THEAD				= "<thead>";
	public static String TB_TBODY				= "<tbody>";
	public static String TB_TR_IPB				= "<tr id='" + REGEX + "' class='part_tr'>";
	public static String TB_TR_IPB_NAME			= "<tr name='" + REGEX + "' class='part_tr'>";
	public static String TB_TR_IPB_NAME_SEARCH	= "<tr name='" + REGEX + "' class='part_tr' style=\"background-color: #FF0;\">";
	public static String TB_THEAD_END		= "</thead>";
	public static String TB_TBODY_END		= "</tbody>";
	
	public static String A_END	= "</a>";
	public static String B				= "<b>";
	public static String B_END	= "</b>";
	
	
	/**
	 * FI 교범 관련 CSS 및 HTML 태그
	 */
	public static String DIV_FI_CONT		= "<div class='fi-content'>";
	private final  static String DIV_FI_TOPIC	= "<div class='fi-topic'><a href='javascript:;' class='ac-arrow-open' id='ac-title_"+REGEX+"'  onclick='topicOpen(this);'></a>";
	private final  static String DIV_FI_TASK	= "<div class='fi-task' id='ac-div_" + REGEX + "'>";
	public static String DIV_FI_LINK		= "<div class='fi-link'>";
	
	public static String UL_FI					= "<ul class='fi-input-item'>";
	
	public static String P_FI_TB_NUM	= "<p class='num'>";
	public static String P_FI_TB_CODE	= "<p class='column'>";
	public static String P_FI_TB_ULINE	= "<p class='lin-underline'>";
	public static String P_FI_TB_DI		= "<p class='di'>";
	
	public static String P_FI_TD_NUM	= "<td class='num'>";
	public static String P_FI_TD_CODE	= "<td class='column'>";
	public static String P_FI_TD_ULINE	= "<td class='lin-underline'>";
	public static String P_FI_TD_DI		= "<td class='di'>";
	
	
	/**
	 * WC 교범 관련 CSS 및 HTML 태그
	 */
	public static String TB_TABLE_WC	= "<table class='in_table_wc'>";
	public static String TB_TABLE_WC2	= "<table class='in_table_wc_dep2'>";
	public static String TB_TABLE_WC_WIMG	= "<table class='in_table_wc wimg'>";
	public static String TB_TFOOT			= "<tfoot>";
	public static String TB_TFOOT_END	= "</tfoot>";
	public static String INPUT_CHK			= "<input type='checkbox' value='' name=''>";
	public static String SPAN_WC_STEP1	= "<span class='wcstep1'>";
	public static String SPAN_WC_STEP2	= "<span class='wcstep2'>";
	
	public static String P_WC_STEP1		= "<p class='wcstep1'>";
	public static String P_WC_STEP2		= "<p class='wcstep2'>";
	
	public static String SIZE_WC_ICON	= "16px";
	
	
	
	/**************************************************/
	
	
	/**
	 * 표(테이블) 내 스타일 관련 CSS 및 HTML 태그
	 */
	public static final String STY_VAL_TOP			= "top";
	public static final String STY_VAL_BOTTOM	= "bottom";
	
	/**
	 * 정렬 스타일 관련 CSS 및 HTML 태그
	 */
	public static final String STY_AL_CENTER		= "center";
	public static final String STY_AL_LEFT			= "left";
	public static final String STY_AL_RIGHT		= "right";
	
	
	/**************************************************/
	@Value("${app.expis.project}")
	private static String PROJECT;
	
	public static String getDivSection() {
		
		return CSS.DIV_SECTION;
	}
	
	public static String getDivTopic() {
		
		return CSS.DIV_TOPIC;
	}
	
	public static String getDivSubTopic() {

		return CSS.DIV_SUBTOPIC;
	}
	
	public static String getDivDesc() {
		
		return CSS.DIV_DESC;
	}

	public static String getDivTask() {
		
		return CSS.DIV_TASK;
	}

	public static String circleClass(String num) {
		return StringUtil.replace(CSS.DIV_CIRCLE_TEXT, CSS.IMG_REGEX, num);
	}
	
	
	/**
	 * 텍스트 시현 - para, step
	 */
	public static String getSpanPara(String attr) {
		
		return StringUtil.replace(CSS.P_PARA, CSS.REGEX, attr);
	}
	
	public static String getSpanStep(String attr) {
		return StringUtil.replace(CSS.P_STEP, CSS.REGEX, attr);
	}

	
	/**
	 * 테이블 관련 - 태그 내에 변경되는 값 적용시 메소드로 별도 구현
	 */
	public static String getTableTd(String attr) {
		
		return StringUtil.replace(CSS.TB_TDREG, CSS.REGEX, attr);
	}
	
	public static String getTableColWidth(String attr) {
		
		return StringUtil.replace(CSS.TB_COLWIDTH, CSS.REGEX, attr);
	}
	
	public static String getTableTh(String attr) {
		
		return StringUtil.replace(CSS.TB_THREG, CSS.REGEX, attr);
	}
	
	public static String getTableTh(int rowspan, int colspan) {
		
		String temp = "rowspan='" + rowspan + "' colspan='" + colspan + "'";
		
		return StringUtil.replace(CSS.TB_THREG, CSS.REGEX, temp);
	}
	
	public static String getTableTd(int rowspan, int colspan) {
		
		String temp = "rowspan='" + rowspan + "' colspan='" + colspan + "'";
		
		return StringUtil.replace(CSS.TB_TDREG, CSS.REGEX, temp);
	}

	
	/**
	 * 그래픽 관련 - 태그 내에 변경되는 값 적용시 메소드로 별도 구현
	 */
	public static String getDivFlash(String grphId, String grphName, String gWidth, String gHeight, String param) {
		
		String rtStr = "";
		rtStr = "<object>" 
				+ "<embed id='" +grphId + "' src='" + grphName + "' width='" + gWidth + "' height='" + gHeight + "' wmode='Opaque' />"
				+ "</object>";
		
		return rtStr;
	}
	
	public static String getDivJpeg(String grphId, String grphName, String gWidth, String gHeight, String param) {
		
		String rtStr = "";
		rtStr = "<img id='" + grphId + "' src='" + grphName+ "' width='" + gWidth + "' height='" + gHeight + "' class='graphic' alt='' />";
		
		return rtStr;
	}
	
	public static String getDivVideo(String grphId, String grphName, String gWidth, String gHeight, String param) {
		if(PROJECT == null) {

			AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
			ExternalFileEx ext = ctx.getBean("extConf",ExternalFileEx.class);
			PROJECT = ext.getPROJECT();
		}
		String rtStr = "";

		rtStr = "<a title='동영상 보기' class='popup' href='javascript:void(0)' onclick=\"videoOpen('', '" + grphName + "', '" + gWidth + "', '" + gHeight + "')\">"
				+ "<img src='"+PROJECT+"web/image/comm/ico_video.png' title='동영상보기' />"
				+ "</a>";
		
		return rtStr;
	}

	public static String getDivAudio(String grphId, String grphName, String gWidth, String gHeight, String param) {
		
		String rtStr = "";
		rtStr = "<audio controls>"
				+ "<source id='" + grphId + "' src='" + grphName+ "' width='" + gWidth + "' height='" + gHeight + "' type='audio/mpeg' />"
				+ "</audio>";
		
		return rtStr;
	}
	
	public static String getDivVR(String grphId, String grphName, String gWidth, String gHeight, String param) {
		
		String rtStr = "";
		rtStr = "<object>" 
				+ "<embed id='" +grphId + "' src='" + grphName + "' width='" + gWidth + "' height='" + gHeight + "' wmode='Opaque' />"
				+ "</object>";
		
		return rtStr;
	}
	
	/**
	 * 아이콘을 img 테그로 반환 
	 */
	public static String getDivIcon(String grphId, String grphName, String gWidth, String gHeight, String param) {
		
		String rtStr = "";
		rtStr = "<img id='" + grphId + "' src='" + grphName+ "' width='" + gWidth + "' height='" + gHeight + "' class='icon' alt='' />";
		if ( "".equalsIgnoreCase(param) ) {
			return rtStr;
		}
		
		String[] args = param.split(",");
		for ( String kp : args ) {
			String[] kv = kp.split("=");
			switch (kv[0]) {
			case "class" :
				rtStr = rtStr.replace("class='icon'", "class='icon "+kv[1]+"'");
				break;
			case "alt" :
				rtStr = rtStr.replace("alt=''", "alt='"+kv[1]+"'");
				break;
			}
		}
		
		return rtStr;
		
	}
	
	
	/**
	 * 내용 중 메모 관련 CSS 및 HTML 태그 
	 */
	public static final String DIV_MEMO_ID			= "memo_id_";
	public static final String DIV_MEMO_DATE	= "yyyy.MM.dd";
	
	/**
	 * 내용 중 메모 관련 CSS 및 HTML 태그 
	 */
	public static String getDivMemo(String memoId, String memoCont, String createUserInfo, String createDate, String shareType, String languageType) {
		
		String rtStr = "";
		
		String iconStyleName = "";
		if (shareType.equals(IConstants.MEMOSHARE_TYPE1)) {
			iconStyleName = "ico-memo-lock";
		} else if (shareType.equals(IConstants.MEMOSHARE_TYPE2)) {
			iconStyleName = "ico-memo-myshare";
		} else {
			iconStyleName = "ico-memo-share";
		}
		
		rtStr = "&nbsp;"
			+ "<div class='" + iconStyleName + "' onMouseOver=\"javascript:memoView('" + memoId + "');\" onMouseOut=\"memoClose('" + memoId + "');\" onClick=\"memoStick('" + memoId + "');\"></div>"
			+ "<div class='memo-form'>"
			+ "	<div id='" + memoId + "' class='memo-body' style='display:none;'>"
			+ "		<div class='content'><p>" + memoCont + "</p></div>";
		if(languageType!=null && "en".equalsIgnoreCase(languageType)) {
			rtStr = rtStr + "<div class='memo-close'><div class='memo-close-user' title='"+createUserInfo+"'>" + TITLE.MEMO_USER_EN + " : " + createUserInfo + "</div><div class='memo-close-date'>" + createDate + "</div></div>"
					+ "	</div>"
					+ "</div>";  
		} else {
			rtStr = rtStr + "<div class='memo-close'><div class='memo-close-user' title='"+createUserInfo+"'>" + TITLE.MEMO_USER + " : " + createUserInfo + "</div><div class='memo-close-date'>" + createDate + "</div></div>"
			+ "	</div>"
			+ "</div>";
		}
			
		return rtStr;
	}
	
	
	/**************************************************/
	
	
	/**
	 * 내용 중 링크 관련 CSS 및 HTML 태그
	 * 단일 링크일 경우에는 getDivSingleLink() 안에 setLinkScript() 한번만 호출해서 사용
	 * 다중 링크일 경우에는 getDivMultiLink() 안에 setLinkScript() 여러번 호출해서 사용
	 */
	public static final String LINK_IS_COVER		= "표지";
	public static final String LINK_IS_CONT		= "";
	public static final String LINK_IDX_FIRST		= "first";
	public static final String LINK_IDX_LAST		= "last";
	public static final String LINK_IDX_MIDDLE	= "";
	public static final String LINK_IDX_SINGLE	= "link-txt";
	public static final String LINK_JS_TO				= "viewExContents"; //"goLinkTo";
	public static final String LINK_JS_TOCO		= "viewExContents"; //"goLinkToco";
	public static final String LINK_JS_GRPH		= "goLinkGrph";
	public static final String LINK_JS_TABLE		= "goLinkTable";
	public static final String LINK_JS_IPB			= "goLinkIpb";
	public static final String LINK_JS_RDN			= "goLinkRdn";
	public static final String LINK_JS_PDF			= "goLinkPdf";
	public static final String LINK_JS_FILE			= "goLinkOutsidefile";
	
	
	/**
	 * 다중(멀티) 링크 스크립트 생성 함수
	 */
	public static String setLinkMultiScript(String sysId, String linkSubject) {
		
		String rtStr = "";
		rtStr += "<span class='link-multi'>";
		rtStr += "<a href='javascript:;' onclick=\"showLinkMulti('" + sysId + "', event);\" class='link-txt'>" + linkSubject + "</a>";
		rtStr += "<span id='link_" + sysId + "' class='link-list'>";

		return rtStr;
	}

	public static String setLinkScript(String uuid, String titleStr, String jScript, String isIndex) {
		
		String rtStr = "";
		rtStr = "<a href='javascript:;' class='" + isIndex + "' onClick=\"javascript:" + jScript+ "\" title='" + titleStr + "'>" + titleStr + "</a>";
		
		return rtStr;
	}
	
	
	/**
	 * 그림목차, 표목차 목록 관련 CSS 및 HTML 태그 
	 * @MethodName	: getSubTocoList
	 * @AuthorDate		: LIM Y.M. / 2015.
	 * @ModificationHistory	: LIM Y.M. / 2016. 8. 9.
	 * @ModificationHistory	: LIM Y.M. / 2017. 1. 21.
	 * @return
	 */
	public static String getSubTocoList(String toKey, String tocoId, String tocoName, String vcontKind, String grphId, String grphName, int nOdd) {
		
		String trStyle = (nOdd == 0) ? "" : "class='lin_bg'";
		
		//그림/표목차를 바로 클릭시엔 해당 그림/표만 나와야하고, 원목차를 눌러야 해당 위치로 이동 - 스크립트명 상이
		String rtStr = "";
		rtStr += "<tr " + trStyle + "><td><a href='javascript:;' onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '', '" + vcontKind + "', '" + grphId + "');\">"
				+ grphName.trim()
				+ "</a> </td><td>"
				+ "<a href='javascript:;' onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '', '" + IConstants.VCONT_KIND_CONT + "', '');\">"
				+ tocoName.trim()
				+ "</a> </td></tr>";
		
		return rtStr;
	}
	public static String TB_TABLE_SUBTOCO	= "<table class='all_list'><colgroup><col width='50%' /><col width='50%' /></colgroup>";
	
	
	/**
	 * 목차 링크 관련 CSS 및 HTML 태그 (경고,그림,표 에서 원목차로 바로 이동시 호출)
	 * @MethodName	: getTocoLink
	 * @AuthorDate		: LIM Y.M. / 2015. 
	 * @ModificationHistory	: LIM Y.M. / 2016. 8. 9.
	 * @return
	 *
	 * 2025.04.17 - osm
	 * 기존 viewExContents 함수 호출 메서드 주석처리
	 * 렌더링 통일화를 위해 tocoContView 함수로 호출하도록 변경
	 */
	public static String getTocoLink(String tocoId, String tocoName, String languageType) {

		String rtStr = "";
		if(languageType!=null && "en".equalsIgnoreCase(languageType)) {rtStr = "<p class='toco-link'><span>Chapter/Title</span> : ";}
		else {rtStr = "<p class='toco-link'><span>해당목차</span> : ";}
		rtStr = rtStr + "<a href='javascript:;' onclick=\"tocoContView('" + tocoId + "');\" style='text-decoration:none;' >"
				+ tocoName
				+ "</a></p>";

		return rtStr;
	}

	/*
	public static String getTocoLink(String toKey, String tocoId, String tocoName, String languageType) {
		
		String rtStr = "";
		if(languageType!=null && "en".equalsIgnoreCase(languageType)) {rtStr = "<p class='link_orginal'><span>Chapter/Title</span> : ";}
		else {rtStr = "<p class='link_orginal'><span>해당목차</span> : ";} 
		rtStr = rtStr + "<a href='javascript:;' onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '', '" + IConstants.VCONT_KIND_CONT + "', '');\">"
				+ tocoName
				+ "</a></p>";
		
		return rtStr;
	}
	*/
	
	/**************************************************/
	
	
	/**
	 * IPB 플래쉬 플레이어 구성
	 * 201? embed 태그로만 하면 핫스팟 이벤트 안먹힘 -> object
	 * 20200318 edit LYM object 태그로만 하면 크롬에서 이미지 시현이 안됨 -> 로 변경 
	 */
	public static String getDivIPBFlash(String grphId, String grphName, String gWidth, String gHeight, String param) {
		
		String rtStr = "";

		rtStr = "<object id='IPBPlayer' name ='IPBPlayer' border='0' width='810' height='755' align='center' "
				+ " classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000'  codebase='swflash.cab'  viewastext>\n"
				+ "  <param name='movie' id='ipb_move' value='" + grphName + "' />\n"
				+ "  <param name='quality' value='high' />\n"
				+ "  <param name='wmode' value='transparent'>\n"
				+ "  <param name='allowScriptAccess' value='always'>\n"
				+ "  <embed id='IPBPlayerEm' name='IPBPlayerEm' src='" + grphName + "' width='345' height='496' wmode='transparent' "
				+ "   align='middle' play='true' loop='true' quality='high' allowScriptAccess='always'"
				+ "   type='application/x-shockwave-flash' pluginspage='http://www.macromedia.com/go/getflashplayer'>\n"
				+ "</object>";
		
		return rtStr;
	}
	
	/**
	 * IPB 모바일 플레이어 구성
	 */
	public static String getDivIPBImg(String grphId, String grphName, String gWidth, String gHeight, String param) {
		
		String rtStr = "";
		rtStr = "<img id='IPBPlayer' name ='IPBPlayer' border='0' width='" + gWidth + "' height='" + gHeight + "' align='center' "
				+ "src='" + grphName + "'>"
				+ "</img>";
		
		return rtStr;
	}


	/**
	 * IPB 표(테이블) 내용 시현시 부품번호와 설명 클릭시 하위 부품으로 이동 관련  CSS 및 HTML 태그
	 */
	public static String getIPBChildMove(String toKey, String tocoId, String scWord) {
		
		String rtStr = "";
		if (tocoId.equals("")) {
			rtStr = "";
		} else {
			rtStr = "<a href='javascript:;' onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + scWord + "', '05', '');\">";
		}
		
		return rtStr;
	}
	
	/**
	 * IPB 표(테이블) 내용 시현시 부품번호와 설명 클릭시 하위 부품으로 이동 관련  CSS 및 HTML 태그 (javascirpt 제거)
	 */
	public static String getIPBChildMove_add(String toKey, String tocoId, String scWord) {
		
		String rtStr = "";
		if (tocoId.equals("")) {
			rtStr = "";
		} else {
			//스크립트 호출시  viewToContents()는 해당 목차의 내용 시현이고, 트리에서 selected 효과까지 주려면 viewExContents() 사용
			rtStr = "viewExContents('" + toKey + "', '" + tocoId + "', '" + scWord + "', '05', ''))\";'";
		}
		
		return rtStr;
	}
	
	
	/**
	 * IPB 표(테이블) 내용 시현시 IPB Control 버튼에서 상위부품 클릭시 상위 부품으로 이동 관련  CSS 및 HTML 태그
	 */
	public static String getIPBControl(String toKey, String tocoId, ParserDto psDto, String prGraphicnum, String prType, String bizCode) {
		
		String scWord = psDto.getSearchWord();
		String rtStr = "";

		if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
			if (tocoId.equals("") && ("LSAM".equalsIgnoreCase(bizCode) || "NLS".equalsIgnoreCase(bizCode)) || "KBOB".equalsIgnoreCase(bizCode) || "KBOB".equalsIgnoreCase(bizCode))  {
				rtStr = "<a href='javascript:;' onclick=\"javascript:alert('(Next)Higher part does not exist');\">";
			} else {
				//2022 07 25 Park.J.S. Update : 상위 메뉴 선택 처리 기능 추가 ipbHighContentCall
				if(tocoId.equals("") && "".equals(prGraphicnum)) {
					rtStr = "<a href='javascript:;' onclick=\"javascript:alert('(Next)Higher part does not exist');\">";
				}else if(!tocoId.equals("")) {
					rtStr = "<a href='javascript:;' onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + scWord + "', '05', '');\">";
				}else if(!"".equals(prGraphicnum)) {
					rtStr = "<a href='javascript:;' onclick=\"ipbHighContentCall('" + prGraphicnum + "', '" + prType + "');\">";
				}else {
					rtStr = "<a href='javascript:;' onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + scWord + "', '05', '');\">";
				}
			}
			rtStr	+= "";
			rtStr	+= "NHA</a>";
			rtStr	+= "<a href='javascript:;' onclick=\"resizeIpbControl('" + IConstants.IPB_CONTROL_GRPH + "');\">FIGURE</a>";
			rtStr	+= "<a href='javascript:;' onclick=\"resizeIpbControl('" + IConstants.IPB_CONTROL_TABLE + "');\">MPL</a>";
			rtStr	+= "<a href='javascript:;' onclick=\"resizeIpbControl('" + IConstants.IPB_CONTROL_ALL + "');\">DEFAULT</a>";
			
			//2023.05.19 jysi EDIT : 영어버전에서도 항목선택기능 사용
			if(!psDto.getWebMobileKind().equals("02")) {
				rtStr	+= "<a href='javascript:;' onclick=\"pupIpbCols();\">ITEM</a>";
			}
			
		}else {

			boolean isCanon = false;
			if ( ("LSAM".equalsIgnoreCase(bizCode) || "NLS".equalsIgnoreCase(bizCode) || "KBOB".equalsIgnoreCase(bizCode) || "KICC".equalsIgnoreCase(bizCode) || "MUAV".equalsIgnoreCase(bizCode) || "SENSOR".equalsIgnoreCase(bizCode)) ) {
				isCanon = true;
			}
			if (tocoId.equals("") && isCanon ) {
				rtStr = "<a href='javascript:;' onclick=\"javascript:alert('상위 부품이 없습니다.');\">";
			} else {
				//2022 07 25 Park.J.S. Update : 상위 메뉴 선택 처리 기능 추가 ipbHighContentCall
				if(tocoId.equals("") && "".equals(prGraphicnum)) {
					rtStr = "<a href='javascript:;' onclick=\"javascript:alert('상위 부품이 없습니다.');\">";
				}else if(!tocoId.equals("")) {
					rtStr = "<a href='javascript:;' onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + scWord + "', '05', '');\">";
				}else if(!"".equals(prGraphicnum)) {
					rtStr = "<a href='javascript:;' onclick=\"ipbHighContentCall('" + prGraphicnum + "', '" + prType + "');\">";
				}else {
					rtStr = "<a href='javascript:;' onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + scWord + "', '05', '');\">";
				}
			}
			
			rtStr	+= "";
			rtStr	+= "상위</a>";
			rtStr	+= "<a href='javascript:;' onclick=\"resizeIpbControl('" + IConstants.IPB_CONTROL_GRPH + "');\">도해도</a>";
			rtStr	+= "<a href='javascript:;' onclick=\"resizeIpbControl('" + IConstants.IPB_CONTROL_TABLE + "');\">테이블</a>";
			rtStr	+= "<a href='javascript:;' onclick=\"resizeIpbControl('" + IConstants.IPB_CONTROL_ALL + "');\">전체보기</a>";
			
			if(!psDto.getWebMobileKind().equals("02")) {
				rtStr	+= "<a href='javascript:;' onclick=\"pupIpbCols();\">항목선택</a>";
			}
			
		}
		rtStr	+= "<script language='JavaScript'></script>";
		
		return rtStr;
	}
	
	
	/**
	 * IPB 도해 그래픽 페이징 관련  CSS 및 HTML 태그
	 */
	public static String getIPBGrphChange(String grphNo, String grphPath) {
		
		String rtStr = "";
		rtStr	= "<a id='ipb_paging_" + grphNo + "' class='ipb_paging' name='" + grphPath + "' no='" + grphNo 
				+ "' href='javascript:;' onclick=\"changeIPBGrph('" + grphPath + "');\">";
		
		if (grphNo.equals("1")) {
			rtStr += grphNo + " ";
		} else {
			rtStr += "[" + grphNo + "] ";
		}
		
		return rtStr;
	}
	
	
	/**
	 * IPB 표(테이블)에서 품목번호 클릭시 도해 그래픽으로 핫스팟 기능 관련  CSS 및 HTML 태그
	 */
	public static String getIPBPartHotspot(String grphPath, String indexnum) {
		
		String rtStr = "";
		rtStr = "<a href='javascript:;' onclick=\"clickPartHotspot('" + grphPath + "', '" + indexnum + "');\">";
		
		return rtStr;
	}
	
	
	/**************************************************/
	
	
	/**
	 * FI 관련
	 */
	public static String getDivFITopic(String titleCnt) {
		
		return StringUtil.replace(CSS.DIV_FI_TOPIC, CSS.REGEX, titleCnt);
	}
	
	public static String getDivFITask(String titleCnt) {
		
		return StringUtil.replace(CSS.DIV_FI_TASK, CSS.REGEX, titleCnt);
	}
	
	public static String getTableFIDI(String grphHtml, String cbHtml, String codeHtml) {
		
		String colwidth = "";
		int scwidth1 = grphHtml.indexOf("scwidth");
		if (scwidth1 == -1) { colwidth = "40%"; } 
		else { String tempstr1 = grphHtml.substring(scwidth1);
			String tempstr2 = tempstr1.substring(tempstr1.indexOf("'")+1);
			int tempwidth = Integer.parseInt(tempstr2.substring(0, tempstr2.indexOf("'")));
			if (tempwidth > 547) { colwidth = "" + (tempwidth*100/1344 +1) + "%"; }
			else { colwidth = "40%"; }
		}

		String rtStr = "";
		rtStr = "<table class='in_table_fi'>"
				+ "<colgroup><col width='"+colwidth+"'/><col /></colgroup>"
				+ "<tbody>"
				+ "  <tr><td rowspan='2'  class='part1'>" + grphHtml + "</td>"
				+ "  <td class='part2'>" + cbHtml + "</td>"
				+ "</tr><tr>"
				+ "  <td class='part3'>" + codeHtml + "</td></tr>"
				+ "</tbody>"
				+ "</table>";
		
		return rtStr;
	}
	
	
	/**
	 * FI교범에서 고장탐구절차(DDS) 일 경우, 하위 고장탐구 다이어그램 호출하는 링크 추가
	 */
	public static String getFIDDSLink(String toKey, String tocoId, String tocoName) {
		
		String rtStr = "";
		rtStr += "<div class='fi-link link1'>";
		rtStr	+= "<a id='fi_dds' name='" + tocoName + "' href='javascript:;' onclick=\"viewToContents('" + toKey + "', '" + tocoId + "', '', '01', '');\">";
		rtStr += tocoName + "</a>";
		rtStr += "</div>";
		
		return rtStr;
	}
	
	/**
	 * FI교범에서 고장탐구절차(DDS) 일 경우, 하위 고장탐구 다이어그램 호출하는 링크 추가
	 */
	public static String getFILink(String toKey, String tocoId, String tocoName) {
		
		String rtStr = "";
		rtStr += "<div class='fi-link link1'>";
		rtStr	+= "<a id='fi_dds' name='" + tocoName + "' href='javascript:;' onclick=\"viewToContents('" + toKey + "', '" + tocoId + "', '', '01', '');\">";
		rtStr += tocoName + "</a>";
		rtStr += "</div>";
		
		return rtStr;
	}
	


	/**
	 * FI Diagram 그리기 ActiveX -> Java Applet 변경
	 */
	public static String getDivFIDigram(String fiId, String fiType, String contXml, 
			String serverUrl, String stepFlowKind, String lastVersionId, String langMode, String gWidth, String gHeight, String webMobileType) {
		
		if(PROJECT == null) {

			AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
			ExternalFileEx ext = ctx.getBean("extConf",ExternalFileEx.class);
			PROJECT = ext.getPROJECT();
		}
		
		if (gWidth == null || gWidth.equals("")) {
			gWidth = "100%";
		}
		if (gHeight == null || gHeight.equals("")) {
			gHeight = "100%";
		}
		
		if (stepFlowKind.equals("01")) {
			stepFlowKind = "0";
		} else {
			stepFlowKind = "1";
		}
		
		String rtStr = "";
		rtStr += "<applet ";
		rtStr += "name='application' id='applicationID' code='com.soltworks.graphics.Application' ";
		if(webMobileType.equals("02")) {
			return contXml.toString();
		} else {
			rtStr += "archive='/"+PROJECT+"/web/object/application.jar' ";
		}
		
		rtStr += "scriptable='yes' type='application/x-java-applet;version=1.7' ";
		rtStr += "width='" + gWidth + "' height='" + gHeight + "' ";
		rtStr += "pluginspage='http://java.sun.com/products/plugin/1.3/plugin-install.html'>";
		rtStr += "  <param name='Language' value='" + langMode + "' />";	// 0:korean, 1:english
		rtStr += "  <param name='ItemID' value='" + fiId + "' />";
		rtStr += "  <param name='ViewType' value='" + stepFlowKind + "' />";	//frame
		rtStr += "  <param name='ViewObject' value='1' />";
		rtStr += "  <param name='Xml' value=\"" + contXml + "\" />";
		rtStr += "  <param name='SzType' value='" + fiType + "' />";
		rtStr += "  <param name='FromPage' value='loadfi' />";
		rtStr += "  <param name='LastVersionID' value='" + lastVersionId + "' />";
		rtStr += "  </applet>";
		return rtStr;
	}
	
	
	/**
	 * 변경 버전 정보 시현 위한 변경바 HTML 구성
	 */
	public static String getDivVersion(String toKey, String tocoId, String contId, String versionId, String statusCd, String styleName, String changeNum) {
		
		String rtStr = "";
		
		if (styleName.equals(IConstants.VER_STATUS_APPEND)) {
			styleName = "version_bar_append";
		} else if (styleName.equals(IConstants.VER_STATUS_UPDATE)) {
			styleName = "version_bar_update";
		} else if (styleName.equals(IConstants.VER_STATUS_LATEST)) {
			styleName = "version_bar_last";
		} else if (styleName.equals(IConstants.VER_STATUS_TP)) {
			styleName = "version_bar_tp";
		}
		
		rtStr += "<div class='div_version'>";
		rtStr += "<span class='" + styleName + "' onClick=\"popupVersionInfo('" + toKey + "', '" + tocoId + "', '" + contId + "', '" + versionId + "', '" + statusCd + "', '" + changeNum + "');\"> </span>";
		
		return rtStr;
	}
	
	/**
	 * 준비사항 - 필수교환품목 및 소모성물자 변경 버전 정보 시현 위한 변경바 HTML 구성
	 */
	public static String getTdVersion(String toKey, String tocoId, String contId, String versionId, String statusCd, String styleName, String changebasis) {
		
		String rtStr = "";
		String bgColor = "";
		if(statusCd.equals("u")) {
			bgColor = " #839197";
		} else {
			//2022 09 15 Park.J.S. 버전바 색상 레드 사용 금지
			bgColor = " #839197";
		}
		if(IConstants.VER_STATUS_LATEST.equalsIgnoreCase(styleName)){
			bgColor = "blue";
		}
		
		rtStr += "width='1%' style='border: none; border-bottom: white 2px solid; border-top: white 2px solid; cursor: pointer; background-color: " + bgColor + ";' onClick=\"popupVersionInfo('" + toKey + "', '" + tocoId + "', '" + contId + "', '" + versionId + "', '" + statusCd + "', '" + changebasis + "');\"";

		return rtStr;
	}
	
	
	/**
	 * CL교범의 체크리스트 항목 HTML 구성
	 */
	public static String getInputChecklist(String toKey, String tocoId, String checklistId) {
		
		String rtStr = "";
		rtStr += "<input type='checkbox' id='"+checklistId+"'  name='c_list' onclick=\"checkContentCL('"+toKey+"', '"+tocoId+"', '"+checklistId+"')\"> ";
		
		return rtStr;
	}
	/**
	 * 스타일 추가 용으로 생성
	 */
	public static String addStyle(String styleStr , String addStr) {
		if(styleStr.indexOf("style=") > -1) {
			styleStr =  styleStr.substring(0,styleStr.indexOf("style=")+7)+addStr+styleStr.substring(styleStr.indexOf("style=")+7,styleStr.length());
		}
		return styleStr;
	}

}
