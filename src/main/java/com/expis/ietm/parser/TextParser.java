package com.expis.ietm.parser;

import com.expis.common.ExpisCommonUtile;
import com.expis.domparser.*;
import com.expis.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * [공통모듈]텍스트(Text) Parser Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TextParser {


	private final LinkParser linkParser;
	private final VersionParser verParser;

	/**
	 * <pre>
	 * 2022 12 20 Park.J.S. Update 
	 * 특수한 경우 verParser.checkVehicleType을 스킵해야해서 추가 
	 * 기존 소스 호출 부분은 getTextPara(psDto, paNode, true)로 변환해서 요청함
	 * </pre>
	 * 
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public StringBuffer getTextPara(ParserDto psDto, Node paNode) {
		return getTextPara(psDto, paNode, true);
	}

	/**
	 * <pre>
	 * 2022 12 20 Park.J.S. Update
	 * 특수한 경우 verParser.checkVehicleType을 스킵해야해서 checkVehicleTypeFlag 추가
	 * 주의!!!! : 로그북 리포트에서만 false로 직접 호출 해야함 다른곳 호출시에 문제 발생할수 있음
	 * </pre>
	 * 
	 * <text> 파싱 - ContParser 에서 para의 텍스트 추출시 호출
	 * 
	 * @MethodName : getTextHtml
	 * @AuthorDate : LIM Y.M. / 2014. 6. 25.
	 * @ModificationHistory :
	 * @param psDto, paNode, checkVehicleTypeFlag : verParser.checkVehicleType 사용 유무
	 * @return
	 */
	public StringBuffer getTextPara(ParserDto psDto, Node paNode, boolean checkVehicleTypeFlag) {
		log.info("getTextPara checkVehicleTypeFlag : " + checkVehicleTypeFlag);
		StringBuffer rtSB = new StringBuffer();

		try {
			if (verParser.checkVehicleType(psDto, paNode) == false && checkVehicleTypeFlag) {
				return rtSB;
			}

			StringBuffer nodeSB = new StringBuffer();

			// 링크 여부에 따라 상이 처리
			NamedNodeMap paAttr = paNode.getAttributes();
			String linkType = XmlDomParser.getAttributes(paAttr, ATTR.LINK_TYPE);
			log.info("linkType : " + linkType);
			if (linkType.equals(VAL.LINK_TYPE)) {
				nodeSB.append(linkParser.getLinkHtml(psDto, paNode));

			} else {
				String cont = XmlDomParser.getTxt(paNode);
				// 태그 기호 변환
				cont = StringUtil.replace(cont, CHAR.CHAR_LT, CHAR.MARK_LT);
				cont = StringUtil.replace(cont, CHAR.CHAR_GT, CHAR.MARK_GT);
				cont = StringUtil.replace(cont, CODE.ENM_LT, CHAR.MARK_LT);
				cont = StringUtil.replace(cont, CODE.ENM_GT, CHAR.MARK_GT);
				// 머리말기호
				if (cont.indexOf(CODE.ENM_QUOT) > -1 && cont.indexOf(CODE.ENM_QUOT) < 3) {
					// 2022. 02. 16 Park.J.S : 따움표 치환처리 체크 추가
					ExpisCommonUtile ecu = ExpisCommonUtile.getInstance();
					if (ecu.checkQUOTReplace()) {
						if (cont.startsWith("")) {// "로 시작할경우
							cont = StringUtil.replace(cont, CODE.ENM_QUOT, CHAR.MARK_DOT);
						} else if (cont.indexOf("&#13;" + CODE.ENM_QUOT) > 0) {// 줄바꿈후 "일경우
							cont = StringUtil.replace(cont, "&#13;" + CODE.ENM_QUOT, "&#13;" + CHAR.MARK_DOT);
						} else if (cont.indexOf("&amp;#13;" + CODE.ENM_QUOT) > 0) {// 줄바꿈후 "일경우
							cont = StringUtil.replace(cont, "&amp;#13;" + CODE.ENM_QUOT, "&amp;#13;" + CHAR.MARK_DOT);
						} else if (cont.indexOf("<br/>" + CODE.ENM_QUOT) > 0) {// 줄바꿈후 "일경우
							cont = StringUtil.replace(cont, "<br/>" + CODE.ENM_QUOT, "<br/>" + CHAR.MARK_DOT);
						} else if (cont.indexOf("<br>" + CODE.ENM_QUOT) > 0) {// 줄바꿈후 "일경우
							cont = StringUtil.replace(cont, "<br>" + CODE.ENM_QUOT, "<br>" + CHAR.MARK_DOT);
						}
					}
				}
				// 2022 07 12 Park. J.S. : UPDATE getCodeConverter
				if (cont == null || "".equals(cont.trim())) {
					NamedNodeMap paParentAttr = paNode.getParentNode().getAttributes();
					log.info("paParentAttr Version : " + XmlDomParser.getAttributes(paParentAttr, "version"));
					if (XmlDomParser.getAttributes(paParentAttr, "version") != null
							&& !"".equals(XmlDomParser.getAttributes(paParentAttr, "version"))
							&& !"".equals(XmlDomParser.getAttributes(paParentAttr, "version").trim())) {
						cont = "&nbsp;";
					}
				}
				nodeSB.append(CodeConverter.getCodeConverter(psDto, cont, "", ""));
			}
			log.info("nodeSB1 : " + nodeSB.toString());
			StringBuffer verSB = verParser.checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.endVersionHtml(verSB);

			rtSB.append(verSB);
			rtSB.append(nodeSB);
			rtSB.append(verEndStr);
			log.info("nodeSB2 : " + nodeSB.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("TextParser.getTextPara Exception:" + ex.toString());
		}

		return rtSB;
	}

	/**
	 * <text> 파싱 - ContParser 에서 step의 텍스트 추출시 호출
	 * 
	 * @MethodName : getTextStep
	 * @AuthorDate : LIM Y.M. / 2014. 7. 22.
	 * @ModificationHistory :
	 * @param paNode
	 * @return
	 */
	public StringBuffer getTextStep(ParserDto psDto, Node paNode) {

		return getTextPara(psDto, paNode);
	}

	/**
	 * <text> 파싱 - TableParser 에서 entry의 텍스트 추출시 호출
	 * 
	 * @MethodName : getTextTable
	 * @AuthorDate : LIM Y.M. / 2014. 9. 3.
	 * @ModificationHistory :
	 * @param psDto
	 * @param paNode
	 * @return
	 */
	public StringBuffer getTextTable(ParserDto psDto, Node paNode) {

		return getTextPara(psDto, paNode);
	}

	public StringBuffer getTextGrphCaption(ParserDto psDto, Node paNode) {

		return getTextPara(psDto, paNode);
	}

	/**
	 * <text> 파싱 - InputParser 에서 텍스트 추출시 호출
	 * 
	 * @MethodName : getTextInput
	 * @AuthorDate : LIM Y.M. / 2014. 7. 22.
	 * @ModificationHistory :
	 * @param paNode
	 * @return
	 */
	public StringBuffer getTextInput(ParserDto psDto, Node paNode) {

		StringBuffer rtSB = new StringBuffer();

		try {
			if (verParser.checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}

			StringBuffer nodeSB = new StringBuffer();

			// 링크 여부에 따라 상이 처리
			NamedNodeMap paAttr = paNode.getAttributes();
			String linkType = XmlDomParser.getAttributes(paAttr, ATTR.LINK_TYPE);

			if (linkType.equals(VAL.LINK_TYPE)) {
				nodeSB.append(linkParser.getLinkHtml(psDto, paNode));

			} else {
				nodeSB.append(XmlDomParser.getTxt(paNode));
			}

			StringBuffer verSB = verParser.checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.endVersionHtml(verSB);

			rtSB.append(verSB);
			rtSB.append(nodeSB);
			rtSB.append(verEndStr);

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("TextParser.getTextInput Exception:" + ex.toString());
		}

		return rtSB;
	}

	/**
	 * <text> 파싱 - WorkcardParser 에서 텍스트 추출시 호출
	 * 
	 * @MethodName : getTextWorkcard
	 * @AuthorDate : LIM Y.M. / 2015. 1. 12.
	 * @ModificationHistory :
	 * @param paNode
	 * @return
	 */
	public StringBuffer getTextWorkcard(ParserDto psDto, Node paNode) {

		return getTextPara(psDto, paNode);
	}

	public String getCssStyle(String itemType, String contText, int nDepth) {

		String rtStr = "";

		try {
			switch (nDepth) {
			case 1:
				rtStr = " class='Chapter' ";
				break;
			case 2:
				rtStr = " class='ac-a' ";
				break;
			case 3:
				rtStr = " class='ac-div' ";
				break;
			case 4:
				rtStr = " class='' ";
				break;
			case 5:
				rtStr = " class='Introduction' ";
				break;
			default:
				rtStr = " class='ac-a' ";
				break;
			}

//			log.info("------------->getCssStyle="+rtStr);

		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("TextParser.getCssStyle Exception:" + ex.toString());
		}

		return rtStr;
	}

}
