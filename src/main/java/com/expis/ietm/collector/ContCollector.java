package com.expis.ietm.collector;

import com.expis.common.IConstants;
import com.expis.domparser.ATTR;
import com.expis.domparser.DTD;
import com.expis.domparser.XmlDomParser;
import com.expis.ietm.dao.*;
import com.expis.ietm.dto.XContDto;
import com.expis.ietm.parser.ContParser;
import com.expis.ietm.parser.ParserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import java.util.ArrayList;

/**
 * [IETM-TM]교범 내용 Collector Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContCollector {

	private final XContAllMapper allMapper;
	private final XContAlertMapper alertMapper;
	private final XContGraphicMapper grphMapper;
	private final XContTableMapper tableMapper;
	private final XContWCMapper wcMapper;
	private final ContParser contParser;

	// fi xml 파싱 -jsh-
	public String fiParser(XContDto contDto, ParserDto psDto) throws Exception{
		StringBuffer rtSB = new StringBuffer();

		// 1. Xml 문자열 생성
		String xcont = generateXmlFromXCont(contDto);
		if (xcont == null || xcont.isBlank()){
			return "";
		}

		// 2. 문자열 -> Document 변환
		Document xmlDoc = contParser.parseXmlString(xcont);

		// 3. system 태그 추가
		NodeList systemList = xmlDoc.getElementsByTagName("system");
		Element systemElement = (Element) systemList.item(0);
		contParser.appendSystemTag(systemElement, rtSB);

		// 4. desinfo 태그 추가
		NodeList descList = xmlDoc.getElementsByTagName("descinfo");
		contParser.appendTag(descList, rtSB, null);

		// 5. faultinf 태그 추가
		NodeList faultList  = xmlDoc.getElementsByTagName("faultinf");
		contParser.appendTag(faultList, rtSB, node -> {
			String fiId = XmlDomParser.getAttributes(node.getAttributes(), "id");
			return fiId != null && fiId.equals(psDto.getTocoId());
		});

		rtSB.append("</system>");
		return rtSB.toString();
	}

	// xml문서 String으로 변환 -jsh-
	public String generateXmlFromXCont(XContDto contDto) throws Exception {
		Element rtElem = null;
		StringBuffer contSB = new StringBuffer();
		ArrayList<XContDto> contList = allMapper.selectListDao(contDto);

		if (contList != null && contList.size() > 0) {
			for (XContDto rsDto : contList) {
				contSB.append(rsDto.getXcont());
			}
		}

		if (contSB.length() > 0){
			final Document dom = XmlDomParser.createDomTree(contSB.toString(), 1);
			rtElem = dom.getDocumentElement();

			return contParser.nodeToString(rtElem);
		}
		return null;
	}

	/**
	 * 교범 내용 XML 데이터 받아서 DOM으로 반환
	 */
	public Element getAllContDom(XContDto contDto) {
		Document doc = null;
		Element rtElem = null;
		StringBuffer contSB = new StringBuffer();

		try {
			ArrayList<XContDto> paramList = new ArrayList<XContDto>();
			ArrayList<XContDto> contList = new ArrayList<XContDto>();

			//1. DB에서 데이터 추출하는 MyBatis의 DAO 처리 모듈 호출 -> Contents 추출 -> DOM으로 생성
			if (contDto.getOutputMode().equals(IConstants.OPT_OUMODE_MULTI)) {
				if(contDto.getVehicleType() == null) {
					contDto.setVehicleType("");
				}
				paramList = allMapper.selectMultiListDao(contDto);

				//2024.11.18 - Type에 맞는 데이터만 추출하도록 보완 - jingi.kim
				for(int i=0; i<paramList.size(); i++) {
					if(paramList.get(i).getTocoVehicleType().equals("1")) {
						contList.add(paramList.get(i));
					}
				}
				log.info("contList : "+contList);
				//20170110 add WC교범의 장/절 클릭시 하위 작업카드 호출이 필요한 경우
				ArrayList<XContDto> wcList = wcMapper.selectAllMultiListDao(contDto);
				log.info("wcList : "+wcList.size());
				if (wcList.size() > 0) {
					paramList = new ArrayList<XContDto>();
					contList = new ArrayList<XContDto>();
					paramList = allMapper.selectWCMultiListDao(contDto);
					
					//2024.11.18 - Type에 맞는 데이터만 추출하도록 보완 - jingi.kim
					for(int i=0; i<paramList.size(); i++) {
						if(paramList.get(i).getTocoVehicleType().equals("1")) {
							contList.add(paramList.get(i));
						}
					}
					contList.addAll(wcList);
				}
			} else {
				contList = allMapper.selectListDao(contDto);
			}
			log.info("contList : "+contList);
			if (contList != null && contList.size() > 0) {
				for (XContDto rsDto : contList) {
					contSB.append(rsDto.getXcont());
				}
			}
			log.info("contSB.append : "+contSB.toString());
			if (contSB.length() > 0) {
				if (contDto.getOutputMode().equals(IConstants.OPT_OUMODE_MULTI)) {
					contSB.insert(0, "<eXPISMultiInfo>");
					contSB.append("</eXPISMultiInfo>");
					doc = XmlDomParser.createDomTree(contSB.toString(), 1);
					rtElem = doc.getDocumentElement();
					
				} else {
					doc = XmlDomParser.createDomTree(contSB.toString(), 1);
					rtElem = doc.getDocumentElement();
					
					if (rtElem != null) {
						
						String nodeName = rtElem.getNodeName();
						String nodeType = XmlDomParser.getAttributes(rtElem.getAttributes(), ATTR.TYPE);

						//20161025 delete system태그 붙이는것 제거, 20161101 edit 조건 추가하면서 되살림
						//20161120 edit <system>태그가 있지만, 목차의 처음이 아닌 내용중에 있는 system 일 경우에는 한번 더 감싸야함
						if ( (!nodeName.equals(DTD.EXPISROOT) && !nodeName.equals(DTD.SYSTEM))
								|| (nodeName.equals(DTD.SYSTEM) && !nodeType.equals(IConstants.STOCO_TYPE_INTRO) && !nodeType.equals(IConstants.STOCO_TYPE_CHAPTER)
										&& !nodeType.equals(IConstants.STOCO_TYPE_SECTION) && !nodeType.equals(IConstants.STOCO_TYPE_TOPIC)
										&& !nodeType.equals(IConstants.STOCO_TYPE_SUBTOPIC)) ) {
							contSB.insert(0, "<system>");
							contSB.append("</system>");
							log.info("contSB : " + contSB);
							doc = XmlDomParser.createDomTree(contSB.toString(), 1);
							
							rtElem = doc.getDocumentElement();
						}
					}
				}
			}else {
				log.info("Else : "+rtElem);
			}
		}  catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContCollector.getAllContDom Exception:"+ex.toString());
		} finally {
			
		}
		
		return rtElem;
	}
	
	/**
	 * 교범 내용 XML 데이터 받아서 DOM으로 반환(순차적으로반환)
	 * @MethodName	: getStepAllContDom
	 * @AuthorDate		: KIM K.S. / 2018. 6. 4
	 * @ModificationHistory	: 
	 * @return
	 */
	public Element getStepAllContDom(XContDto contDto, String returnData) {
		Document doc = null;
		Element rtElem = null;
		StringBuffer contSB = new StringBuffer();
		
		try {
			contSB.append(returnData);
			
			if (contSB.length() > 0) {
				if (contDto.getOutputMode().equals(IConstants.OPT_OUMODE_MULTI)) {
					contSB.insert(0, "<eXPISMultiInfo>");
					contSB.append("</eXPISMultiInfo>");
					doc = XmlDomParser.createDomTree(contSB.toString(), 1);
					rtElem = doc.getDocumentElement();
					
				} else {
					doc = XmlDomParser.createDomTree(contSB.toString(), 1);
					rtElem = doc.getDocumentElement();
					
					if (rtElem != null) {
						
						String nodeName = rtElem.getNodeName();
						String nodeType = XmlDomParser.getAttributes(rtElem.getAttributes(), ATTR.TYPE);

						if ( (!nodeName.equals(DTD.EXPISROOT) && !nodeName.equals(DTD.SYSTEM))
								|| (nodeName.equals(DTD.SYSTEM) && !nodeType.equals(IConstants.STOCO_TYPE_INTRO) && !nodeType.equals(IConstants.STOCO_TYPE_CHAPTER)
										&& !nodeType.equals(IConstants.STOCO_TYPE_SECTION) && !nodeType.equals(IConstants.STOCO_TYPE_TOPIC)
										&& !nodeType.equals(IConstants.STOCO_TYPE_SUBTOPIC)) ) {
							contSB.insert(0, "<system>");
							contSB.append("</system>");
							doc = XmlDomParser.createDomTree(contSB.toString(), 1);
							
							rtElem = doc.getDocumentElement();
						}
					}
				}
			}
		}  catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContCollector.getAllContDom Exception:"+ex.toString());
		} finally {
			
		}
		
		return rtElem;
	}
	
	
	/**
	 * 교범 내용 중 경고 XML 데이터 받아서 DOM으로 반환
	 * @MethodName	: getAlertContDom
	 * @AuthorDate		: LIM Y.M. / 2014. 9.17
	 * @ModificationHistory	: 
	 * @return
	 */
	public Element getAlertContDom(XContDto contDto) {
		
		Document doc = null;
		Element rtElem = null;
		StringBuffer contSB = new StringBuffer();
		
		try {
			ArrayList<XContDto> contList = alertMapper.selectListDao(contDto);
			if (contList != null && contList.size() > 0) {
				for (XContDto rsDto : contList) {
					contSB.append(rsDto.getXcont());
				}
			}
			
			if (contSB.length() > 0) {
				contSB.insert(0, "<system>");
				contSB.append("</system>");
				doc = XmlDomParser.createDomTree(contSB.toString(), 1);
				rtElem = doc.getDocumentElement();
			}
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContCollector.getAlertContDom Exception:"+ex.toString());
		} finally {
			
		}
		
		return rtElem;
	}
	
	
	/**
	 * 교범 내용 중 그림(그래픽) XML 데이터 받아서 DOM으로 반환
	 * @MethodName	: getGrphContDom
	 * @AuthorDate		: LIM Y.M. / 2014. 9.17
	 * @ModificationHistory	: 
	 * @return
	 */
	public Element getGrphContDom(XContDto contDto) {
		
		Document doc = null;
		Element rtElem = null;
		StringBuffer contSB = new StringBuffer();
		
		try {
			//2023 02 07 Park.J.S. ADD : fileOrgName 처음은 없이 조회 
			String fileOrgName = contDto.getFileOrgName();
			contDto.setFileOrgName(null);
			log.info("fileOrgName : "+fileOrgName);
			ArrayList<XContDto> contList = grphMapper.selectListDao(contDto);
			if (contList != null && contList.size() > 0) {
				for (XContDto rsDto : contList) {
					contSB.append(rsDto.getXcont());
				}
			//2023 02 07 Park.J.S. ADD : 못찾을 경우 파일 명으로 명시해서 다시 찾기(중복인 경우 존재할수 있어서 Toco ID 사용 했으나 못찾는 경우 발생해서 추가함) 
			}else {
				String tocoId = contDto.getTocoId();
				log.info("못찾을 경우 파일 명으로 명시해서 다시 찾기 : "+fileOrgName +", "+tocoId);
				contDto.setTocoId(null);
				contDto.setFileOrgName(fileOrgName);
				contList = grphMapper.selectListDao(contDto);
				if (contList != null && contList.size() > 0) {
					for (XContDto rsDto : contList) {
						contSB.append(rsDto.getXcont());
					}
				}
				contDto.setTocoId(tocoId);
			}
			
			if (contSB.length() > 0) {
				contSB.insert(0, "<system>");
				contSB.append("</system>");
				doc = XmlDomParser.createDomTree(contSB.toString(), 1);
				rtElem = doc.getDocumentElement();
			}
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContCollector.getGrphContDom Exception:"+ex.toString());
		} finally {
			
		}
		
		return rtElem;
	}
	
	
	/**
	 * 교범 내용 중 표(테이블) XML 데이터 받아서 DOM으로 반환
	 * @MethodName	: getTableContDom
	 * @AuthorDate		: LIM Y.M. / 2014. 9.17
	 * @ModificationHistory	: 
	 * @return
	 */
	public Element getTableContDom(XContDto contDto) {
		
		Document doc = null;
		Element rtElem = null;
		StringBuffer contSB = new StringBuffer();
		
		try {
			ArrayList<XContDto> contList = tableMapper.selectListDao(contDto);
			if (contList != null && contList.size() > 0) {
				for (XContDto rsDto : contList) {
					contSB.append(rsDto.getXcont());
				}
			}
			
			if (contSB.length() > 0) {
				contSB.insert(0, "<system>");
				contSB.append("</system>");
				doc = XmlDomParser.createDomTree(contSB.toString(), 1);
				rtElem = doc.getDocumentElement();
			}
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContCollector.getTableContDom Exception:"+ex.toString());
		} finally {
			
		}
		
		return rtElem;
	}
	
	
	/**
	 * 교범 내용 XML 데이터 받아서 DOM으로 반환
	 * @MethodName	: getWCContDom
	 * @AuthorDate		: LIM Y.M. / 2014. 12. 30
	 * @ModificationHistory	: 
	 * @return
	 */
	public Element getWCContDom(XContDto contDto) {
		
		Document doc = null;
		Element rtElem = null;
		StringBuffer contSB = new StringBuffer();
		
		try {
			ArrayList<XContDto> contList = null;
			if (contDto.getOutputMode().equals(IConstants.OPT_OUMODE_MULTI)) {
				contList = wcMapper.selectMultiListDao(contDto);
			} else {
				contList = wcMapper.selectListDao(contDto);
			}
			
			if (contList != null && contList.size() > 0) {
				for (XContDto rsDto : contList) {
					contSB.append(rsDto.getXcont());
				}
			}
			
			if (contSB.length() > 0) {
				contSB.insert(0, "<system>");
				contSB.append("</system>");
				doc = XmlDomParser.createDomTree(contSB.toString(), 1);
				rtElem = doc.getDocumentElement();
			}
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContCollector.getWCContDom Exception:"+ex.toString());
		} finally {
			
		}
		
		return rtElem;
	}

}
