package com.expis.ietm.parser;

import com.expis.common.ExpisCommonUtile;
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
 * [공통모듈]JG(준비사항-Input) Parser Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InputParser {

	private final  AlertParser alertParser;
	private final  TableParser tableParser;
	private final  TextParser textParser;
	private final  LinkParser linkParser;
	private final  VersionParser verParser;
	

	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	ExternalFileEx ext = ctx.getBean("extConf", ExternalFileEx.class);

	/**
	 * 준비사항
	 * @MethodName	: getInputHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 6. 26.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getInputHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.getNodeName().equals(DTD.INPUT)) {
			return rtSB;
		}
		
		try {
			StringBuffer nodeSB	= new StringBuffer();
			
			if (paNode != null) {
				log.info("CALL Make 준비사항");
				if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
					nodeSB.append( this.getRangeHtml(psDto, paNode) );			//적용범위
				}
				log.info("준비사항 Pass 1");
				nodeSB.append( this.getReqcondHtml(psDto, paNode) );		//요구조건
				log.info("준비사항 Pass 2");
				nodeSB.append( this.getPersonHtml(psDto, paNode) );			//소요인원
				log.info("준비사항 Pass 3");
				nodeSB.append( this.getEquipHtml(psDto, paNode) );			//지원장비
				log.info("준비사항 Pass 4");
				if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
					nodeSB.append( this.getConsumHtml(psDto, paNode) );		//필수교환품목 및 소모성물자
				}else {
					nodeSB.append( this.getConsumHtmlKTA(psDto, paNode) );	//필수교환품목 및 소모성물자
				}
				log.info("준비사항 Pass 5");
				nodeSB.append( this.getAlertHtml(psDto, paNode) );			//안전요건
				log.info("준비사항 Pass 6");
				if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
					nodeSB.append( this.getOthercondHtml(psDto, paNode) );		//기타요건
				}
				log.info("준비사항 Pass 7");
			}
			
			rtSB.append(CSS.DIV_INPUT);
			//2022 06 17 Park.J.S ADD : 영어교범 처리 추가 
			//2022 07 25 Park.J.S ADD : 중복 제거요청으로 주석 처리 
			/*
			if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
				rtSB.append(TITLE.INPUT_EN);	//준비사항
			}else {
				if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
					rtSB.append(TITLE.INPUT);	//준비사항
				}
			}
			*/
			rtSB.append(CSS.DIV_END);
			
			rtSB.append(CSS.UL_INPUT);
			rtSB.append(nodeSB);
			rtSB.append(CSS.UL_END);
			
			
							
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("InputParser.getInputHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 적용범위
	 * @MethodName	: getRangeHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 9.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getRangeHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.getNodeName().equals(DTD.INPUT)) {
			return rtSB;
		}
		
		try {
			NamedNodeMap paAttr = paNode.getAttributes();
			
			String range = XmlDomParser.getAttributes(paAttr, ATTR.TYPE);
			if (range == null || range.equals("")) {
				range = TITLE.NONE;
			}
			
			String titleStr = CSS.LI + TITLE.IN_TYPE + TITLE.COLON + range + CSS.LI_END;
			//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
			if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
				titleStr = CSS.LI + TITLE.IN_TYPE_EN + TITLE.COLON_EN + range + CSS.LI_END;
			}
			
			rtSB.append(titleStr);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("InputParser.getRangeHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 요구조건
	 * @MethodName	: getReqcondHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 9.
	 * @ModificationHistory	: KIM K.S. / 2017 6. 27
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getReqcondHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.getNodeName().equals(DTD.INPUT)) {
			return rtSB;
		}
		
		try {
			NodeList curList = XmlDomParser.getNodeListFromXPathAPI(paNode, XALAN.INXALAN_REQCOND);
			
			if (curList.getLength() == 0) {
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					String str = CSS.LI + TITLE.IN_REQCOND_EN + TITLE.COLON_EN + TITLE.NONE_EN + CSS.LI_END;
					return rtSB.append(str);
				}else {
					String str = CSS.LI + TITLE.IN_REQCOND + TITLE.COLON +"<span>"+TITLE.NONE+"</span>" + CSS.LI_END;
					return rtSB.append(str);
				}
			}
			
			Node curNode				= null;
			Node childNode				= null;
			String nodeName				= "";
			StringBuffer nodeSB			= new StringBuffer();
			StringBuffer tableSB		= new StringBuffer();
			StringBuffer alertSB		= new StringBuffer();
			
			for (int i=0; i<curList.getLength(); i++){
				//2022 10 12 Park.J.S. ADD : 텍스트 중간에 알럿 들어가야 한다고해서 추가
				StringBuffer textAlertSB	= new StringBuffer();
				curNode = curList.item(i);
				String childText = "";
				//String vehicletype	= XmlDomParser.getAttributes(curNode.getAttributes(), "vehicletype");
				if (verParser.checkVehicleType(psDto, curNode) == false) {
					//log.info("Version Is Not Match : "+XmlDomParser.getAttributes(curNode.getAttributes(), "vehicletype"));
					continue;
				}
				
				//요구조건
				NodeList childList = curNode.getChildNodes();
				for (int k=0; k<childList.getLength(); k++)
				{
					childNode = childList.item(k);
					nodeName = childNode.getNodeName();
					
					//<reqcond> 내 <table>, <alert>, <text> 추출
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						
						if (nodeName.equals(DTD.TABLE)) {
							tableSB.append( tableParser.getTableHtml(psDto, childNode, 1, "") );
							
						} else if (nodeName.equals(DTD.ALERT)) {
							alertSB.append( alertParser.getAlertHtml(psDto, childNode) );
							
						} else if (nodeName.equals(DTD.TEXT)) {
							//2022 10 11 Park.J.S. UPDATE : TYPE에 따라 주기로 변경되야 하는 경우 있어서 수정
							String textType = XmlDomParser.getAttributes(childNode.getParentNode().getAttributes(), "type");
							log.info("Check textType : "+textType);
							if(textType != null && (
									textType.equalsIgnoreCase("NOTE") || textType.equalsIgnoreCase("NOPE") || textType.equalsIgnoreCase("WARNING") || textType.equalsIgnoreCase("CAUTION") ||
									textType.indexOf("주의") >-1 || textType.indexOf("경고") > -1 || textType.indexOf("주") > -1
									 )) {
								ExpisCommonUtile utile = ExpisCommonUtile.getInstance();
								String xmlStr = utile.xmlToString(childNode);
								//2022 07 13 경고창에 여러줄 나올경우 처리
								for (int idx=k+1; idx<childList.getLength(); idx++) {
									xmlStr += utile.xmlToString(childList.item(idx));
								}
								String alertType = "note";
								if(textType.indexOf("NOPE") >-1) {
									alertType = "note";
								}else if(textType.indexOf("NOTE") >-1) {
									alertType = "note";
								}else if(textType.indexOf("경고") >-1) {
									alertType = "warning";
								}else if(textType.indexOf("주의") >-1) {
									alertType = "caution";
								}else if(textType.indexOf("주") >-1) {
									alertType = "note";
								}else if(textType.indexOf("WARNING") >-1) {
									alertType = "warning";
								}else if(textType.indexOf("CAUTION") >-1) {
									alertType = "caution";
								}
								//2022 10 13 Version 처리 추가 curNode
								String versionStr		= XmlDomParser.getAttributes(curNode.getAttributes(), "version");
								String changebasisStr	= XmlDomParser.getAttributes(curNode.getAttributes(), "changebasis");
								String alertStr			= "<alert id=\"ABCD"+k+new Date().getTime()+k+"ABCD\" changebasis=\""+changebasisStr+"\" name=\"\" type=\""+alertType+"\" itemid=\"\" ref=\"\" status=\"a\" version=\""+versionStr+"\">";
								Node alertNode = utile.createDomTree(alertStr+xmlStr+"</alert>", 1);
								log.info("alertNodeStr : "+alertStr+xmlStr+"</alert>");
								//alertSB.append( alertParser.getAlertHtml(psDto, alertNode.getFirstChild()).toString());
								textAlertSB.append( alertParser.getAlertHtml(psDto, alertNode.getFirstChild()).toString());
								break;
							}else {
								childText += textParser.getTextInput(psDto, childNode);
							}
						}
					}
				}//for end
				
				//2022 09 26 Park.J.S. 버전처리 추가
				StringBuffer verSB = verParser.checkVersionHtml(psDto, curNode);
				String verEndStr = verParser.endVersionHtml(verSB);
				
				if (!childText.equals("")) {
					nodeSB.append(CSS.LI);
					nodeSB.append(CHAR.MARK_DOT).append(CHAR.CHAR_SPACE);
					nodeSB.append(verSB);
					nodeSB.append(verEndStr);
					nodeSB.append( CodeConverter.getCodeConverter(psDto, childText, "", "") );
					nodeSB.append(CSS.LI_END);	
				}
				//2022 10 12 Park.J.S. ADD : 텍스트 중간에 알럿 들어가야 한다고해서 추가
				if (!textAlertSB.toString().equals("")) {
					nodeSB.append(CSS.LI);
					nodeSB.append(verSB);
					nodeSB.append( textAlertSB );
					nodeSB.append(verEndStr);
					nodeSB.append(CSS.LI_END);
				}
				/*
				if (!childText.equals("")) {
					nodeSB.append(CSS.LI);
					nodeSB.append(CHAR.MARK_DOT).append(CHAR.CHAR_SPACE);
					nodeSB.append( CodeConverter.getCodeConverter(psDto, childText, "", "") );
					nodeSB.append(CSS.LI_END);	
				}
				*/
			}
			//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
			if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
				rtSB.append( makeLI(DTD.IN_REQCOND, TITLE.IN_REQCOND_EN, nodeSB, tableSB, alertSB) );
			}else {
				if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
					rtSB.append( makeLI(DTD.IN_REQCOND, TITLE.IN_REQCOND, nodeSB, tableSB, alertSB) );
				}else{
					rtSB.append( makeLI(DTD.IN_REQCOND, "항공기 상태", nodeSB, tableSB, alertSB) );
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("InputParser.getReqcondHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 소요인원
	 * <person quantity=''>1건
	 *   <text>N건
	 * @MethodName	: getPersonHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 9.
	 * @ModificationHistory	: KIM K.S. / 2017 6. 27
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getPersonHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.getNodeName().equals(DTD.INPUT)) {
			return rtSB;
		}
		
		try {
			String titleStr = "";
			StringBuffer nodeSB		= new StringBuffer();
			
			Node curNode = XmlDomParser.getNodeFromXPathAPI(paNode, XALAN.INXALAN_PERSON);
			if (curNode == null) {
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					String str = CSS.LI + TITLE.IN_PERSON_EN + TITLE.COLON_EN + TITLE.NONE_EN + CSS.LI_END;
					return rtSB.append(str);
				}else {
					if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
						String str = CSS.LI + TITLE.IN_PERSON + TITLE.COLON + "<span>"+TITLE.NONE+"</span>" + CSS.LI_END;
						return rtSB.append(str);
					}else {
						String str = CSS.LI + "수행 인원" + TITLE.COLON + "<span>"+TITLE.NONE+"</span>" + CSS.LI_END;
						return rtSB.append(str);
					}
				}
				
			} else {
				//소요인원수
				NamedNodeMap curAttr = curNode.getAttributes();
				String quantity = XmlDomParser.getAttributes(curAttr, ATTR.IN_QUANTITY);
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					titleStr = TITLE.IN_PERSON_EN + TITLE.COLON_EN + quantity + TITLE.MEN_EN;
				}else {
					if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
						titleStr = TITLE.IN_PERSON + TITLE.COLON + quantity + TITLE.MEN;
					}else {
						titleStr = "수행 인원" + TITLE.COLON + quantity + TITLE.MEN;
					}
				}
				
				//소요인원 설명 리스트
				Node childNode			= null;
				String childText			= "";
				String nodeName			= "";
				
				NodeList childList = curNode.getChildNodes();
				for (int k=0; k<childList.getLength(); k++)
				{
					childNode = childList.item(k);
					nodeName = childNode.getNodeName();
					
					//<person> 내 <text> 추출 - 소요인원만 예외적으로 <text>별로 코드 변환 (text 내 링크기능 없고 단락 끊어짐)
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						if (nodeName.equals(DTD.TEXT)) {
							childText = textParser.getTextInput(psDto, childNode).toString();
							
							if (!childText.equals("")) {
								nodeSB.append(CSS.LI);
								nodeSB.append(CHAR.MARK_DOT).append(CHAR.CHAR_SPACE);
								nodeSB.append( CodeConverter.getCodeConverter(psDto, childText, "", "") );
								nodeSB.append(CSS.LI_END);	
							}
						}
					}
				}//for end
			}
			
			rtSB.append( makeLI(DTD.IN_PERSON, titleStr, nodeSB, null, null) );
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("InputParser.getPersonHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 지원장비
	 * @MethodName	: getEquipHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 9.
	 * @ModificationHistory	: KIM K.S. / 2017 6. 27
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getEquipHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.getNodeName().equals(DTD.INPUT)) {
			return rtSB;
		}
		
		try {
			NodeList curList = XmlDomParser.getNodeListFromXPathAPI(paNode, XALAN.INXALAN_EQUIP);
			if (curList.getLength() == 0) {
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					String str = CSS.LI + TITLE.IN_EQUIP_EN + TITLE.COLON_EN + TITLE.NONE_EN + CSS.LI_END;
					return rtSB.append(str);
				}else {
					String str = CSS.LI + TITLE.IN_EQUIP + TITLE.COLON + "<span>"+TITLE.NONE+"</span>" + CSS.LI_END;
					return rtSB.append(str);
				}
			}
			
			Node curNode				= null;
			Node childNode			= null;
			String nodeName			= "";
			StringBuffer nodeSB		= new StringBuffer();
			
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				String childText = "";
				//2022 07 07 Park.J.S. Alert 인경우 처리 추가
				String typeStr = null;
				if(curNode != null && curNode.getAttributes() != null) {
					typeStr = XmlDomParser.getAttributes(curNode.getAttributes(), "type");
				}
				
				if(typeStr != null && (
						typeStr.equalsIgnoreCase("NOTE") || typeStr.equalsIgnoreCase("NOPE") || typeStr.equalsIgnoreCase("WARNING") || typeStr.equalsIgnoreCase("CAUTION") ||
						typeStr.indexOf("주의") >-1 || typeStr.indexOf("경고") > -1 || typeStr.indexOf("주") > -1
						 )) {
					ExpisCommonUtile utile = ExpisCommonUtile.getInstance();
					
					// 텍스트 노드 때문에 경고창 텍스트 미출력 오류 수정
					String xmlStr = "";
					NodeList childList = curNode.getChildNodes();
					for (int k = 0; k < childList.getLength(); k++) {
						xmlStr += utile.xmlToString(childList.item(k));
					}
					
					String alertType = "note";
					if(typeStr.indexOf("NOPE") >-1) {
						alertType = "note";
					}else if(typeStr.indexOf("NOTE") >-1) {
						alertType = "note";
					}else if(typeStr.indexOf("경고") >-1) {
						alertType = "warning";
					}else if(typeStr.indexOf("주의") >-1) {
						alertType = "caution";
					}else if(typeStr.indexOf("주") >-1) {
						alertType = "note";
					}else if(typeStr.indexOf("WARNING") >-1) {
						alertType = "warning";
					}else if(typeStr.indexOf("CAUTION") >-1) {
						alertType = "caution";
					}
					String alertStr  = "<alert id=\"ABCD"+i+new Date().getTime()+i+"ABCD\" name=\"\" type=\""+alertType+"\" itemid=\"\" ref=\"\" status=\"a\" version=\"\">";
					Node alertNode = utile.createDomTree(alertStr+xmlStr+"</alert>", 1);
					log.info("alertNodeStr : "+alertStr+xmlStr+"</alert>");
					nodeSB.append(alertParser.getAlertHtml(psDto, alertNode.getFirstChild()).toString());
				}else {
					//지원장비 리스트
					NodeList childList = curNode.getChildNodes();
					for (int k=0; k<childList.getLength(); k++)
					{
						childNode = childList.item(k);
						nodeName = childNode.getNodeName();
						
						//<equip> 내 <text> 추출
						if (childNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.TEXT)) {
							childText += textParser.getTextInput(psDto, childNode).toString();
						}
					}//for end
					
					//2022 09 22 Park.J.S. 버전처리 추가
					StringBuffer verSB = verParser.checkVersionHtml(psDto, curNode);
					String verEndStr = verParser.endVersionHtml(verSB);
					
					if (!childText.equals("")) {
						nodeSB.append(CSS.LI);
						nodeSB.append(CHAR.MARK_DOT).append(CHAR.CHAR_SPACE);
						nodeSB.append(verSB);
						nodeSB.append(verEndStr);
						nodeSB.append( CodeConverter.getCodeConverter(psDto, childText, "", "") );
						nodeSB.append(CSS.LI_END);	
					}
				}
			}
			//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
			if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
				rtSB.append( makeLI(DTD.IN_EQUIP, TITLE.IN_EQUIP_EN, nodeSB, null, null) );
			}else {
				rtSB.append( makeLI(DTD.IN_EQUIP, TITLE.IN_EQUIP, nodeSB, null, null) );
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("InputParser.getEquipHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	/**
	 * KTA 물자
	 * @MethodName	: getConsumHtmlKTA
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 9.
	 * @ModificationHistory	: KIM K.S. / 2017 6. 27
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getConsumHtmlKTA(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.getNodeName().equals(DTD.INPUT)) {
			return rtSB;
		}
		
		try {
			NodeList curList = XmlDomParser.getNodeListFromXPathAPI(paNode, XALAN.INXALAN_CONSUM);
			if (curList.getLength() == 0) {
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					String str = CSS.LI + TITLE.IN_EQUIP_EN + TITLE.COLON_EN + TITLE.NONE_EN + CSS.LI_END;
					return rtSB.append(str);
				}else {
					String str = CSS.LI + "물자" + TITLE.COLON + "<span>"+TITLE.NONE+"</span>" + CSS.LI_END;
					return rtSB.append(str);
				}
			}
			
			Node curNode				= null;
			Node childNode			= null;
			NamedNodeMap curAttr		= null;
			NamedNodeMap childAttr		= null;
			String nodeName			= "";
			StringBuffer nodeSB		= new StringBuffer();
			StringBuffer alertSB		= new StringBuffer();
			boolean isRepart			= false;
			
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				curAttr = curNode.getAttributes();
				
				//품명, 규격, 부품번호(생산자부호), 대체품, 수량
				String partname	= "";
				String govstd		= XmlDomParser.getAttributes(curAttr, ATTR.IN_GOVSTD);
				String partnum		= "";
				String cage			= "";
				String repart		= "";
				String versionId	= XmlDomParser.getAttributes(curAttr, ATTR.SYS_TOCO_VERID);
				String versionStatus= XmlDomParser.getAttributes(curAttr, ATTR.SYS_TOCO_STATUSCD);
				String contId		= XmlDomParser.getAttributes(curAttr, ATTR.SYS_ID);
				String changebasis	= XmlDomParser.getAttributes(curAttr, "changebasis");//2022 09 15 Park.J.S. ADD changebasis

				//2023 01 03 Park.J.S. Update : 버전처리 변겅
				//StringBuffer verSB = verParser.consumVersionHtml(psDto, curNode, contId, versionId, versionStatus, changebasis);
				StringBuffer verSB = verParser.checkVersionHtml(psDto, curNode);
				String verEndStr = verParser.endVersionHtml(verSB);
				
				log.info("getConsumHtmlKTA verSB : "+verSB);
				if (curAttr.getNamedItem(ATTR.IN_REPART) != null) {
					repart = XmlDomParser.getAttributes(curAttr, ATTR.IN_REPART);
					if (!repart.equals("")) {
						isRepart = true;
					}
				}
				
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					isRepart = true;
				}
				String quantity		= XmlDomParser.getAttributes(curAttr, ATTR.IN_QUANTITY);
				
				//필수교환품목
				NodeList childList = curNode.getChildNodes();
				for (int k=0; k<childList.getLength(); k++)
				{
					childNode = childList.item(k);
					childAttr = childNode.getAttributes();
					nodeName = childNode.getNodeName();
					
					//<consum> 내 <partbase> 와 <alert> 추출
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						
						if (nodeName.equals(DTD.IN_PARTBASE)) {
							
							partname	= XmlDomParser.getAttributes(childAttr, ATTR.IN_NAME);
							/*
							nodeSB.append(CSS.LI);
							nodeSB.append(CHAR.MARK_DOT).append(CHAR.CHAR_SPACE);
							nodeSB.append( CodeConverter.getCodeConverter(psDto, partname, "", "") );
							nodeSB.append(CSS.LI_END);	
							 */
							//2023 01 03 Park.J.S. Update : 버전정보 구현
							nodeSB.append(CSS.LI);
							nodeSB.append(CHAR.MARK_DOT).append(CHAR.CHAR_SPACE);
							nodeSB.append(verSB);
							nodeSB.append(verEndStr);
							nodeSB.append( CodeConverter.getCodeConverter(psDto, partname, "", "") );
							nodeSB.append(CSS.LI_END);	
							
						} else if (nodeName.equals(DTD.ALERT)) {
							alertSB.append( alertParser.getAlertHtml(psDto, childNode) );
						}
					}
				}//for end
			}
			//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
			if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
				rtSB.append( makeLI(DTD.IN_EQUIP, TITLE.IN_EQUIP_EN, nodeSB, null, null) );
			}else {
				rtSB.append( makeLI(DTD.IN_EQUIP, "물자", nodeSB, null, null) );
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("InputParser.getEquipHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}


	/**
	 * 필수교환품목 및 소모성물자
	 * @MethodName	: getConsumHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 9.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getConsumHtml(ParserDto psDto, Node paNode) {
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.getNodeName().equals(DTD.INPUT)) {
			return rtSB;
		}
		
		try {
			NodeList curList = XmlDomParser.getNodeListFromXPathAPI(paNode, XALAN.INXALAN_CONSUM);
			if (curList.getLength() == 0) {
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					String str = CSS.LI + TITLE.IN_CONSUM_EN + TITLE.COLON_EN + TITLE.NONE_EN + CSS.LI_END;
					return rtSB.append(str);
				}else {
					if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
						String str = CSS.LI + TITLE.IN_CONSUM + TITLE.COLON + "<span>"+TITLE.NONE+"</span>" + CSS.LI_END;
						return rtSB.append(str);
					}else {
						String str = CSS.LI + "물자" + TITLE.COLON + "<span>"+TITLE.NONE+"</span>" + CSS.LI_END;
						return rtSB.append(str);
						
					}
				}
			}
			
			Node curNode				= null;
			Node childNode			= null;
			NamedNodeMap curAttr		= null;
			NamedNodeMap childAttr		= null;
			String nodeName			= "";
			StringBuffer nodeSB		= new StringBuffer();
			StringBuffer alertSB		= new StringBuffer();
			boolean isRepart			= false;
			
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				curAttr = curNode.getAttributes();
				
				// 2024.02.14 - 비행타입별로 표시 추가 - jingi.kim
				if (verParser.checkVehicleType(psDto, curNode) == false) {
					continue;
				}
				
				//품명, 규격, 부품번호(생산자부호), 대체품, 수량
				String partname	= "";
				String govstd		= XmlDomParser.getAttributes(curAttr, ATTR.IN_GOVSTD);
				String partnum		= "";
				String cage			= "";
				String repart		= "";
				String versionId	= XmlDomParser.getAttributes(curAttr, ATTR.SYS_TOCO_VERID);
				String versionStatus= XmlDomParser.getAttributes(curAttr, ATTR.SYS_TOCO_STATUSCD);
				String contId		= XmlDomParser.getAttributes(curAttr, ATTR.SYS_ID);
				String changebasis	= XmlDomParser.getAttributes(curAttr, "changebasis");//2022 09 15 Park.J.S. ADD changebasis

				StringBuffer verSB = verParser.consumVersionHtml(psDto, paNode, contId, versionId, versionStatus, changebasis);
				String verEndStr = verParser.endVersionHtml(verSB);
				
				if (curAttr.getNamedItem(ATTR.IN_REPART) != null) {
					repart = XmlDomParser.getAttributes(curAttr, ATTR.IN_REPART);
					if (!repart.equals("")) {
						isRepart = true;
					}
				}
				
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					isRepart = true;
				}
				String quantity		= XmlDomParser.getAttributes(curAttr, ATTR.IN_QUANTITY);
				
				//필수교환품목
				NodeList childList = curNode.getChildNodes();
				for (int k=0; k<childList.getLength(); k++)
				{
					childNode = childList.item(k);
					childAttr = childNode.getAttributes();
					nodeName = childNode.getNodeName();
					
					//<consum> 내 <partbase> 와 <alert> 추출
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						
						if (nodeName.equals(DTD.IN_PARTBASE)) {
							
							partname	= XmlDomParser.getAttributes(childAttr, ATTR.IN_NAME);
							partnum	= XmlDomParser.getAttributes(childAttr, ATTR.IN_PARTNUM);
							cage			= XmlDomParser.getAttributes(childAttr, ATTR.IN_CAGE);
							if (!cage.equals("")) {
								partnum = partnum + "(" + cage + ")";
							}
							
							//2024.02.14 - 검색어 처리 추가 - jingi.kim
							partname = CodeConverter.getSearchKeywordIPBWC(partname, psDto.getSearchWord());
							govstd = CodeConverter.getSearchKeywordIPBWC(govstd, psDto.getSearchWord());
							partnum = CodeConverter.getSearchKeywordIPBWC(partnum, psDto.getSearchWord());
							repart = CodeConverter.getSearchKeywordIPBWC(repart, psDto.getSearchWord());
							quantity = CodeConverter.getSearchKeywordIPBWC(quantity, psDto.getSearchWord());
							
							nodeSB.append( makeConsumTR(false, isRepart, partname, govstd, partnum, repart, quantity, verSB, verEndStr) );
							
						} else if (nodeName.equals(DTD.ALERT)) {
							alertSB.append( alertParser.getAlertHtml(psDto, childNode) );
						}
					}
				}//for end
			}
			
			if (nodeSB.length() > 0) {
				StringBuffer verSB = verParser.checkVersionHtml(psDto, paNode);
				String verEndStr = verParser.endVersionHtml(verSB);
				log.info("psDto.getLanguageType() : "+psDto.getLanguageType());
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					nodeSB.insert(0, makeConsumTR(true, isRepart, TITLE.IN_CON_NAME_EN, TITLE.IN_CON_GOVSTD_EN, TITLE.IN_CON_PARTNUM_EN, TITLE.IN_CON_REPART_EN, TITLE.IN_CON_QUANTITY_EN, verSB, verEndStr) );
				}else {
					nodeSB.insert(0, makeConsumTR(true, isRepart, TITLE.IN_CON_NAME, TITLE.IN_CON_GOVSTD, TITLE.IN_CON_PARTNUM, TITLE.IN_CON_REPART, TITLE.IN_CON_QUANTITY, verSB, verEndStr) );
				}
				nodeSB.insert(0,  CSS.TB_TABLE_JG);
				nodeSB.append(CSS.TB_TABLEEND);
			}
			//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
			if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
				rtSB.append( makeLI(DTD.IN_CONSUM, TITLE.IN_CONSUM_EN, nodeSB, null, alertSB) );
			}else {
				rtSB.append( makeLI(DTD.IN_CONSUM, TITLE.IN_CONSUM, nodeSB, null, alertSB) );
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("InputParser.getConsumHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 안전요건
	 * @MethodName	: getAlertHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 9.
	 * @ModificationHistory	: KIM K.S. / 2017 6. 27
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getAlertHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.getNodeName().equals(DTD.INPUT)) {
			return rtSB;
		}
		
		try {
			NodeList curList = XmlDomParser.getNodeListFromXPathAPI(paNode, XALAN.INXALAN_ALERT);
			if (curList.getLength() == 0) {
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					String str = CSS.LI + TITLE.IN_ALERT_EN + TITLE.COLON_EN + TITLE.NONE_EN + CSS.LI_END;
					return rtSB.append(str);
				}else {
					if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
						String str = CSS.LI + TITLE.IN_ALERT + TITLE.COLON + "<span>"+TITLE.NONE+"</span>" + CSS.LI_END;
						return rtSB.append(str);
					}else {
						String str = CSS.LI + "안전 규정" + TITLE.COLON + "<span>"+TITLE.NONE+"</span>" + CSS.LI_END;
						return rtSB.append(str);
					}
				}
			}
			
			Node curNode				= null;
			String nodeName			= "";
			StringBuffer nodeSB		= new StringBuffer();
			
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				//안전요건 경고
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.ALERT)) {
						nodeSB.append( alertParser.getAlertHtml(psDto, curNode) );
					}
				}
			}
			//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
			if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
				rtSB.append( makeLI(DTD.IN_ALERT, TITLE.IN_ALERT_EN, nodeSB, null, null) );
			}else {
				if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
					rtSB.append( makeLI(DTD.IN_ALERT, TITLE.IN_ALERT, nodeSB, null, null) );
				}else{
					rtSB.append( makeLI(DTD.IN_ALERT, "안전 규정", nodeSB, null, null) );
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("InputParser.getAlertHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}


	/**
	 * 기타요건
	 * @MethodName	: getOthercondHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 9.
	 * @ModificationHistory	: KIM K.S. / 2017 6. 27
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getOthercondHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.getNodeName().equals(DTD.INPUT)) {
			return rtSB;
		}
		
		try {
			NodeList curList = XmlDomParser.getNodeListFromXPathAPI(paNode, XALAN.INXALAN_OTHERCOND);
			if (curList.getLength() == 0) {
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					String str = CSS.LI + TITLE.IN_OTHERCOND_EN + TITLE.COLON_EN + TITLE.NONE_EN + CSS.LI_END; 
					return rtSB.append(str);
				}else {
					String str = CSS.LI + TITLE.IN_OTHERCOND + TITLE.COLON + "<span>"+TITLE.NONE+"</span>" + CSS.LI_END; 
					return rtSB.append(str);
				}
				
			}
			
			Node curNode			= null;
			Node childNode			= null;
			NamedNodeMap curAttr	= null;
			String nodeName			= "";
			String linkInfo 		= "";
			String otherItem 		= "";
			StringBuffer nodeSB		= new StringBuffer();
			StringBuffer alertSB	= new StringBuffer();
			
			for(int i=0; i<curList.getLength(); i++) {
				curNode = curList.item(i);
				curAttr = curNode.getAttributes();
				
				linkInfo = linkParser.getOthercondLink(psDto, curNode);
				
				//기타요건 리스트 - 항목, 목적
				otherItem	= XmlDomParser.getAttributes(curAttr, ATTR.IN_OTH_ITEM);
				String otherDesc	= XmlDomParser.getAttributes(curAttr, ATTR.IN_OTH_DESC);
				
				//2022 09 22 Park.J.S. 버전처리 추가
				String versionId	= XmlDomParser.getAttributes(curAttr, ATTR.SYS_TOCO_VERID);
				String versionStatus= XmlDomParser.getAttributes(curAttr, ATTR.SYS_TOCO_STATUSCD);
				String contId		= XmlDomParser.getAttributes(curAttr, ATTR.SYS_ID);
				String changebasis	= XmlDomParser.getAttributes(curAttr, "changebasis");//2022 09 15 Park.J.S. ADD changebasis

				StringBuffer verSB = verParser.consumVersionHtml(psDto, paNode, contId, versionId, versionStatus, changebasis);
				
				
				if(linkInfo != "") {
					otherItem = "<a href='javascript:void' onclick=\"otherLink('" + linkInfo + "')\">" + otherItem + "</a>";
				}
				
				nodeSB.append( makeOthercondTR(false, otherItem, otherDesc, verSB.toString()) );
				
				//경고
				NodeList childList = curNode.getChildNodes();
				for (int k=0; k<childList.getLength(); k++)
				{
					childNode = childList.item(k);
					nodeName = childNode.getNodeName();
					
					//<othercond> 내 <alert> 추출
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						if (nodeName.equals(DTD.ALERT)) {
							alertSB.append( alertParser.getAlertHtml(psDto, childNode) );
						}
					}
				}//for end
			}
			
			log.info("nodeSB : " + nodeSB);
			if (nodeSB.length() > 0) {
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					nodeSB.insert(0, makeOthercondTR(true, TITLE.IN_OTH_ITEM_EN, TITLE.IN_OTH_DESC_EN, "") );
				}else {
					nodeSB.insert(0, makeOthercondTR(true, TITLE.IN_OTH_ITEM, TITLE.IN_OTH_DESC, "") );
				}
				nodeSB.insert(0,  CSS.TB_TABLE_JG);
				nodeSB.append(CSS.TB_TABLEEND);
			}
			//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
			if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
				rtSB.append( makeLI(DTD.IN_OTHERCOND, TITLE.IN_OTHERCOND_EN, nodeSB, null, alertSB) );
			}else {
				rtSB.append( makeLI(DTD.IN_OTHERCOND, TITLE.IN_OTHERCOND, nodeSB, null, alertSB) );
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("InputParser.getOthercondHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 모든 항목에 마지막으로 LI로 묶음
	 * @MethodName	: makeLI
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 10.
	 * @ModificationHistory	: 
	 * @param titleSB
	 * @param nodeSB
	 * @param alertSB
	 * @return
	 */
	public StringBuffer makeLI(String inputType, String titleStr, StringBuffer nodeSB, StringBuffer tableSB, StringBuffer alertSB) {
		
		StringBuffer rtSB		= new StringBuffer();
		StringBuffer titleSB	= new StringBuffer();
		
		titleSB.append(titleStr);
		if (!inputType.equals(DTD.IN_PERSON)) {
			titleSB.append(TITLE.COLON);
			if (nodeSB.length() == 0) {
				titleSB.append(CSS.SPAN);
				titleSB.append(TITLE.NONE);
				titleSB.append(CSS.SPAN_END);
			}
		}
		
		if (tableSB == null) {
			tableSB = new StringBuffer();
		}
		if (alertSB == null) {
			alertSB = new StringBuffer();
		}
		
		rtSB.append(CSS.LI);
		rtSB.append(titleSB);
		rtSB.append(CSS.UL);
		rtSB.append(nodeSB);
		rtSB.append(tableSB);
		rtSB.append(alertSB);
		rtSB.append(CSS.UL_END);
		rtSB.append(CSS.LI_END);
		
		return rtSB;
	}
	
	
	/**
	 * 필수교환품목에 테이블 표시시 테이블 만들어주는 메소드 <input><consum><partbase>
	 * @MethodName	: makeTRTD
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 10.
	 * @ModificationHistory	: KIM K.S. / 2017 7. 5.
	 * @param isRepart
	 * @return
	 */
	public StringBuffer makeConsumTR(boolean isTH, boolean isRepart, String val1, String val2, String val3, String val4, String val5, StringBuffer verSB, String verEndStr) {

		StringBuffer rtSB = new StringBuffer();
		
		rtSB.append(CSS.TB_TR);
		
		//준비사항 테이블 변경바 start
		if(verSB.length() <= 0) {
			rtSB.append(CSS.getTableTd("width=1% style=border:none"));
		} else {
			rtSB.append(CSS.getTableTd(verSB.toString()));
		}
		rtSB.append(CSS.TB_TDEND);
		//end
		
		rtSB.append( makeTD(isTH, val1, "30%", "1") );
		rtSB.append( makeTD(isTH, val2, "20%", "2") );
		rtSB.append( makeTD(isTH, val3, "20%", "2") );
		if (isRepart == true) {
			rtSB.append( makeTD(isTH, val4, "20%", "2") );
		}
		rtSB.append( makeTD(isTH, val5, "10%", "2") );
		
		rtSB.append(CSS.TB_TREND);
		
		return rtSB;
	}
	
	
	/**
	 * 기타요건 테이블 표시시 테이블 만들어주는 메소드 <input><othercond>
	 * @MethodName	: makeOthercondTR
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 10.
	 * @ModificationHistory	: 2022 09 22  ADD verSB
	 * @param val1
	 * @param val2
	 * @return
	 */
	public StringBuffer makeOthercondTR(boolean isTH, String val1, String val2, String verSB) {
		
		StringBuffer rtSB = new StringBuffer();
		
		rtSB.append(CSS.TB_TR);
		
		if(verSB.length() <= 0) {
			rtSB.append(CSS.getTableTd("width=1% style=border:none"));
		} else {
			rtSB.append(CSS.getTableTd(verSB.toString()));
		}
		
		rtSB.append( makeTD(isTH, val1, "50%", "1") );
		rtSB.append( makeTD(isTH, val2, "50%", "1") );
		
		rtSB.append(CSS.TB_TREND);
		
		return rtSB;
	}
	
	
	/**
	 * TD 태그 생성해주는 메소드
	 * @MethodName	: makeTD
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 10.
	 * @ModificationHistory	: 
	 * @param value
	 * @return
	 */
	public StringBuffer makeTD(boolean isTH, String value, String width, String param) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (isTH == true) {
			rtSB.append(CSS.TB_TH);
			rtSB.append(value);
			rtSB.append(CSS.TB_THEND);
		} else {
			
			width = "width='" + width + "'";
			
			if(param.equals("1")) {
				width += " style='text-align:left'";
			}
			rtSB.append( CSS.getTableTd(width) );
			rtSB.append(value);
			rtSB.append(CSS.TB_TDEND);
		}
		
		return rtSB;
	}
	
	/**
	 * <pre>
	 * 버전 팝업 전용 
	 * </pre>
	 * 필수교환품목 및 소모성물자
	 * @MethodName	: getVersionConsumHtml
	 * @AuthorDate		: 2022 09 22 Park.J.S.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getVersionConsumHtml(ParserDto psDto, Node curNode) {
		StringBuffer rtSB = new StringBuffer();
		
		try {
			Node childNode				= null;
			NamedNodeMap curAttr		= null;
			NamedNodeMap childAttr		= null;
			String nodeName				= "";
			StringBuffer nodeSB			= new StringBuffer();
			StringBuffer alertSB		= new StringBuffer();
			boolean isRepart			= false;
			curAttr 					= curNode.getAttributes();
			
			//품명, 규격, 부품번호(생산자부호), 대체품, 수량
			String partname	= "";
			String govstd		= XmlDomParser.getAttributes(curAttr, ATTR.IN_GOVSTD);
			String partnum		= "";
			String cage			= "";
			String repart		= "";

			StringBuffer verSB = new StringBuffer();;
			String verEndStr = verParser.endVersionHtml(verSB);
			
			if (curAttr.getNamedItem(ATTR.IN_REPART) != null) {
				repart = XmlDomParser.getAttributes(curAttr, ATTR.IN_REPART);
				if (!repart.equals("")) {
					isRepart = true;
				}
			}
			
			//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
			if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
				isRepart = true;
			}
			String quantity		= XmlDomParser.getAttributes(curAttr, ATTR.IN_QUANTITY);
			
			//필수교환품목
			NodeList childList = curNode.getChildNodes();
			for (int k=0; k<childList.getLength(); k++)
			{
				childNode = childList.item(k);
				childAttr = childNode.getAttributes();
				nodeName = childNode.getNodeName();
				
				//<consum> 내 <partbase> 와 <alert> 추출
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					
					if (nodeName.equals(DTD.IN_PARTBASE)) {
						
						partname	= XmlDomParser.getAttributes(childAttr, ATTR.IN_NAME);
						partnum	= XmlDomParser.getAttributes(childAttr, ATTR.IN_PARTNUM);
						cage			= XmlDomParser.getAttributes(childAttr, ATTR.IN_CAGE);
						if (!cage.equals("")) {
							partnum = partnum + "(" + cage + ")";
						}
						nodeSB.append( makeConsumTR(false, isRepart, partname, govstd, partnum, repart, quantity, verSB, verEndStr) );
						
					} else if (nodeName.equals(DTD.ALERT)) {
						alertSB.append( alertParser.getAlertHtml(psDto, childNode) );
					}
				}
			}//for end
			
			if (nodeSB.length() > 0) {
				log.info("psDto.getLanguageType() : "+psDto.getLanguageType());
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					nodeSB.insert(0, makeConsumTR(true, isRepart, TITLE.IN_CON_NAME_EN, TITLE.IN_CON_GOVSTD_EN, TITLE.IN_CON_PARTNUM_EN, TITLE.IN_CON_REPART_EN, TITLE.IN_CON_QUANTITY_EN, verSB, verEndStr) );
				}else {
					nodeSB.insert(0, makeConsumTR(true, isRepart, TITLE.IN_CON_NAME, TITLE.IN_CON_GOVSTD, TITLE.IN_CON_PARTNUM, TITLE.IN_CON_REPART, TITLE.IN_CON_QUANTITY, verSB, verEndStr) );
				}
				nodeSB.insert(0,  CSS.TB_TABLE_JG);
				nodeSB.append(CSS.TB_TABLEEND);
			}
			log.info("nodeSB : "+nodeSB);
			return nodeSB;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("InputParser.getConsumHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	/**
	 * <pre>
	 * 버전 팝업 전용
	 * </pre>
	 * 기타요건
	 * @MethodName			: getVersionOthercondHtml
	 * @AuthorDate			: 2022 09 26 Park.J.S
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getVersionOthercondHtml(ParserDto psDto, Node curNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			Node childNode			= null;
			NamedNodeMap curAttr	= null;
			String nodeName			= "";
			String linkInfo 		= "";
			String otherItem 		= "";
			StringBuffer nodeSB		= new StringBuffer();
			StringBuffer alertSB	= new StringBuffer();
			
			curAttr = curNode.getAttributes();
			
			linkInfo = linkParser.getOthercondLink(psDto, curNode);
			
			//기타요건 리스트 - 항목, 목적
			otherItem	= XmlDomParser.getAttributes(curAttr, ATTR.IN_OTH_ITEM);
			String otherDesc	= XmlDomParser.getAttributes(curAttr, ATTR.IN_OTH_DESC);
			
			//2022 09 22 Park.J.S. 버전처리 추가
			String versionId	= XmlDomParser.getAttributes(curAttr, ATTR.SYS_TOCO_VERID);
			String versionStatus= XmlDomParser.getAttributes(curAttr, ATTR.SYS_TOCO_STATUSCD);
			String contId		= XmlDomParser.getAttributes(curAttr, ATTR.SYS_ID);
			String changebasis	= XmlDomParser.getAttributes(curAttr, "changebasis");//2022 09 15 Park.J.S. ADD changebasis

			
			if(linkInfo != "") {
				otherItem = "<a href='javascript:void' onclick=\"otherLink('" + linkInfo + "')\">" + otherItem + "</a>";
			}
			
			nodeSB.append( makeOthercondTR(false, otherItem, otherDesc, "") );
			
			//경고
			NodeList childList = curNode.getChildNodes();
			for (int k=0; k<childList.getLength(); k++)
			{
				childNode = childList.item(k);
				nodeName = childNode.getNodeName();
				
				//<othercond> 내 <alert> 추출
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.ALERT)) {
						alertSB.append( alertParser.getAlertHtml(psDto, childNode) );
					}
				}
			}//for end
			
			log.info("nodeSB : " + nodeSB);
			if (nodeSB.length() > 0) {
				//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
				if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
					nodeSB.insert(0, makeOthercondTR(true, TITLE.IN_OTH_ITEM_EN, TITLE.IN_OTH_DESC_EN, "") );
				}else {
					nodeSB.insert(0, makeOthercondTR(true, TITLE.IN_OTH_ITEM, TITLE.IN_OTH_DESC, "") );
				}
				nodeSB.insert(0,  CSS.TB_TABLE_JG);
				nodeSB.append(CSS.TB_TABLEEND);
			}
			//2022 06 17 Park.J.S ADD : 영어교범 처리 추가
			if(psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType())) {
				rtSB.append( makeLI(DTD.IN_OTHERCOND, TITLE.IN_OTHERCOND_EN, nodeSB, null, alertSB) );
			}else {
				rtSB.append( makeLI(DTD.IN_OTHERCOND, TITLE.IN_OTHERCOND, nodeSB, null, alertSB) );
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("InputParser.getOthercondHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}

}
