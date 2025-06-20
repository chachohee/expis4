package com.expis.ietm.parser;

import com.expis.common.IConstants;
import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.domparser.*;
import com.expis.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Date;


/**
 * [공통모듈]표(Table) Parser Class			
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TableParser {

//	private final AlertParser alertParser;
	private final ObjectProvider<AlertParser> alertParser;
//	private final GrphParser grphParser;
	private final ObjectProvider<GrphParser> grphParser;
	private final TextParser textParser;
//	private final VersionParser verParser;
	private final ObjectProvider<VersionParser> verParser;
	private final IconParser iconParser;

	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	ExternalFileEx ext = ctx.getBean("extConf", ExternalFileEx.class);
	
	/**
	 * private final  한 클래스 변수 선언
	 */
	private double TABLE_WIDTH = 0.0;

	
	/**
	 * Table 파싱하여 HTML로 리턴 메소드
	 * @MethodName			: getTableHtml
	 * @AuthorDate			: LIM Y.M.  / 2014. 6. 24.
	 * @ModificationHistory	: LIM Y.M.	/ 2019. 9. 06. 표 제목 추출하여 추가(표 인쇄 기능 추가 관련)
	 * @ModificationHistory	: LIM Y.M.	/ 2019.12.03. 표 제목 없는 고장배제표 출력 기능 추가
	 * @ModificationHistory	: Park J.S.	/ 2022.01.18. 테이블 들여쓰기 LSAM 제외
	 * @ModificationHistory	: Park J.S.	/ 2022.03.03. boolean lineFlag 처리해서 준비사항의 경우 테이블 선 나오게 수정
	 * @ModificationHistory	: Park J.S.	/ 2023.02.02. Table 하위 객체 들여쓰기 댑스 부모 값상속 받지 않게 0로 변경(L-SAM만 반영 사이트 이펙트 이슈)
	 * @param psDto, paNode, nDepth, status
	 * @return
	 */
	public StringBuffer getTableHtml(ParserDto psDto, Node paNode, int nDepth, String status) {
		log.info("getTableHtml : "+psDto.getFrameClassNum());
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.getNodeName().equals(DTD.TABLE)) {
			return rtSB;
		}
		int tableDepth = 0;

		boolean lineFlag = false;
		try {
			log.info("Table Parent Node Name : "+paNode.getParentNode().getNodeName());
			if(paNode.getParentNode().getNodeName().equals(DTD.IN_REQCOND)) {
				lineFlag = true;
			}
			log.info("lineFlag : "+lineFlag);
		}catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
			Node curNode			= null;
			Node statNode = null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			int cnt = 0;
			StringBuffer captionSB	= new StringBuffer();
			
			//Table의 column width 계산
			// 2023.04.07 - 일부 테이블 정렬 문제로 이전 모듈 원복 - jingi.kim
			nodeSB.append( this.getColWidth(paNode) );
			//2023 01 11 속도 개선 
			//nodeSB.append(this.getColWidth_New(paNode));
			//Table의 id 추출
			String tblId = XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.ID);
			
			//Table의 rowhhef 시현
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++) {
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.TB_ROWH)) {
					
					//Table의 entry 내용 시현
					//20190906 edit LYM 표 제목 row인지, 표 본문 row인지 구별하여 추출(표 제목에 인쇄 기능 추가)
					Node captionNode = XmlDomParser.getNodeFromXPathAPI(curNode, "./descendant::entry[@caption='1']"); //joe1
					log.info("captionNode : "+captionNode);
					if (captionNode != null) {
						//2023.02.02. Table 하위 객체 들여쓰기 댑스 부모 값상속 받지 않게 1로 변경
						captionSB.append( this.getTrHtml(psDto, curNode, tableDepth, tblId,lineFlag) );
					} else {
						//2023.02.02. Table 하위 객체 들여쓰기 댑스 부모 값상속 받지 않게 1로 변경
						nodeSB.append( this.getTrHtml(psDto, curNode, tableDepth, tblId,lineFlag) );
					}
					
					//grph Table td 갯수 체크 - ??
					NodeList statusChkList = curNode.getChildNodes();
					cnt = 0;
					for (int j=0; j<statusChkList.getLength(); j++) {
						statNode = statusChkList.item(j);
						nodeName = statNode.getNodeName();
						
						if (statNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.TB_ENTRY)) {
							cnt++;
						}
					}
				}
			}
			
			//고장배제표 출력 기능
			//20191203 add LYM 표(테이블) 타입이 고장배제표일 경우, 제목이 없어도 표출력 기능이 있어야함
			String tableType = XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TB_TABLETYPE);
			if (tableType.equals(VAL.TB_TBTYPE_FITABLE)) {
				String colcnt = XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TB_COLCNT);
				captionSB.append(CSS.TB_TR);
				captionSB.append(CSS.getTableTd(" colspan='"+colcnt+"' class='caption cap_top' "));
				captionSB.append("<div id='img_controll'><a title='표 출력' class='print' onclick=\"printAreaTable('tbl_"+tblId+"')\" href=\"javascript:void(0)\">표 출력</a></div>"); //joe 2
				captionSB.append(CSS.TB_TDEND);
				captionSB.append(CSS.TB_TREND);
			}
			
			//버전 정보
			StringBuffer verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.getObject().endVersionHtml(verSB);
			rtSB.append(verSB);
			
			if(status.equals("grph")) {
				if((cnt % 4) == 0) {
					rtSB.append(CSS.TB_TABLE_NONE);
				} else {
					rtSB.append(CSS.TB_TABLE_NONE_SUB);
				}
			} else {
				//2018이전 edit 표(테이블)에 CSS 추가 <table class='frame_1'> 
				//20190730 edit 표(테이블) 출력 기능 추가위해, <table id='' class='frame_1'> ID값 추가
				StringBuffer tblStyle = new StringBuffer(CSS.TB_TABLE);
				/////////////////////////////////////////////////////////////////////////////////////////
				//2021 12 27 Step Style 처리 추가
				//2022 01 18 LSAM일 경우 테이블 들여 쓰기 없게 수정 
				//2022 05 27 LSAM일 경우 테이블 기본 선 사용 않하게 수정
				//2023.07.10 jysi EDIT : LSAM분기점에 NLS추가	
				//2024.09.09 - MUAV 추가 - jingi.kim
				//2024.11.14 - SENSOR 추가 - chohee.cha
				boolean isCanon = false;
				if ("LSAM".equalsIgnoreCase(ext.getBizCode()) || "NLS".equalsIgnoreCase(ext.getBizCode()) || "KBOB".equalsIgnoreCase(ext.getBizCode()) || "MUAV".equalsIgnoreCase(ext.getBizCode()) || "SENSOR".equalsIgnoreCase(ext.getBizCode())) {
					isCanon = true;
				}
				if ( "KICC".equalsIgnoreCase(ext.getBizCode()) ) {
					isCanon = false;
				}
				if( isCanon == false ) {
					//by ejkim 2022.10.05 in_table의 경우 margin unset 처리 (표공백처리로 표밖으로 벗어남)
					//2022 11 15 Park.J.S. Update width: -webkit-fill-available;
					//tblStyle.insert(22, " step" + nDepth + " ");
					//2023 02 08 Park.J.S. Update width: -webkit-fill-available; 제거
					//String cssStr = " step" + nDepth + "' style='width: -webkit-fill-available;'";
					String cssStr = " step" + nDepth + "'";
					tblStyle.insert(22, cssStr);
					tblStyle.insert(14, "frame_" + psDto.getFrameClassNum() + " ");
				}else {
					//2022 11 15 LSAM일 경우 테이블 자체 들여 쓰기 ADD
					tblStyle.insert(22, " step" + nDepth);
					tblStyle.insert(14, "frame_0 ");
					log.info("LSAM일 경우 Table 기본 테두리선 사용 X : "+ext.getBizCode());
					//log.info("LSAM일 경우 테이블 들여 쓰기 X Table 기본 테두리선 사용 X : "+ext.getBizCode());
				}
				/////////////////////////////////////////////////////////////////////////////////////////
				tblStyle.insert(7, ATTR.ID + "='tbl_" + tblId + "' ");
				rtSB.append(tblStyle);
			}
			
			if (IConstants.TB_CAPTION_FOCUS.equals(IConstants.TB_CAPTION_TOP)) {
				rtSB.append(captionSB);
				rtSB.append(nodeSB);
			} else {
				rtSB.append(nodeSB);
				rtSB.append(captionSB);
			}
			
			rtSB.append(CSS.TB_TABLEEND);
			rtSB.append(verEndStr);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("TableParser.getTableHtml Exception:"+ex.toString());
		}
		return rtSB;
	}
	
	/**
	 * Table의 TR 정보 파싱하여 HTML로 리턴 메소드
	 * @MethodName			: getTrHtml
	 * @AuthorDate			: LIM Y.M. / 2014. 7. 4.
	 * @ModificationHistory	: Park.J.J. / 2022.03.03. :  boolean lineFlag 추가해서 반드시 테이블 선이 필요한 경우를 처리할수 있게 수정 
	 * @param psDto, paNode, nDepth
	 * @return
	 */
	public StringBuffer getTrHtml(ParserDto psDto, Node paNode, int nDepth, String tblId, boolean lineFlag) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.getNodeName().equals(DTD.TB_ROWH)) {
			return rtSB;
		}
		
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
			Node curNode		= null;
			String nodeName	= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				log.info("curNode.getNodeType() : "+curNode.getNodeType()+", Node.ELEMENT_NODE : "+Node.ELEMENT_NODE+", nodeName.equals(DTD.TB_ENTRY) : "+nodeName.equals(DTD.TB_ENTRY)+", lineFlag : "+lineFlag);
				if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.TB_ENTRY)) {
					nodeSB.append( this.getTdHtml(psDto, curNode, nDepth, tblId, lineFlag) );
				}
			}
			
			StringBuffer verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.getObject().endVersionHtml(verSB);
			
			rtSB.append(CSS.TB_TR);
			rtSB.append(verSB);
			rtSB.append(nodeSB);
			rtSB.append(verEndStr);
			rtSB.append(CSS.TB_TREND);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("TableParser.getTrHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * Table 의 TD 정보 파싱하여 HTML로 리턴 메소드
	 * @MethodName			: getTdHtml
	 * @AuthorDate			: LIM Y.M. / 2014. 7. 7.
	 * @ModificationHistory	: png 관련 수정 2021 06 jspark
	 * @ModificationHistory	: Park.J.J. / 2022.03.03. :  boolean lineFlag 추가해서 반드시 테이블 선이 필요한 경우를 처리할수 있게 수정
	 * @param psDto, paNode, nDepth
	 * @return
	 */
	public StringBuffer getTdHtml(ParserDto psDto, Node paNode, int nDepth, String tblId, boolean lineFlag) {
		Date date = new Date(); 
		log.info("Time Test : "+date.getTime());
		log.info(this.getClass().getName() + " : getTdHtml");
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.getNodeName().equals(DTD.TB_ENTRY)) {
			return rtSB;
		}
		//by ejkim 2022.10.09 height 값 조정 유무 체크
		boolean tableHtml_flag = false;
		//2022 11 16 jysi ADD : 텍스트 정렬 초기화 이동(TextParser.getTextPara => TableParser.getTdHtml)
		CodeConverter.CM_STYLE_ALIGN = "";
		
		boolean isHasIconTag = false;
		if( "KTA".equalsIgnoreCase(ext.getBizCode()) ) {	isHasIconTag = true;	}
		
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
			NamedNodeMap paAttr = paNode.getAttributes();
			Node curNode		= null;
			String nodeName	= "";
			StringBuffer nodeSB	= new StringBuffer();
		
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++) {
				//tableHtml 중복 입력을 막기위해서 초기화 되게 생성자 위치 변경
				String tableHtml	= "";
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.ALERT)) {
						nodeSB.append( alertParser.getObject().getAlertHtml(psDto, curNode) );
						
					} else if (nodeName.equals(DTD.GRPH)) {
						nodeSB.append( grphParser.getObject().getGrphHtml(psDto, curNode, true) );
						
					} else if (nodeName.equals(DTD.TABLE)) {
						nodeSB.append( this.getTableHtml(psDto, curNode, nDepth+1, "") );
						
					} else if (nodeName.equals(DTD.TEXT)) {
						tableHtml = textParser.getTextPara(psDto, curNode).toString();
						
						// 2023.05.22 - KTA, 아이콘 테그를 포함한 경우 image tag로 치환 - jingi.kim
						if ( isHasIconTag ) {
							tableHtml = iconParser.getTemplateIconTag(psDto, tableHtml);
						}
						
						String caption		= StringUtil.checkNull(XmlDomParser.getAttributes(paAttr, ATTR.TB_CAPTION));
						if (!caption.equals("") && caption.equals(IConstants.TB_ENTRY_CAPTION_YES)) {
							tableHtml += "<div id='img_controll'><a title='표 출력' class='print' onclick=\"printAreaTable('tbl_"+tblId+"')\" href=\"javascript:void(0)\">표 출력</a></div>"; //joe 2
						}
					}
					
					if (!tableHtml.equals("")) {
						//2024.05.13 - NLS, 아이콘이 포함된 경우, 아이콘 명에 검색어가 포함된 경우, 이미지 테그 자체가 변환되는 문제 보완 - jingi.kim
						if ( "NLS".equalsIgnoreCase(ext.getBizCode()) && nodeName.equals(DTD.TEXT) && !psDto.getSearchWord().equals("") ) {
							nodeSB.append(tableHtml);
							tableHtml_flag = true;
						} else {
							tableHtml = CodeConverter.getCodeConverter(psDto, tableHtml, "", "");
							nodeSB.append(tableHtml);
							tableHtml_flag = true;
						}
					}
				}
			}
			
			StringBuffer verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.getObject().endVersionHtml(verSB);
			String entryStryleStr = this.getEntryAttributes(paAttr, lineFlag).toString();
			
			//by ejkim 2022.10.09 테이블 내 값이 없을 경우 높이 고정
			if( tableHtml_flag == false) {
				entryStryleStr = "height='20px' "+ entryStryleStr;
			}
			
			log.info("entryStryleStr : "+entryStryleStr);
			rtSB.append( CSS.getTableTd(entryStryleStr) );
			rtSB.append(verSB);
			rtSB.append(nodeSB);
			rtSB.append(verEndStr);
			rtSB.append(CSS.TB_TDEND);
			
			CodeConverter.CM_STYLE_ALIGN = "";
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("TableParser.getTdHtml Exception:"+ex.toString());
		}
		log.info("Time Test : "+(new Date().getTime()-date.getTime()));
		return rtSB;
	}
	
	
	/**
	 * 테이블의 <TD> Attributes 속성 값에 의한 스타일 추출
	 * @MethodName	: getEntryAttributes
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 7.
	 * @ModificationHistory	:  LIM Y.M. / 2017. 1. 11.
	 * @ModificationHistory	: Park.J.S. / 2022.03.03. :  boolean lineFlag 추가해서 반드시 테이블 선이 필요한 경우를 처리할수 있게 수정 
	 * @param paNode, nDepth
	 * @return
	 */
	public StringBuffer getEntryAttributes(NamedNodeMap curAttr ,boolean lineFlag) {
		log.info("TableParser getEntryAttributes : "+curAttr+", lineFlag : "+lineFlag);
		StringBuffer rtSB = new StringBuffer();
		
		try {
			if (curAttr != null) {
				String colspan		= StringUtil.checkNull(XmlDomParser.getAttributes(curAttr, ATTR.TB_COLSPAN), "1");
				String rowspan		= StringUtil.checkNull(XmlDomParser.getAttributes(curAttr, ATTR.TB_ROWSPAN), "1");
				String align		= StringUtil.checkNull(XmlDomParser.getAttributes(curAttr, ATTR.TB_ALIGN));
				String valign		= XmlDomParser.getAttributes(curAttr, ATTR.TB_VALIGN);
				String width		= XmlDomParser.getAttributes(curAttr, ATTR.TB_WIDTH);
				String bold			= XmlDomParser.getAttributes(curAttr, ATTR.TB_BOLD);
				String caption		= StringUtil.checkNull(XmlDomParser.getAttributes(curAttr, ATTR.TB_CAPTION));
				log.info("getEntryAttributes width : "+width+", align : "+align);
				if (align.equals("")) {
					align = CodeConverter.CM_STYLE_ALIGN;
				}
				if (valign.equals(VAL.VALIGN_0)) {
					valign = CSS.STY_VAL_TOP;
				} else if (valign.equals(VAL.VALIGN_1)) {
					valign = CSS.STY_VAL_BOTTOM;
				}
				
				if (!width.equals("")) {
					double dWidth = Double.parseDouble(width);
					if (dWidth > 0) {
						//2022 05 26 Park.J.S ADD : 100%를 넘을 경우 절못된 값으로 판단해서 사용하지 않음 추가 주로 테이블 안에 테이블이 들어가는 상황이 첫번째 줄부터 발생할경우 문제됨
						if((dWidth / TABLE_WIDTH * 100.) <= 100) {
							width = String.format("%.2f", (dWidth / TABLE_WIDTH * 100.)) + CHAR.WIDTH_PER;
							log.info("getEntryAttributes TABLE_WIDTH : "+TABLE_WIDTH);
						}else {
							log.info("getEntryAttributes TABLE_WIDTH Over 100%");
							width = "";
						}
						//20190312 add joe 임시로 넣음
						if (TABLE_WIDTH == 29418) {
							width = String.format("%.2f", (dWidth / 47834. * 100.)) + CHAR.WIDTH_PER;
							log.info("getEntryAttributes Joe width : "+width);
						}
					}
				}
				
				rtSB.append(" colspan='");
				rtSB.append(colspan);
				rtSB.append("' rowspan='");
				rtSB.append(rowspan);
				rtSB.append("' align='");
				rtSB.append(align);
				rtSB.append("' valign='");
				rtSB.append(valign);
				rtSB.append("' width='");
				rtSB.append(width);
				rtSB.append("'");
				log.info("getEntryAttributes width : "+rtSB.toString());
				String styleText = "";
				if (!align.equals("")) {
					styleText += "text-align:" + align + ";";
				}
				if (!valign.equals("")) {
					styleText += "vertical-align:" + valign + ";";
				}
				if (bold.equals(VAL.BOLD_1)) {
					styleText += "text-weight:bold;";
				}
				if (!caption.equals("") && caption.equals(IConstants.TB_ENTRY_CAPTION_YES)) {
					log.info("Check Type 1");
					if (IConstants.TB_CAPTION_FOCUS.equals(IConstants.TB_CAPTION_TOP)) {
						rtSB.append(" class='caption cap_top'");
					} else {
						rtSB.append(" class='caption cap_bottom'");
					}
				} else {
					log.info("Check Type 2");
					String border = "border:1px solid #000;";//2017.07.11 수정(변경전 1px)
					String cellLeft		= this.getCellBorder( "border-left-style",		XmlDomParser.getAttributes(curAttr, "cell_left_type")	, lineFlag);
					String cellRight	= this.getCellBorder( "border-right-style",		XmlDomParser.getAttributes(curAttr, "cell_right_type")	, lineFlag);
					String cellTop		= this.getCellBorder( "border-top-style",		XmlDomParser.getAttributes(curAttr, "cell_top_type")	, lineFlag);
					String cellBottom	= this.getCellBorder( "border-bottom-style",	XmlDomParser.getAttributes(curAttr, "cell_bottom_type")	, lineFlag);
					log.info("cellLeft   : "+cellLeft);
					log.info("cellRight  : "+cellRight);
					log.info("cellTop    : "+cellTop);
					log.info("cellBottom : "+cellBottom);
					styleText += border;
					styleText += cellLeft;
					styleText += cellRight;
					styleText += cellTop;
					styleText += cellBottom;
				}
				
				rtSB.append(" style='").append(styleText).append("'");
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("TableParser.getEntryAttributes Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * <pre>
	 * 2023 01 11 Park.J.S
	 * 느린 코드 고도화 진행
	 * 테이블 가로 사이즈 계산을 위해 colgroup을 만드는 함수
	 * 화면 사이즈에 자동 대응하기 위해서 기본적으로 비율을 사용함
	 * 비율 처리가 불가능한 경우 전제 길이만 셋팅 하도록 되어 있음
	 * 추후 TR TD 만드는 로직에 녹여서 2중으로 작업하는 for문 1개로 통일 하면 조금더 단축 가능(사실상 몇 초 차이 안나서 의미 없음) 
	 * log에 TODO Csae 찍힐경우 코드 추가 필요 
	 * </pre> 
	 * @param paNode
	 * @return
	 */
	public StringBuffer getColWidth_New(Node paNode) {
		Date date = new Date(); 
		log.info("Time Test : "+date.getTime());
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null) {
			return rtSB;
		}
		int 	tableColcnt	= 0;
		double	tableWidth	= 0;
		try {
			//////////////////////////////////////////////////////////////////////////////////////
			//테이블 전체 TD의 갯수를 table Node에서 꺼낸다.	ATTR.TB_COLCNT
			//테이블 전체 Width를 table Node에서 꺼낸다.	ATTR.TB_WIDTH  ==> 현재 미사용
			//////////////////////////////////////////////////////////////////////////////////////
			if (paNode != null && paNode.getAttributes() != null) {
				String colcnt	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TB_COLCNT);
				String withStr	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TB_WIDTH);
				if (!StringUtil.checkNull(colcnt).equals("")) {
					tableColcnt = Integer.parseInt(colcnt);
				}
				if (!StringUtil.checkNull(withStr).equals("")) {
					tableWidth = Integer.parseInt(withStr);
				}
			}
			log.info("tableColcnt : "+tableColcnt+", tableWidth : "+tableWidth);
			//Csae 1 table Node에 colcnt가 정상 적으로 존재 할경우
			if(tableColcnt > 0){
				log.info("Csae 1 table Node에 colcnt가 정상 적으로 존재 할경우");
				double[] arrWidth	= new double[tableColcnt];	//각 컬럼 별 사이즈가 들어갈 배열
				double emputyNum	= 0;						//arrWidth안에 설정 안된 값이 존재하는 숫자
				double rowWidth		= 0;						//tr 안 TD 가로 길이 합(TR 단위로 갱신됨)
				double setWidth		= 0;						//배열안에 셋팅된 값의 총함(추후 배열이 전체 셋팅 되지 않았을 경우 공백 배열 채우는데 사용)
				
				for(int i=0;i<paNode.getChildNodes().getLength();i++) {
					Node rowhddefNode = paNode.getChildNodes().item(i);
					if(!"rowhddef".equalsIgnoreCase(rowhddefNode.getNodeName())) {
						continue;
					}
					
					NodeList curList = rowhddefNode.getChildNodes();;
					int colcnt = curList.getLength();
					emputyNum				= 0;	
					Node curNode			= null;
					NamedNodeMap curAttr	= null;
					int tdIdx 				= 0;	//TD 위치
					rowWidth				= 0;
					if (colcnt > 0) {
						
						//테이블 전체 가로폭 계산 및 colspan 이용해서 1일 경우 해당 값 배열에 넣기
						for (int j=0; j<curList.getLength(); j++){
							curNode = curList.item(j);
							if(!"entry".equalsIgnoreCase(curNode.getNodeName())) {
								continue;
							}
							
							curAttr				= curNode.getAttributes();
							String width		= StringUtil.checkNull( XmlDomParser.getAttributes(curAttr, ATTR.TB_WIDTH), "0");
							String colspanStr	= StringUtil.checkNull( XmlDomParser.getAttributes(curAttr, "colspan"), "1");
							log.info("getColWidth ADD colspanStr : "+colspanStr+", width : "+width);
							
							if(!"0".equals(width)) {
								log.info("Csae 1-1 entry Node에 width가 정상 적으로 존재 할경우");
								if("1".equals(colspanStr)) {//1개 짜리 만 실제 width로 판단 함
									//2023 02 03 Park.J.S. Update : 한번 셋팅되면 중복해서 넣지 않음
									if(arrWidth[tdIdx] == 0){
										log.info("Set arrWidth["+tdIdx+"] : "+Double.parseDouble(width));
										arrWidth[tdIdx] = Double.parseDouble(width);
										setWidth		= setWidth+Double.parseDouble(width);
									}
								}else if((tableColcnt+"").equals(colspanStr)) {//Table 하위에 합쳐진 TR 한개만 존재 할수 있어서 전체 합처진 경우
									log.info("colspan이 전체인 경우");
								}
								rowWidth = rowWidth+Double.parseDouble(width);
								tdIdx = tdIdx + Integer.valueOf(colspanStr);//colspan경우 처리 위해 배열상 값 셋팅 위치를 해당 위치로 이동
							}else {
								log.error("TODO Csae 1-2 entry Node에 width가 비정상 적인 경우");
							}
						}
						//tr 한번 처리 할때마다 전체 셋팅이 끝났는지 확인 끝났을 경우 break;
						for (int j=0; j<arrWidth.length; j++){
							if(arrWidth[j] == 0 ) {
								emputyNum++;
							}
						}
						
						log.info("emputyNum : "+emputyNum);
						if(emputyNum == 0) {
							break;
						}
					}
				}
				TABLE_WIDTH = rowWidth;//전체 길이 값 셋팅
				boolean tempCheckErrorSetFlag = false;
				log.info("TABLE_WIDTH : "+TABLE_WIDTH+", setWidth : "+setWidth+", emputyNum : "+emputyNum);
				if(emputyNum > 0) {//배열에 전체 길이가 셋팅되지 못했을 경우
					//전체 길이에서 배열에 셋팅되지 못한 길이를 구해서 셋팅값안에 분할해서 등록
					double tempNum = (TABLE_WIDTH-setWidth)/emputyNum;
					if(tempNum > 0) {
						for (int i=0; i<arrWidth.length; i++){
							if(arrWidth[i] == 0) {
								arrWidth[i] = tempNum;
							}
							log.info("Table Cols Check Num : "+arrWidth[i]);
						}
					}else {
						log.info("배열값 처리중에 비정상적인 계산 발생 함 TABLE_WIDTH : "+TABLE_WIDTH+", setWidth : "+setWidth+", emputyNum : "+emputyNum);
						tempCheckErrorSetFlag = true;
					}
				}else {
					//2023 02 03 Park.J.S. ADD : 전체 길이 값보다 배열에 셋팅된 길이합이 다를경우 셋팅된 길이를 정상 길이로 판단하는 로직 추가
					//관련해서 첫번째 row의 합을 TABLE_WIDTH 값으로 사용하는 방법도 고려해야함
					if(TABLE_WIDTH != setWidth) {
						log.info("배열값 과 테이블 with 값이 다르다고 판단됨 중간에 잘못된 값이 존재 할수 있음 이경우 셋팅된 값의 합을 전체 길이 판단하는데 사용함");
						TABLE_WIDTH = setWidth;
					}
				}
				if(tempCheckErrorSetFlag) {
					log.info("Can Not Set Table Width...");
				}else {
					for (int i=0; i<arrWidth.length; i++){
						if (arrWidth[i] > 0 && TABLE_WIDTH > 0) {
							String perWidth = String.format("%.2f", (arrWidth[i] / TABLE_WIDTH * 100.));
							log.info("TableParser getColWidth colcnt == tableColcnt && totWidth > 0");
							log.info("arrWidth[i] : "+arrWidth[i]+", TABLE_WIDTH : "+TABLE_WIDTH+", perWidth : "+perWidth);
							rtSB.append( CSS.getTableColWidth(perWidth+"%"));
						}
					}
					rtSB.insert(0, CSS.TB_COLGROUP);
					rtSB.append(CSS.TB_COLEND);
				}
			}else {
				log.error("TODO Csae 2 table Node에 colcnt가 정상적이지 않을 경우 로직 추가 필요");
			}
		} catch (Exception ex) {
			log.error("TableParser.getColWidth Exception:"+ex.toString(),ex);
			if(tableWidth != 0) {//문제 발생시에 Table 자체 길이 설정된 경우 해당 길이 사용
				TABLE_WIDTH = tableWidth;//전체 길이 값 셋팅
			}
		}
		log.info("getColWidth rtSB : "+rtSB.toString());
		log.info("Time Test : "+(new Date().getTime()-date.getTime()));
		return rtSB;
	}
	
	/**
	 * 테이블의 전체 가로폭과 <colgroup> 생성
	 * @MethodName	: getColWidth
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 14.
	 * @ModificationHistory	: 
	 * @param curAttr
	 * @return
	 */
	public StringBuffer getColWidth(Node paNode) {
		Date date = new Date(); 
		log.info("Time Test : "+date.getTime());
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null) {
			return rtSB;
		}
		
		try {
			//<table>의 colcnt 값과 첫번째 <rowhddef>의 <td>의 갯수가 같으면 <colgroup>을 생성하고 없으면 전체 width값만 계산하여 저장
			int tableColcnt = 0;
			if (paNode != null && paNode.getAttributes() != null) {
				String colcnt = XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TB_COLCNT);
				if (!StringUtil.checkNull(colcnt).equals("")) {
					tableColcnt = Integer.parseInt(colcnt);
				}
			}
			//<table>의 첫번째 <rowhddef>의 <td> 리스트 추출
			//2022 09 19 Park.J.S. 다를 경우 같은 경우까지 FOR 문 처리로 변경  하고 끝까지 다를 경우 동일 비율로 
			double[] arrWidth		= new double[tableColcnt];	//각 컬럼 별 사이즈가 들어갈 배열
			double totWidth			= 0.0;						//전체 테이블 사이즈
			boolean widthSetFlag	= false;					//가로 길이 설정이 정상적으로 되었는지 확인하는 플레그
			boolean headerSet 		= false;					//중간에 길이 틀어지는 현상 발생 뭐든지 최초 값 사용을 위해 추가
			for(int j=0;j<paNode.getChildNodes().getLength();j++) {
				totWidth    = 0.0;
				Node rowhddefNode = paNode.getChildNodes().item(j);
				/*
				String xalanSql = XALAN.TBXALAN_COLGROUP;
				NodeList curList = XmlDomParser.getNodeListFromXPathAPI(paNode, xalanSql);
				*/
				if(!"rowhddef".equalsIgnoreCase(rowhddefNode.getNodeName())) {
					continue;
				}
				//NodeList curList = XmlDomParser.getNodeListFromXPathAPI(rowhddefNode, ".entry");
				NodeList curList = rowhddefNode.getChildNodes();;
				//log.info("Table Node : "+ExpisCommonUtile.getInstance().nodeToString(rowhddefNode));
				int colcnt = curList.getLength();
				if (colcnt > 0) {
					
					Node curNode			= null;
					NamedNodeMap curAttr	= null;
					int nowColNum = 0;
					//테이블 전체 가로폭 계산 및 colspan 이용해서 1일 경우 해당 값 배열에 넣기
					for (int i=0; i<curList.getLength(); i++)
					{
						curNode = curList.item(i);
						if(!"entry".equalsIgnoreCase(curNode.getNodeName())) {
							continue;
						}
						curAttr = curNode.getAttributes();
						String width	= StringUtil.checkNull( XmlDomParser.getAttributes(curAttr, ATTR.TB_WIDTH), "0");
						String colspanStr	= StringUtil.checkNull( XmlDomParser.getAttributes(curAttr, "colspan"), "1");
						log.info("getColWidth ADD colspanStr : "+colspanStr+", width : "+width);
						//2022 10 21 jysi edit : 기존의 nowColNum은 colspan의 합이 tableColcnt와 맞지 않는 경우가 있어 해당 entry의 column속성을 그대로 가져오도록 변경
						//                       column속성이 없을 경우 기존의 nowColNum을 쓰도록 함.
						int xmlColNum	= Integer.valueOf(StringUtil.checkNull( XmlDomParser.getAttributes(curAttr, "column"), "0"));
						if("1".equals(colspanStr)) {
							//log.info("Set entry["+nowColNum+"] : "+ExpisCommonUtile.getInstance().nodeToString(curNode));
							int colNum = 0;
							if(xmlColNum == 0) {colNum = nowColNum;}
							else {colNum = xmlColNum;}
							/*
							if(arrWidth[nowColNum] < 1) {
								arrWidth[nowColNum] = Double.parseDouble(width);
							}else {
								log.info("already set : "+arrWidth[nowColNum]);
							}
							*/
							//if(arrWidth[colNum] < 1 || arrWidth[colNum] > Double.parseDouble(width)) {
							if(arrWidth[colNum] < 1) {
								arrWidth[colNum] = Double.parseDouble(width);
							}else {
								log.info("already set : "+arrWidth[colNum]);
							}
						}
						nowColNum += Integer.valueOf(colspanStr);
						totWidth += Double.parseDouble(width);
					}
					if(!headerSet && totWidth != 0.0){
						TABLE_WIDTH = totWidth;
						headerSet = true;
					}
					log.info("getColWidth TABLE_WIDTH : "+TABLE_WIDTH+", colcnt : "+colcnt+", tableColcnt : "+tableColcnt);
					/*
					if (colcnt == tableColcnt && totWidth > 0) {
						
						for (int i=0; i<arrWidth.length; i++)
						{
							if (arrWidth[i] > 0 && totWidth > 0) {
								String perWidth = String.format("%.2f", (arrWidth[i] / totWidth * 100.));
								//2021 08 park js 같을경우 비율로 처리
								log.info("TableParser getColWidth colcnt == tableColcnt && totWidth > 0");
								log.info("arrWidth[i] : "+arrWidth[i]+", totWidth : "+totWidth+", perWidth : "+perWidth);
								rtSB.append( CSS.getTableColWidth(perWidth+"%")); // 2021 08 % 추가함
								//rtSB.append( CSS.getTableColWidth(perWidth));
							}
						}
						
						rtSB.insert(0, CSS.TB_COLGROUP);
						rtSB.append(CSS.TB_COLEND);
						widthSetFlag = true;
						break;
					}
					*/
				}
			}
			if(!widthSetFlag) {
				log.info("테이블 가로 길이 자체 계산 Start");
				double emputyNum = 0;
				double setWidth = 0;
				for (int i=0; i<arrWidth.length; i++){
					log.info("Table Cols Check Num : "+arrWidth[i]);
					if(arrWidth[i] == 0) {
						emputyNum++;
					}else {
						setWidth += arrWidth[i];
					}
				}
				log.info("----------------------------------------------------------------------------------");
				double tempNum = (TABLE_WIDTH-setWidth)/emputyNum;
				boolean tempCheckErrorSetFlag = false;
				for (int i=0; i<arrWidth.length; i++){
					if(arrWidth[i] == 0) {
						arrWidth[i] = tempNum;
						if(tempNum < 0) {
							tempCheckErrorSetFlag = true;
						}
					}
					log.info("Table Cols Check Num : "+arrWidth[i]);
				}
				//2022 09 20 설정 자체가 불가능 한 경우 발생
				if(tempCheckErrorSetFlag) {
					log.info("Can Not Set Table Width...");
				}else {
					for (int i=0; i<arrWidth.length; i++){
						if (arrWidth[i] > 0 && TABLE_WIDTH > 0) {
							String perWidth = String.format("%.2f", (arrWidth[i] / TABLE_WIDTH * 100.));
							log.info("TableParser getColWidth colcnt == tableColcnt && totWidth > 0");
							log.info("arrWidth[i] : "+arrWidth[i]+", TABLE_WIDTH : "+TABLE_WIDTH+", perWidth : "+perWidth);
							rtSB.append( CSS.getTableColWidth(perWidth+"%")); // 2021 08 % 추가함
						}
					}
					
					rtSB.insert(0, CSS.TB_COLGROUP);
					rtSB.append(CSS.TB_COLEND);
					log.info("emputyNum : "+emputyNum+", setWidth : "+setWidth);
					log.info("테이블 가로 길이 자체 계산 Fin");
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("TableParser.getColWidth Exception:"+ex.toString());
		}
		log.info("getColWidth rtSB : "+rtSB.toString());
		log.info("Time Test : "+(new Date().getTime()-date.getTime()));
		return rtSB;
	}
	
	
	/**
	 * 테이블 노드에서 표의 제목 추출
	 * @MethodName	: getTableCaption
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 2.
	 * @ModificationHistory	: 
	 * @param paNode, tbId
	 * @return
	 */
	public StringBuffer getTableCaption(Node paNode, String tbId) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || tbId == null || tbId.equals("")) {
			return rtSB;
		}
		
		try {
			String captionStyle = "";
			String captionHtml = "";
			
			NamedNodeMap paAttr = paNode.getAttributes();
			if (paAttr != null) {
				String caption		= StringUtil.checkNull(XmlDomParser.getAttributes(paAttr, ATTR.TB_CAPTION));
				String colspan		= StringUtil.checkNull(XmlDomParser.getAttributes(paAttr, ATTR.TB_COLSPAN), "1");
				
				if (!caption.equals("") && caption.equals(IConstants.TB_ENTRY_CAPTION_YES)) {
					if (!IConstants.TB_CAPTION_FOCUS.equals(IConstants.TB_CAPTION_TOP)) {
						captionStyle = " class='caption cap_top' ";
					} else {
						captionStyle = " class='caption cap_bottom' ";
					}
					
					captionStyle += " style=\" colspan='"+colspan+"'\" ";
					captionHtml = "<div id='img_controll'><a title='표 출력' class='print' onclick=\"printAreaTable('"+tbId+"')\" href=\"javascript:void(0)\">표 출력</a></div>";
					
//					Node tbNode = XmlDomParser.getNodeFromXPathAPI(paNode, "./ancestor::table");
//					if (tbNode != null) {
//						String tbId = StringUtil.checkNull(XmlDomParser.getAttributes(tbNode.getAttributes(), ATTR.ID));
//						captionHtml = "<div id='img_controll'><a title='표 출력' class='print' onclick=\"printAreaTable('"+tbId+"')\" href=\"javascript:void(0)\">표 출력</a></div>";
//					}
				}
			}
			
			rtSB.append(CSS.TB_TR);
			rtSB.append( CSS.getTableTd(captionStyle) );
			rtSB.append(captionHtml);
			rtSB.append(CSS.TB_TDEND);
			rtSB.append(CSS.TB_TREND);
			
			
//			if (!captionStr.equals("")) {
//				captionStr = CodeConverter.deleteAllCode(captionStr);
//				captionStr = CodeConverter.convertEntityToTag(captionStr);
//				
//				//joe 새창띄우기 모듈 추가
//				//linkStr = "";
//				//joe 링크 기능 모듈 추가
//				
//				//nodeSB.append(linkStr);
//				//nodeSB.append( CodeConverter.getCodeConverter(psDto, captionStr, "", "") );
//			}
			
			log.info("===> captionStr="+rtSB.toString());
			
//			rtSB.append(CSS.P);
//			//rtSB.append(nodeSB);
//			rtSB.append(CSS.P_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("TableParser.getTableCaption Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	/*
	public String getTableCaption(ParserDto psDto, Node tableNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (tableNode == null) {
			return rtSB.toString();
		}
		
		try {
			String linkStr = "";
			String captionStr = StringUtil.checkNull(XmlDomParser.getTxt(tableNode));
			StringBuffer nodeSB = new StringBuffer();
			
			if (!captionStr.equals("")) {
				captionStr = CodeConverter.deleteAllCode(captionStr);
				captionStr = CodeConverter.convertEntityToTag(captionStr);
				
				//joe 새창띄우기 모듈 추가
				linkStr = "";
				//joe 링크 기능 모듈 추가
				
				nodeSB.append(linkStr);
				nodeSB.append( CodeConverter.getCodeConverter(psDto, captionStr, "", "") );
			}
			
			log.info("===> captionStr="+captionStr);
			
			rtSB.append(CSS.P);
			rtSB.append(nodeSB);
			rtSB.append(CSS.P_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("TableParser.getTableCaption Exception:"+ex.toString());
		}
		
		return rtSB.toString();
	}
	*/

	
	/**
	 * 표(테이블) 내 셀의 속성 중 Border 값 치환
	 * @MethodName			: getCellBorder
	 * @AuthorDate			: LIM Y.M. / 2017. 2. 1.
	 * @ModificationHistory	: Park.J.J. / 2022.03.03. :  boolean lineFlag 추가해서 반드시 테이블 선이 필요한 경우를 처리할수 있게 수정
	 * @param borderName, borderType
	 * @return
	 */
	public String getCellBorder(String borderName, String borderType, boolean lineFlag) {
		log.info("getCellBorder borderName : "+borderName+", borderType : "+borderType+", lineFlag : "+lineFlag);
		String rtStr = "";
		
		if (borderType == null || borderType.equals("")) {
			return rtStr;
		}
		try {
			String borderValue = "";
			switch (StringUtil.getInt(borderType)) {
				case 0 :
					borderValue = "none";
					break;
				case 1:
					borderValue = "solid";
					break;
				case 2:
					borderValue = "dash";
					break;
				case 3:
					borderValue = "dot";
					break;
				case 4:
					borderValue = "dashdot";
					break;
				case 5:
					borderValue = "dash";
					break;
				default:
					borderValue = "solid";
					break;
			}
			
			if(lineFlag) {
				borderValue = "solid";
			}
			
			if (!borderValue.equals("")) {
				rtStr = borderName + ":" + borderValue + ";";
			}
				
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("TableParser.getCellBorder Exception:"+ex.toString());
		}
		
		return rtStr;
	}
	
	/**
	 * Table 파싱하여 HTML로 리턴 메소드
	 * @MethodName			: getTableHtmlXPath
	 * @AuthorDate			: jingi.kim  / 2023. 03. 10. 체크 로직 제거
	 * @param psDto, paNode, nDepth, status
	 * @return
	 */
	public StringBuffer getTableHtmlXPath(ParserDto psDto, Node paNode, int nDepth, String status) {
		
		log.info("getTableHtmlXPath : "+psDto.toString());
		StringBuffer rtSB = new StringBuffer();
		if (!paNode.getNodeName().equals(DTD.TABLE)) {
			return rtSB;
		}
		
		boolean lineFlag = false;
		try {
			log.info("Table Parent Node Name : "+paNode.getParentNode().getNodeName());
			if(paNode.getParentNode().getNodeName().equals(DTD.IN_REQCOND)) {
				lineFlag = true;
			}
			log.info("lineFlag : "+lineFlag);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
			// StringBuffer nodeSB		= new StringBuffer();
			// StringBuffer captionSB	= new StringBuffer();
			
			//Table의 id 추출
			String tblId = XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.ID);
			String tblType = XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TYPE);
			
			StringBuffer tblStyle = new StringBuffer(CSS.TB_TABLE);
			String cssStr = " step" + nDepth + "'";
			tblStyle.insert(22, cssStr);
			tblStyle.insert(14, "frame_" + psDto.getFrameClassNum() + " ");
			tblStyle.insert(7, ATTR.ID + "='tbl_" + tblId + "' ");
			rtSB.append(tblStyle);
			
			//Table의 column width 계산
			//nodeSB.append( this.getColWidth(paNode) );
			//2023 01 11 속도 개선 
			rtSB.append(this.getColWidth_New(paNode));
			
			NodeList rowList = XmlDomParser.getNodeListFromXPathAPI(paNode, "//" + DTD.TB_ROWH);		// <rowhddef 전체
			log.info("rowList . length : " + rowList.getLength());
			if ( rowList.getLength() <= 0 ) {
				return rtSB;
			}
			
			//2023.05.08 T50일 경우, ~00WD-00-3 교범에서 항공기유형 (7열)의 값에 따라 표시제한 기능 추가 - jingi.kim
			boolean isTDFilter = false;
			boolean isShowTR = true;
			String fndTDVehicle = "";
			int idxChkTD = -1;
			
			//2023.07.17 ~00WD-00-3 교범에서 type=WDC 일 경우 항공기유형 (7열) , type=WDW 일 경우 항공기유형 (13열) 의 값에 따라 표시제한 기능 동작하도록 - jingi.kim
			if ( "BLOCK2".equalsIgnoreCase(ext.getBizCode()) && psDto.getBizIetmdata().contains("ExpisWebT50/") ) {
				if ( "WDC".equalsIgnoreCase(tblType) ) {
					isTDFilter = true;
					idxChkTD = 6;
				} else if ( "WDW".equalsIgnoreCase(tblType) ) {
					isTDFilter = true;
					idxChkTD = 12;
				}
			}
			
			int rowLength = rowList.getLength();
			for ( int tr=0; tr<rowLength; tr++ ) {
				Node trNode = rowList.item(tr);
				
				isShowTR = true;
				fndTDVehicle = "";
				
				StringBuffer trSB		= new StringBuffer();
				
				if ( trNode.getNodeType() == Node.ELEMENT_NODE ) {
					
					// String statusCd		= XmlDomParser.getAttributes(trNode.getAttributes(), ATTR.SYS_TOCO_STATUSCD);
					
					StringBuffer trVerSB = verParser.getObject().checkVersionHtml(psDto, trNode);
					String trVerEndStr = verParser.getObject().endVersionHtml(trVerSB);
					
					trSB.append(CSS.TB_TR);
					trSB.append(trVerSB);
					
					NodeList entryList = trNode.getChildNodes();
					if ( entryList.getLength() <= 0 ) continue;
					
					int tdLength = entryList.getLength();
					for ( int td=0; td<tdLength; td++ ) {
						Node tdNode = entryList.item(td);
						
						if (tdNode.getNodeType() == Node.ELEMENT_NODE) {
							// StringBuffer tdVerSB = verParser.checkVersionHtml(psDto, tdNode);
							// String tdVerEndStr = verParser.endVersionHtml(tdVerSB);
							
							if ( tr == 0 || tr == (rowLength-1) ) {
								String entryStryleStr = this.getEntryAttributes(tdNode.getAttributes(), lineFlag).toString();
								// log.info("entryStryleStr : "+entryStryleStr);
								trSB.append( CSS.getTableTd(entryStryleStr) );
							} else {
								trSB.append(CSS.TB_TD);
								
								if ( isTDFilter == true ) {
									String colnum = XmlDomParser.getAttributes(tdNode.getAttributes(), ATTR.TB_COLUMN);;
									
									if ( Integer.parseInt(colnum) == idxChkTD ) {
										fndTDVehicle = getTDVehicleType(tdNode);
										// log.info("fndTDVehicle : "+fndTDVehicle +" // psDto.getVehicleType() : "+ psDto.getVehicleType() +" // fndTDVehicle.contains(psDto.getVehicleType()) : "+ fndTDVehicle.contains(psDto.getVehicleType()) );
										if ( fndTDVehicle.contains(psDto.getVehicleType()) == false ) {
											isShowTR = false;
										}
									}
								}
							}
							
							// trSB.append(tdVerSB);
							StringBuffer tdText = textParser.getTextPara(psDto, tdNode);
							trSB.append( CodeConverter.getCodeConverter(psDto, tdText.toString(), "", "") );
							// trSB.append(tdVerEndStr);
							trSB.append(CSS.TB_TDEND);
							
						}
					}
					
					trSB.append(trVerEndStr);
					trSB.append(CSS.TB_TREND);
				}
				
				if ( isTDFilter == true ) {
					if ( isShowTR == true ) {
						rtSB.append(trSB);
					}
				} else {
					rtSB.append(trSB);
				}
			}
			
			rtSB.append(CSS.TB_TABLEEND);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("TableParser.getTableHtmlXPath Exception:"+ex.toString());
		}
		return rtSB;
	}
	
	
	/**
	 * Table 파싱하여 HTML로 리턴 메소드
	 * @MethodName			: getTDVehicleType
	 * @AuthorDate			: jingi.kim  / 2023. 05. 08. 항공기 문구에 따라 비행타입 리턴
	 * 							T50일 경우, 1T-50A-2-00WD-00-3 에서 연결목록의 테이블에서 사용
	 * @param psDto, paNode, nDepth, status
	 * @return
	 */
	public String getTDVehicleType(Node tdNode) {
		
		String rtnType = "";
		String tdText = XmlDomParser.getTxt(tdNode);
		
		tdText = tdText.replaceAll("&#254;", "");	// 테그 시작
		tdText = tdText.replaceAll("&#136;", "");	// 첨자
		tdText = tdText.replaceAll("&#138;", "");	// align
		tdText = tdText.replaceAll("&#169;", "");	// font
		tdText = tdText.replaceAll("&#188;", "");	// font
		tdText = tdText.replaceAll("&#199;", "");	// font
		tdText = tdText.replaceAll("&#0;", "");
		tdText = tdText.replaceAll("&#255;", "");	// 테그 종료
		tdText = tdText.trim();
		
		if ( "".equalsIgnoreCase(tdText) ) {
			rtnType = "A:B:C:D:";
			return rtnType;
		}
		if ( tdText.contains("(삭제됨)") ) {
			rtnType = "A:B:C:D:";
			return rtnType;
		}
		
		if ( tdText.contains("KA") ) {
			rtnType += "A:";
		}
		if ( tdText.contains("KB") ) {
			rtnType += "C:";
		}
		if ( tdText.contains("KD") ) {
			rtnType += "B:D:";
		}
		
		//2023.07.18 - KA, KB, KD 외 다른 값이 지정된 경우 전부 표시하도록 보완 - jingi.kim
		// KC가 포함된 경우 확인됨 === 다른 값이 포함된 경우 전부 표시되도록
		tdText = tdText.replaceAll(",", "");
		tdText = tdText.replaceAll("KA", "");
		tdText = tdText.replaceAll("KB", "");
		tdText = tdText.replaceAll("KD", "");
		if ( !"".equalsIgnoreCase(tdText) ) {
			rtnType += "A:B:C:D:";
		}
		
		return rtnType;
	}
	
	
}
