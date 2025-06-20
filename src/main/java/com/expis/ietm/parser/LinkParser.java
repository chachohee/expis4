package com.expis.ietm.parser;

import com.expis.common.ExpisCommonUtile;
import com.expis.common.IConstants;
import com.expis.domparser.*;
import com.expis.ietm.dao.TocoInfoMapper;
import com.expis.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * [공통모듈]링크(Link) Parser Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LinkParser {

	public  static boolean  TB_CAPTION = true; //Caption(표제목) 위치 (true:표위, false:표아래)
	private final TocoInfoMapper tocoMapper;

	/**
	 * 링크 태그 파싱
	 */
	public StringBuffer getLinkHtml(ParserDto psDto, Node paNode) {
		StringBuffer rtSB = new StringBuffer();
		
		if (psDto == null || paNode == null) {
			log.info("Return null");
			return rtSB;
		}
		try {
			log.info(ExpisCommonUtile.getInstance().nodeToString(paNode));
			StringBuffer nodeSB	= new StringBuffer();
			
			/**
			 * 링크 정보 추출
			 * linktype="link" id="8afb7b6922b44d5597453168230882d6" linkid="3291ace1d606487aaf062a748b81ae55"
			 * tmname="1T-50A-2-27JG-00-1" listname="1.4 외부 전원 연결 (27-00-00)" revelationtype="목차" viewtype="self"
			 */
			NamedNodeMap paAttr = paNode.getAttributes();
			String sysId				=  XmlDomParser.getAttributes(paAttr, ATTR.ID);
			String linkRvlType			=  XmlDomParser.getAttributes(paAttr, ATTR.LINK_RVL_TP);
			String linkToKey			=  XmlDomParser.getAttributes(paAttr, ATTR.LINK_TO_KEY);
			String linkTocoId			=  XmlDomParser.getAttributes(paAttr, ATTR.LINK_TOCO_ID);
			String linkTocoNm			=  XmlDomParser.getAttributes(paAttr, ATTR.LINK_TOCO_NM);
			String linkSecId			=  XmlDomParser.getAttributes(paAttr, ATTR.LINK_SEC_ID);
			String linkViewType			=  XmlDomParser.getAttributes(paAttr, ATTR.LINK_VIEW_TP);
			log.info("linkTocoId : "+linkTocoId+", linkRvlType : "+linkRvlType);
			//멀티 링크일 경우
			if (!linkTocoId.equals("") && linkRvlType.indexOf(CHAR.LINK_REGEX_COMMA) > -1) {
				log.info("Multi Link");
				String[] arrRvlType		= StringUtil.splitStr(linkRvlType, CHAR.LINK_REGEX_COMMA);
				String[] arrToKey		= StringUtil.splitStr(linkToKey, CHAR.LINK_REGEX_COMMA);
				String[] arrTocoId		= StringUtil.splitStr(linkTocoId, CHAR.LINK_REGEX_COMMA);
				String[] arrTocoNm		= StringUtil.splitStr(linkTocoNm, CHAR.LINK_REGEX_COMMA);
				String[] arrSecId		= StringUtil.splitStr(linkSecId, CHAR.LINK_REGEX_COMMA);
				String[] arrViewType	= StringUtil.splitStr(linkViewType, CHAR.LINK_REGEX_COMMA);
				log.info("arrRvlType.length  : "+arrRvlType.length);
				String tocoName		= "";
				String secId		= "";
				String isIndex		= "";
				//String subId		= "";
				String linkSubject = XmlDomParser.getTxt(paNode);
				linkSubject = this.convertLinkSubject(linkSubject);
				//링크 못만들어도 글자는 리턴하기 위해 수정
				if(linkTocoId.replaceAll(",", "").equals("")) {
					log.info("Can not make link Data is all Emputy");
					rtSB.append( linkSubject);
					return rtSB;
				}
				for (int i=0; i<arrRvlType.length; i++) {
					log.info("Make Multi Link Start : "+i);
					if (i >= arrTocoNm.length) {
						tocoName = "";
					} else {
						tocoName = this.convertLinkSubject(arrTocoNm[i]);
					}
					String tocoId = "";
					if (i >= arrTocoId.length) {
						tocoId = "";
					} else {
						tocoId = arrTocoId[i];
					}
					//section id 값 유무에 따라 다르게 처리
					if (arrSecId.length > 0) {
						if (arrTocoId.length > arrSecId.length && i >= arrSecId.length) {
							secId = "";
						} else {
							secId = arrSecId[i];
						}
					}
					
					//CSS 스타일 다르게 주기위해 첫/마지막/중간 항목으로 구분
					if (i == 0) {
						isIndex = CSS.LINK_IDX_FIRST;
					} else if (i == (arrTocoNm.length-1)) {
						isIndex = CSS.LINK_IDX_LAST;
					} else {
						isIndex	= CSS.LINK_IDX_MIDDLE;
					}
					try {
						log.info("Make Multi Link ["+i+"] arrRvlType : "+arrRvlType.length+", arrToKey : "+arrToKey.length+", arrTocoId : "+arrTocoId.length+", arrViewType : "+arrViewType.length);
						//tocoId 비대칭적으로 설정되는 경우 있어서 수정
						nodeSB.append( this.makeHtml(sysId, arrRvlType[i], arrToKey[i], tocoId, tocoName, secId, arrViewType[i], tocoName, isIndex, tocoName) );
					}catch (Exception e) {
						e.printStackTrace();
						log.info("Make Link Error");
					}
					log.info("Make Multi Link Fin : "+i);
				}

				log.info("rtSB : "+rtSB.toString());
				rtSB.append(CSS.setLinkMultiScript(sysId, linkSubject));
				rtSB.append( nodeSB.toString() );
				// div => span으로 변경
				rtSB.append(CSS.SPAN_END).append(CSS.SPAN_END);
				log.info("Multi Link End : "+rtSB.toString());
			} else {	//단일 링크일 경우
				log.info("Single Link");
				//커맨드 테그 처리 추가
				String linkSubject = XmlDomParser.getTxt(paNode);
				linkSubject = this.convertLinkSubject(linkSubject);
				//태그 기호 변환
				linkSubject = StringUtil.replace(linkSubject, CHAR.CHAR_LT, CHAR.MARK_LT);
				linkSubject = StringUtil.replace(linkSubject, CHAR.CHAR_GT, CHAR.MARK_GT);
				linkSubject = StringUtil.replace(linkSubject, CODE.ENM_LT, CHAR.MARK_LT);
				linkSubject = StringUtil.replace(linkSubject, CODE.ENM_GT, CHAR.MARK_GT);
				//머리말기호
				if (linkSubject.indexOf(CODE.ENM_QUOT) > -1 && linkSubject.indexOf(CODE.ENM_QUOT) < 3) {
					//따움표 치환처리 체크 추가
					ExpisCommonUtile ecu = ExpisCommonUtile.getInstance();
					if(ecu.checkQUOTReplace()) {
						if(linkSubject.startsWith("")) {//"로 시작할경우
							linkSubject = StringUtil.replace(linkSubject, CODE.ENM_QUOT, CHAR.MARK_DOT);
						}else if(linkSubject.indexOf("&#13;"+CODE.ENM_QUOT) > 0) {//줄바꿈후 "일경우
							linkSubject = StringUtil.replace(linkSubject, "&#13;"+CODE.ENM_QUOT, "&#13;"+CHAR.MARK_DOT);
						}else if(linkSubject.indexOf("&amp;#13;"+CODE.ENM_QUOT) > 0) {//줄바꿈후 "일경우
							linkSubject = StringUtil.replace(linkSubject, "&amp;#13;"+CODE.ENM_QUOT, "&amp;#13;"+CHAR.MARK_DOT);
						}else if(linkSubject.indexOf("<br/>"+CODE.ENM_QUOT) > 0) {//줄바꿈후 "일경우
							linkSubject = StringUtil.replace(linkSubject, "<br/>"+CODE.ENM_QUOT, "<br/>"+CHAR.MARK_DOT);
						}else if(linkSubject.indexOf("<br>"+CODE.ENM_QUOT) > 0) {//줄바꿈후 "일경우
							linkSubject = StringUtil.replace(linkSubject, "<br>"+CODE.ENM_QUOT, "<br>"+CHAR.MARK_DOT);
						}
					}
				}
				linkSubject = CodeConverter.getCodeConverter(psDto, linkSubject, "", "");
				log.info("linkSubject : "+linkSubject);
				String isIndex	= CSS.LINK_IDX_SINGLE;
				nodeSB.append( this.makeHtml(sysId, linkRvlType, linkToKey, linkTocoId, linkSubject, linkSecId, linkViewType, linkSubject, isIndex, linkTocoNm) );
				rtSB.append( nodeSB.toString() );
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("LinkParser.getLinkHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 링크 제목(목차명) 코드 컨버트
	 */
	public String convertLinkSubject(String linkSubject) {
		
		String rtStr = "";
		
		try {
			linkSubject = StringUtil.replace(linkSubject, TITLE.LINK_GRPH_KO, TITLE.LINK_GRPH);
			linkSubject = StringUtil.replace(linkSubject, TITLE.LINK_TABLE_KO, TITLE.LINK_TABLE);
			
			linkSubject = StringUtil.replace(linkSubject, CHAR.CHAR_LT, CHAR.MARK_LT);
			linkSubject = StringUtil.replace(linkSubject, CHAR.CHAR_GT, CHAR.MARK_GT);
			linkSubject = StringUtil.replace(linkSubject, CODE.ENM_LT, CHAR.MARK_LT);
			linkSubject = StringUtil.replace(linkSubject, CODE.ENM_GT, CHAR.MARK_GT);
			if (linkSubject.indexOf(CODE.ENM_QUOT) > -1 && linkSubject.indexOf(CODE.ENM_QUOT) < 3) {
				linkSubject = StringUtil.replace(linkSubject, CODE.ENM_QUOT, CHAR.MARK_DOT);
			}
			
			rtStr = linkSubject;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("LinkParser.convertLinkSubject Exception:"+ex.toString());
		}
		
		return rtStr;
	}
	
	
	/**
	 * 링크 타입에 따라 조건 분기하여 HTML 구성
	 * @MethodName	: makeHtml
	 * @param sysId, toKey, linkName, secId
	 * @param linkId : 링크 연결된 목차 ID (목차일 경우는 tocoId, 도해/테이블은 grphId, tableId 등임)
	 * @param rvlType : 링크의 종류(TO, 목차, 도해, 테이블, IPB, RDN, OUTSIDEFILE), (필요없음 - WC, PDF)
	 * @param viewType : 뷰 타입(self, window)
	 * @param linkSubject, isIndex
	 * @param tocoNm : RDN 목차명, WC CLICK 계통 명칭
	 * @return
	 */
	public StringBuffer makeHtml(String sysId, String rvlType, String toKey, String linkId, String linkName, String secId, String viewType, String linkSubject, String isIndex, String tocoNm) {
		
		StringBuffer rtSB = new StringBuffer();
		
		//링크 못만들어도 글자는 리턴하기 위해 수정
		if (rvlType == null || rvlType.equals("")) {
			log.info("makeHtml return null can not make link");
			rtSB.append(linkName);
			return rtSB;
		}
		
		try {
			String funcName	= "";
			String funcParam = "";
			String tocoId = "";
			rvlType		= rvlType.toUpperCase();
			viewType	= viewType.toUpperCase();
			log.info("rvlType : "+rvlType);
			//링크 타입별로 Function 파라미터 다르게 정의
			if (rvlType.equals(VAL.LINK_TO)) {
				// 쿼리 돌려서  toco id 넣어
				tocoId = tocoMapper.selectTocoId(toKey);
				funcParam = "('"+toKey+"', '" + tocoId + "', '', '"+IConstants.VCONT_KIND_CONT+"', '')";
				
			} else if (rvlType.equals(VAL.LINK_TOCO)) {
				funcParam = "('"+toKey+"', '"+linkId+"', '', '"+IConstants.VCONT_KIND_CONT+"', '')";
				
			} else if (rvlType.equals(VAL.LINK_GRPH)) {

				funcParam = "('"+toKey+"', '"+secId+"', '', '"+IConstants.VCONT_KIND_GRPH+"', '"+linkId+"')";
				
			} else if (rvlType.equals(VAL.LINK_TABLE)) {

				funcParam = "('"+toKey+"', '"+secId+"', '', '"+IConstants.VCONT_KIND_TABLE+"', '"+linkId+"')";

			} else if (rvlType.equals(VAL.LINK_IPB)) {
				funcParam = "('"+toKey+"', '"+linkId+"', '', '"+IConstants.VCONT_KIND_IPB+"', '"+secId+"')";
				
			} else if (rvlType.equals(VAL.LINK_RDN)) {
				//RDN 검색시에 IPB 테이블 노란색 음영 처리위해 tocoNm 관련 추가
				log.info("---------------- IN RDN Link Start ----------------");
				log.info("sysId : "+sysId+", rvlType : "+rvlType+", toKey : "+toKey+", linkId : "+linkId+", linkName : "+linkName+", secId : "+secId+", viewType : "+viewType+", linkSubject : "+linkSubject+", isIndex : "+isIndex+", tocoNm : "+tocoNm);
				funcParam = "('"+toKey+"', '"+linkId+"', '', '"+IConstants.VCONT_KIND_IPB+"', '"+secId+"', '"+linkName.replaceAll(CSS.FONT_CC, "").replaceAll(CSS.FONT_END, "")+"', '"+tocoNm+"')";
				log.info("---------------- IN RDN Link Fin ----------------");
			} else if (rvlType.equals(VAL.LINK_FILE)) {
				funcParam = "('"+linkId+"')";
			} else if (rvlType.equals("WCLINK")) {
				funcParam = "('"+toKey+"', '"+tocoNm+"')";
			} else {
				funcParam = "('"+toKey+"', '"+linkId+"', '', '"+ IConstants.VCONT_KIND_CONT+"', '')";
			}
			
			//링크 타입별로 Function Name 다르게 정의하여 호출
			//튤팁 내용으로 변경 (title=linkname -> listname )
			if( tocoNm == null || tocoNm.equals("undefined")) {
				tocoNm = "";
			}
			
			if (rvlType.equals(VAL.LINK_FILE)) {
				funcName = "downOutsidefile";
				rtSB.append("<a href='javascript:;' class='").append(isIndex);
				rtSB.append("' onClick=\"javascript:onclickColorChange(this);" + funcName + funcParam + ";\" title='" + tocoNm + "'>" + linkName + "</a>");
				
			} else {
				if (viewType.equals(VAL.LINK_VTYPE_WIN)) {
					funcName = "viewExOpenWin";
				} else {
					funcName = "viewExContents";
				}
				
				if(rvlType.equals(VAL.LINK_RDN)) {
					funcName = "rdnOpenWin";
					rtSB.append("<a href='javascript:;' class='").append(isIndex);
					rtSB.append("' onClick=\"javascript:onclickColorChange(this);" + funcName + funcParam + ";\" title='" + tocoNm + "'>" + linkName + "</a>");
					
				} else {
					if(rvlType.equals(VAL.LINK_TO) && tocoId != null) {
						rtSB.append("<a href='javascript:;' class='").append(isIndex);
						rtSB.append("' onClick=\"javascript:onclickColorChange(this);" + funcName + funcParam + ";\" title='" + tocoNm + "'>" + linkName + "</a>");
					} else if (rvlType.equals("WCLINK")) {//2023 01 12 Park.J.S. : WC 카드 내 계통별 링크 기능 구현 
						funcName = "viewWCLink";
						rtSB.append("<a href='javascript:;' class='").append(isIndex);
						rtSB.append("' onClick=\"javascript:onclickColorChange(this);" + funcName + funcParam + ";\" title='" + tocoNm + "'>" + linkName + "</a>");
					} else {
						rtSB.append("<a href='javascript:;' class='").append(isIndex);
						rtSB.append("' onClick=\"javascript:onclickColorChange(this);" + funcName + funcParam + ";\" title='" + tocoNm + "'>" + linkName + "</a>");
					}
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("LinkParser.makeHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 기타요건 테이블 링크
	 */
	public String getOthercondLink(ParserDto psDto, Node paNode) {
		
		NodeList curList = XmlDomParser.getNodeListFromXPathAPI(paNode, XALAN.INXALAN_LINKINFO);
		Node curNode					= null;
		NamedNodeMap curAttr	= null;
		String linkInfo = "";
		
		for(int i=0; i<curList.getLength(); i++) {
			curNode = curList.item(i);
			curAttr = curNode.getAttributes();
			
			linkInfo = XmlDomParser.getAttributes(curAttr, "listname");
		}
		
		return linkInfo;
	}
	
}
