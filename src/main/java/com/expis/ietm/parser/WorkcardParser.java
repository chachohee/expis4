package com.expis.ietm.parser;

import com.expis.common.ExpisCommonUtile;
import com.expis.common.IConstants;
import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.domparser.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Date;


/**
 * [공통모듈]WC(Workcard) Parser Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkcardParser {

	private final ContParser contParser;
	private final AlertParser alertParser;
	private final GrphParser grphParser;
	private final TextParser textParser;
	private final TableParser tableParser;
	private final VersionParser verParser;
	private final LinkParser linkParser;

	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	ExternalFileEx ext = ctx.getBean("extConf", ExternalFileEx.class);

	/**
	 * Private 한 클래스 변수 선언
	 */
	//13개+9개 항목별로 3개 속성(id, name, ename)
	private int WC_MAX_COLS				= 13;
	private int WSTEP_MAX_COLS			= 12;
	private String[][] WC_ATTR			= new String[WC_MAX_COLS][3];
	private String[][] WSTEP_ATTR		= new String[WSTEP_MAX_COLS][3];
	//private int IDX					= 3; //2차 배열의 수 (colId, colName, colEname)
	private int colId					= 0;
	private int colName					= 1;
	//private int colEname				= 2;

	//<workcard> 순서 변경할시 WC_ATTR배열과 아래 integer 변수들 위치만 바꿔주면됨
	private int nameN			= 0;
	private int powerN			= 0;
	private int serviceN		= 0;
	private int cardnoN			= 0;
	private int workareaN		= 0;
	private int engineerN		= 0;
	private int engineernoN		= 0;
	private int cardtimeN		= 0;
	private int tonoN			= 0;
	private int pubdateN		= 0;
	private int changenoN		= 0;
	private int fignoN			= 0;
	private int revnoN			= 0;

	//<workcard><wstep> 순서 변경할시 WC_ATTR배열과 아래 integer 변수들 위치만 바꿔주면됨
	private int typeN			= 0;
	private int srowN			= 0;
	private int steptimeN		= 0;
	private int stepareaN		= 0;
	private int systemN			= 0;
	private int subsystemN		= 0;
	private int shapeN			= 0;
	private int significanceN	= 0;
	private int seqnumN			= 0;
	private int reiterN			= 0;
	private int checkN			= 0;
	private int etcN			= 0;
	//2022 07 12 Park.J.S. ADD
	private String picStr		= "그림";//그림 테이블 컬럼 추가로 인해 추가
	private String checkList	= "검사항목";//검사항목 테이블 컬럼 추가로 인해 추가


	/**
	 * 생성자
	 * WC(작업카드) 항목 등을 초기에 1번만 셋팅위해 생성자 안에서 호출
	 * 2022 06 16 Park.J.S. Update : 언어 처리위해서 new ParserDto() 추가
	 * 2022 08 08 Park.J.S. Update : 타입별 테이블 헤더변경 가능하게 수정(setWorkcardVariable 생성자 이외에도 호출되는 부분 있음)
	 */
//	public WorkcardParser() {
//		this.setWorkcardVariable(new ParserDto(),"");
//	}


	/**
	 * WC카드 테이블 항목 등을 초기에 셋팅
	 * @param parserDto
	 * @param wcType
	 * @MethodName	: setIPBVariable
	 * @AuthorDate		: LIM Y.M. / 2014. 10. 17.
	 * @ModificationHistory	: 2022 06 16 Park.J.S. Update : 언어 처리위해서 new ParserDto() 추가
	 */
	public void setWorkcardVariable(ParserDto parserDto, String wcType) {

		try {
			/**
			 * attributes 명, Title 한글명, Title 영문명
			 * WC카드 테이블 표시시 WC_ATTR 배열 순서로 나옴(순서 변경할시 WC_ATTR배열과 아래 integer 변수들 위치만 바꿔주면 됨)
			 * <workcard> 항목들
			 * 사용	: name, power, service, cardno, workarea, engineer, engineerno, cardtime, tono, pubdate, changeno
			 * 미사용	: id, type, figno, revno
			 * <wstep> 항목들
			 * 사용	: type, srow, steptime, steparea, system, subsystem, shape, significance, SeqNum
			 * 미사용	: id, name
			 */
			int cnt				= 0;
			nameN				= cnt++;
			powerN				= cnt++;
			serviceN			= cnt++;
			cardnoN				= cnt++;
			workareaN			= cnt++;
			engineerN			= cnt++;
			engineernoN			= cnt++;
			cardtimeN			= cnt++;
			tonoN				= cnt++;
			pubdateN			= cnt++;
			changenoN			= cnt++;
			fignoN				= cnt++;
			revnoN				= cnt++;

			cnt					= 0;
			typeN				= cnt++;
			srowN				= cnt++;
			steptimeN			= cnt++;
			stepareaN			= cnt++;
			systemN				= cnt++;
			subsystemN			= cnt++;
			shapeN				= cnt++;
			significanceN		= cnt++;
			seqnumN				= cnt++;
			reiterN				= cnt++;
			checkN				= cnt++;
			etcN				= cnt++;
			//2022 06 16 Park.J.S. Update : 언어 처리위해서 추가
			if(parserDto.getLanguageType() != null && "en".equalsIgnoreCase(parserDto.getLanguageType())) {
				WC_ATTR[nameN]				= new String[] {"name"			, ""								, ""	};
				WC_ATTR[powerN]				= new String[] {"power"			, "ELECTRICAL<br/>POWER"			, ""	};
				WC_ATTR[serviceN]			= new String[] {"service"		, "SERVICE"							, ""	};
				WC_ATTR[cardnoN]			= new String[] {"cardno"		, "CARD NO."						, ""	};
				WC_ATTR[workareaN]			= new String[] {"workarea"		, "WORK AREA"						, ""	};
				WC_ATTR[engineerN]			= new String[] {"engineer"		, "TYPE MECH<br/>RQR"				, ""	};
				WC_ATTR[engineernoN]		= new String[] {"engineerno"	, "MECH NO."						, ""	};
				WC_ATTR[cardtimeN]			= new String[] {"cardtime"		, "CARD<br/>TIME"					, ""	};
				WC_ATTR[tonoN]				= new String[] {"tono"			, "PUBLICATION NUMBER<br/>AND DATE"	, ""	};
				WC_ATTR[pubdateN]			= new String[] {"pubdate"		, ""							, ""	};
				WC_ATTR[changenoN]			= new String[] {"changeno"		, "CHANGE NO."						, ""	};
				WC_ATTR[fignoN]				= new String[] {"figno"			, ""								, ""	};
				WC_ATTR[revnoN]				= new String[] {"revno"			, ""								, ""	};

				WSTEP_ATTR[typeN]			= new String[] {"type"			, ""					, ""	};
				WSTEP_ATTR[srowN]			= new String[] {"srow"			, ""					, ""	};
				WSTEP_ATTR[steptimeN]		= new String[] {"steptime"		, "MAN<br/>MIN"			, ""	};
				WSTEP_ATTR[stepareaN]		= new String[] {"steparea"		, "WORK AREA"			, ""	};
				WSTEP_ATTR[systemN]			= new String[] {"system"		, "SYS"					, ""	};
				WSTEP_ATTR[subsystemN]		= new String[] {"subsystem"		, "SUB"					, ""	};
				WSTEP_ATTR[shapeN]			= new String[] {"shape"			, ""					, ""	};
				WSTEP_ATTR[significanceN]	= new String[] {"significance"	, ""					, ""	};
				WSTEP_ATTR[seqnumN]			= new String[] {"SeqNum"		, ""					, ""	};
				WSTEP_ATTR[reiterN]			= new String[] {"reiter"		, ""					, ""	};
				WSTEP_ATTR[checkN]			= new String[] {""				, ""					, ""	};
				WSTEP_ATTR[etcN]			= new String[] {""				, "WORK UNIT CODE"		, ""	};
				picStr = "Pic";
				checkList	= "Check<br/>List";
			}else {
				checkList	= "검사<br/>항목";
				picStr = "그림";
				//2022 07 12 Park.J.S ADD
				if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
					WC_ATTR[nameN]				= new String[] {"name"			, ""					, ""	};
					WC_ATTR[powerN]				= new String[] {"power"			, "전원"					, ""	};
					WC_ATTR[serviceN]			= new String[] {"service"		, "서비스"				, ""	};
					WC_ATTR[cardnoN]			= new String[] {"cardno"		, "카드번호"				, ""	};
					WC_ATTR[workareaN]			= new String[] {"workarea"		, "작업구역"				, ""	};
					WC_ATTR[engineerN]			= new String[] {"engineer"		, "요구정비사"				, ""	};
					WC_ATTR[engineernoN]		= new String[] {"engineerno"	, "정비사번호"				, ""	};
					WC_ATTR[cardtimeN]			= new String[] {"cardtime"		, "카드시간"				, ""	};
					WC_ATTR[tonoN]				= new String[] {"tono"			, "기술도서 번호 및"			, ""	};
					WC_ATTR[pubdateN]			= new String[] {"pubdate"		, "발간일"				, ""	};
					WC_ATTR[changenoN]			= new String[] {"changeno"		, "변경판번호"				, ""	};
					WC_ATTR[fignoN]				= new String[] {"figno"			, ""					, ""	};
					WC_ATTR[revnoN]				= new String[] {"revno"			, ""					, ""	};

					WSTEP_ATTR[typeN]			= new String[] {"type"			, ""					, ""	};
					WSTEP_ATTR[srowN]			= new String[] {"srow"			, ""					, ""	};
					WSTEP_ATTR[steptimeN]		= new String[] {"steptime"		, "작업<br/>시간<br/>(분)"	, ""	};
					WSTEP_ATTR[stepareaN]		= new String[] {"steparea"		, "작업<br/>구역"			, ""	};
					WSTEP_ATTR[systemN]			= new String[] {"system"		, "계통"					, ""	};
					WSTEP_ATTR[subsystemN]		= new String[] {"subsystem"		, "부계통<br/>및<br/>구성품"	, ""	};
					WSTEP_ATTR[shapeN]			= new String[] {"shape"			, ""					, ""	};
					WSTEP_ATTR[significanceN]	= new String[] {"significance"	, ""					, ""	};
					WSTEP_ATTR[seqnumN]			= new String[] {"SeqNum"		, ""					, ""	};
					WSTEP_ATTR[reiterN]			= new String[] {"reiter"		, ""					, ""	};
					WSTEP_ATTR[checkN]			= new String[] {""				, ""					, ""	};
					WSTEP_ATTR[etcN]			= new String[] {""				, "작업단위부호"			, ""	};
				}else {
					if("typeb_2_img".equalsIgnoreCase(wcType)) {
						WC_ATTR[nameN]				= new String[] {"name"			, ""					, ""	};
						WC_ATTR[powerN]				= new String[] {"power"			, "전원"					, ""	};
						WC_ATTR[serviceN]			= new String[] {"service"		, "서비스"				, ""	};
						WC_ATTR[cardnoN]			= new String[] {"cardno"		, "카드번호"				, ""	};
						WC_ATTR[workareaN]			= new String[] {"workarea"		, "작업구역"				, ""	};
						WC_ATTR[engineerN]			= new String[] {"engineer"		, "요구정비사"				, ""	};
						WC_ATTR[engineernoN]		= new String[] {"engineerno"	, "정비사번호"				, ""	};
						WC_ATTR[cardtimeN]			= new String[] {"cardtime"		, "카드시간"				, ""	};
						WC_ATTR[tonoN]				= new String[] {"tono"			, "기술도서 번호 및"			, ""	};
						WC_ATTR[pubdateN]			= new String[] {"pubdate"		, "발간일"				, ""	};
						WC_ATTR[changenoN]			= new String[] {"changeno"		, "변경판번호"				, ""	};
						WC_ATTR[fignoN]				= new String[] {"figno"			, ""					, ""	};
						WC_ATTR[revnoN]				= new String[] {"revno"			, "변경판번호"				, ""	};

						WSTEP_ATTR[typeN]			= new String[] {"type"			, ""					, ""	};
						WSTEP_ATTR[srowN]			= new String[] {"srow"			, ""					, ""	};
						WSTEP_ATTR[steptimeN]		= new String[] {"steptime"		, "인시수<br/>(분)"		, ""	};
						WSTEP_ATTR[stepareaN]		= new String[] {"steparea"		, "작업<br/>구역"			, ""	};
						WSTEP_ATTR[systemN]			= new String[] {"system"		, "계통"					, ""	};
						WSTEP_ATTR[subsystemN]		= new String[] {"subsystem"		, "하부계통"				, ""	};
						WSTEP_ATTR[shapeN]			= new String[] {"shape"			, ""					, ""	};
						WSTEP_ATTR[significanceN]	= new String[] {"significance"	, ""					, ""	};
						WSTEP_ATTR[seqnumN]			= new String[] {"SeqNum"		, ""					, ""	};
						WSTEP_ATTR[reiterN]			= new String[] {"reiter"		, ""					, ""	};
						WSTEP_ATTR[checkN]			= new String[] {""				, ""					, ""	};
						WSTEP_ATTR[etcN]			= new String[] {""				, "작업단위부호"			, ""	};
					}else {
						WC_ATTR[nameN]				= new String[] {"name"			, ""					, ""	};
						WC_ATTR[powerN]				= new String[] {"power"			, "전원"					, ""	};
						WC_ATTR[serviceN]			= new String[] {"service"		, "서비스"				, ""	};
						WC_ATTR[cardnoN]			= new String[] {"cardno"		, "카드번호"				, ""	};
						WC_ATTR[workareaN]			= new String[] {"workarea"		, "작업구역"				, ""	};
						WC_ATTR[engineerN]			= new String[] {"engineer"		, "요구정비사"				, ""	};
						WC_ATTR[engineernoN]		= new String[] {"engineerno"	, "정비사번호"				, ""	};
						WC_ATTR[cardtimeN]			= new String[] {"cardtime"		, "카드시간"				, ""	};
						WC_ATTR[tonoN]				= new String[] {"tono"			, "기술도서 번호 및"			, ""	};
						WC_ATTR[pubdateN]			= new String[] {"pubdate"		, "발간일"				, ""	};
						WC_ATTR[changenoN]			= new String[] {"changeno"		, "변경판번호"				, ""	};
						WC_ATTR[fignoN]				= new String[] {"figno"			, ""					, ""	};
						WC_ATTR[revnoN]				= new String[] {"revno"			, "변경판번호"				, ""	};

						WSTEP_ATTR[typeN]			= new String[] {"type"			, ""					, ""	};
						WSTEP_ATTR[srowN]			= new String[] {"srow"			, ""					, ""	};
						WSTEP_ATTR[steptimeN]		= new String[] {"steptime"		, "인시수<br/>(분)"		, ""	};
						WSTEP_ATTR[stepareaN]		= new String[] {"steparea"		, "작업<br/>구역"			, ""	};
						WSTEP_ATTR[systemN]			= new String[] {"system"		, "계통"					, ""	};
						WSTEP_ATTR[subsystemN]		= new String[] {"subsystem"		, "하부계통"				, ""	};
						WSTEP_ATTR[shapeN]			= new String[] {"shape"			, ""					, ""	};
						WSTEP_ATTR[significanceN]	= new String[] {"significance"	, ""					, ""	};
						WSTEP_ATTR[seqnumN]			= new String[] {"SeqNum"		, ""					, ""	};
						WSTEP_ATTR[reiterN]			= new String[] {"reiter"		, ""					, ""	};
						WSTEP_ATTR[checkN]			= new String[] {""				, ""					, ""	};
						WSTEP_ATTR[etcN]			= new String[] {""				, "작업단위부호"			, ""	};
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.setWorkcardVariable Exception:"+ex.toString());
		}
	}


	/**
	 * WC카드(작업카드)
	 * @MethodName	: getWorkcardGroup
	 * @AuthorDate		: LIM Y.M. / 2014. 12. 18.
	 * @ModificationHistory	:
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public StringBuffer getWorkcardGroup(ParserDto psDto, Node paNode) {

		StringBuffer rtSB = new StringBuffer();

		if (paNode == null || !paNode.getNodeName().equals(DTD.WC_WCS)) {
			return rtSB;
		}

		try {
			Node curNode			= null;
			StringBuffer nodeSB	= new StringBuffer();

			NodeList curList = XmlDomParser.getNodeListFromXPathAPI(paNode, "//workcard");

			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);

				if (curNode.getNodeName().equals(DTD.WC_WORKCARD)) {
					nodeSB.append( this.getWorkcardHtml(psDto, curNode) );
				}
			}

			rtSB.append(nodeSB);

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.getWorkcardGroup Exception:"+ex.toString());
		}

		return rtSB;
	}


	/**
	 * WC카드(작업카드)
	 * @MethodName	: getWorkcardHtml
	 * @AuthorDate		: LIM Y.M. / 2015. 1. 7.
	 * @ModificationHistory	:
	 * @param psDto
	 * @param paNode
	 * @return rtSB = <div class='ac-content'><div class="ac-task"><table class="in_table in_table_wc">
	 */
	public StringBuffer getWorkcardHtml(ParserDto psDto, Node paNode) {
		StringBuffer rtSB = new StringBuffer();

		if (paNode == null || !paNode.getNodeName().equals(DTD.WC_WORKCARD)) {
			return rtSB;
		}
		try {
			StringBuffer nodeSB	= new StringBuffer();

			String wcType	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TYPE);

			//2022 08 08 Park.J.S. ADD : 타입별 테이블 수정 필요 해서 추가함
			setWorkcardVariable(psDto, wcType);

			NodeList imgList = XmlDomParser.getNodeListFromXPathAPI(paNode, XALAN.WC_WIMG);
			log.info("wcType : "+wcType+", imgList : "+imgList.getLength());
			//2022 07 06 Park.J.S. imgList 확인해서 이미지 있으면 이미지로 전달하는 방식으로 수정
			if(imgList != null && imgList.getLength() > 0 ) {
				nodeSB.append( this.getWCTypeBHtml(psDto, paNode) );//Img
			}else {
				nodeSB.append( this.getWCTypeAHtml(psDto, paNode) );//Text
			}
			/*
			if (wcType.equals(VAL.WC_TYPE_A)) {
				nodeSB.append( this.getWCTypeAHtml(psDto, paNode) );//Text
			} else if (wcType.equals(VAL.WC_TYPE_B)) {
				nodeSB.append( this.getWCTypeBHtml(psDto, paNode) );//Img
			} else if (wcType.equals(VAL.WC_TYPE_A_IMG)) {//2022 06 24 WC TYPE 다른 경우 처리
				nodeSB.append( this.getWCTypeBHtml(psDto, paNode) );//Img
			} else if (wcType.equals(VAL.WC_TYPE_B_IMG)) {//2022 06 24 WC TYPE 다른 경우 처리
				nodeSB.append( this.getWCTypeBHtml(psDto, paNode) );//Img
			} else if (wcType.equals(VAL.WC_TYPE_B_2_IMG)) {//2022 06 24 WC TYPE 다른 경우 처리
				nodeSB.append( this.getWCTypeBHtml(psDto, paNode) );//Img
			} else if (wcType.equals(VAL.WC_TYPE_B_2_IMG)) {//2022 06 24 WC TYPE 다른 경우 처리
				nodeSB.append( this.getWCTypeBHtml(psDto, paNode) );//Img
			} else if (wcType.equals(VAL.WC_TYPE_C)) {//2022 06 24 WC TYPE 다른 경우 처리
				nodeSB.append( this.getWCTypeAHtml(psDto, paNode) );//Text
			} else if (wcType.equals(VAL.WC_TYPE_C_2)) {//2022 06 24 WC TYPE 다른 경우 처리
				nodeSB.append( this.getWCTypeAHtml(psDto, paNode) );//Text
			} else if (wcType.equals(VAL.WC_TYPE_D)) {//2022 06 24 WC TYPE 다른 경우 처리
				nodeSB.append( this.getWCTypeAHtml(psDto, paNode) );//Text
			} else if (wcType.equals(VAL.WC_TYPE_E)) {//2022 06 24 WC TYPE 다른 경우 처리
				nodeSB.append( this.getWCTypeBHtml(psDto, paNode) );//Img
			} else if (wcType.equals(VAL.WC_TYPE_F)) {//2022 06 24 WC TYPE 다른 경우 처리
				nodeSB.append( this.getWCTypeAHtml(psDto, paNode) );//Text
			} else {
				nodeSB.append( this.getWCTypeBHtml(psDto, paNode) );//Img
			}
			*/
			log.info("getWorkcardHtml rtSB1 : "+rtSB.toString());
			//절 제목 - 접었다 펼기치 기능
			ContParser.TITLE_CNT++;
			String wcId		= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.ID);
			String wcName	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.NAME);
			String cardNo	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.WC_CARDNO);

			//if(!wcType.equals(VAL.WC_TYPE_B)) {
			if(ContParser.TITLE_CNT==1) {
				rtSB.append(CSS.getDivTopic());
				rtSB.append(wcName + " " + cardNo);
			}
			log.info("getWorkcardHtml rtSB2 : "+rtSB.toString());
			rtSB.append( contParser.getMemoHtml(psDto, wcId) );
			if(!"".equals(rtSB.toString())) {
				rtSB.append(CSS.DIV_END);
			}
			log.info("getWorkcardHtml rtSB3 : "+rtSB.toString());
			log.info("getWorkcardHtml ContParser.TITLE_CNT : "+ContParser.TITLE_CNT);
			//2022 07 14 Park.J.S. ContParser.TITLE_CNT !=1 일경우 글자 크기 이슈 있어서 수정
			if(ContParser.TITLE_CNT == 1) {
				rtSB.append( CSS.getDivFITask(ContParser.TITLE_CNT+"") );
			}else {
				//rtSB.append( "<div class='ac-content fi-task' id='ac-div_\" + ContParser.TITLE_CNT + \"'>" );
				rtSB.append( "<div class='ac-content fi-task' id='ac-div_" + ContParser.TITLE_CNT + "'>" );
			}
			log.info("getWorkcardHtml rtSB4 : "+rtSB.toString());
			rtSB.append(nodeSB);
			rtSB.append(CSS.DIV_END);
			log.info("getWorkcardHtml rtSB5 : "+rtSB.toString());

			/*
			rtSB.append(CSS.DIV_CONT);
			rtSB.append(nodeSB);
			rtSB.append(CSS.DIV_END);
			*/

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.getWorkcardHtml Exception:"+ex.toString());
		}

		return rtSB;
	}


	/**
	 * 작업카드 중 TypeA <workcard type='typea'>
	 * @MethodName	: getWCTypeAHtml
	 * @AuthorDate		: LIM Y.M. / 2015. 1. 6.
	 * @ModificationHistory	:
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public StringBuffer getWCTypeAHtml(ParserDto psDto, Node paNode) {

		StringBuffer rtSB = new StringBuffer();

		if (paNode == null || !paNode.getNodeName().equals(DTD.WC_WORKCARD)) {
			return rtSB;
		}

		try {
			//어튜리뷰티 속성 값 배열 변수에 입력
			String[] wcAttr = this.inputWCAttr(psDto, paNode);

			rtSB.append(CSS.TB_TABLE_WC);
			rtSB.append( this.getHeaderAHtml(wcAttr) );
			if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
				rtSB.append( this.getFooterAHtml(wcAttr) );
			}else {
				String wcType	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TYPE);
				if("typeb_2_img".equalsIgnoreCase(wcType) || "typeb".equalsIgnoreCase(wcType)) {
					log.info("Pass Footer");
				}else {
					rtSB.append( this.getFooterAHtml(wcAttr) );
				}
			}
			rtSB.append( this.getWStepAHtml(psDto, paNode) );
			rtSB.append(CSS.TB_TABLEEND);

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.getWCTypeAHtml Exception:"+ex.toString());
		}

		return rtSB;
	}


	/**
	 * 작업카드 중 TypeB <workcard type='typeb'>
	 * @MethodName	: getWCTypeBHtml
	 * @AuthorDate		: LIM Y.M. / 2015. 1. 8.
	 * @ModificationHistory	:
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public StringBuffer getWCTypeBHtml(ParserDto psDto, Node paNode) {

		StringBuffer rtSB = new StringBuffer();

		if (paNode == null || !paNode.getNodeName().equals(DTD.WC_WORKCARD)) {
			return rtSB;
		}

		try {
			//rtSB.append(CSS.TB_TABLE_WC);
			rtSB.append(CSS.TB_TABLE_WC_WIMG);
			rtSB.append( this.getWStepBHtml(psDto, paNode) );
			rtSB.append(CSS.TB_TABLEEND);

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.getWCTypeBHtml Exception:"+ex.toString());
		}

		return rtSB;
	}


	/**
	 * <workcard> 속성값 변수 입력
	 * @MethodName	: inputWCAttr
	 * @AuthorDate		: LIM Y.M. / 2015. 1. 7.
	 * @ModificationHistory	:
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public String[] inputWCAttr(ParserDto psDto, Node paNode) {

		if (paNode == null || !paNode.getNodeName().equals(DTD.WC_WORKCARD)) {
			return null;
		}

		String[] rtSA = new String[this.WC_MAX_COLS];

		try {
			NamedNodeMap curAttr = paNode.getAttributes();

			if (curAttr != null) {
				rtSA[nameN]			= XmlDomParser.getAttributes(curAttr, WC_ATTR[nameN][colId]);
				rtSA[powerN]			= XmlDomParser.getAttributes(curAttr, WC_ATTR[powerN][colId]);
				rtSA[serviceN]			= XmlDomParser.getAttributes(curAttr, WC_ATTR[serviceN][colId]);
				rtSA[cardnoN]			= XmlDomParser.getAttributes(curAttr, WC_ATTR[cardnoN][colId]);
				rtSA[workareaN]		= XmlDomParser.getAttributes(curAttr, WC_ATTR[workareaN][colId]);
				rtSA[engineerN]		= XmlDomParser.getAttributes(curAttr, WC_ATTR[engineerN][colId]);
				rtSA[engineernoN]	= XmlDomParser.getAttributes(curAttr, WC_ATTR[engineernoN][colId]);
				rtSA[cardtimeN]		= XmlDomParser.getAttributes(curAttr, WC_ATTR[cardtimeN][colId]);
				rtSA[tonoN]				= XmlDomParser.getAttributes(curAttr, WC_ATTR[tonoN][colId]);
				rtSA[pubdateN]		= XmlDomParser.getAttributes(curAttr, WC_ATTR[pubdateN][colId]);
				rtSA[changenoN]		= XmlDomParser.getAttributes(curAttr, WC_ATTR[changenoN][colId]);
				rtSA[fignoN]				= XmlDomParser.getAttributes(curAttr, WC_ATTR[fignoN][colId]);
				rtSA[revnoN]			= XmlDomParser.getAttributes(curAttr, WC_ATTR[revnoN][colId]);

				//20170114 add 작업카드(WC)에도 검색어 반전 효과 구현
				if (!psDto.getSearchWord().equals("")) {
					String scWord			= psDto.getSearchWord();
					rtSA[nameN]			= CodeConverter.getSearchKeywordIPBWC(rtSA[nameN]			, scWord);
					rtSA[powerN]			= CodeConverter.getSearchKeywordIPBWC(rtSA[powerN]			, scWord);
					rtSA[serviceN]			= CodeConverter.getSearchKeywordIPBWC(rtSA[serviceN]		, scWord);
					rtSA[cardnoN]			= CodeConverter.getSearchKeywordIPBWC(rtSA[cardnoN]		, scWord);
					rtSA[workareaN]		= CodeConverter.getSearchKeywordIPBWC(rtSA[workareaN]	, scWord);
					rtSA[engineerN]		= CodeConverter.getSearchKeywordIPBWC(rtSA[engineerN]		, scWord);
					rtSA[engineernoN]	= CodeConverter.getSearchKeywordIPBWC(rtSA[engineernoN]	, scWord);
					rtSA[cardtimeN]		= CodeConverter.getSearchKeywordIPBWC(rtSA[cardtimeN]		, scWord);
					rtSA[tonoN]				= CodeConverter.getSearchKeywordIPBWC(rtSA[tonoN]			, scWord);
					rtSA[pubdateN]		= CodeConverter.getSearchKeywordIPBWC(rtSA[pubdateN]		, scWord);
					rtSA[changenoN]		= CodeConverter.getSearchKeywordIPBWC(rtSA[changenoN]	, scWord);
					rtSA[fignoN]				= CodeConverter.getSearchKeywordIPBWC(rtSA[fignoN]			, scWord);
					rtSA[revnoN]			= CodeConverter.getSearchKeywordIPBWC(rtSA[revnoN]			, scWord);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.inputWCAttr Exception:"+ex.toString());
		}

		return rtSA;
	}


	/**
	 * <workcard><wstep> 속성값 변수 입력
	 * @MethodName			: inputWStepAttr
	 * @AuthorDate			: LIM Y.M. / 2015. 1. 7.
	 * @ModificationHistory	: Park.J.S. 2022.02.25.
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public String[] inputWStepAttr(ParserDto psDto, Node paNode) {
		log.info("inputWStepAttr paNode : "+paNode+", paNode.getNodeName() : "+paNode.getNodeName());
		if (paNode == null || !paNode.getNodeName().equals(DTD.WC_WSTEP)) {
			return null;
		}

		String[] rtSA = new String[this.WSTEP_MAX_COLS];

		try {
			NamedNodeMap curAttr = paNode.getAttributes();

			if (curAttr != null) {
				rtSA[typeN]				= XmlDomParser.getAttributes(curAttr, WSTEP_ATTR[typeN][colId]);
				rtSA[srowN]				= XmlDomParser.getAttributes(curAttr, WSTEP_ATTR[srowN][colId]);
				rtSA[steptimeN]			= XmlDomParser.getAttributes(curAttr, WSTEP_ATTR[steptimeN][colId]);
				rtSA[stepareaN]			= XmlDomParser.getAttributes(curAttr, WSTEP_ATTR[stepareaN][colId]);
				rtSA[systemN]			= XmlDomParser.getAttributes(curAttr, WSTEP_ATTR[systemN][colId]);
				rtSA[subsystemN]		= XmlDomParser.getAttributes(curAttr, WSTEP_ATTR[subsystemN][colId]);
				rtSA[shapeN]			= XmlDomParser.getAttributes(curAttr, WSTEP_ATTR[shapeN][colId]);
				rtSA[significanceN]		= XmlDomParser.getAttributes(curAttr, WSTEP_ATTR[significanceN][colId]);
				rtSA[seqnumN]			= XmlDomParser.getAttributes(curAttr, WSTEP_ATTR[seqnumN][colId]);
				rtSA[reiterN]			= XmlDomParser.getAttributes(curAttr, WSTEP_ATTR[reiterN][colId]);
				//20170114 add 작업카드(WC)에도 검색어 반전 효과 구현
				if (!psDto.getSearchWord().equals("")) {
					String scWord			= psDto.getSearchWord();
					rtSA[typeN]				= CodeConverter.getSearchKeywordIPBWC(rtSA[typeN]			, scWord);
					rtSA[srowN]				= CodeConverter.getSearchKeywordIPBWC(rtSA[srowN]			, scWord);
					rtSA[steptimeN]			= CodeConverter.getSearchKeywordIPBWC(rtSA[steptimeN]		, scWord);
					rtSA[stepareaN]			= CodeConverter.getSearchKeywordIPBWC(rtSA[stepareaN]		, scWord);
					rtSA[systemN]			= CodeConverter.getSearchKeywordIPBWC(rtSA[systemN]			, scWord);
					rtSA[subsystemN]		= CodeConverter.getSearchKeywordIPBWC(rtSA[subsystemN]		, scWord);
					rtSA[shapeN]			= CodeConverter.getSearchKeywordIPBWC(rtSA[shapeN]			, scWord);
					rtSA[significanceN]		= CodeConverter.getSearchKeywordIPBWC(rtSA[significanceN]	, scWord);
					rtSA[seqnumN]			= CodeConverter.getSearchKeywordIPBWC(rtSA[seqnumN]			, scWord);
					rtSA[reiterN]			= CodeConverter.getSearchKeywordIPBWC(rtSA[reiterN]			, scWord);
				}

				// 2023.11.28 - 작업단위부호 (WUC) 검색시 결과 적용 - jingi.kim
				if ( !"".equalsIgnoreCase(psDto.getSearchWord()) ) {
					String wucCode = rtSA[systemN]+rtSA[subsystemN];
					String scWord = psDto.getSearchWord();
					if ( isSearchWordWUC( wucCode, scWord ) ) {
						rtSA[systemN] = CSS.FONT_CC + rtSA[systemN] + CSS.FONT_END;
						rtSA[subsystemN] = CSS.FONT_CC + rtSA[subsystemN] + CSS.FONT_END;
					}
				}

				log.info("rtSA[steptimeN]     : "+rtSA[steptimeN]);
				log.info("rtSA[stepareaN]     : "+rtSA[stepareaN]);
				log.info("rtSA[systemN]       : "+rtSA[systemN]);
				log.info("rtSA[subsystemN]    : "+rtSA[subsystemN]);
				log.info("rtSA[shapeN]        : "+rtSA[shapeN]);
				log.info("rtSA[significanceN] : "+rtSA[significanceN]);
				log.info("rtSA[seqnumN]       : "+rtSA[seqnumN]);
				/* 2022 02 25 Park.J.S. : 특정상황에 안나오는 아래 코드 제거 추후 문제 발생할경우 해당 부분 수정
				if (rtSA[steptimeN].equals("") && rtSA[stepareaN].equals("") && rtSA[systemN].equals("") && rtSA[subsystemN].equals("")
						&& rtSA[shapeN].equals("") && rtSA[significanceN].equals("") && rtSA[seqnumN].equals("")) {
					rtSA = null;
				}
				*/
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.inputWStepAttr Exception:"+ex.toString());
		}

		return rtSA;
	}


	/**
	 * 작업카드 중 TypeA <workcard type='typea'><wstep>
	 * @MethodName			: getWStepAHtml
	 * @AuthorDate			: LIM Y.M. / 2015. 1. 6.
	 * @ModificationHistory	: Park.J.S. 2022.02.25.
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public StringBuffer getWStepAHtml(ParserDto psDto, Node paNode) {
		log.info("paNode : "+paNode.getNodeName());
		StringBuffer rtSB = new StringBuffer();

		if (paNode == null || !paNode.getNodeName().equals(DTD.WC_WORKCARD)) {
			return rtSB;
		}

		try {
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();

			String tr			= CSS.TB_TR;
			String trE			= CSS.TB_TREND;
			String td			= CSS.TB_TD;
			String tdC6			= CSS.getTableTd(1, 6); //param=rowspan, colspan
			String tdC8			= CSS.getTableTd(1, 8); //param=rowspan, colspan
			String tdE			= CSS.TB_TDEND;
			String imgPath		= "";

			if(psDto.getWebMobileKind().equals("02")) {
				imgPath = ext.getDF_M_IMG();
//				imgPath	= CommonConfig.getKey(psDto.getBiz() + "_" + IConstants.M_PROP_IMG);
			} else {
				imgPath = ext.getDF_IMG();
//				imgPath	= CommonConfig.getKey(psDto.getBiz() + "_" + IConstants.PROP_IMG);
			}

			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();

				if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.WC_WSTEP)) {
					//2023 01 12 Park.J.S. Update : if 문 처이 위해 타입 변경 String tbodyHtml => StringBuffer tbodyHtml
					StringBuffer tbodyHtml	= new StringBuffer();
					String checkText		= "";
					String seqnumText		= "";
					String shapeText		= "";
					String descText			= "";
					String alertText		= "";

					String[] wstepAttr = this.inputWStepAttr(psDto, curNode);
					log.info("wstepAttr : "+wstepAttr+", ");
					if (wstepAttr != null) {

						NodeList childList = curNode.getChildNodes();
						for (int k=0; k<childList.getLength(); k++)
						{
							Node childNode = childList.item(k);
							String childName = childNode.getNodeName();
							log.info("childNode.getNodeType()  : "+childNode.getNodeType()+", childName : "+childName);
							//경고 or 문자열단락
							if (childNode.getNodeType() == Node.ELEMENT_NODE && childName.equals(DTD.ALERT)) {
								checkText	= "";
								alertText 	+= alertParser.getAlertHtml(psDto, childNode).toString();
							} else if (childNode.getNodeType() == Node.ELEMENT_NODE && childName.equals(DTD.TEXT)) {
								//2022 06 17 Park J.S. : 특정 상황에서 <alert 태그 없는 경우 처리 위해 추가  현재 전부 note 처리됨 추가 양식 있을 경우 해당 처리 로직 추가 필요
								String seqNum = XmlDomParser.getAttributes(childNode.getParentNode().getAttributes(), "SeqNum");
								if(seqNum != null && "".equals(seqNum)) {
									//2022 06 17 Park J.S. : SeqNum 없는 경우 type 체크 필요
									seqNum = XmlDomParser.getAttributes(childNode.getParentNode().getAttributes(), "type");
								}
								log.info("Check seqNum : "+seqNum);
								if(seqNum != null && (
										seqNum.equalsIgnoreCase("NOTE") || seqNum.equalsIgnoreCase("NOPE") || seqNum.equalsIgnoreCase("WARNING") || seqNum.equalsIgnoreCase("CAUTION") ||
												seqNum.indexOf("주의") >-1 || seqNum.indexOf("경고") > -1 || seqNum.indexOf("주") > -1
								)) {
									checkText	= "";
									ExpisCommonUtile utile = ExpisCommonUtile.getInstance();
									String xmlStr = utile.xmlToString(childNode);
									//2022 07 13 경고창에 여러줄 나올경우 처리
									for (int idx=k+1; idx<childList.getLength(); idx++) {
										xmlStr += utile.xmlToString(childList.item(idx));
									}
									String alertType = "note";
									if(seqNum.indexOf("NOPE") >-1) {
										alertType = "note";
									}else if(seqNum.indexOf("NOTE") >-1) {
										alertType = "note";
									}else if(seqNum.indexOf("경고") >-1) {
										alertType = "warning";
									}else if(seqNum.indexOf("주의") >-1) {
										alertType = "caution";
									}else if(seqNum.indexOf("주") >-1) {
										alertType = "note";
									}else if(seqNum.indexOf("WARNING") >-1) {
										alertType = "warning";
									}else if(seqNum.indexOf("CAUTION") >-1) {
										alertType = "caution";
									}
									String alertStr  = "<alert id=\"ABCD"+k+new Date().getTime()+k+"ABCD\" name=\"\" type=\""+alertType+"\" itemid=\"\" ref=\"\" status=\"a\" version=\"\">";
									Node alertNode = utile.createDomTree(alertStr+xmlStr+"</alert>", 1);
									log.info("alertNodeStr : "+alertStr+xmlStr+"</alert>");
									alertText 	+= alertParser.getAlertHtml(psDto, alertNode.getFirstChild()).toString();
									break;
								}else {
									//2022 02 25 Park J.S. : 주기등 특이상황에서 체크박스 안나오게 추가
									if(checkStepStrPass(wstepAttr[seqnumN])) {
										checkText	= "";
									}else if(checkStepStrPass(wstepAttr[typeN])) {
										checkText	= "";
									}else{
										checkText	= CSS.INPUT_CHK;
									}
									descText 	+= textParser.getTextWorkcard(psDto, childNode).toString();
								}
							}
						}
						//설명 부분
						if (!descText.equals("")) {
							String style = "";
							if (!wstepAttr[seqnumN].equals("") && wstepAttr[seqnumN].charAt(0) < VAL.WC_SEQNUM_ALPHABET) {
								if (wstepAttr[reiterN].equals(VAL.WC_REITER_2)) {
									String iconPath = imgPath + IConstants.WC_ICON_REITER + wstepAttr[seqnumN] + IConstants.WC_ICON_EXT_JPG;
									seqnumText = CSS.getDivIcon("", iconPath, CSS.SIZE_WC_ICON, CSS.SIZE_WC_ICON, "");
								} else if (wstepAttr[reiterN].equals(VAL.WC_REITER_3)) {
									String iconPath = imgPath + IConstants.WC_ICON_REITER3 + wstepAttr[seqnumN] + IConstants.WC_ICON_EXT_JPG;
									seqnumText = CSS.getDivIcon("", iconPath, CSS.SIZE_WC_ICON, CSS.SIZE_WC_ICON, "");
								} else if(checkStepStrPass(wstepAttr[seqnumN])){//2022 02 25 Park J.S.
									seqnumText = wstepAttr[seqnumN] + "<br/> ";
								} else {
									if("".equals(wstepAttr[seqnumN])){
										seqnumText = wstepAttr[seqnumN] + "&nbsp;&nbsp;";
									}else {
										seqnumText = wstepAttr[seqnumN] + ". ";
									}
								}
								style	= CSS.P_WC_STEP1;
							} else {
								if(checkStepStrPass(wstepAttr[seqnumN])){//2022 02 25 Park J.S.
									seqnumText = wstepAttr[seqnumN] + "<br/> ";
								} else {
									if("".equals(wstepAttr[seqnumN])){
										seqnumText = wstepAttr[seqnumN] + "&nbsp;&nbsp;";
									}else {
										seqnumText = wstepAttr[seqnumN] + ". ";
									}
								}
								style	= CSS.P_WC_STEP2;
							}

							if (!descText.equals("")) {
								descText = CodeConverter.getCodeConverter(psDto, descText, "", "");
							}

							//2022 02 28 Park.J.S. : 체크박스 없는경우  P가 아닌 Span 사용
							if(checkText.equals("")){
								style = style.replace("<p", "<span");

								// type 이 "제목" 인 경우 해당 텍스트는 bold 처리
								if("제목".equals(wstepAttr[typeN])) {
									style = style.replace("class='wcstep2'", "class='wcstep2 wc_title_bold'");
								}
							}

							//by ejkim 2022.10.03 workcard 내 제목 미표기로 제목이 있으면 표시되지 않도록 수정, css도 변경
							if( seqnumText.trim().equals("제목.")) {
								style = style.replace(">", "style='text-indent:unset; margin-left: unset;'>");
								descText = style + descText + CSS.P_END;
							}else {
								descText = style + seqnumText + descText + CSS.P_END;
							}
						}

						//shape 값이 A,B,C,1,2 일 경우 아이콘으로 표시, 그 외는 문자대로 표시
						String shape = wstepAttr[shapeN];
						if (shape.length() == 1 && VAL.WC_SHAPE.indexOf(shape) > -1) {
							String iconPath = imgPath + IConstants.WC_ICON_SHAPE + shape + IConstants.WC_ICON_EXT_JPG;
							shapeText = CSS.getDivIcon("", iconPath, CSS.SIZE_WC_ICON, CSS.SIZE_WC_ICON, "");
						} else {
							shapeText = shape;

							// 2023.08.24 - 여러개 타입 지정시 갯수만큼 아이콘 표시되도록 추가 - jingi.kim
							if ( shape.length() > 1 ) {
								StringBuffer shapeSB = new StringBuffer();
								for ( char chs : shape.toCharArray() ) {
									if ( VAL.WC_SHAPE.indexOf(chs) > -1 ) {
										String iconPath = imgPath + IConstants.WC_ICON_SHAPE + chs + IConstants.WC_ICON_EXT_JPG;
										shapeSB.append( CSS.getDivIcon("", iconPath, CSS.SIZE_WC_ICON, CSS.SIZE_WC_ICON, "") );
									}
								}
								if ( shapeSB.length() > 0 ) {
									shapeText = shapeSB.toString();
								}
							}
						}

						//2022.02.28 Park.J.S. : descText가 공백 문자일경우 checkText 공백처리(뜨어쓰기 경고창 등 실제로 사용할 경우를 대비해서 체크 박스만 제거)
						// ((checkText.equals("")) ? td : "<td style='text-align: left;'>") 변경

						if(descText.trim().equals("")) {
							checkText = "";
						}
						//2022 11 23 Park.J.S. : 버전관련 수정 추가.
						StringBuffer verSB = verParser.checkVersionHtml(psDto, curNode);
						//System.out.println("verSB : "+verSB.toString());
						String verEndStr = verParser.endVersionHtml(verSB);
						if (verSB != null && verSB.toString().length() > 0) {
							verSB.append("&nbsp;");
						}

						//2022 06 10 Park.J.S. : CheckBoc의 경우 상단 정렬 요청으로 수정 및 툭수문자 대비해서 기본 넓이 추가 현재 ** 대비해서 20px 사용함 추후 문제 발생시 해당부분 수정 필요
						//2022 07 26 Park.J.S. : CheckBoc의 경우 상단 정렬 요청으로 수정 및 툭수문자 대비해서 기본 넓이 추가 현재 *** 대비해서 30px 사용함
						//2022 12 14 Park.J.S. : 버전바 존재 할경우 줄바뀜 되는 현상 관련 이미지 부분과 통합 되서 사용 가능하게 수정
						tbodyHtml.append("");
						tbodyHtml.append(tr + "<td style=\"vertical-align: top;\">"	+ (verSB.toString())+checkLineEnd(wstepAttr[steptimeN])+verEndStr		+ tdE);
						tbodyHtml.append("<td style=\"vertical-align: top;\">"			+ checkLineEnd(wstepAttr[stepareaN])									+ tdE);
						//2023 01 12 Park.J.S. : KTA 일경우 WC 카드 내 계통별 링크 기능 구현
						if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
							tbodyHtml.append("<td style=\"vertical-align: top;\">"		+ checkLineEnd(wstepAttr[systemN])  		+ tdE);
						}else {
							//if문 경우의 수 eXPIS2에서 가지고 옴
							String wcType	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TYPE);
							if(("typea".equalsIgnoreCase(wcType) || "typeb".equalsIgnoreCase(wcType) || "typeb_2".equalsIgnoreCase(wcType)) && (wstepAttr[systemN] != null && !"".equals(wstepAttr[systemN]))){
								//tbodyHtml.append("<td style=\"vertical-align: top;\">"			+ "<a href='javascript:;' class='link-txt' onClick='javascript:viewWCLink('"+checkLineEnd(wstepAttr[systemN])+"');'>"+checkLineEnd(wstepAttr[systemN])+"</a>&nbsp"+ tdE);
								tbodyHtml.append("<td style=\"vertical-align: top;\">"			+ linkParser.makeHtml("", "WCLINK", psDto.getToKey(), "", checkLineEnd(wstepAttr[systemN]), "", "", "", "link-txt", checkLineEnd(wstepAttr[systemN]))+ tdE);
							}else {
								tbodyHtml.append("<td style=\"vertical-align: top;\">"			+ checkLineEnd(wstepAttr[systemN])										+ tdE);
							}
						}
						tbodyHtml.append("<td style=\"vertical-align: top;\">"		+ checkLineEnd(wstepAttr[subsystemN])		+ tdE);
						tbodyHtml.append(tdC8);
						tbodyHtml.append( CSS.TB_TABLE_WC2 + CSS.TB_TBODY + CSS.addStyle(tr,"display: block;"));
						tbodyHtml.append( "<td style=\"vertical-align: top;\">"		+ checkText							+ tdE);
						tbodyHtml.append( "<td style=\"width: 20px;\">"				+ shapeText							+ tdE);
						tbodyHtml.append( "<td style=\"width: 30px;\">"				+ wstepAttr[significanceN]			+ tdE);
						//2022.10.02 WC교범 중앙정렬 처리로 왼쪽 정렬할 수 있게 수정
						//2022.10.03 제목을 체크박스 처리 및 제거로 기존 수정사항 원복처리
						tbodyHtml.append( ((checkText.equals("")) ? "<td style='display: block;'>" : "<td style='text-align: left;'>") + alertText + descText + tdE);
						tbodyHtml.append( trE + CSS.TB_TBODY_END + CSS.TB_TABLEEND);
						tbodyHtml.append( tdE + trE);
						nodeSB.append(tbodyHtml);
					}
				}
			}

			rtSB.append(CSS.TB_TBODY);
			rtSB.append(nodeSB);
			rtSB.append(CSS.TB_TBODY_END);

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.getWStepAHtml Exception:"+ex.toString());
		}

		return rtSB;
	}


	/**
	 * 작업카드 중 TypeB <workcard type='typeb'><wstep><wimg>
	 * @MethodName	: getWStepBHtml
	 * @AuthorDate		: LIM Y.M. / 2015. 1. 8.
	 * @ModificationHistory	:
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public StringBuffer getWStepBHtml(ParserDto psDto, Node paNode) {

		StringBuffer rtSB = new StringBuffer();

		if (paNode == null || !paNode.getNodeName().equals(DTD.WC_WORKCARD)) {
			return rtSB;
		}

		try {
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			String grphHtml		= "";

			NodeList curList = XmlDomParser.getNodeListFromXPathAPI(paNode, XALAN.WC_WIMG);
			for (int i=0; i<curList.getLength(); i++)
			{
				if (curList.getLength() == 0) { break; }

				curNode = curList.item(i);
				nodeName = curNode.getNodeName();

				if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.WC_WIMG)) {
					//2021 06 true add jsaprk
					grphHtml = grphParser.getGrphWorkcardHtml(psDto, curNode, true).toString();
					/**
					 * 2022 08 08 Park.J.S ADD : KTA 일경우 type 체크해서 이미지에 테이블 테두리 만드는 로직 추가
					 */
					if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
						nodeSB.append(CSS.TB_TR);
						nodeSB.append(CSS.TB_TD);
						nodeSB.append(grphHtml);
						nodeSB.append(CSS.TB_TDEND);
						nodeSB.append(CSS.TB_TREND);
					}else {
						String wcType	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TYPE);
						String[] wcAttr = this.inputWCAttr(psDto, paNode);
						log.info("wcType : "+wcType);
						if("typea_img".equalsIgnoreCase(wcType)) {
							nodeSB.append(getHeaderImgHtml(wcAttr, wcType));
							try {
								//2023 01 18 Park.J.S. Update : 이미지에도 별도 ATTR 내용 있는 경우 추가 하게 수정
								String[] wstepAttr = this.inputWStepAttr(psDto, curNode.getParentNode());
								//String tbodyHtml = "<tr><td></td><td></td><td></td><td></td><td colspan=\"8\">"+grphHtml+"</td></tr>";
								String tbodyHtml = "<tr><td>"+checkLineEnd(wstepAttr[steptimeN])+"</td><td>"+checkLineEnd(wstepAttr[stepareaN])+"</td><td>"+checkLineEnd(wstepAttr[systemN])+"</td><td>"+checkLineEnd(wstepAttr[subsystemN])+"</td><td colspan=\"8\">"+grphHtml+"</td></tr>";
								nodeSB.append(tbodyHtml);
							}catch (Exception e) {
								String tbodyHtml = "<tr><td></td><td></td><td></td><td></td><td colspan=\"8\">"+grphHtml+"</td></tr>";
								nodeSB.append(tbodyHtml);
							}
							nodeSB.append(getFooterImageHtml(wcAttr, wcType));
						}else if("typeb_2_img".equalsIgnoreCase(wcType)) {
							String tbodyHtml = "<tr><td colspan=\"12\">"+grphHtml+"</td></tr>";
							nodeSB.append(getHeaderImgHtml(wcAttr, wcType));
							nodeSB.append(tbodyHtml);
						}else if("typef".equalsIgnoreCase(wcType) || "typee".equalsIgnoreCase(wcType)) {
							String tbodyHtml = "<tr><td colspan=\"8\">"+grphHtml+"</td></tr>";
							nodeSB.append(getHeaderImgHtml(wcAttr, wcType));
							nodeSB.append(tbodyHtml);
						}else if("typec".equalsIgnoreCase(wcType)) {
							String tbodyHtml = "<tr><td colspan=\"6\">"+grphHtml+"</td></tr>";
							nodeSB.append(getHeaderImgHtml(wcAttr, wcType));
							nodeSB.append(tbodyHtml);
						}else if("typeb_img".equalsIgnoreCase(wcType)) {
							String tbodyHtml = "<tr><td></td><td></td><td></td><td></td><td colspan=\"8\">"+grphHtml+"</td></tr>";
							nodeSB.append(getHeaderImgHtml(wcAttr, wcType));
							nodeSB.append(tbodyHtml);
						}else if("typec_2".equalsIgnoreCase(wcType)) {
							String tbodyHtml = "<tr><td colspan=\"7\">"+grphHtml+"</td></tr>";
							nodeSB.append(getHeaderImgHtml(wcAttr, wcType));
							nodeSB.append(tbodyHtml);
							nodeSB.append(getFooterImageHtml(wcAttr, wcType));
						}else if("typed".equalsIgnoreCase(wcType)) {
							String tbodyHtml = "<tr><td colspan=\"7\">"+grphHtml+"</td></tr>";
							nodeSB.append(getHeaderImgHtml(wcAttr, wcType));
							nodeSB.append(tbodyHtml);
						}else {
							nodeSB.append(CSS.TB_TR);
							nodeSB.append(CSS.TB_TD);
							nodeSB.append(grphHtml);
							nodeSB.append(CSS.TB_TDEND);
							nodeSB.append(CSS.TB_TREND);
						}
						/*
						nodeSB.append(CSS.TB_TR);
						nodeSB.append(CSS.TB_TD);
						nodeSB.append(grphHtml);
						nodeSB.append(CSS.TB_TDEND);
						nodeSB.append(CSS.TB_TREND);
						*/
					}
				}
			}

			rtSB.append(CSS.TB_TBODY);
			rtSB.append(nodeSB);
			rtSB.append(CSS.TB_TBODY_END);

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.getWStepBHtml Exception:"+ex.toString());
		}

		return rtSB;
	}


	/**
	 * 헤더 - TypeA
	 * @MethodName	: getHeaderAHtml
	 * @AuthorDate		: LIM Y.M. / 2015. 1. 6.
	 * @ModificationHistory	:
	 * @param wcAttr
	 * @return
	 *  "<tr>"+ "<th>카드번호 1-004</th>"+ "<th colspan='3'>직업구역 00</th>"+ "<th>요구정비사 항공기</th>"+ "<th>정비사번호</th>"+ "<th>카드시간 0.5</th>"
	+ "<th colspan='2'>기술도서 번호 및 발간일<br>K.T.O.1T-50A-6WC-1 2005. 07. 30</th>"+ "<th>변경판번호</th></tr>"
	+ "<tr>"+ "<th rowspan='2'>직업시간(분)</th>"+ "<th rowspan='2'>직업구역</th>"+ "<th colspan='2'>작업단위부호</th>"+ "<th rowspan='2' colspan='3'>통합 비행전/비행후</th>"
	+ "<th rowspan='2'>전원 OFF5</th>"+ "<th rowspan='2'>서비스</th>"+ "<th rowspan='2'>카드번호1-004</th>"+ "</tr>"
	+ "<tr><th>계통</th>"+ "<th>부호계통 및 구성품</th></tr>"
	 */
	public StringBuffer getHeaderAHtml(String[] wcAttr) {

		StringBuffer rtSB = new StringBuffer();

		if (wcAttr == null) {
			return rtSB;
		}

		try {
			String br			= CSS.BR;
			String tr			= CSS.TB_TR;
			String trE			= CSS.TB_TREND;
			String th			= CSS.TB_TH;
			String thR2			= CSS.getTableTh(2, 1); //param=rowspan, colspan
			String thC2			= CSS.getTableTh(1, 2);
			String thC3			= CSS.getTableTh(1, 3);
			String thR2C3		= CSS.getTableTh(2, 3);
			String thE			= CSS.TB_THEND;
			//2022 07 04 Park.J.S. : KRA  헤더 문구 삭제 요청으로 분기 처리
			//2022 07 26 Park.J.S. : 가로사이즈 고정 추가
			//2022 09 16 Park.J.S. : 가로사이즈 조절 처리함 (%처리)
			if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
				String theadHtml = ""
						+ tr
						+ "<th rowspan=\"1\" colspan=\"1\" style=\"width: 10%;\">"	+ WC_ATTR[cardnoN][colName]			+ br 	+ wcAttr[cardnoN]		+ thE
						+ "<th rowspan=\"1\" colspan=\"3\" style=\"width: 20%;\">"	+ WC_ATTR[workareaN][colName]		+ br 	+ wcAttr[workareaN]	+ thE
						+ "<th rowspan=\"1\" colspan=\"1\" style=\"width: 20%;\">"	+ WC_ATTR[engineerN][colName]		+ br 	+ wcAttr[engineerN]		+ thE
						+ "<th rowspan=\"1\" colspan=\"1\" style=\"width: 10%;\">"	+ WC_ATTR[engineernoN][colName]		+ br 	+ wcAttr[engineernoN]	+ thE
						+ "<th rowspan=\"1\" colspan=\"1\" style=\"width: 10%;\">"	+ WC_ATTR[cardtimeN][colName]		+ br 	+ wcAttr[cardtimeN]		+ thE
						+ "<th rowspan=\"1\" colspan=\"2\" style=\"width: 20%;\">"	+ WC_ATTR[tonoN][colName] 			+ " " 	+ WC_ATTR[pubdateN][colName] + br + wcAttr[tonoN] + br + wcAttr[pubdateN] + thE
						+ "<th rowspan=\"1\" colspan=\"1\" style=\"width: 10%;\">"	+ WC_ATTR[changenoN][colName]		+ br 	+ wcAttr[changenoN]	+ thE
						+ trE
						+ tr
						+ "<th rowspan=\"2\" colspan=\"1\" >"	+ WSTEP_ATTR[steptimeN][colName]	+ thE
						+ "<th rowspan=\"2\" colspan=\"1\" >"	+ WSTEP_ATTR[stepareaN][colName]	+ thE
						+ "<th rowspan=\"1\" colspan=\"2\" >"	+ WSTEP_ATTR[etcN][colName]			+ thE
						+ thR2C3								+ wcAttr[nameN]						+ thE
						+ thR2									+ WC_ATTR[powerN][colName]			+ br + wcAttr[powerN]	+ thE
						+ thR2									+ WC_ATTR[serviceN][colName]		+ br + wcAttr[serviceN]	+ thE
						+ thR2									+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]	+ thE
						+ trE
						+ tr
						+ th									+ WSTEP_ATTR[systemN][colName]		+ thE
						+ th									+ WSTEP_ATTR[subsystemN][colName]	+ thE
						+ trE;

				rtSB.append(CSS.TB_THEAD);
				rtSB.append(theadHtml);
				rtSB.append(CSS.TB_THEAD_END);
				log.info("WC Header : "+rtSB.toString());
			}else {
				String theadHtml = ""
						+ tr
						+ thR2			+ WSTEP_ATTR[steptimeN][colName]	+ thE
						+ thR2			+ WSTEP_ATTR[stepareaN][colName]	+ thE
						+ thC2			+ WSTEP_ATTR[etcN][colName]			+ thE
						+ thR2C3		+ wcAttr[nameN]						+ thE
						+ thR2			+ checkList							+ thE
						+ thR2			+ WC_ATTR[powerN][colName]			+ br + wcAttr[powerN]	+ thE
						+ thR2			+ WC_ATTR[serviceN][colName]		+ br + wcAttr[serviceN]	+ thE
						+ thR2			+ picStr							+ thE
						+ thR2			+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]	+ thE
						+ trE
						+ tr
						+ th			+ WSTEP_ATTR[systemN][colName]		+ thE
						+ th			+ WSTEP_ATTR[subsystemN][colName]	+ thE
						+ trE;
				rtSB.append(CSS.TB_THEAD);
				rtSB.append(theadHtml);
				rtSB.append(CSS.TB_THEAD_END);
				log.info("WC Header : "+rtSB.toString());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.getHeaderAHtml Exception:"+ex.toString());
		}

		return rtSB;
	}


	/**
	 * 풋터 - TypeA
	 * @MethodName	: getFooterAHtml
	 * @AuthorDate		: LIM Y.M. / 2015. 1. 6.
	 * @ModificationHistory	: 2023.03.28 KTA 인 경우 revno 를 사용하도록 변경 - jingi.kim
	 * @param wcAttr
	 * @return
	 * "<tr><th>카드번호 1-004</th><th colspan='3'>직업구역 00</th><th>요구정비사 항공기</th><th>정비사번호</th><th>카드시간 0.5</th>"
	+ "<th colspan='2'>기술도서 번호 및 발간일<br>K.T.O.1T-50A-6WC-1 2005. 07. 30</th><th>변경판번호</th></tr>"
	 */
	public StringBuffer getFooterAHtml(String[] wcAttr) {

		StringBuffer rtSB = new StringBuffer();

		if (wcAttr == null) {
			return rtSB;
		}

		try {
			String br			= CSS.BR;
			String tr			= CSS.TB_TR;
			String trE			= CSS.TB_TREND;
			String th			= CSS.TB_TH;
			String thC2		= CSS.getTableTh(1, 2);
			String thC3		= CSS.getTableTh(1, 3);
			String thC4		= CSS.getTableTh(1, 4);
			String thE			= CSS.TB_THEND;
			//2022 07 04 Park.J.S. : KRA  헤더 문구 삭제 요청으로 분기 처리
			if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
				String tfootHtml = ""
						+ tr
						+ th			+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]		+ thE
						+ thC3		+ WC_ATTR[workareaN][colName]		+ br + wcAttr[workareaN]	+ thE
						+ th			+ WC_ATTR[engineerN][colName]		+ br + wcAttr[engineerN]		+ thE
						+ th			+ WC_ATTR[engineernoN][colName]	+ br + wcAttr[engineernoN]	+ thE
						+ th			+ WC_ATTR[cardtimeN][colName]		+ br + wcAttr[cardtimeN]		+ thE
						+ thC2		+ WC_ATTR[tonoN][colName] + " " + WC_ATTR[pubdateN][colName] + br + wcAttr[tonoN] + br + wcAttr[pubdateN] + thE
						+ th			+ WC_ATTR[changenoN][colName]		+ br + wcAttr[changenoN]	+ thE
						+ trE;

				rtSB.append(CSS.TB_TFOOT);
				rtSB.append(tfootHtml);
				rtSB.append(CSS.TB_TFOOT_END);
			}else {
				String tfootHtml = ""
						+ tr
						+ th			+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]		+ thE
						+ thC3		+ WC_ATTR[workareaN][colName]		+ br + wcAttr[workareaN]	+ thE
						+ th			+ WC_ATTR[engineerN][colName]		+ br + wcAttr[engineerN]		+ thE
						+ th			+ WC_ATTR[engineernoN][colName]	+ br + wcAttr[engineernoN]	+ thE
						+ th			+ WC_ATTR[cardtimeN][colName]		+ br + wcAttr[cardtimeN]		+ thE
						+ thC4		+ WC_ATTR[tonoN][colName] + " " + WC_ATTR[pubdateN][colName] + br + wcAttr[tonoN] + br + wcAttr[pubdateN] + thE
						+ th			+ WC_ATTR[changenoN][colName]		+ br + wcAttr[revnoN]	+ thE
						+ trE;

				rtSB.append(CSS.TB_TFOOT);
				rtSB.append(tfootHtml);
				rtSB.append(CSS.TB_TFOOT_END);
			}
			// log.info("WC getFooterAHtml : "+rtSB.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.getFooterAHtml Exception:"+ex.toString());
		}

		return rtSB;
	}
	/**
	 * <pre>
	 * 체크 박스 스킵 여부
	 * wstep SeqNum 확인해서 어떨게 표시할지 판단하는 함수
	 * wstep type 확인해서 어떨게 표시할지 판단하는 함수
	 * </pre>
	 * @since 2022 02 25
	 * @author Park. J.S.
	 * @param str
	 * @return
	 */
	public boolean checkStepStrPass(String str) {
		if("주기".equalsIgnoreCase(str)) {
			return true;
			//by ejkim 2022.10.03 제목을 체크박스 처리 로직으로 하단로직 제거
			//}else if("제목".equalsIgnoreCase(str)) {
			//	return true;
		}else if("경고".equalsIgnoreCase(str)) {
			return true;
		}else if("주의".equalsIgnoreCase(str)) {
			return true;
		}else if("제목".equalsIgnoreCase(str) && "KTA".equalsIgnoreCase(ext.getBizCode())) {//2023 01 03 Park.J.S. ADD : KTA 제목 타입 추가 2023 01 17 Park.J.S. Update : "KTA".equalsIgnoreCase(ext.getBizCode())
			return true;
		}else {
			return false;
		}
	}
	/**
	 * <pre>
	 * Attribuute 문자열 줄바끔 처리
	 * </pre>
	 * @since 2022 02 28
	 * @author Park. J.S.
	 * @param str
	 * @return
	 */
	private String checkLineEnd(String str) {
		//2023 01 18 Park.J.S. ADD  : null 처리 추가
		if(str == null ) {
			return "";
		}
		if(str.indexOf("&#13;") > -1) {
			return str.replaceAll("&#13;", "<br/>");
		}
		return str;
	}

	/**
	 * 2023.11.28 - workcard에서 작업단위부호 (WUC) 존재여부 확인 - jingi.kim
	 * @param wucCode, searchWord
	 * @return
	 */
	private boolean isSearchWordWUC( String wucCode, String searchWord ) {
		if ( wucCode == null ) return false;
		if ( searchWord == null ) return false;
		if ( "".equalsIgnoreCase(searchWord) ) return false;

		boolean isMatch = false;
		try {
			String[] arrWord = searchWord.split(IConstants.SEARCH_WORD_MARK);
			if (arrWord.length <= 0) return false;

			for (int i=0; i<arrWord.length; i++) {
				if ( wucCode.trim().equals(arrWord[i]) ) 	{ 	isMatch = true; 	}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return isMatch;
	}

	/**
	 * 2022 08 08 Park.J.S.
	 * 이미지 영역 용 별도 테이블 해더용으로 제작
	 * @param wcAttr
	 * @param wcType
	 * @return
	 */
	public StringBuffer getHeaderImgHtml(String[] wcAttr, String wcType) {
		log.info("getHeaderImgHtml wcType : "+wcType);
		StringBuffer rtSB = new StringBuffer();

		if (wcAttr == null) {
			return rtSB;
		}

		try {
			String br			= CSS.BR;
			String tr			= CSS.TB_TR;
			String trE			= CSS.TB_TREND;
			String th			= CSS.TB_TH;
			String thR2			= CSS.getTableTh(2, 1); //param=rowspan, colspan
			String thC2			= CSS.getTableTh(1, 2);
			String thC3			= CSS.getTableTh(1, 3);
			String thR2C3		= CSS.getTableTh(2, 3);
			String thE			= CSS.TB_THEND;
			//KTA 에서만 호출 아닌경우 호출 될경우 문제 있음
			if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
				String theadHtml = ""
						+ tr
						+ th			+ WC_ATTR[cardnoN][colName]			+ br 	+ wcAttr[cardnoN]		+ thE
						+ "<th rowspan=\"1\" colspan=\"3\" style=\"width: 150px;\">"			+ WC_ATTR[workareaN][colName]		+ br 	+ wcAttr[workareaN]	+ thE
						+ th			+ WC_ATTR[engineerN][colName]		+ br 	+ wcAttr[engineerN]		+ thE
						+ th			+ WC_ATTR[engineernoN][colName]		+ br 	+ wcAttr[engineernoN]	+ thE
						+ th			+ WC_ATTR[cardtimeN][colName]		+ br 	+ wcAttr[cardtimeN]		+ thE
						+ thC2			+ WC_ATTR[tonoN][colName] 			+ " " 	+ WC_ATTR[pubdateN][colName] + br + wcAttr[tonoN] + br + wcAttr[pubdateN] + thE
						+ th			+ WC_ATTR[changenoN][colName]		+ br 	+ wcAttr[changenoN]	+ thE
						+ trE
						+ tr
						+ thR2			+ WSTEP_ATTR[steptimeN][colName]	+ thE
						+ thR2			+ WSTEP_ATTR[stepareaN][colName]	+ thE
						+ thC2			+ WSTEP_ATTR[etcN][colName]			+ thE
						+ thR2C3		+ wcAttr[nameN]						+ thE
						+ thR2			+ WC_ATTR[powerN][colName]			+ br + wcAttr[powerN]	+ thE
						+ thR2			+ WC_ATTR[serviceN][colName]		+ br + wcAttr[serviceN]	+ thE
						+ thR2			+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]	+ thE
						+ trE
						+ tr
						+ th			+ WSTEP_ATTR[systemN][colName]		+ thE
						+ th			+ WSTEP_ATTR[subsystemN][colName]	+ thE
						+ trE;

				rtSB.append(CSS.TB_THEAD);
				rtSB.append(theadHtml);
				rtSB.append(CSS.TB_THEAD_END);
				log.info("WC Header : "+rtSB.toString());
			}else {
				String theadHtml = ""
						+ tr
						+ thR2			+ WSTEP_ATTR[steptimeN][colName]	+ thE
						+ thR2			+ WSTEP_ATTR[stepareaN][colName]	+ thE
						+ thC2			+ WSTEP_ATTR[etcN][colName]			+ thE
						+ thR2C3		+ wcAttr[nameN]						+ thE
						+ thR2			+ checkList							+ thE
						+ thR2			+ WC_ATTR[powerN][colName]			+ br + wcAttr[powerN]	+ thE
						+ thR2			+ WC_ATTR[serviceN][colName]		+ br + wcAttr[serviceN]	+ thE
						+ thR2			+ picStr							+ thE
						+ thR2			+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]	+ thE
						+ trE
						+ tr
						+ th			+ WSTEP_ATTR[systemN][colName]		+ thE
						+ th			+ WSTEP_ATTR[subsystemN][colName]	+ thE
						+ trE;
				if("typef".equalsIgnoreCase(wcType) || "typee".equalsIgnoreCase(wcType)) {
					theadHtml = ""
							+ tr
							+ thR2C3		+ wcAttr[nameN]						+ thE
							+ thR2			+ checkList							+ thE
							+ thR2			+ WC_ATTR[powerN][colName]			+ br + wcAttr[powerN]	+ thE
							+ thR2			+ WC_ATTR[serviceN][colName]		+ br + wcAttr[serviceN]	+ thE
							+ thR2			+ picStr							+ thE
							+ thR2			+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]	+ thE
							+ trE;
				}else if("typec".equalsIgnoreCase(wcType)) {
					theadHtml = ""
							+ tr
							+ th		+ WC_ATTR[tonoN][colName] + " " + WC_ATTR[pubdateN][colName] + br + wcAttr[tonoN] + br + wcAttr[pubdateN] + thE
							+ th		+ WC_ATTR[workareaN][colName]		+ br + wcAttr[workareaN]	+ thE
							+ th		+ checkList		+ thE
							+ th		+ picStr		+ thE
							+ th		+ WC_ATTR[revnoN][colName]		+ br + wcAttr[revnoN]	+ thE
							+ th		+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]	+ thE
							+ trE;
				}else if("typec_2".equalsIgnoreCase(wcType)) {
					theadHtml = ""
							+ tr
							+ th		+ WC_ATTR[tonoN][colName] + " " + WC_ATTR[pubdateN][colName] + br + wcAttr[tonoN] + br + wcAttr[pubdateN] + thE
							+ thC2		+ WC_ATTR[workareaN][colName]		+ br + wcAttr[workareaN]	+ thE
							+ th		+ checkList		+ thE
							+ th		+ picStr		+ thE
							+ th		+ WC_ATTR[revnoN][colName]		+ br + wcAttr[revnoN]	+ thE
							+ th		+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]	+ thE
							+ trE;
				} else if("typed".equalsIgnoreCase(wcType)) {
					theadHtml = ""
							+ tr
							+ th		+ WC_ATTR[tonoN][colName] + " " + WC_ATTR[pubdateN][colName] + br + wcAttr[tonoN] + br + wcAttr[pubdateN] + thE
							+ th		+ WC_ATTR[workareaN][colName]		+ br + wcAttr[workareaN]	+ thE
							+ th		+ checkList		+ thE
							+ thR2		+ WC_ATTR[powerN][colName]			+ br + wcAttr[powerN]	+ thE
							+ thR2		+ WC_ATTR[serviceN][colName]		+ br + wcAttr[serviceN]	+ thE
							+ th		+ picStr		+ thE
							+ th		+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]	+ thE
							+ trE;
				}
				rtSB.append(CSS.TB_THEAD);
				rtSB.append(theadHtml);
				rtSB.append(CSS.TB_THEAD_END);
				log.info("WC Header : "+rtSB.toString());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.getHeaderAHtml Exception:"+ex.toString());
		}

		return rtSB;
	}
	/**
	 * 2022 08 08 Park.J.S
	 * @param wcAttr
	 * @param wcType
	 * @return
	 */
	public StringBuffer getFooterImageHtml(String[] wcAttr, String wcType) {

		StringBuffer rtSB = new StringBuffer();

		if (wcAttr == null) {
			return rtSB;
		}

		try {
			String br			= CSS.BR;
			String tr			= CSS.TB_TR;
			String trE			= CSS.TB_TREND;
			String th			= CSS.TB_TH;
			String thC2			= CSS.getTableTh(1, 2);
			String thC3			= CSS.getTableTh(1, 3);
			String thC4			= CSS.getTableTh(1, 4);
			String thE			= CSS.TB_THEND;
			//KTA 만 호출해야함
			if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
				String tfootHtml = ""
						+ tr
						+ th			+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]		+ thE
						+ thC3		+ WC_ATTR[workareaN][colName]		+ br + wcAttr[workareaN]	+ thE
						+ th			+ WC_ATTR[engineerN][colName]		+ br + wcAttr[engineerN]		+ thE
						+ th			+ WC_ATTR[engineernoN][colName]	+ br + wcAttr[engineernoN]	+ thE
						+ th			+ WC_ATTR[cardtimeN][colName]		+ br + wcAttr[cardtimeN]		+ thE
						+ thC2		+ WC_ATTR[tonoN][colName] + " " + WC_ATTR[pubdateN][colName] + br + wcAttr[tonoN] + br + wcAttr[pubdateN] + thE
						+ th			+ WC_ATTR[changenoN][colName]		+ br + wcAttr[changenoN]	+ thE
						+ trE;

				rtSB.append(CSS.TB_TFOOT);
				rtSB.append(tfootHtml);
				rtSB.append(CSS.TB_TFOOT_END);
			}else {
				String tfootHtml = ""
						+ tr
						+ th			+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]		+ thE
						+ thC3		+ WC_ATTR[workareaN][colName]		+ br + wcAttr[workareaN]	+ thE
						+ th			+ WC_ATTR[engineerN][colName]		+ br + wcAttr[engineerN]		+ thE
						+ th			+ WC_ATTR[engineernoN][colName]	+ br + wcAttr[engineernoN]	+ thE
						+ th			+ WC_ATTR[cardtimeN][colName]		+ br + wcAttr[cardtimeN]		+ thE
						+ thC4		+ WC_ATTR[tonoN][colName] + " " + WC_ATTR[pubdateN][colName] + br + wcAttr[tonoN] + br + wcAttr[pubdateN] + thE
						+ th			+ WC_ATTR[revnoN][colName]		+ br + wcAttr[revnoN]	+ thE
						+ trE;
				if("typec_2".equalsIgnoreCase(wcType)) {
					tfootHtml = ""
							+ tr
							+ th			+ WC_ATTR[cardnoN][colName]			+ br + wcAttr[cardnoN]		+ thE
							+ th		+ WC_ATTR[workareaN][colName]		+ br + wcAttr[workareaN]	+ thE
							+ th			+ WC_ATTR[engineerN][colName]		+ br + wcAttr[engineerN]		+ thE
							+ th			+ WC_ATTR[engineernoN][colName]	+ br + wcAttr[engineernoN]	+ thE
							+ th			+ WC_ATTR[cardtimeN][colName]		+ br + wcAttr[cardtimeN]		+ thE
							+ th		+ WC_ATTR[tonoN][colName] + " " + WC_ATTR[pubdateN][colName] + br + wcAttr[tonoN] + br + wcAttr[pubdateN] + thE
							+ th			+ WC_ATTR[revnoN][colName]		+ br + wcAttr[revnoN]	+ thE
							+ trE;
				}
				rtSB.append(CSS.TB_TFOOT);
				rtSB.append(tfootHtml);
				rtSB.append(CSS.TB_TFOOT_END);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.getFooterAHtml Exception:"+ex.toString());
		}

		return rtSB;
	}

	/**
	 * 2022 09 13
	 * <pre>
	 * 야전급 워크 카드 전용으로 추가함 현재  ContParser에서 직접 호출중
	 * </pre>
	 * @author Park.J.S.
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public StringBuffer getWCTypeCHtml(ParserDto psDto, Node paNode) {

		StringBuffer rtSB = new StringBuffer();

		if (paNode == null || !paNode.getNodeName().equals(DTD.SYSTEM)) {
			return rtSB;
		}

		try {
			NodeList curList = paNode.getChildNodes();
			Node curNode;
			String nodeName;
			String headType = "";
			rtSB.append(CSS.TB_TABLE_WC);
			for (int i=0; i<curList.getLength(); i++){
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				//Attribute Check
				NamedNodeMap curAttr 		= curNode.getAttributes();
				String nodeAttName			=  XmlDomParser.getAttributes(curAttr, "name");
				String nodeAttTypt			=  XmlDomParser.getAttributes(curAttr, "type");
				String nodeAttCardnum		=  XmlDomParser.getAttributes(curAttr, "cardnum");
				String nodeAttToname		=  XmlDomParser.getAttributes(curAttr, "toname");
				String nodeAttPublicdate	=  XmlDomParser.getAttributes(curAttr, "publicdate");
				String nodeAttCardchangeno	=  XmlDomParser.getAttributes(curAttr, "cardchangeno");
				String nodeAttTitle			=  XmlDomParser.getAttributes(curAttr, "title1");
				log.info("Node Attribute nodeAttName : "+nodeAttName+", nodeAttTypt : "+nodeAttTypt+", nodeAttCardnum : "+nodeAttCardnum+", nodeAttToname : "+nodeAttToname+", nodeAttPublicdate : "+nodeAttPublicdate+", nodeAttCardchangeno : "+nodeAttCardchangeno+", nodeAttTitle : "+nodeAttTitle);
				log.info("nodeName : "+nodeName);
				if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals("workcards")) {
					log.info("WorkCard Type C Head Make : "+nodeAttTypt);
					if("field_wc_b".equalsIgnoreCase(nodeAttTypt)) {
						headType = "B";
						StringBuffer colGroupBubber = new StringBuffer();
						colGroupBubber.append("<colgroup>");
						for(int j=0;j<20;j++) {
							colGroupBubber.append("<col width=\"5%\">");
						}
						colGroupBubber.append("</colgroup>");
						String theadHtml = colGroupBubber.toString()
								+ CSS.TB_TR
								+ CSS.getTableTh(1, 2)	+ "카드<br/>"+nodeAttCardnum						+ CSS.TB_THEND
								+ CSS.getTableTh(1, 5)	+ nodeAttToname+"<br/>발간일 "+nodeAttPublicdate	+ CSS.TB_THEND
								+ CSS.getTableTh(1, 3)	+ "변경판 번호 "	+nodeAttCardchangeno				+ CSS.TB_THEND
								+ CSS.getTableTh(1, 7)	+ nodeAttTitle									+ CSS.TB_THEND
								+ "<th rowspan=\"2\" colspan=\"3\" style=\"padding:unset;\">"
								+ "<table width=\"100%\">"
								+ "<tr style=\"border: 1px solid;\"><td>전원</td><td>"+XmlDomParser.getAttributes(curAttr, "power")+"</td></tr>"
								+ "<tr style=\"border: 1px solid;\"><td>유압</td><td>"+XmlDomParser.getAttributes(curAttr, "oilpressure")+"</td></tr>"
								+ "<tr style=\"border: 1px solid;\"><td>냉각공기</td><td>"+XmlDomParser.getAttributes(curAttr, "aircooling")+"</td></tr>"
								+ "</table>"									+ CSS.TB_THEND
								+ CSS.TB_TREND
								+ CSS.TB_TR
								+ CSS.getTableTh(1, 1)	+ "작업<br/>구역"																+ CSS.TB_THEND
								+ CSS.getTableTh(1, 3)	+ "작업 단위 부호"+"<br/>(WUC)"													+ CSS.TB_THEND
								+ CSS.getTableTh(1, 1)	+ "CR<br/>SN"																+ CSS.TB_THEND
								+ CSS.getTableTh(1, 2)	+ "작업 시간"+"<br/>"+	XmlDomParser.getAttributes(curAttr, "worktime")			+ CSS.TB_THEND
								+ CSS.getTableTh(1, 5)	+ XmlDomParser.getAttributes(curAttr, "title2").replaceAll("&#13;", "<br/>")+ CSS.TB_THEND
								+ CSS.getTableTh(1, 5)	+ XmlDomParser.getAttributes(curAttr, "title3")								+ CSS.TB_THEND
								+ CSS.TB_TREND;
						rtSB.append(theadHtml);
					}else {
						headType = "A";
						StringBuffer colGroupBubber = new StringBuffer();
						colGroupBubber.append("<colgroup>");
						for(int j=0;j<20;j++) {
							colGroupBubber.append("<col width=\"5%\">");
						}
						colGroupBubber.append("</colgroup>");
						String theadHtml = ""
								+ colGroupBubber.toString()
								+ CSS.TB_TR
								+ CSS.getTableTh(1, 2)			+ "카드<br/>"+nodeAttCardnum						+ CSS.TB_THEND
								+ CSS.getTableTh(1, 8)			+ nodeAttToname+"<br/>발간일 "+nodeAttPublicdate	+ CSS.TB_THEND
								+ CSS.getTableTh(1, 3)			+ "변경판 번호 "	+nodeAttCardchangeno				+ CSS.TB_THEND
								+ CSS.getTableTh(1, 7)			+ nodeAttTitle									+ CSS.TB_THEND
								+ CSS.TB_TREND;
						rtSB.append(theadHtml);
					}
				}else if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					//NodeList rowList	= XmlDomParser.getNodeListFromXPathAPI(curNode, XALAN.REG_TABLE_ROWH);
					if(!"descinfo".equalsIgnoreCase(curNode.getNodeName())) {continue;}
					NodeList tableList	= XmlDomParser.getNodeListFromXPathAPI(curNode, "//table");
					log.info("tableList : "+tableList+", size "+tableList.getLength());
					NodeList rowList	= tableList.item(0).getChildNodes();
					String colcntStr = XmlDomParser.getAttributes(tableList.item(0).getAttributes(), "colcnt");
					int colcnt = 0;
					if(colcntStr != null &&  !"".equals(colcntStr) ) {
						log.info("colcntStr : "+colcntStr);
						try {colcnt = Integer.valueOf(colcntStr);}catch (Exception e) {}
					}
					if("B".equalsIgnoreCase(headType)) {
						int maxTdNum = 0;
						int tempMaxTdNum = 0;
						for(int j=0;j<rowList.getLength();j++) {
							Node trNode = rowList.item(j);
							log.info("rowList.item["+rowList.getLength()+"] : "+j+", tempMaxTdNum : "+tempMaxTdNum+", maxTdNum : "+maxTdNum);
							if(tempMaxTdNum > maxTdNum) {
								maxTdNum = tempMaxTdNum;
							}
							tempMaxTdNum = 0;
							if (trNode.getNodeType() == Node.ELEMENT_NODE) {
								if (!trNode.getNodeName().equals(DTD.TB_ROWH)) {continue;}
								try {
									Node tdNode				= null;
									String tdNodeName		= "";
									StringBuffer nodeSB		= new StringBuffer();
									NodeList tdList = trNode.getChildNodes();
									int idx = 1;
									boolean isLastTd = false;//마지막  TD 여부
									int tempCount	= 0;//마지막  TD 여부 확인 용
									int tempAddNum	= 0;//15 분할 최종 처리용
									log.info("tdList.getLength() : "+tdList.getLength());
									for (int k=0; k<tdList.getLength(); k++){
										tdNode = tdList.item(k);
										tdNodeName = tdNode.getNodeName();
										log.info("curNode.getNodeType() : "+tdNode.getNodeType()+", nodeName.equals(DTD.TB_ENTRY) : "+nodeName.equals(DTD.TB_ENTRY));
										if (tdNode.getNodeType() == Node.ELEMENT_NODE && tdNodeName.equals(DTD.TB_ENTRY)) {
											String colspanStr = XmlDomParser.getAttributes(tdNode.getAttributes(), "colspan");
											int colspanNum = 0;
											if(colspanStr != null && !"".equals(colspanStr)) {
												colspanNum = Integer.valueOf(colspanStr);
											}
											tempCount = tempCount+colspanNum;
											if(tempCount == colcnt) {
												isLastTd =  true;
											}
											//2022 11 21 Park.J.S. WC 카드의 경우 TD가 단 1개인 경우 처리 추가
											if("1".equals(colspanStr) && tdList.getLength() == 1) {
												log.info("colspan is only 1.......");
												Node colspanNode = tdNode.getAttributes().getNamedItem("colspan");
												colspanNode.setNodeValue("8");
											}else if(tdList.getLength() == 1) {//2022 12 16 Park.J.S. : WC 카드의 경우 TD가 단 1개인 경우 예외 상황 처리 추가
												if(colspanStr != "") {
													if(maxTdNum != 0) {//최초가 아닐경우 최초인경우 중간에 rowspan 이없어서 칸당 계산 불필요
														Node colspanNode = tdNode.getAttributes().getNamedItem("colspan");
														log.info("tdList.getLength() is 1 maxTdNum : "+maxTdNum);
														colspanNode.setNodeValue((Integer.valueOf(colspanStr)*(15/maxTdNum))+"");
													}
												}
											}
											String tdStr = tableParser.getTdHtml(psDto, tdNode, 1, new Date().getTime()+"_"+j+"_"+k, false).toString();
											log.info(isLastTd+"-"+idx+"==>tdStr : "+tdStr);
											if(idx == 1) {
												//tdStr = tdStr.replace("", "");
											}else if(idx == 2) {
												tdStr = tdStr.replaceFirst("colspan=\"1\"", "colspan=\"3\"");
												tdStr = tdStr.replaceFirst("colspan='1'", "colspan='3'");
											}else if(idx == 3) {
												//tdStr = tdStr.replace("colspan=\"1\"", "colspan=\"1\"");
											}else {
												int colSpanNum = 15;
												tempMaxTdNum += colspanNum;
												if(colcnt != 0) {
													colSpanNum = (int) Math.floor(((double)15/(colcnt-3))*colspanNum);
													log.info("idx : "+idx+", colcnt : "+colcnt+", check : "+(Math.floor(15*100/(colcnt-3)) != Math.floor(15/(colcnt-3))*100)+", colSpanNum : "+colSpanNum);
													if(isLastTd && idx == colcnt && Math.floor(15*100/(colcnt-3)) != Math.ceil(15/(colcnt-3))*100) {
														colSpanNum++;
													}else{

													}
													tempAddNum = tempAddNum+colSpanNum;
													if(isLastTd) {
														if(tempAddNum < 15) {
															colSpanNum = colSpanNum+15-tempAddNum;
														}
														log.info("idx : "+idx+", colcnt : "+colcnt+", colSpanNum : "+colSpanNum+", tempAddNum : "+tempAddNum);
													}
												}
												tdStr = tdStr.replaceFirst("colspan=\""+colspanStr+"\"", "colspan=\""+colSpanNum+"\"");
												tdStr = tdStr.replaceFirst("colspan='"+colspanStr+"'", "colspan='"+colSpanNum+"'");
												tdStr = tdStr.replaceFirst("style=\"", "style=\"text-align:left;");
												tdStr = tdStr.replaceFirst("style='", "style='text-align:left;");
											}
											log.info(isLastTd+"-"+idx+"==>tdStr : "+tdStr);
											nodeSB.append(tdStr);
											idx++;
										}
									}
									StringBuffer verSB = verParser.checkVersionHtml(psDto, paNode);
									String verEndStr = verParser.endVersionHtml(verSB);
									rtSB.append(CSS.TB_TR);
									rtSB.append(verSB);
									rtSB.append(nodeSB);
									rtSB.append(verEndStr);
									rtSB.append(CSS.TB_TREND);
								} catch (Exception ex) {
									ex.printStackTrace();
									log.info("TableParser.getTrHtml Exception:"+ex.toString());
								}
							}
						}
					}else {
						/*
						String tr			= CSS.TB_TR;
						String trE			= CSS.TB_TREND;
						String thE			= CSS.TB_THEND;
						String thC			= CSS.getTableTh(1, 20);
						rtSB.append(tr+thC+contParser.getDescinfoHtml(psDto, curNode, 1)+thE+trE);
						*/
						for(int j=0;j<rowList.getLength();j++) {
							Node trNode = rowList.item(j);
							if (trNode.getNodeType() == Node.ELEMENT_NODE) {
								if (!trNode.getNodeName().equals(DTD.TB_ROWH)) {continue;}
								try {
									Node tdNode				= null;
									String tdNodeName		= "";
									StringBuffer nodeSB		= new StringBuffer();
									NodeList tdList = trNode.getChildNodes();
									int idx = 1;
									for (int k=0; k<tdList.getLength(); k++){
										tdNode = tdList.item(k);
										tdNodeName = tdNode.getNodeName();
										log.info("curNode.getNodeType() : "+tdNode.getNodeType()+", nodeName.equals(DTD.TB_ENTRY) : "+nodeName.equals(DTD.TB_ENTRY));
										if (tdNode.getNodeType() == Node.ELEMENT_NODE && tdNodeName.equals(DTD.TB_ENTRY)) {
											String tdStr = tableParser.getTdHtml(psDto, tdNode, 1, new Date().getTime()+"_"+j+"_"+k, false).toString();
											log.info("tdStr : "+tdStr);
											if(idx == 1 && colcnt == 1) {
												tdStr = tdStr.replaceFirst("colspan=\"1\"", "colspan=\"20\"");
												tdStr = tdStr.replaceFirst("colspan='1'", "colspan='20'");
											}else if(idx == 2 &&  colcnt == 3) {
												tdStr = tdStr.replaceFirst("colspan=\"1\"", "colspan=\"18\"");
												tdStr = tdStr.replaceFirst("colspan='1'", "colspan='18'");
												tdStr = tdStr.replaceFirst("style=\"", "style=\"text-align:left;");
												tdStr = tdStr.replaceFirst("style='", "style='text-align:left;");
											}else if(idx == 2 &&  colcnt == 2) {
												tdStr = tdStr.replaceFirst("colspan=\"1\"", "colspan=\"19\"");
												tdStr = tdStr.replaceFirst("colspan='1'", "colspan='19'");
												tdStr = tdStr.replaceFirst("style=\"", "style=\"text-align:left;");
												tdStr = tdStr.replaceFirst("style='", "style='text-align:left;");
											}else {
												if(idx > 4 &&  colcnt > 3) {
												}else if(idx == 4 && "4".equals(colcnt)) {
												}else if(idx == 5 && "4".equals(colcnt)) {
												}else if(idx == 6 && "4".equals(colcnt)) {
												}else if(idx == 7 && "4".equals(colcnt)) {
												}else if(idx == 3 && "4".equals(colcnt)) {
												}else if(idx == 3 && "4".equals(colcnt)) {
												}
											}
											log.info("tdStr : "+tdStr);
											nodeSB.append(tdStr);
											idx++;
										}
									}
									StringBuffer verSB = verParser.checkVersionHtml(psDto, paNode);
									String verEndStr = verParser.endVersionHtml(verSB);
									rtSB.append(CSS.TB_TR);
									rtSB.append(verSB);
									rtSB.append(nodeSB);
									rtSB.append(verEndStr);
									rtSB.append(CSS.TB_TREND);
								} catch (Exception ex) {
									ex.printStackTrace();
									log.info("TableParser.getTrHtml Exception:"+ex.toString());
								}
							}
						}
					}
				}
			}
			rtSB.append(CSS.TB_TABLEEND);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.getWCTypeCHtml Exception:"+ex.toString());
		}

		return rtSB;
	}

	/**
	 * <pre>
	 * 2022 12 29 Park.J.S.
	 * 버전파서에서 사용하기 위해 신규 생성
	 * </pre>
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public StringBuffer getWStepForVersionHtml(ParserDto psDto, Node curNode) {
		log.info("paNode : "+curNode.getNodeName());
		StringBuffer rtSB = new StringBuffer();

		if (curNode == null || !curNode.getNodeName().equals(DTD.WC_WSTEP)) {
			return rtSB;
		}

		try {
			StringBuffer nodeSB	= new StringBuffer();

			String tr			= CSS.TB_TR;
			String trE			= CSS.TB_TREND;
			String tdC5			= CSS.getTableTd(1, 5); //param=rowspan, colspan
			String tdE			= CSS.TB_TDEND;
			String imgPath		= "";

			String nodeName = curNode.getNodeName();

			if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.WC_WSTEP)) {

				String tbodyHtml		= "";
				String checkText		= "";
				String seqnumText		= "";
				String shapeText		= "";
				String descText			= "";
				String alertText		= "";

				String[] wstepAttr = this.inputWStepAttr(psDto, curNode);
				log.info("wstepAttr : "+wstepAttr+", ");
				if (wstepAttr != null) {

					NodeList childList = curNode.getChildNodes();
					for (int k=0; k<childList.getLength(); k++)
					{
						Node childNode = childList.item(k);
						String childName = childNode.getNodeName();
						log.info("childNode.getNodeType()  : "+childNode.getNodeType()+", childName : "+childName);
						//경고 or 문자열단락
						if (childNode.getNodeType() == Node.ELEMENT_NODE && childName.equals(DTD.ALERT)) {
							checkText	= "";
							alertText 	+= alertParser.getAlertHtml(psDto, childNode).toString();
						} else if (childNode.getNodeType() == Node.ELEMENT_NODE && childName.equals(DTD.TEXT)) {
							//2022 06 17 Park J.S. : 특정 상황에서 <alert 태그 없는 경우 처리 위해 추가  현재 전부 note 처리됨 추가 양식 있을 경우 해당 처리 로직 추가 필요
							String seqNum = XmlDomParser.getAttributes(childNode.getParentNode().getAttributes(), "SeqNum");
							if(seqNum != null && "".equals(seqNum)) {
								//2022 06 17 Park J.S. : SeqNum 없는 경우 type 체크 필요
								seqNum = XmlDomParser.getAttributes(childNode.getParentNode().getAttributes(), "type");
							}
							log.info("Check seqNum : "+seqNum);
							if(seqNum != null && (
									seqNum.equalsIgnoreCase("NOTE") || seqNum.equalsIgnoreCase("NOPE") || seqNum.equalsIgnoreCase("WARNING") || seqNum.equalsIgnoreCase("CAUTION") ||
											seqNum.indexOf("주의") >-1 || seqNum.indexOf("경고") > -1 || seqNum.indexOf("주") > -1
							)) {
								checkText	= "";
								ExpisCommonUtile utile = ExpisCommonUtile.getInstance();
								String xmlStr = utile.xmlToString(childNode);
								//2022 07 13 경고창에 여러줄 나올경우 처리
								for (int idx=k+1; idx<childList.getLength(); idx++) {
									xmlStr += utile.xmlToString(childList.item(idx));
								}
								String alertType = "note";
								if(seqNum.indexOf("NOPE") >-1) {
									alertType = "note";
								}else if(seqNum.indexOf("NOTE") >-1) {
									alertType = "note";
								}else if(seqNum.indexOf("경고") >-1) {
									alertType = "warning";
								}else if(seqNum.indexOf("주의") >-1) {
									alertType = "caution";
								}else if(seqNum.indexOf("주") >-1) {
									alertType = "note";
								}else if(seqNum.indexOf("WARNING") >-1) {
									alertType = "warning";
								}else if(seqNum.indexOf("CAUTION") >-1) {
									alertType = "caution";
								}
								String alertStr  = "<alert id=\"ABCD"+k+new Date().getTime()+k+"ABCD\" name=\"\" type=\""+alertType+"\" itemid=\"\" ref=\"\" status=\"a\" version=\"\">";
								Node alertNode = utile.createDomTree(alertStr+xmlStr+"</alert>", 1);
								log.info("alertNodeStr : "+alertStr+xmlStr+"</alert>");
								alertText 	+= alertParser.getAlertHtml(psDto, alertNode.getFirstChild()).toString();
								break;
							}else {
								//2022 02 25 Park J.S. : 주기등 특이상황에서 체크박스 안나오게 추가
								if(checkStepStrPass(wstepAttr[seqnumN])) {
									checkText	= "";
								}else if(checkStepStrPass(wstepAttr[typeN])) {
									checkText	= "";
								}else{
									checkText	= CSS.INPUT_CHK;
								}
								descText 	+= textParser.getTextWorkcard(psDto, childNode).toString();
							}
						}
					}
					//설명 부분
					if (!descText.equals("")) {
						String style = "";
						if (!wstepAttr[seqnumN].equals("") && wstepAttr[seqnumN].charAt(0) < VAL.WC_SEQNUM_ALPHABET) {
							if (wstepAttr[reiterN].equals(VAL.WC_REITER_2)) {
								String iconPath = imgPath + IConstants.WC_ICON_REITER + wstepAttr[seqnumN] + IConstants.WC_ICON_EXT_JPG;
								seqnumText = CSS.getDivIcon("", iconPath, CSS.SIZE_WC_ICON, CSS.SIZE_WC_ICON, "");
							} else if (wstepAttr[reiterN].equals(VAL.WC_REITER_3)) {
								String iconPath = imgPath + IConstants.WC_ICON_REITER3 + wstepAttr[seqnumN] + IConstants.WC_ICON_EXT_JPG;
								seqnumText = CSS.getDivIcon("", iconPath, CSS.SIZE_WC_ICON, CSS.SIZE_WC_ICON, "");
							} else if(checkStepStrPass(wstepAttr[seqnumN])){//2022 02 25 Park J.S.
								seqnumText = wstepAttr[seqnumN] + "<br/> ";
							} else {
								if("".equals(wstepAttr[seqnumN])){
									seqnumText = wstepAttr[seqnumN] + "&nbsp;&nbsp;";
								}else {
									seqnumText = wstepAttr[seqnumN] + ". ";
								}
							}
							style	= CSS.P_WC_STEP1;
						} else {
							if(checkStepStrPass(wstepAttr[seqnumN])){//2022 02 25 Park J.S.
								seqnumText = wstepAttr[seqnumN] + "<br/> ";
							} else {
								if("".equals(wstepAttr[seqnumN])){
									seqnumText = wstepAttr[seqnumN] + "&nbsp;&nbsp;";
								}else {
									seqnumText = wstepAttr[seqnumN] + ". ";
								}
							}
							style	= CSS.P_WC_STEP2;
						}

						if (!descText.equals("")) {
							descText = CodeConverter.getCodeConverter(psDto, descText, "", "");
						}

						//2022 02 28 Park.J.S. : 체크박스 없는경우  P가 아닌 Span 사용
						if(checkText.equals("")){
							style = style.replace("<p", "<span");
						}

						//by ejkim 2022.10.03 workcard 내 제목 미표기로 제목이 있으면 표시되지 않도록 수정, css도 변경
						if( seqnumText.trim().equals("제목.")) {
							style = style.replace(">", "style='text-indent:unset; margin-left: unset;'>");
							descText = style + descText + CSS.P_END;
						}else {
							descText = style + seqnumText + descText + CSS.P_END;
						}
					}

					//shape 값이 A,B,C,1,2 일 경우 아이콘으로 표시, 그 외는 문자대로 표시
					String shape = wstepAttr[shapeN];
					if (shape.length() == 1 && VAL.WC_SHAPE.indexOf(shape) > -1) {
						String iconPath = imgPath + IConstants.WC_ICON_SHAPE + shape + IConstants.WC_ICON_EXT_JPG;
						shapeText = CSS.getDivIcon("", iconPath, CSS.SIZE_WC_ICON, CSS.SIZE_WC_ICON, "");
					} else {
						shapeText = shape;

						// 2023.08.24 - 여러개 타입 지정시 갯수만큼 아이콘 표시되도록 추가 - jingi.kim
						if ( shape.length() > 1 ) {
							StringBuffer shapeSB = new StringBuffer();
							for ( char chs : shape.toCharArray() ) {
								if ( VAL.WC_SHAPE.indexOf(chs) > -1 ) {
									String iconPath = imgPath + IConstants.WC_ICON_SHAPE + chs + IConstants.WC_ICON_EXT_JPG;
									shapeSB.append( CSS.getDivIcon("", iconPath, CSS.SIZE_WC_ICON, CSS.SIZE_WC_ICON, "") );
								}
							}
							if ( shapeSB.length() > 0 ) {
								shapeText = shapeSB.toString();
							}
						}
					}

					//2022.02.28 Park.J.S. : descText가 공백 문자일경우 checkText 공백처리(뜨어쓰기 경고창 등 실제로 사용할 경우를 대비해서 체크 박스만 제거)
					// ((checkText.equals("")) ? td : "<td style='text-align: left;'>") 변경

					if(descText.trim().equals("")) {
						checkText = "";
					}
					//2022 11 23 Park.J.S. : 버전관련 수정 추가.
					StringBuffer verSB = verParser.checkVersionHtml(psDto, curNode);
					//System.out.println("verSB : "+verSB.toString());
					String verEndStr = verParser.endVersionHtml(verSB);
					if (verSB != null && verSB.toString().length() > 0) {
						verSB.append("&nbsp;");
					}
					tbodyHtml = CSS.TB_TABLE_WC
							+ tr + "<td style=\"vertical-align: top;\">"	+ (verSB.toString())+checkLineEnd(wstepAttr[steptimeN])+verEndStr		+ tdE
							+ "<td style=\"vertical-align: top;\">"			+ checkLineEnd(wstepAttr[stepareaN])									+ tdE
							+ "<td style=\"vertical-align: top;\">"			+ checkLineEnd(wstepAttr[systemN])										+ tdE
							+ "<td style=\"vertical-align: top;\">"			+ checkLineEnd(wstepAttr[subsystemN])									+ tdE
							+ tdC5
							+ CSS.TB_TABLE_WC2 + CSS.TB_TBODY + CSS.addStyle(tr,"display: block;")
							+ "<td style=\"width: 20px;\">"				+ shapeText							+ tdE
							+ "<td style=\"width: 20px;\">"				+ wstepAttr[significanceN]			+ tdE
							+ "<td style='text-align: left;'>" + alertText + descText + tdE
							+ trE + CSS.TB_TBODY_END + CSS.TB_TABLEEND
							+ tdE + trE + CSS.TB_TABLEEND;
					nodeSB.append(tbodyHtml);
				}
			}
			rtSB.append(CSS.TB_TBODY);
			rtSB.append(nodeSB);
			rtSB.append(CSS.TB_TBODY_END);

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("WorkcardParser.getWStepAHtml Exception:"+ex.toString());
		}

		return rtSB;
	}
}
