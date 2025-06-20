package com.expis.ietm.parser;

import com.expis.common.ExpisCommonUtile;
import com.expis.common.IConstants;
import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.domparser.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;



/**
 * [공통모듈]경고창(Alert) Parser Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertParser {

//	private final TableParser tableParser;
	private final ObjectProvider<TableParser> tableParser;
	private final LinkParser linkParser;
//	private final VersionParser verParser;
	private final ObjectProvider<VersionParser> verParser;
	private final TextParser textparser;

	/**
	 * 2023 01 17 Park J.S. ADD
	 */
	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	ExternalFileEx ext = ctx.getBean("extConf",ExternalFileEx.class);


	/**
	 *
	 * @MethodName	: getAlertHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 6. 27.
	 * @ModificationHistory	: 
	 * @param paNode
	 * @return
	 */
	public StringBuffer getAlertHtml(ParserDto psDto, Node paNode) {
		StringBuffer rtSB = new StringBuffer();
		try {
			log.info("AlertParser paNode : "+ ExpisCommonUtile.getInstance().nodeToString(paNode));
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		if (!paNode.getNodeName().equals(DTD.ALERT)) {
			log.info("return getAlertHtml : "+paNode.getNodeName());
			return rtSB;
		}

		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				log.info("return getAlertHtml");
				return rtSB;
			}

			//현재 Alert(부모 노드)의 타입을 추출해서 타이틀 적용
			NamedNodeMap paAttr = paNode.getAttributes();
			String paType	= XmlDomParser.getAttributes(paAttr, ATTR.TYPE);
			String itemid	= XmlDomParser.getAttributes(paAttr, ATTR.ITEMID);
			log.info("paType : "+paType+", itemid : "+itemid+", psDto : "+psDto.getToKey());
			log.info("psDto.getToKey() : "+psDto.getToKey()+", "+psDto.getToKey().substring(psDto.getToKey().length()-2,psDto.getToKey().length()));
			String startDiv	= "";
			if (paType.equals(VAL.ALT_ALERT) || paType.equals(VAL.ALT_ALERT_CL)) {
				startDiv	= CSS.DIV_WARNING;
				if(itemid != null && "ENG".equalsIgnoreCase(itemid)) {startDiv	= CSS.DIV_WARNING_ENG;}
				if(psDto.getToKey() != null && "_E".equalsIgnoreCase(psDto.getToKey().substring(psDto.getToKey().length()-2,psDto.getToKey().length()))) {startDiv	= CSS.DIV_WARNING_ENG;}
			} else if (paType.equals(VAL.ALT_WARNING) || paType.equals(VAL.ALT_WARNING_CL)) {
				startDiv	= CSS.DIV_WARNING;
				if(itemid != null && "ENG".equalsIgnoreCase(itemid)) {startDiv	= CSS.DIV_WARNING_ENG;}
				if(psDto.getToKey() != null && "_E".equalsIgnoreCase(psDto.getToKey().substring(psDto.getToKey().length()-2,psDto.getToKey().length()))) {startDiv	= CSS.DIV_WARNING_ENG;}
			} else if (paType.equals(VAL.ALT_CAUTION) || paType.equals(VAL.ALT_CAUTION_CL)) {
				startDiv	= CSS.DIV_CAUTION;
				if(itemid != null && "ENG".equalsIgnoreCase(itemid)) {startDiv	= CSS.DIV_CAUTION_ENG;}
				if(psDto.getToKey() != null && "_E".equalsIgnoreCase(psDto.getToKey().substring(psDto.getToKey().length()-2,psDto.getToKey().length()))) {startDiv	= CSS.DIV_CAUTION_ENG;}
			} else if (paType.equals(VAL.ALT_NOTE) || paType.equals(VAL.ALT_NOTE_CL)) {
				startDiv	= CSS.DIV_NOTE;
				if(itemid != null && "ENG".equalsIgnoreCase(itemid)) {startDiv	= CSS.DIV_NOTE_ENG;}
				if(psDto.getToKey() != null && "_E".equalsIgnoreCase(psDto.getToKey().substring(psDto.getToKey().length()-2,psDto.getToKey().length()))) {startDiv	= CSS.DIV_NOTE_ENG;}
			} else if (paType.equals(VAL.ALT_WARNING_NF) || paType.equals(VAL.ALT_WARNING_CLNF)) {
				startDiv	= CSS.DIV_WARNING_NT;
				if(itemid != null && "ENG".equalsIgnoreCase(itemid)) {startDiv	= CSS.DIV_WARNING_NT_ENG;}
				if(psDto.getToKey() != null && "_E".equalsIgnoreCase(psDto.getToKey().substring(psDto.getToKey().length()-2,psDto.getToKey().length()))) {startDiv	= CSS.DIV_WARNING_NT_ENG;}
			} else if (paType.equals("warning_nf")) {//2022 07 29 Park.J.S. ADD
				startDiv	= CSS.DIV_WARNING_NT;
				if(itemid != null && "ENG".equalsIgnoreCase(itemid)) {startDiv	= CSS.DIV_WARNING_NT_ENG;}
				if(psDto.getToKey() != null && "_E".equalsIgnoreCase(psDto.getToKey().substring(psDto.getToKey().length()-2,psDto.getToKey().length()))) {startDiv	= CSS.DIV_WARNING_NT_ENG;}
			} else {
				startDiv	= CSS.DIV_NOTE;
				if(itemid != null && "ENG".equalsIgnoreCase(itemid)) {startDiv	= CSS.DIV_NOTE_ENG;}
				if(psDto.getToKey() != null && "_E".equalsIgnoreCase(psDto.getToKey().substring(psDto.getToKey().length()-2,psDto.getToKey().length()))) {startDiv	= CSS.DIV_NOTE_ENG;}
			}

			//frame
			StringBuffer addClass = new StringBuffer(startDiv);
			addClass.insert(12,"frame_" + psDto.getFrameClassNum() + " ");
			startDiv = addClass.toString();

			//경고 내 텍스트와 테이블 파싱 모듈
			Node curNode		= null;
			String nodeName	= "";
			String alertHtml	= "";
			StringBuffer nodeSB	= new StringBuffer();
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++) {
				curNode = curList.item(i);

				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					nodeName = curNode.getNodeName();
					log.info("getAlertHtml nodeName : "+nodeName);
					if (nodeName.equals(DTD.TABLE)) {
						if (alertHtml.length() > 0) {
							nodeSB.append( this.getAlertText(psDto, alertHtml) );
							alertHtml = "";
						}
						nodeSB.append( tableParser.getObject().getTableHtml(psDto, curNode, 1, "") );

					} else if (nodeName.equals(DTD.TEXT)) {
						NamedNodeMap paProp = curNode.getAttributes();
						String linkType = XmlDomParser.getAttributes(paProp, ATTR.LINK_TYPE);
						log.info("getAlertHtml linkType : "+linkType);
						if(linkType.equals(VAL.LINK_TYPE)) {
							if (verParser.getObject().checkVehicleType(psDto, curNode) == false) {
								log.info("getAlertHtml checkVehicleType false");
							}else {
								alertHtml += linkParser.getLinkHtml(psDto, curNode);
							}
						} else {
							//2022 10 06 Park.J.S. UPDATE : textparser사용으로 수정 
							alertHtml += textparser.getTextPara(psDto, curNode);
							//alertHtml += XmlDomParser.getTxt(curNode);
						}
					}

				}
			}

			if (alertHtml.length() > 0) {
				nodeSB.append( this.getAlertText(psDto, alertHtml) );
			}
			StringBuffer verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.getObject().endVersionHtml(verSB);
			//2022 10 12 Park.J.S. UPDATE : Crome 자동 설치 버전에서 span 높이 처리 안되는 문제로 수정
			//<div class="div_version"><div class="frame_0 ac-alert-warning alert_class"><span class="version_bar_update"/><div>
			String verSBStr =  verSB.toString();
			if(verSBStr.indexOf("<span") >= 0) {
				rtSB.append("<div class=\"div_version\">");
				rtSB.append(startDiv);
				rtSB.append(verSBStr.substring(25));
				rtSB.append(CSS.DIV);
				rtSB.append(nodeSB);
				rtSB.append(CSS.DIV_END);
				rtSB.append(CSS.DIV_END);
				rtSB.append(verEndStr);
			}else {
				rtSB.append(verSB);
				rtSB.append(startDiv);
				rtSB.append(CSS.DIV);
				rtSB.append(nodeSB);
				rtSB.append(CSS.DIV_END);
				rtSB.append(CSS.DIV_END);
				rtSB.append(verEndStr);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("AlertParser.getAlertHtml Exception:"+ex.toString());
		}
		return rtSB;
	}


	/**
	 *
	 * @MethodName	: getAlertText
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 12.
	 * @ModificationHistory	: 20150112 WC교범의 경고일 경우는 무조건 step(머리기호)으로 처리
	 * @param alertText
	 * @return
	 */
	public StringBuffer getAlertText(ParserDto psDto, String alertText) {

		StringBuffer rtSB = new StringBuffer();

		if (alertText == null || alertText.equals("")) {
			return rtSB;
		}

		try {
			StringBuffer nodeSB = new StringBuffer();

			//머릿기호 검사 &#13;(줄바꿈) 으로 구분하여 <p>태그로 묶음
			//String orgText = StringUtil.replace(alertText, CODE.ENM_AMP, CODE.SYMBOL_AMP);
			//String[] arrText = orgText.split(CODE.ECD_NLINE);
			//2023.03.21 jysi EDIT : <br /> 으로 구분하여 <p>태그로 묶음
			String[] arrText = alertText.split(CSS.BR);
			for (int i=0; i<arrText.length; i++) {
				String splitText = arrText[i];
				log.info("splitText1 : "+splitText);
				//nodeSB = new StringBuffer();
				String cssText = "";
				String cssTextEnd = "";

				//2023.04.26 jysi EDIT : 문장에 볼드처리 있을 경우 들여쓰기 안되는 현상 수정
				int maxLen = 3;
				if (splitText.indexOf("<b>") > -1 && splitText.indexOf("<b>") < 3) { maxLen += 3; }
				int idx = CodeConverter.getMarkIndex(splitText, CHAR.MARK_HEAD_1, maxLen);
				boolean stepFlag = false;

				if (idx >= 0 || (splitText.length() > 0 && splitText.charAt(0) == CHAR.MARK_HEAD_2)) {
					log.info("splitText set1");
					cssText = CSS.P_STEP1;
					cssTextEnd = CSS.P_END;
					stepFlag = true;
				} else if (psDto.getTocoType().equals(IConstants.TOCO_TYPE_WC)) {
					log.info("splitText set2");
					//2022 06 23 Parj.J.S. Update : •
					//splitText = CHAR.MARK_HEAD_2 + " " + splitText;
					//2023 01 17 Parj.J.S. Update : KTA WC일경우 •미사용
					if("KTA".equalsIgnoreCase(ext.getBizCode())) {
						log.info("KTA WC일경우 •미사용");
					}else {
						splitText ="• " + splitText;
					}
					cssText = CSS.P_STEP1;
					cssTextEnd = CSS.P_END;
					stepFlag = true;
				} else {
					log.info("splitText set3");
					cssText = CSS.P_PARA_ALERT;
					cssTextEnd = CSS.P_END;
				}
				log.info("splitText2 : "+splitText);
				/**
				 * 2021 09 13
				 * 2021 11 01 공통클래스로 변경 
				 * parkjs
				 * 들여쓰기 수정
				 */
				String rtStr =  CodeConverter.getCodeConverter(psDto, splitText, cssText, cssTextEnd);
				try {
					log.info("stepFlag : "+stepFlag);
					ExpisCommonUtile ecu = ExpisCommonUtile.getInstance();
					if(ecu.checkLineBreak(stepFlag, rtStr)) {
						StringBuffer addClass = new StringBuffer();
						addClass.append(CSS.P_STEP1);
						addClass.insert(10,"indent_1" + " ");
						cssText = addClass.toString();
					}else {
						log.info("들여쓰기 아님 : "+rtStr);
					}
				}catch (Exception e) {
					log.info(e.getMessage());
				}
				nodeSB.append(cssText);
				nodeSB.append(rtStr);
				nodeSB.append(CSS.P_END);
			}

			rtSB.append( nodeSB.toString() );

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("AlertParser.getAlertText Exception:"+ex.toString());
		}

		return rtSB;
	}

}
