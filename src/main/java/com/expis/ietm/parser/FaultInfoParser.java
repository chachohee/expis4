package com.expis.ietm.parser;

import com.expis.common.ExpisCommonUtile;
import com.expis.common.IConstants;
import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.domparser.*;
import com.expis.ietm.dao.XContAllMapper;
import com.expis.ietm.dto.XContDto;
import com.expis.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

/**
 * [공통모듈]FI(FaultInfo) Parser Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FaultInfoParser {


	private final ContParser contParser;
	private final GrphParser grphParser;
	private final TextParser textParser;
	private final XContAllMapper allMapper;
	private final AlertParser alertParser;

	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	ExternalFileEx ext = ctx.getBean("extConf",ExternalFileEx.class);

	public boolean isFIToco(String tocoType) {
		
		boolean rtBoo = false;
		
		try {
			/*
			 * 21 : DI				(FI_1.결함식별및설명)
			 * 22 : DI_DESC	(FI_1.sub.조종사탐지결함)
			 * 23 : FI_A			(FI_1.sub.(1)설명)
			 * 24 : LR				(FI_2.로그북리포트)
			 * 25 : LR_DESC	(FI_2.sub.조종사탐지결함)
			 * 26 : AP				(FI_3.접근및위치자료)
			 * 27 : FI_PIC		(FI_3.sub.접근및위치자료 밑의 그래픽)
			 * 28 : DDS			(FI_4.고장탐구절차)
			 * 29 : FI_B			(FI_4.sub.AA)
			 */
			
			String[][] arrFIType	= {
					  {"21", "DI"}	, {"22", "DI_DESC"}	, {"23", "FI_A"}
					, {"24", "LR"}	, {"25", "LR_DESC"}
					, {"26", "AP"}	, {"27", "FI_PIC"}
					, {"28", "DDS"}, {"29", "FI_B"}
			};
			
			for (int i=0; i<arrFIType.length; i++) {
				if (tocoType.equals(arrFIType[i][0])) {
					rtBoo = true;
					break;
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.isFIToco Exception:"+ex.toString());
		}
		
		return rtBoo;
	}


	/**
	 * 1. 결함 식별 및 설명
	 * @MethodName	: getDIHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 17.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return rtSB = <div class='fi-content'><div class='fi-topic' /><div class='fi-task' id='ac-div_1' />
	 */
	public StringBuffer getDIHtml(ParserDto psDto, Node paNode) {
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.SYSTEM)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			//<faultinf>는 한번만 호출하면 형제들 노드 추출해서 계산하기에 boolean 비교값 넣은것임
			boolean isFaultinf		= false;
			
			ContParser.TITLE_CNT++;
			String fiTopic	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.NAME);
			String sysId		= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.ID);
			
			rtSB.append( CSS.getDivFITopic(ContParser.TITLE_CNT+"") );
			rtSB.append(fiTopic);
			rtSB.append( contParser.getMemoHtml(psDto, sysId) );
			rtSB.append(CSS.DIV_END);
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					log.info("nodeName : "+nodeName);
					if (nodeName.equals(DTD.DESC)) {
						nodeSB.append( this.getDIDescHtml(psDto, curNode) );
						
					} else if (nodeName.equals(DTD.FI_FAULTINF)) {
						log.info("psDto.getTocoType() : "+psDto.getTocoType());
						if (psDto.getTocoType().equals(IConstants.TOCO_TYPE_FI_FIA)) {
							String fiId = XmlDomParser.getAttributes(curNode.getAttributes(), ATTR.ID);
							log.info("fiId : "+fiId);
							if (fiId.equals(psDto.getTocoId())) {
								/**
								 * 2021 11 30 
								 * Icon 이미지처리를 위해서 아이콘 엘리먼트 가지고 오는 부분 추가
								 */
								ExpisCommonUtile ecu = ExpisCommonUtile.getInstance();
								nodeSB.append( this.getDIFaultinfFIAHtml(psDto, ecu.setIconElement((Element)paNode,curNode,true)));
								//nodeSB.append( this.getDIFaultinfFIAHtml(psDto, curNode) );
								log.info("nodeSB A : "+nodeSB.toString());
							} else {
								continue;
							}
						} else {
							//2022 02 23 Park.J.S. : FI 교범에서 불필요 리스트 생성 제거 추후 문제 생길경우 추가적이 보완 필요함
							/*
							//tocoType = IConstants.TOCO_TYPE_FI
							if (isFaultinf == false) {
								nodeSB.append( this.getDIFaultinfListHtml(psDto, curNode) );
								log.info("nodeSB B : "+nodeSB.toString());
								isFaultinf = true;
							} else {
								continue;
							}
							*/
						}
					}
				}
			}
			
			rtSB.append( CSS.getDivFITask(ContParser.TITLE_CNT+"") );
			rtSB.append(nodeSB);
			rtSB.append(CSS.DIV_END);
			
			//목차별로 감사는 태그 (접기/펼치기 기능 위함)
			rtSB.insert(0, CSS.DIV_FI_CONT);
			rtSB.append(CSS.DIV_END);
			
			log.info("~~~>>>>DI rtSB 111=\n"+rtSB.toString());
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getDIHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 1. 결함 식별 및 설명 > 조종사 탐지결함 내 표 형식 중 <descinfo>
	 * @MethodName	: getDIDescHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 17.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getDIDescHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.DESC)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.PARASEQ)) {
						nodeSB.append( this.getDIParaSeqHtml(psDto, curNode) );
					}
				}
			}
			
			rtSB.append(nodeSB);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getDIDescHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 1. 결함 식별 및 설명 > 조종사 탐지결함 내 표 형식 중 <para-seq>
	 * @MethodName	: getDIParaSeqHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 17.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getDIParaSeqHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.PARASEQ)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.PARA)) {
						nodeSB.append( this.getDIParaHtml(psDto, curNode) );
					}
				}
			}
			
			rtSB.append(nodeSB);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getDIParaSeqHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 1. 결함 식별 및 설명 > 조종사 탐지결함 내 표 형식 중 <para>
	 * @MethodName	: getDIParaHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 17.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getDIParaHtml(ParserDto psDto, Node paNode) {
		log.info(this.getClass().getName() + " : getDIParaHtml");
		StringBuffer rtSB = new StringBuffer();
			
		if (paNode == null || !paNode.getNodeName().equals(DTD.PARA)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer grphSB		= new StringBuffer();
			StringBuffer cbSB		= new StringBuffer();
			StringBuffer codeSB		= new StringBuffer();
			StringBuffer alertSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					
					if (nodeName.equals(DTD.TEXT)) {
						
					} else if (nodeName.equals(DTD.GRPH)) {
						grphSB.append( grphParser.getGrphHtml(psDto, curNode, true) );
						
					} else if (nodeName.equals(DTD.TABLE)) {
						String tableType = XmlDomParser.getAttributes(curNode.getAttributes(), ATTR.TB_TABLETYPE);
						//2022 10 06 Park.J.S. Table 방식으로 변경
						if (tableType.equals(VAL.TB_TBTYPE_CB)) {
							//cbSB.append( this.getDITableHtml(psDto, curNode) );
							cbSB.append( this.getDITableHtml_NEW(psDto, curNode) );
							
						} else if (tableType.equals(VAL.TB_TBTYPE_CODE)) {
							//codeSB.append( this.getDITableHtml(psDto, curNode) );
							codeSB.append( this.getDITableHtml_NEW(psDto, curNode) );
						}
					//2022 07 07 Park.J.S. ADD
					}else if (nodeName.equals(DTD.ALERT)) {
						
						//by ejkim 2022.10.07 가상목차라도 상위 클릭시 하위 내용 표시 건 중 주기창은 제외 (주기는 toco_type:23)
						//alertSB.append(alertParser.getAlertHtml(psDto, curNode));
						
						if(!psDto.getTocoType().equals("23") ) {
							alertSB.append(alertParser.getAlertHtml(psDto, curNode));
						}
						
						
					}
				}
			}
			
			// rtSB.append( CSS.getTableFIDI(grphSB.toString(), cbSB.toString(), codeSB.toString()) );
			// 2023.05.24 - 테이블의 이미지 최소, 최대 사이즈 고정하도록 보완 - jingi.kim 
			// 			  - T-50, 이미지가 최대 사이즈로 설정된 경우 === <grphprim widthcheck="1"> 테이블의 모양이 틀어지는 현상 보정 - jingi.kim
			// 			  - colgroup width="38%" == 508 , colgroup width="48%" == 645
			String grph = cnvGrphFIDI( grphSB.toString(), 500, 640);
			rtSB.append( CSS.getTableFIDI(grph, cbSB.toString(), codeSB.toString()) );
			//2024.04.29 - 도해사이즈 확대 - jingi.kim
			//-rtSB.append( CSS.getTableFIDI(grphSB.toString(), cbSB.toString(), codeSB.toString()) );
			rtSB.append( alertSB );
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getDIParaHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	/**
	 * FIDI 형태, 2열 테이블의 좌측이이밎, 우측열 설명글, 테이블 하단에 FI 형태 표시 
	 *   FIDI 형식일때 좌측 그래픽의 width, height 를 조절하기 위한 함수
	 *   width 값을 기준으로 height를 설정
	 *   !! 테그내 width, height 스타일의 값이 동일하다는 가정하에 동작 === 첫번째 검색된 값만 처리
	 * @MethodName		: cnvGrphFIDI
	 * @AuthorDate		: 2023.05.24. jingi.kim
	 * @ModificationHistory	: 
	 * @param 이미지 테그를 포함한 String : grph , 최소 width : wmin , 최대 width : wmax
	 * @return 변환된 이미지 테그를 포함한 String
	 */
	public String cnvGrphFIDI( String grph, int wmin, int wmax ) {
		
		String rtnStr = grph;
		
		try {
			String sb = "";
			String fndTag = "img name='pngImgArea'";
			String fndWidth = "width:";
			String fndHeight = "height:";
			int imgidx = 0;
			int sidx = 0;
			int eidx = 0;
			String sWidth = "";
			String sHeight = "";
			
			imgidx = rtnStr.indexOf(fndTag);
			if ( imgidx == -1 ) 			{	return rtnStr;	}
			sb = rtnStr.substring(imgidx);
			
			sidx = sb.indexOf(fndWidth);
			if ( sidx == -1 ) 				{	return rtnStr;	}
			sb = sb.substring( sidx + fndWidth.length() );
			
			eidx = sb.indexOf(";");
			if ( eidx == -1 ) 	{ eidx = sb.indexOf("'");	}
			
			sidx = imgidx + sidx + fndWidth.length();
			eidx = sidx + eidx;
			sWidth = rtnStr.substring( sidx, eidx ).trim();
			if ( "".equals(sWidth) ) 		{	return rtnStr;	}
			if ( "100%".equals(sWidth) ) 	{	return rtnStr;	}
			sWidth = sWidth.replace("px", "");
			
			sb = rtnStr.substring(imgidx);
			sidx = sb.indexOf(fndHeight);
			if ( sidx > 0 ) {
				sb = sb.substring( sidx + fndHeight.length() );
				eidx = sb.indexOf(";");
				if ( eidx == -1 ) 	{ eidx = sb.indexOf("'");	}
				
				sidx = imgidx + sidx + fndHeight.length();
				eidx = sidx + eidx;
				sHeight = rtnStr.substring(sidx, eidx).trim();
				sHeight = sHeight.replace("px", "");
			}
			
			double scale = 1.0;
			double dWidth = 0.0;
			double dHeight = 0.0;
			String cnvWidth = "";
			String cnvHeight = "";
			
			if ( Integer.parseInt(sWidth) < wmin ) {
				scale = (double)Integer.parseInt(sWidth) / (double)wmin;
			}
			if ( Integer.parseInt(sWidth) > wmax ) {
				scale = (double)Integer.parseInt(sWidth) / (double)wmax;
			}
			
			dWidth = Double.parseDouble(sWidth) / scale;
			cnvWidth = Integer.toString( (int)Math.ceil(dWidth) );
			if ( !"".equals(sHeight) ) {
				dHeight = Double.parseDouble(sHeight) / scale;
				cnvHeight = Integer.toString( (int)Math.ceil(dHeight) );
			}
			
			rtnStr = rtnStr.replaceAll(sWidth, cnvWidth);
			if ( !"".equals(sHeight) ) {
				rtnStr = rtnStr.replaceAll(sHeight, cnvHeight);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("FaultInfoParser.cnvGrphFIDI Exception:"+e.toString());
		}
		
		return rtnStr;
	}
	
	/**
	 * 1. 결함 식별 및 설명 > 조종사 탐지결함 내 표 형식 중 <table> 내용
	 * @MethodName	: getDITableHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 12. 1.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return rtSB = <ul><li><p>..col</p>..row</li></ul>
	 */
	public StringBuffer getDITableHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
			
		if (paNode == null || !paNode.getNodeName().equals(DTD.TABLE)) {
			return rtSB;
		}
		
		String tbType = XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TB_TABLETYPE); 
		if (!tbType.equals(VAL.TB_TBTYPE_CB) && !tbType.equals(VAL.TB_TBTYPE_CODE)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			NamedNodeMap curAttr	= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					
					if (nodeName.equals(DTD.TB_ENTRY)) {
						curAttr = curNode.getAttributes();
						//String tdRow	= XmlDomParser.getAttributes(curAttr, ATTR.FI_TB_TD_ROW);
						String tdCol		= XmlDomParser.getAttributes(curAttr, ATTR.FI_TB_TD_COL);
						//String tdUline	= XmlDomParser.getAttributes(curAttr, ATTR.FI_TB_TD_ULINE);
						String tdText	= textParser.getTextPara(psDto, curNode).toString().trim();
						if (!tdText.equals("")) {
							tdText = CodeConverter.getCodeConverter(psDto, tdText, "", "");
						}
						
						String pStartHtml = "";
						if (tdCol.equals(VAL.FI_TB_TD_NUM)) {
							pStartHtml = CSS.P;
						} else if (tdCol.equals(VAL.FI_TB_TD_CODE)) {
							pStartHtml = CSS.P_FI_TB_DI;
						} else {
							pStartHtml = CSS.P;
						}
						
						if (tdCol.equals(VAL.FI_TB_TD_NUM)) {
							if (nodeSB != null && !nodeSB.toString().equals("")) {
								nodeSB.append(CSS.LI_END);
							}
							nodeSB.append(CSS.LI);
						}
						
						nodeSB.append(pStartHtml);
						nodeSB.append(tdText);
						nodeSB.append(CSS.P_END);
					}
				}
			}
			
			if (nodeSB != null && !nodeSB.toString().equals("")) {
				nodeSB.append(CSS.LI_END);
			}
			
			rtSB.append(CSS.UL_FI);
			rtSB.append(nodeSB);
			rtSB.append(CSS.UL_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getDITableHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 1. 결함 식별 및 설명 > 조종사 탐지결함 내 하위 목차 리스트 링크
	 * @MethodName	: getDIFaultinfListHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 12. 1.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getDIFaultinfListHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.FI_FAULTINF)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			//<system type='DI_DESC'><faultinf> 형제 노드들을 일괄 추출하여 구성
			NodeList curList	= XmlDomParser.getNodeListFromXPathAPI(paNode, XALAN.FI_FAULTINF);
			
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.FI_FAULTINF)) {
					NamedNodeMap curAttr = curNode.getAttributes();
					
					String fiId			= XmlDomParser.getAttributes(curAttr, ATTR.ID);
					String fiName	= XmlDomParser.getAttributes(curAttr, ATTR.NAME);
					String fiType		= XmlDomParser.getAttributes(curAttr, ATTR.FAULT_TYPE);
					
					if (fiType.equals(VAL.FI_FAULTTYPE_A)) {
						String jScript = CSS.LINK_JS_TOCO + "('" + psDto.getToKey() + "', '" + fiId + "', '')";
						nodeSB.append( CSS.setLinkScript(fiId, fiName, jScript, "") );
					}
				}
			}
			
			rtSB.append(CSS.DIV_FI_LINK);
			rtSB.append(nodeSB);
			rtSB.append(CSS.DIV_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getDIFaultinfListHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 1. 결함 식별 및 설명 > 조종사 탐지결함 내 하위 목차 다이어그램
	 * @MethodName	: getDIFaultinfFIAHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 12. 3.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getDIFaultinfFIAHtml(ParserDto psDto, Node paNode) {
		log.info("getDIFaultinfFIAHtml");
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.FI_FAULTINF)) {
			return rtSB;
		}
		
		try {
			Node curNode			= paNode;
			String nodeName		= curNode.getNodeName();
			StringBuffer nodeSB	= new StringBuffer();
			StringBuffer imageSB	= new StringBuffer();
			//FI 다이어그램 표시(결함식별 하위에서 탐지결함 직접 선택시에는 다이어그램 표시되지만, 상위 목차에서는 링크 리스트 정보만 있으면 됨)
			if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.FI_FAULTINF)) {
				NamedNodeMap curAttr = curNode.getAttributes();
				
				String fiId			= XmlDomParser.getAttributes(curAttr, ATTR.ID);
				//String fiName	= XmlDomParser.getAttributes(curAttr, ATTR.NAME);
				String fiType		= XmlDomParser.getAttributes(curAttr, ATTR.FAULT_TYPE);
				String refimage		= XmlDomParser.getAttributes(curAttr, ATTR.FAULT_REFIMAGE);
				log.info("fiId : "+fiId+", fiType : "+fiType+", refimage : "+refimage);
				if(!"".equals(refimage.trim())) {
					imageSB.append(CSS.DIV);
					//그래픽 ID, 그래픽명, 확장자명, 그래픽 경로 추출
					String grphName		= refimage;
					String extOrgName	= StringUtil.getExtName(grphName);
					String extName		= extOrgName.toUpperCase();
					log.info("grphName : "+grphName+", extOrgName : "+extOrgName+", extName : "+extName);
					if (extName.equals(VAL.IMG_EXT_TIF)) {
						log.info("TRANCE VAL.IMG_EXT_TIF");
						extName = VAL.IMG_EXT_JPG;
					}
					//가로 길이 고정
					String widthCheck	= "1";
					String grphPath		= psDto.getBizIetmdata();
					String systemPath	= psDto.getBizSyspath();
					log.info("grphPath : "+grphPath+", systemPath : "+systemPath+", widthCheck : "+widthCheck);
					
					if(extName.equals("AVI") || extName.equals("WMV") || extName.equals("MOV") || extName.equals("MP4")) {
						grphPath		+= "/" + IConstants.GRPH_PATH_VIDEO + "/" +grphName;
						systemPath	+=  IConstants.GRPH_PATH_VIDEO  + "\\" + grphName;
					} else {
						grphPath		+= "/" + IConstants.GRPH_PATH_IMAGE + "/" +grphName;
						systemPath	+=  IConstants.GRPH_PATH_IMAGE + "\\" + grphName;
					}
					//그래픽 사이즈 계산해서 HTML 생성
					File systemFile = new File(systemPath);
					if (systemFile.exists() == true) {
						String[] arrSize	= grphParser.calculateGrphSize(psDto, extName, systemPath, widthCheck,null);
						//2023 02 21 jysi EDIT : grphName을 매개변수로 추가하여 하위 사용함수에서 grphName을 다시 구하는 과정을 제거하고자함 
						//imageSB = grphParser.gridGrphHtml(psDto, extName, "", grphPath, arrSize[0], arrSize[1], systemPath, true);	
						imageSB = grphParser.gridGrphHtml(psDto, extName, "", grphPath, arrSize[0], arrSize[1], systemPath, true, grphName);
					} else {		
						log.info("No Image........ : "+systemPath);
						imageSB = grphParser.gridGrphNoImage();
						//20201119 add LYM No-image일 경우 디버깅위해 코드 추가
						imageSB.append("<span alt='").append(grphPath).append("' />");
					}
					//grphParser.getGrphCaption(psDto, paNode, true, grphName);
					log.info("imageSB : "+imageSB.toString());
					imageSB.append(CSS.DIV_END);
				}
				//if (fiId.equals(psDto.getTocoId()) && fiType.equals(VAL.FI_FAULTTYPE_A)) {
				if (fiType.equals(VAL.FI_FAULTTYPE_A)) {
					/*
					String serverUrl		= "";
					String stepFlowKind	= "0";
					String lastVersionId	= "";
					String langMode		= "0";
					String gWidth			= "100%";
					String gHeight			= "600";
					String webMobileType = psDto.getWebMobileKind();
					*/
					fiType = "FI_A";
					
					String contXml = this.createXml(curNode).toString();
					
					contXml = contXml.replaceAll("&amp;","");
					contXml = contXml.replaceAll("amp;","");
					contXml = contXml.replaceAll("#254;","");
					contXml = contXml.replaceAll("#200;","");
					contXml = contXml.replaceAll("#1;","");
					contXml = contXml.replaceAll("#255;","");
					contXml = contXml.replaceAll("#0;","");
					contXml = contXml.replaceAll("#0","");
					contXml = contXml.replaceAll("&#32;"," ");
					contXml = contXml.replaceAll("#32;"," ");
					contXml = contXml.replaceAll("&amp;", "&");
					contXml = contXml.replaceAll("&#254;", "");
					contXml = contXml.replaceAll("&#200;", "");
					contXml = contXml.replaceAll("&#1;", "");
					contXml = contXml.replaceAll("&#255;", "");
					
					nodeSB.append(contXml);
//					nodeSB.append( CSS.getDivFIDigram(fiId, fiType, contXml, serverUrl, stepFlowKind, lastVersionId, langMode, gWidth, gHeight, webMobileType) );
				} else {
					CodeConverter.FI_CHK = true;
					String contXml = XmlDomParser.getXmlDocumentNode(curNode, "");
					contXml = contXml.replaceAll("&amp;", "&");
					contXml = contXml.replaceAll("&#13;", "<br/>");
					contXml = contXml.replaceAll("&#254;", "");
					contXml = contXml.replaceAll("&#200;", "");
					contXml = contXml.replaceAll("&#1;", "");
					contXml = contXml.replaceAll("&#255;", "");
					nodeSB.append(contXml);
					
				}
			}
			
			log.info("fi nodeSB : " + nodeSB.toString());
			rtSB.append(imageSB);
			rtSB.append(CSS.DIV_FI_LINK);
			rtSB.append(nodeSB);
			rtSB.append(CSS.DIV_END);
			log.info("fi rtSB : " + rtSB.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getDIFaultinfFIAHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 2. 로그북 리포트
	 * DTD 구성 <system type='LR_DESC'><descinfo><para-seq><para><table><entry></>
	 * @MethodName	: getLRHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 17.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getLRHtml(ParserDto psDto, Node paNode) {
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.SYSTEM)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			ContParser.TITLE_CNT++;
			String fiTopic	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.NAME);
			String sysId		= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.ID);
			
			rtSB.append( CSS.getDivFITopic(ContParser.TITLE_CNT+"") );
			rtSB.append(fiTopic);
			rtSB.append( contParser.getMemoHtml(psDto, sysId) );
			rtSB.append(CSS.DIV_END);
			
			log.info("==>getLRHtml memo="+psDto.getMemoList().size()+", id="+psDto.getTocoId());
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.DESC)) {
						nodeSB.append( this.getLRDescHtml(psDto, curNode) );
					}
				}
			}
			
			rtSB.append( CSS.getDivFITask(ContParser.TITLE_CNT+"") );
			rtSB.append(nodeSB);
			rtSB.append(CSS.DIV_END);
			
			//목차별로 감사는 태그 (접기/펼치기 기능 위함)
			rtSB.insert(0, CSS.DIV_FI_CONT);
			rtSB.append(CSS.DIV_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getLRHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 2. 로그북 리포트 > 조종사 탐지결함 내 <descinfo>
	 * @MethodName	: getLRDescHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 17.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getLRDescHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.DESC)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.PARASEQ)) {
						nodeSB.append( this.getLRParaSeqHtml(psDto, curNode) );
					}
				}
			}
			
			rtSB.append(nodeSB);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getLRDescHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 2. 로그북 리포트 > 조종사 탐지결함 내 <para-seq> 
	 * @MethodName	: getLRParaSeqHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 17.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getLRParaSeqHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.PARASEQ)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.PARA)) {
						nodeSB.append( this.getLRParaHtml(psDto, curNode) );
					}
				}
			}
			
			rtSB.append(nodeSB);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getLRParaSeqHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 2. 로그북 리포트 > 조종사 탐지결함 내 <para>
	 * @MethodName	: getLRParaHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 17.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getLRParaHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
			
		if (paNode == null || !paNode.getNodeName().equals(DTD.PARA)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.TEXT)) {
						
					} else if (nodeName.equals(DTD.TABLE)) {
						String tableType = XmlDomParser.getAttributes(curNode.getAttributes(), ATTR.TB_TABLETYPE);
						if (tableType.equals(VAL.TB_TBTYPE_LOGBOOK)) {
							log.info("Table Code : "+this.getLRTableHtmlNew(psDto, curNode).toString());
							//2022 09 29 테이블 형식으로 변경
							//nodeSB.append( this.getLRTableHtml(psDto, curNode) );
							nodeSB.append(this.getLRTableHtmlNew(psDto, curNode));
						}
					}
				}
			}
			
			rtSB.append(nodeSB);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getLRParaHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	/**
	 * <pre>
	 * 기존 ul li 양식을 Table 양식으로 수정
	 * </pre>
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public StringBuffer getLRTableHtmlNew(ParserDto psDto, Node paNode) {
		StringBuffer rtSB = new StringBuffer();
		if (paNode == null || !paNode.getNodeName().equals(DTD.TABLE)) {
			return rtSB;
		}
		
		String tbType = XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TB_TABLETYPE); 
		if (!tbType.equals(VAL.TB_TBTYPE_LOGBOOK)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			NamedNodeMap curAttr	= null;
			String nodeName			= "";
			StringBuffer nodeSB		= new StringBuffer();
			String rowStr			= "";
			String colStr			= "";
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++){
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.TB_ENTRY)) {
						curAttr = curNode.getAttributes();
						String tdRow		= XmlDomParser.getAttributes(curAttr, ATTR.FI_TB_TD_ROW);
						String tdCol		= XmlDomParser.getAttributes(curAttr, ATTR.FI_TB_TD_COL);
						String tdUline		= XmlDomParser.getAttributes(curAttr, ATTR.FI_TB_TD_ULINE);
						//2022 12 16 Park.J.S. ADD : vehicletype 에 따라 아이콘 처리 하기위해 추가
						String vehicletype	= XmlDomParser.getAttributes(curAttr, ATTR.VEHICLETYPE);
						//2022 12 20 Park.J.S. Update : vehicletype 무시하게 수정(로그북 리포트에서만 사용 해야함)
						String tdText	= textParser.getTextPara(psDto, curNode, false).toString().trim();
						if (!tdText.equals("")) {
							tdText = CodeConverter.getCodeConverter(psDto, tdText, "", "");
						}
						log.info("tdText is version check : "+tdText.indexOf("div_version")+", "+tdCol);
						log.info("tdText befor : "+tdText);
						String pStartHtml = "";
						//2022 09 27 버전바가 존재 할경우 CSS class 추가
						if (tdCol.equals(VAL.FI_TB_TD_NUM)) {
							pStartHtml = CSS.P_FI_TD_NUM;
							if(tdText.indexOf("div_version") > 0) {//버전바가 존재하는 경우 
								log.info("Version class Add num");
								tdText  = tdText.substring(0,tdText.indexOf("div_version")+11)+" num'"+tdText.substring(tdText.indexOf("div_version")+12,tdText.length());
							}
						} else if (tdCol.equals(VAL.FI_TB_TD_CODE)) {
							pStartHtml = CSS.P_FI_TD_CODE;
							if(tdText.indexOf("div_version") > 0) {//버전바가 존재하는 경우
								log.info("Version class Add code");
								//2022 12 20 Park.J.S. 버전바 아이콘 동시 존재할경우 한줄 처리 추가. : 현재 아이콘은 호기 선택이 가능한 경우에만 사용중 문제 발생할경우 BLOCL2,T50, FA50 등 추가적인 IF문 필요
								String typeAStr = "<img src=\""+ext.getPROJECT()+"web/image/ietm/fi/Type_A.jpg\" width=\"18\" height=\"18\" class=\"icon\" alt=\"\"/>";
								String typeBStr = "<img src=\""+ext.getPROJECT()+"web/image/ietm/fi/Type_B.jpg\" width=\"18\" height=\"18\" class=\"icon\" alt=\"\"/>";
								String typeCStr = "<img src=\""+ext.getPROJECT()+"web/image/ietm/fi/Type_C.jpg\" width=\"18\" height=\"18\" class=\"icon\" alt=\"\"/>";
								String typeDStr = "<img src=\""+ext.getPROJECT()+"web/image/ietm/fi/Type_D.jpg\" width=\"18\" height=\"18\" class=\"icon\" alt=\"\"/>";
								tdText  = tdText.substring(0,tdText.indexOf("div_version")+11)+" code %ICONAREA%'"+tdText.substring(tdText.indexOf("div_version")+12,tdText.length());
								if(!tdText.equals("") && !"".equals(vehicletype) && "TYPEA".equals(vehicletype.toUpperCase())){
									tdText = tdText.substring(0, tdText.indexOf(">")+1)+typeAStr+tdText.substring(tdText.indexOf(">")+1,tdText.length());
								}else if(!tdText.equals("") && !"".equals(vehicletype) && "TYPEB".equals(vehicletype.toUpperCase())){
									tdText = tdText.substring(0, tdText.indexOf(">")+1)+typeBStr+tdText.substring(tdText.indexOf(">")+1,tdText.length());
								}else if(!tdText.equals("") && !"".equals(vehicletype) && "TYPEC".equals(vehicletype.toUpperCase())){
									tdText = tdText.substring(0, tdText.indexOf(">")+1)+typeCStr+tdText.substring(tdText.indexOf(">")+1,tdText.length());
								}else if(!tdText.equals("") && !"".equals(vehicletype) && "TYPED".equals(vehicletype.toUpperCase())){
									tdText = tdText.substring(0, tdText.indexOf(">")+1)+typeDStr+tdText.substring(tdText.indexOf(">")+1,tdText.length());
								}else {
									//2024.02.15 - TYPEAB 패턴인 경우 추가 - jingi.kim
									if ( !tdText.equals("") && !"".equals(vehicletype) ) {
										if ( vehicletype.toUpperCase().contains("TYPE") && vehicletype.length() > 5 ) {
											String strTypes = vehicletype.toUpperCase().replace("TYPE", "");
											if ( !"".equals(strTypes) ) {
												String appendText = "";
												for ( char chty : strTypes.toCharArray() ) {
													String iconId = "iconType-" + chty;
													String iconPath = ext.getPROJECT() + "web/image/ietm/fi/Type_" + chty + ".jpg";
													appendText += CSS.getDivIcon(iconId, iconPath, "18", "18", "");
												}
												if ( !"".equals(appendText) ) {
													appendText = "<div class='multi-icon'>" + appendText + "</div>";
												}
												tdText = tdText.replace("%ICONAREA%", "icon-area");
												tdText = tdText.substring(0, tdText.indexOf(">")+1)+appendText+tdText.substring(tdText.indexOf(">")+1,tdText.length());
											}
										}
									}
								}
								tdText = tdText.replace("%ICONAREA%", "");
								
							}else {
								//2022 12 20 Park.J.S. 현재 아이콘은 호기 선택이 가능한 경우에만 사용중 문제 발생할경우 BLOCL2,T50, FA50 등 추가적인 IF문 필요
								if(!tdText.equals("") && !"".equals(vehicletype) && "TYPEA".equals(vehicletype.toUpperCase())){
									tdText = "<img src=\""+ext.getPROJECT()+"web/image/ietm/fi/Type_A.jpg\" width=\"18\" height=\"18\" class=\"icon\" alt=\"\"/>"+CodeConverter.getCodeConverter(psDto, tdText, "", "");
								}else if(!tdText.equals("") && !"".equals(vehicletype) && "TYPEB".equals(vehicletype.toUpperCase())){
									tdText = "<img src=\""+ext.getPROJECT()+"web/image/ietm/fi/Type_B.jpg\" width=\"18\" height=\"18\" class=\"icon\" alt=\"\"/>"+CodeConverter.getCodeConverter(psDto, tdText, "", "");
								}else if(!tdText.equals("") && !"".equals(vehicletype) && "TYPEC".equals(vehicletype.toUpperCase())){
									tdText = "<img src=\""+ext.getPROJECT()+"web/image/ietm/fi/Type_C.jpg\" width=\"18\" height=\"18\" class=\"icon\" alt=\"\"/>"+CodeConverter.getCodeConverter(psDto, tdText, "", "");
								}else if(!tdText.equals("") && !"".equals(vehicletype) && "TYPED".equals(vehicletype.toUpperCase())){
									tdText = "<img src=\""+ext.getPROJECT()+"web/image/ietm/fi/Type_D.jpg\" width=\"18\" height=\"18\" class=\"icon\" alt=\"\"/>"+CodeConverter.getCodeConverter(psDto, tdText, "", "");
								} else {
									//2024.02.15 - TYPEAB 패턴인 경우 추가 - jingi.kim
									if ( !tdText.equals("") && !"".equals(vehicletype) ) {
										if ( vehicletype.toUpperCase().contains("TYPE") && vehicletype.length() > 5 ) {
											String strTypes = vehicletype.toUpperCase().replace("TYPE", "");
											if ( !"".equals(strTypes) ) {
												String appendText = "";
												for ( char chty : strTypes.toCharArray() ) {
													String iconId = "iconType-" + chty;
													String iconPath = ext.getPROJECT() + "web/image/ietm/fi/Type_" + chty + ".jpg";
													appendText += CSS.getDivIcon(iconId, iconPath, "18", "18", "");
												}
												if ( !"".equals(appendText) ) {
													appendText = "<div class='multi-icon'>" + appendText + "</div>";
												}
												tdText = tdText.substring(0, tdText.indexOf(">")+1)+appendText+tdText.substring(tdText.indexOf(">")+1,tdText.length());
												if ( !"".equals(appendText) ) {
													tdText = "<div class='icon-area'>" + tdText + "</div>";
												}
											}
										}
									}
								}
							}
						} else if (tdUline.equals(VAL.FI_TB_TD_ULINE)) {
							pStartHtml = CSS.P_FI_TD_ULINE;
							if(tdText.indexOf("div_version") > 0) {//버전바가 존재하는 경우
								log.info("Version class Add uline");
								tdText  = tdText.substring(0,tdText.indexOf("div_version")+11)+" uline'"+tdText.substring(tdText.indexOf("div_version")+12,tdText.length());
							}
						} else {
							pStartHtml = CSS.TB_TD;
						}
						log.info("tdText after : "+tdText);
						if (tdCol.equals(VAL.FI_TB_TD_NUM)) {
							if (nodeSB != null && !nodeSB.toString().equals("")) {
								nodeSB.append(CSS.TB_TREND);
							}
							nodeSB.append(CSS.TB_TR);
						}
						
						nodeSB.append(pStartHtml);
						nodeSB.append(tdText);
						nodeSB.append(CSS.TB_TDEND);
					}
				}
			}
			
			if (nodeSB != null && !nodeSB.toString().equals("")) {
				nodeSB.append(CSS.TB_TREND);
			}
			
			rtSB.append("<table class=\"fi_logbook_table\">");
			rtSB.append(nodeSB);
			rtSB.append("</table>");
		}catch (Exception e) {
			e.printStackTrace();
			log.error("getLRTableHtmlNew Error", e);
		}
		return rtSB;
	}
	
	
	/**
	 * 2. 로그북 리포트 > 조종사 탐지결함 내 테이블 형식의 내용
	 * @MethodName	: getLRTableHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 17.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getLRTableHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
			
		if (paNode == null || !paNode.getNodeName().equals(DTD.TABLE)) {
			return rtSB;
		}
		
		String tbType = XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TB_TABLETYPE); 
		if (!tbType.equals(VAL.TB_TBTYPE_LOGBOOK)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			NamedNodeMap curAttr	= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					
					if (nodeName.equals(DTD.TB_ENTRY)) {
						curAttr = curNode.getAttributes();
						//String tdRow	= XmlDomParser.getAttributes(curAttr, ATTR.FI_TB_TD_ROW);
						String tdCol		= XmlDomParser.getAttributes(curAttr, ATTR.FI_TB_TD_COL);
						String tdUline	= XmlDomParser.getAttributes(curAttr, ATTR.FI_TB_TD_ULINE);
						String tdText	= textParser.getTextPara(psDto, curNode).toString().trim();
						if (!tdText.equals("")) {
							tdText = CodeConverter.getCodeConverter(psDto, tdText, "", "");
						}
						log.info("tdText is version check : "+tdText.indexOf("div_version")+", "+tdCol);
						log.info("tdText befor : "+tdText);
						String pStartHtml = "";
						//2022 09 27 버전바가 존재 할경우 CSS class 추가
						if (tdCol.equals(VAL.FI_TB_TD_NUM)) {
							pStartHtml = CSS.P_FI_TB_NUM;
							if(tdText.indexOf("div_version") > 0) {//버전바가 존재하는 경우 
								log.info("Version class Add num");
								tdText  = tdText.substring(0,tdText.indexOf("div_version")+11)+" num'"+tdText.substring(tdText.indexOf("div_version")+12,tdText.length());
							}
						} else if (tdCol.equals(VAL.FI_TB_TD_CODE)) {
							pStartHtml = CSS.P_FI_TB_CODE;
							if(tdText.indexOf("div_version") > 0) {//버전바가 존재하는 경우
								log.info("Version class Add code");
								tdText  = tdText.substring(0,tdText.indexOf("div_version")+11)+" code'"+tdText.substring(tdText.indexOf("div_version")+12,tdText.length());
							}
						} else if (tdUline.equals(VAL.FI_TB_TD_ULINE)) {
							pStartHtml = CSS.P_FI_TB_ULINE;
							if(tdText.indexOf("div_version") > 0) {//버전바가 존재하는 경우
								log.info("Version class Add uline");
								tdText  = tdText.substring(0,tdText.indexOf("div_version")+11)+" uline'"+tdText.substring(tdText.indexOf("div_version")+12,tdText.length());
							}
						} else {
							pStartHtml = CSS.P;
						}
						log.info("tdText after : "+tdText);
						if (tdCol.equals(VAL.FI_TB_TD_NUM)) {
							if (nodeSB != null && !nodeSB.toString().equals("")) {
								nodeSB.append(CSS.LI_END);
							}
							nodeSB.append(CSS.LI);
						}
						
						nodeSB.append(pStartHtml);
						nodeSB.append(tdText);
						nodeSB.append(CSS.P_END);
					}
				}
			}
			
			if (nodeSB != null && !nodeSB.toString().equals("")) {
				nodeSB.append(CSS.LI_END);
			}
			
			rtSB.append(CSS.UL_FI);
			rtSB.append(nodeSB);
			rtSB.append(CSS.UL_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getLRTableHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 
	 * @MethodName	: setTableText
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 22.
	 * @ModificationHistory	: 
	 * @param descType, paraText
	 * @return
	 */
	public StringBuffer setTableText(ParserDto psDto, String tdCol, String entryText) {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			if (!entryText.equals("")) {
				entryText = CodeConverter.getCodeConverter(psDto, entryText, "", "");
				
				if (tdCol.equals("1")) {
					rtSB.append(entryText);
				} else {
					rtSB.append(CSS.P_PARA1);
					rtSB.append(entryText);
					rtSB.append(CSS.P_END);
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.setParaText Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 3. 접근 및 위치자료
	 * @MethodName	: getAPHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 17.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getAPHtml(ParserDto psDto, Node paNode, long titleCnt, String apHtml) {
		StringBuffer rtSB = new StringBuffer();
		
		try {
			//제목
			String apTitle	= XmlDomParser.getTxt(paNode);
			String sysId		= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.ID);
			
			rtSB.append( CSS.getDivTopic() );
			rtSB.append(apTitle);
			rtSB.append( contParser.getMemoHtml(psDto, sysId) );
			rtSB.append(CSS.DIV_END);
			
			//내용
			rtSB.append( CSS.getDivDesc() );
			rtSB.append(apHtml);
			rtSB.append(CSS.DIV_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getAPHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 4. 고장탐구절차 - 사실상 사용 안되는것 같음
	 * @MethodName	: getDDSHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 17.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getDDSHtml(ParserDto psDto, Node paNode) {
		StringBuffer rtSB = new StringBuffer();
		
		try {
			
			log.info("===>getDDSHtml");
			
			//rtSB.append( this.getFaultInfoB(psDto, paNode) );
			
			rtSB.append( this.getDDSFaultinfFIBHtml(psDto, paNode) );
			
			//rtSB.insert(0, CSS.DIV_FI_CONT);
			//rtSB.append(CSS.DIV_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getDDSHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 4. 고장탐구절차 > 고장코드 해당 다이어그램
	 * @MethodName	: getDDSFaultinfFIBHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 12. 17.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getDDSFaultinfFIBHtml(ParserDto psDto, Node paNode) {
		log.info("getDDSFaultinfFIBHtml");
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.FI_FAULTINF)) {
			log.info("getDDSFaultinfFIBHtml return");
			return rtSB;
		}
		
		try {
			Node curNode			= paNode;
			String nodeName		= curNode.getNodeName();
			StringBuffer nodeSB	= new StringBuffer();
			log.info("curNode.getNodeType() : "+curNode.getNodeType()+", nodeName : "+nodeName);
			if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.FI_FAULTINF)) {
				NamedNodeMap curAttr = curNode.getAttributes();
				
				String fiId			= XmlDomParser.getAttributes(curAttr, ATTR.ID);
				String fiName		= XmlDomParser.getAttributes(curAttr, ATTR.NAME);
				String fiType		= XmlDomParser.getAttributes(curAttr, ATTR.FAULT_TYPE);
				String type		= XmlDomParser.getAttributes(curAttr, ATTR.TYPE);
				
				//FI ID와 목차 ID가 같을경우는 고장탐구절차 항목을 직접 클릭시이고, 다른 경우는 고장탐구절차 제목을 눌러 다중목차일 상황으로 이때는 링크 기능만 추가
				log.info("fiId : "+fiId+", psDto.getTocoId() : "+psDto.getTocoId()+", fiType : "+fiType+", type : "+type);
				//2022 02 17 Park.J.S. : 특정 기종일경우 FI 하위링크가 아닌 화면에 표시로 수정
				//2022 06 20 Park.J.S. : 특정 기종에서 FI교범이 system으로 묶이지않아서 별도로 확인이 안되는 경우 있어서 해당 경우는 화면에 그냥 표시하게 수정
				//ExpisCommonUtile ecu = ExpisCommonUtile.getInstance(); 
				//if (!fiId.equals(psDto.getTocoId()) && ecu.checkFiMenuView()) {
				XContDto dto = new XContDto();
				dto.setToKey(psDto.getToKey());
				dto.setTocoId(fiId);
				int checkCount = allMapper.selectIPBCount(dto);
				if (!fiId.equals(psDto.getTocoId()) && checkCount > 0) {
					nodeSB.append( CSS.getFIDDSLink(psDto.getToKey(), fiId, fiName) );
				} else {
					//nodeSB.append("<div id='FI_LAYER'>");
					//FI 다이어그램 표시(고장탐구 상위 목차이든, 고장코드 해당 목차이든 모두)
					if ((fiType.equals(VAL.FI_FAULTTYPE_B) && psDto.getWebMobileKind().equals("01"))|| type.equals("field")) {
						String serverUrl		= "";
						String stepFlowKind	= psDto.getFiMode();
						String lastVersionId	= "";
						String langMode		= "0";
						String gWidth			= "100%";
						String gHeight			= "1532";
						String webMobileType = psDto.getWebMobileKind();
						fiType = "FI_B";
						
						String contXml = this.createXml(curNode).toString();
						log.info("FIinfoParser contXml :::::::::: " + contXml);
						contXml = contXml.replaceAll("&amp;#254;&amp;#158;&amp;#0;&amp;#255;&amp;#254;&amp;#160;&amp;#-20;&amp;#255;","");
						//2022 07 08 Park.J.S. & 사용위해 주석 처리
						//contXml = contXml.replaceAll("&amp;","");
						//contXml = contXml.replaceAll("amp;","");
						//2023 01 19 Park.J.S. ADD : &amp;amp; 대응 추가
						contXml = contXml.replaceAll(";amp;", ";");
						contXml = contXml.replaceAll("&amp;#","#");
						contXml = contXml.replaceAll("&amp;&amp;","");
						//contXml = contXml.replaceAll("&amp;amp;","");
						contXml = contXml.replaceAll("&amp;#13;","#13;");
						contXml = contXml.replaceAll("#254;","");
						contXml = contXml.replaceAll("#200;","");
						contXml = contXml.replaceAll("#1;","");
						contXml = contXml.replaceAll("#255;","");
						contXml = contXml.replaceAll("#0;","");
						contXml = contXml.replaceAll("#0","");
						contXml = contXml.replaceAll("&#32;"," ");
						contXml = contXml.replaceAll("#32;"," ");
						contXml = contXml.replaceAll("#-256", "");
						//2022 07 08 Park.J.S. : 특수 문자 처리후 &amp;&amp;만 남는 경우 있어서 제거
						contXml = contXml.replaceAll("&amp;&amp;","");
						contXml = contXml.replaceAll("\t", "");
						log.info("FIinfoParser contXml :::::::::: " + contXml);
						nodeSB.append(contXml);
//						nodeSB.append( CSS.getDivFIDigram(fiId, fiType, contXml, serverUrl, stepFlowKind, lastVersionId, langMode, gWidth, gHeight, webMobileType) );
					} else {
						CodeConverter.FI_CHK = true;
						String contXml = XmlDomParser.getXmlDocumentNode(curNode, "");
						contXml = CodeConverter.getCodeConverter(psDto, contXml, "", "");
						contXml = contXml.replaceAll("&amp;#254;&amp;#158;&amp;#0;&amp;#255;&amp;#254;&amp;#160;&amp;#-20;&amp;#255;","");
						//2022 07 08 Park.J.S. & 사용위해 주석 처리
						//contXml = contXml.replaceAll("&amp;","");
						contXml = contXml.replaceAll("&amp;#","#");
						contXml = contXml.replaceAll("&amp;amp;","&amp;");
						contXml = contXml.replaceAll("&amp;&amp;","");
						contXml = contXml.replaceAll("&amp;#13;","#13;");
						contXml = contXml.replaceAll("#254;","");
						contXml = contXml.replaceAll("#200;","");
						contXml = contXml.replaceAll("#1;","");
						contXml = contXml.replaceAll("#255;","");
						contXml = contXml.replaceAll("#0;","");
						contXml = contXml.replaceAll("#32;","");
						//2022 07 08 Park.J.S. : 특수 문자 처리후 &amp;&amp;만 남는 경우 있어서 제거
						contXml = contXml.replaceAll("&amp;&amp;","");
						
						nodeSB.append(contXml);
					}
					//nodeSB.append("</div>");
				}
			}
			
			rtSB.append(CSS.DIV_FI_LINK);
			rtSB.append(nodeSB);
			rtSB.append(CSS.DIV_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getDDSFaultinfFIBHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * XML 파일 생성
	 * @MethodName	: createXml
	 * @AuthorDate		: LIM Y.M. / 2014. 12. 3.
	 * @ModificationHistory	: 
	 * @param paNode
	 * @return
	 */
	public StringBuffer createXml(Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			
			String szXml = XmlDomParser.getXmlDocumentNode(paNode, "");
			szXml = szXml.replaceAll("\"", "'");
			szXml = szXml.replaceAll("\n"," ");
			
			rtSB.append("<?xml version='1.0' encoding='euc-kr' ?>");
			rtSB.append(szXml);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.createXml Exception:"+ex.toString());
		}
		
		return rtSB;
	}

	/**
	 * <pre>
	 * 2022 10 04 Park.J.S.
	 * FI table 처리용 생성 (version에서 사용)
	 * </pre>
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public StringBuffer getFITableHtml(ParserDto psDto, Node paNode) {
		log.info("getFITableHtml : "+paNode.getNodeName());
		StringBuffer rtSB = new StringBuffer();
		if (paNode == null || !paNode.getNodeName().equals(DTD.TABLE)) {
			return rtSB;
		}
		
		String tbType = XmlDomParser.getAttributes(paNode.getAttributes(), "nodekind");
		log.info("tbType : "+tbType);
		if (!"FaultStepTableNode".equalsIgnoreCase(tbType)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			NamedNodeMap curAttr	= null;
			String nodeName			= "";
			StringBuffer nodeSB		= new StringBuffer();
			NodeList curList = paNode.getChildNodes();
			String tempRowStr = "";
			for (int i=0; i<curList.getLength(); i++){
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.TB_ENTRY)) {
						curAttr = curNode.getAttributes();
						String tdRow	= XmlDomParser.getAttributes(curAttr, ATTR.FI_TB_TD_ROW);
						if(tdRow == null || "".equals(tdRow)) {
							tdRow	= XmlDomParser.getAttributes(curAttr, "rowaddr");
						}
						String tdCol	= XmlDomParser.getAttributes(curAttr, ATTR.FI_TB_TD_COL);
						if(tdCol == null || "".equals(tdCol)) {
							tdCol	= XmlDomParser.getAttributes(curAttr, "coladdr");
						}
						String colspan	= XmlDomParser.getAttributes(curAttr, ATTR.TB_COLSPAN);
						String rowspan	= XmlDomParser.getAttributes(curAttr, ATTR.TB_ROWSPAN);
						
						String tdText	= textParser.getTextPara(psDto, curNode).toString().trim();
						if (!tdText.equals("")) {
							tdText = CodeConverter.getCodeConverter(psDto, tdText, "", "");
						}
						log.info("tdText is version check : "+tdText.indexOf("div_version")+", "+tdCol);
						log.info("tdText befor : "+tdText);
						String pStartHtml = "<td colspan=\""+colspan+"\" rowspan=\""+rowspan+"\" >";
						if("0".equals(tdRow)) {//FI table 첫번째만 가운데 정렬 나머지는 죄측 정렬
							pStartHtml = "<td colspan=\""+colspan+"\" rowspan=\""+rowspan+"\" style=\"text-align: center;\" >";
						}else {
							pStartHtml = "<td colspan=\""+colspan+"\" rowspan=\""+rowspan+"\" style=\"text-align: left;\" >";
						}
						log.info("tdText after : "+tdText);
						if(!tempRowStr.equals(tdRow)) {
							if(!"".equals(tempRowStr)) {
								nodeSB.append(CSS.TB_TREND);
								nodeSB.append(CSS.TB_TR);
							}
							tempRowStr = tdRow;
						}
						nodeSB.append(pStartHtml);
						nodeSB.append(tdText);
						nodeSB.append(CSS.TB_TDEND);
					}
				}
			}
			
			if (nodeSB != null && !nodeSB.toString().equals("")) {
				nodeSB.append(CSS.TB_TREND);
			}
			
			rtSB.append("<table class=\"fi_version_table\">");
			rtSB.append(nodeSB);
			rtSB.append("</table>");
		}catch (Exception e) {
			e.printStackTrace();
			log.error("getLRTableHtmlNew Error", e);
		}
		return rtSB;
	}
	
	
	/**
	 * <pre>
	 * 1. 결함 식별 및 설명 > 조종사 탐지결함 내 표 형식 중 <table> 내용
	 * table 방식으로 리턴하게 신규로 추가 
	 * </pre>
	 * @MethodName			: getDITableHtml_NEW
	 * @AuthorDate			: 2022 10 06 Park.J.S.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return rtSB
	 */
	public StringBuffer getDITableHtml_NEW(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
			
		if (paNode == null || !paNode.getNodeName().equals(DTD.TABLE)) {
			return rtSB;
		}
		
		String tbType = XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TB_TABLETYPE); 
		if (!tbType.equals(VAL.TB_TBTYPE_CB) && !tbType.equals(VAL.TB_TBTYPE_CODE)) {
			return rtSB;
		}
		
		try {
			Node curNode			= null;
			NamedNodeMap curAttr	= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					
					if (nodeName.equals(DTD.TB_ENTRY)) {
						curAttr = curNode.getAttributes();
						String tdCol		= XmlDomParser.getAttributes(curAttr, ATTR.FI_TB_TD_COL);
						String tdText		= textParser.getTextPara(psDto, curNode).toString().trim();
						if (!tdText.equals("")) {
							tdText = CodeConverter.getCodeConverter(psDto, tdText, "", "");
						}
						
						String pStartHtml = "";
						pStartHtml = CSS.TB_TD;
						
						if (tdCol.equals(VAL.FI_TB_TD_NUM)) {
							if (nodeSB != null && !nodeSB.toString().equals("")) {
								nodeSB.append(CSS.TB_TREND);
							}
							nodeSB.append(CSS.TB_TR);
						}
						
						nodeSB.append(pStartHtml);
						nodeSB.append(tdText);
						nodeSB.append(CSS.TB_TDEND);
					}
				}
			}
			
			if (nodeSB != null && !nodeSB.toString().equals("")) {
				nodeSB.append(CSS.TB_TREND);
			}
			
			rtSB.append(CSS.TB_TABLE);
			rtSB.append(nodeSB);
			rtSB.append(CSS.TB_TABLEEND);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("FaultInfoParser.getDITableHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
}
