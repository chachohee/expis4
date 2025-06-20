package com.expis.ietm.parser;

import com.expis.common.ExpisCommonUtile;
import com.expis.common.IConstants;
import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.domparser.*;
import com.expis.ietm.dto.MemoDto;
import com.expis.ietm.dto.SearchDto;
import com.expis.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * [IETM-TM]교범 내용 (페이지보기) Parser Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContParser {
	private final ObjectProvider<AlertParser> alertParser;
	private final ObjectProvider<GrphParser> grphParser;
	private final ObjectProvider<TableParser> tableParser;
	private final ObjectProvider<TextParser> textParser;
	private final ObjectProvider<IconParser> iconParser;
	private final ObjectProvider<LinkParser> linkParser;
	private final ObjectProvider<InputParser> inputParser;
	private final ObjectProvider<IPBParser> ipbParser;
	private final ObjectProvider<FaultInfoParser> fiParser;
	private final ObjectProvider<WorkcardParser> wcParser;
	private final ObjectProvider<VersionParser> verParser;
//	private final ObjectProvider<MemoComponent> memoComponent;

	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	ExternalFileEx ext = ctx.getBean("extConf",ExternalFileEx.class);

	/**
	 * Public 한 클래스 변수 선언
	 */
	//타이틀 갯수(일련번호) : 타이틀 좌측 화살표 접기/펴기 기능 위함
	//private final  long TITLE_CNT	= 0;
	public static int SECTION_CNT = 0;
	public static int TITLE_CNT	= 0;
	public static int TASK_CNT = 0;

	// String 으로 받은 xml 데이터 Dom 구조로 변경 -jsh-
	public Document parseXmlString(String xmlString) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xmlString));
		return builder.parse(is);
	}

	// system 태그 값 추출 -jsh-
	public void appendSystemTag(Element element, StringBuffer sb) {
		sb.append("<").append(element.getTagName());
		NamedNodeMap attributes = element.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attr = (Attr) attributes.item(i);
			sb.append(" ").append(attr.getName()).append("=\"").append(attr.getValue()).append("\"");
		}
		sb.append(">");
	}

	// 조건에 맞는 태그 값 추출 -jsh-
	public void appendTag(NodeList nodeList, StringBuffer sb, Predicate<Node> condition) throws Exception {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			// 조건이 없거나, 조건을 만족하는 경우만 처리
			if (condition == null || condition.test(node)) {
				String xml = nodeToString(node);
				sb.append(xml);
			}
		}
	}

    // DOM 노드를 문자열(XML 형식)로 변환 -jsh-
    public String nodeToString(Node node) {
        try {
            StringWriter sw = new StringWriter();
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // <?xml...?> 제거
            t.setOutputProperty(OutputKeys.INDENT, "no"); // 들여쓰기 없이 한줄로
            t.transform(new DOMSource(node), new StreamResult(sw));
            return sw.toString();
        } catch (TransformerException e) {
            throw new RuntimeException("XML node to string conversion failed", e);
        }
    }

	/**
	 * 교범 내용(XML DOM) 데이터를 파싱하여 문자열(HTML) 구성시에, 다중목차로 선택할 경우 eXPISMultiInfo 태그가 한번더 감싸서 처리
	 * @MethodName	: getMultiHtml
	 * @AuthorDate		: LIM Y.M. / 2016. 11. 20.
	 * @ModificationHistory	:
	 * @param psDto, paElem
	 * @return
	 */
	public StringBuffer getMultiHtml(ParserDto psDto, Element paElem) {

		StringBuffer rtSB = new StringBuffer();

		if (paElem == null) {
			return rtSB;
		}

		try {
			TITLE_CNT = 0;

			NodeList multiList = paElem.getChildNodes();

			if (multiList.getLength() > 0) {
				for (int i=0; i<multiList.getLength(); i++) {
					Node multiNode = multiList.item(i);

					if (multiNode != null) {
						Document doc = null;
						StringBuffer contSB = new StringBuffer();

						String nodeName = multiNode.getNodeName();
						String nodeType = XmlDomParser.getAttributes(multiNode.getAttributes(), ATTR.TYPE);
						contSB.append( XmlDomParser.getXmlDocumentNode(multiNode, ""));

						//20161120 edit <system>태그가 있지만, 목차의 처음이 아닌 내용중에 있는 system 일 경우에는 한번 더 감싸야함
						if ( (!nodeName.equals(DTD.EXPISROOT) && !nodeName.equals(DTD.SYSTEM))
								|| (nodeName.equals(DTD.SYSTEM) && !nodeType.equals(IConstants.STOCO_TYPE_INTRO) && !nodeType.equals(IConstants.STOCO_TYPE_CHAPTER)
										&& !nodeType.equals(IConstants.STOCO_TYPE_SECTION) && !nodeType.equals(IConstants.STOCO_TYPE_TOPIC)
										&& !nodeType.equals(IConstants.STOCO_TYPE_SUBTOPIC)) ) {
							contSB.insert(0, "<system>");
							contSB.append("</system>");
							doc = XmlDomParser.createDomTree(contSB.toString(), 1);
							multiNode = doc.getDocumentElement();
						}
					}
					
					if (multiNode.getNodeType() == Node.ELEMENT_NODE) {
						//2024.06.03 - 아이콘 표시문제 보완 - jingi.kim
						psDto.setExactIcon(true);
						rtSB.append(this.getPageHtml(psDto, (Element) multiNode, TITLE_CNT));
					}
					
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getMultiHtml Exception:"+ex.toString());
		}
		return rtSB;
	}
	
	
	/**
	 * 교범 내용(XML DOM) 데이터를 파싱하여 문자열(HTML) 구성
	 * @MethodName	: getPageHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 6. 16.
	 * @ModificationHistory	: 
	 * @param psDto, paElem, titleCnt
	 * @return
	 */
	public StringBuffer getPageHtml(ParserDto psDto, Element paElem, int titleCnt) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (paElem == null) {
			log.info("getPageHtml paElem == null return");
			return rtSB;
		}
		
		try {
			//아이콘 리스트 추출
			//2024.06.03 - 아이콘 표시문제 보완 - jingi.kim
			if ( psDto.isExactIcon() == true ) {
				psDto.setIconList( iconParser.getObject().getExactIconList(paElem) );
			} else {
				psDto.setIconList( iconParser.getObject().getIconList(paElem) );
			}
			
			int nTitleDepth = -1;
			int nSysDepth = 0;
			boolean isFault = false;
			
			//목차 제목의 접기/펴기 기능에서 고유 번호 갯수인(TITLE_CNT)를 초기화 하는걸 일부 제한
			//TITLE_CNT = 0;
			TITLE_CNT = titleCnt;
			
			//FI 내용 바로 호출시 수행 
			Node paNode = (Node) paElem;
			NamedNodeMap paAttr = paNode.getAttributes();
			String paType = XmlDomParser.getAttributes(paAttr, ATTR.TYPE);
			log.info("getPageHtml paType : "+paType);
			nTitleDepth = this.setTitleDepth(paType);
			//기본 내용 파싱 수행
			if (isFault == false) {
				
				rtSB.append( getPageHtml(psDto, paNode, nSysDepth, nTitleDepth) );
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getPageHtml Exception:"+ex.toString());
		}
		return rtSB;
	}
	
	
	/**
	 * 교범 내용(XML DOM) 데이터를 파싱하여 문자열(HTML) 구성
	 * @MethodName	: getPageHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 6. 26.
	 * @ModificationHistory	: 
	 * @param psDto, paNode, nSysDepth, nDepth
	 * @return
	 */
	public static int FRAME_CLASS_NUM = 0;
	public static boolean FRAME_BEFORE_YN = false;
	public StringBuffer getPageHtml(ParserDto psDto, Node paNode, int nSysDepth, int nDepth) {
		log.info("CALL getPageHtml : "+paNode.getNodeName()+", nDepth : "+nDepth);
		StringBuffer rtSB = new StringBuffer();
		
		try {
			Node curNode		 	= null;
			NamedNodeMap curAttr	= null;
			String nodeName			= "";
			String sysType			= "";
			String sysId			= "";
			StringBuffer nodeSB		= new StringBuffer();
			boolean isIPB			= false;
			boolean isFI			= false;
			
			nSysDepth++;
			
			NodeList curList = paNode.getChildNodes();
			
			for(int i=0; i<curList.getLength(); i++) {
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				log.info("getPageHtml["+i+"] nodeName : "+nodeName+", curNode.getNodeType() : "+curNode.getNodeType()+", nDepth : "+nDepth);
				if(curNode.getNodeType() == Node.ELEMENT_NODE) {
					curAttr = curNode.getAttributes();
					if (nodeName.equals(DTD.SYSTEM)) {
						//2024.04.29 - 도해사이즈 확대 - jingi.kim
						/*psDto.setFigureHeight("0");
						psDto.setFigureWidth("0");*/
						
						sysType = XmlDomParser.getAttributes(curAttr, ATTR.TYPE);
						sysId = XmlDomParser.getAttributes(curAttr, ATTR.ID);
						log.info("getPageHtml sysType : "+sysType+", sysId : "+sysId);
						if (sysType.equals(VAL.FI_DI_DESC)) {
							log.info("ContParser FI_DI_DEESC");
							//2024.04.29 - 도해사이즈 확대 - jingi.kim
							/*HashMap<String, String> figMap = grphParser.calculateGrphSizeWithContentSize(psDto, sysType);
							psDto.setFigureWidth(figMap.get("width"));
							psDto.setFigureHeight(figMap.get("height"));*/
							rtSB.append( fiParser.getObject().getDIHtml(psDto, curNode) );
							//isFI = true;
							continue;
						} else if (sysType.equals(VAL.FI_LR_DESC)) {
							log.info("ContParser FI_LR_DESC");
							rtSB.append( fiParser.getObject().getLRHtml(psDto, curNode) );
							//isFI = true;
							continue;
						} else if (sysType.equals(VAL.FI_FIELD)) {//2022 07 28 Park.J.S. ADD : 야전급 교범 처리 위해 추가.
							log.info("ContParser FI_FIELD");
							Node fiRootNode = XmlDomParser.getNodeFromXPathAPI(curNode, "//faultinf");
							rtSB.append( fiParser.getObject().getDDSHtml(psDto, fiRootNode) );
							//psDto.setTocoType("23");
							continue;
						} else if (sysType.equals(VAL.FI_DDS)) {
							log.info("ContParser FI_DDS");
							rtSB.append( fiParser.getObject().getDDSHtml(psDto, curNode) );
							//isFI = true;
						} else if (sysType.equals("F_WC")) {//2022 09 07 Park.J.S. ADD : 야전급 FI
							log.info("ContParser F_WC workcard 야전급");
							rtSB.append( wcParser.getObject().getWCTypeCHtml(psDto, curNode) );
							continue;
						} else {
							log.info("ContParser else");
							//2022 06 02 Park.J.S. : nDepth 주석 처리  
							//2022 06 07 Park.J.S. : 주석 처리 시에 타이틀 문제 발생 확인되서 해당부분 타이블 받는쪽에서 한번더 확인하게 수정
							//nDepth = this.setTitleDepth(sysType);
							psDto.setTocoId(sysId); //memo 시현 위함
							log.info("nSysDepth : "+nSysDepth);
							if (nSysDepth == 1) {
								//2022 06 02 Park.J.S. : 1보다 작을  경우 처리함 
								if(nDepth  < 1) {
									nDepth = 1;
								}
								rtSB.append( this.getPageHtml(psDto, curNode, nSysDepth, nDepth) );
							}
						}
						log.info("getPageHtml rtSB : "+rtSB.toString());
						
					} else if (nodeName.equals(DTD.DESC)) {
						log.info("HTML Parser DESC");
						//2024.04.29 - 도해사이즈 확대 - jingi.kim
						/*String descType = getDescTypeByToKey(psDto);
						HashMap<String, String> figMap = grphParser.calculateGrphSizeWithContentSize(psDto, descType);
						psDto.setFigureWidth(figMap.get("width"));
						psDto.setFigureHeight(figMap.get("height"));*/
						rtSB.append( this.getDescinfoHtml(psDto, curNode, nDepth) );
					} else if (nodeName.equals(DTD.TASK)) {
						log.info("HTML Parser TASK nDepth : "+nDepth);
						rtSB.append( this.getTaskHtml(psDto, curNode, nDepth) );
					} else if (nodeName.equals(DTD.IPB_PARTINFO)) {
						log.info("HTML Parser IPB_PARTINFO");
						//2024.04.29 - 도해사이즈 확대 - jingi.kim
						/*HashMap<String, String> figMap = grphParser.calculateGrphSizeWithContentSize(psDto, nodeName);
						psDto.setFigureWidth(figMap.get("width"));
						psDto.setFigureHeight(figMap.get("height"));*/
						//IPB refTocoId값 확인
						String refTocoId = XmlDomParser.getAttributes(curAttr, DTD.IPB_REFPARTINFO);
						log.info("getPageHtml refTocoId : "+refTocoId);
						if(refTocoId.equals("")) {//단일 객체
							nodeSB.append(ipbParser.getObject().getIPBHtml(psDto, curNode) );
						} else {//참조 객체가 존재할경우
//							psDto.setTocoId(refTocoId);
							nodeSB.append(ipbParser.getObject().getIPBRefHtml(psDto, curNode, refTocoId));
						}
						
						isIPB = true;
						
					} else if (nodeName.equals(DTD.FI_FAULTINF)) {
						log.info("faultinf Parser");
						rtSB.append( fiParser.getObject().getDDSFaultinfFIBHtml(psDto, curNode) );
						
					} else if (nodeName.equals(DTD.WC_WCS)) {
						log.info("workcards Parser");
						//2024.04.29 - 도해사이즈 확대 - jingi.kim
						/*HashMap<String, String> figMap = grphParser.calculateGrphSizeWithContentSize(psDto, nodeName);
						psDto.setFigureWidth(figMap.get("width"));
						psDto.setFigureHeight(figMap.get("height"));*/
						//2022 06 16 Park.J.S ADD
						wcParser.getObject().setWorkcardVariable(psDto,"");
						rtSB.append( wcParser.getObject().getWorkcardGroup(psDto, curNode) );
					} else if (nodeName.equals(DTD.WC_WORKCARD)) {
						log.info("workcard Parser");
						//2024.04.29 - 도해사이즈 확대 - jingi.kim
						/*HashMap<String, String> figMap = grphParser.calculateGrphSizeWithContentSize(psDto, nodeName);
						psDto.setFigureWidth(figMap.get("width"));
						psDto.setFigureHeight(figMap.get("height"));*/
						
						//2022 06 16 Park.J.S ADD
						wcParser.getObject().setWorkcardVariable(psDto,"");
						rtSB.append( wcParser.getObject().getWorkcardHtml(psDto, curNode) );
					} else {
						if (nSysDepth == 1) {
							log.info("HTML Parser Default");
							rtSB.append( this.getPageHtml(psDto, curNode, nSysDepth, nDepth) );
						}
					}
				}else {
					log.info("No Parser");
				}
				
			} //for end
			
			if (isIPB == true) {
				rtSB = new StringBuffer();
				rtSB.append(nodeSB);
			}
			
			/*
			if (isFI == true) {
				rtSB.append(nodeSB);
			}
			*/
			
			//목차별로 감사는 태그 (접기/펼치기 기능 위함)
			if (isIPB != true && isFI != true && nSysDepth == 1) {
				rtSB.insert(0, CSS.DIV_CONT);
				rtSB.append(CSS.DIV_END);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getPageHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	/**
	 * 교범 내용(XML DOM) Tag 확인
	 * @MethodName	: xmlTagChk
	 * @AuthorDate		: Nam. Dae. Sik / 2017. 11. 30.
	 * @ModificationHistory	: 
	 * @param psDto, paElem
	 * @return
	 */
	public int xmlTagChk(ParserDto psDto, Element paElem) {
		int result = 0;
		if(paElem == null) {
			return result;
		}
		
		try {
			Node paNode = (Node) paElem;
			Node curNode		 	= null;
			Node curChildNode = null;
					
			String nodeName		= "";
			
			NodeList curList = paNode.getChildNodes();
			for(int i=0; i<curList.getLength(); i++) {
				curNode = curList.item(i);
				
				if(curNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList curChildList = curNode.getChildNodes();
					for(int j=0; j<curChildList.getLength(); j++) {
						curChildNode = curChildList.item(j);
						nodeName = curChildNode.getNodeName();
						if(nodeName.equals(DTD.TASK)) {
							result = 1;
							break;
						}
					}
					
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.xmlTagChk Exception:"+ex.toString());
		}
		
		return result;
	}
	
	public int searchTextChk(SearchDto scDto, Element paElem) {
		int result = 0;
		if(paElem == null) {
			return result;
		}
		
		try {
			Node paNode = (Node) paElem;
			Node curNode		 	= null;
			Node curChildNode = null;
					
			String nodeName		= "";
			
			NodeList curList = paNode.getChildNodes();
			for(int i=0; i<curList.getLength(); i++) {
				curNode = curList.item(i);
				
				if(curNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList curChildList = curNode.getChildNodes();
					for(int j=0; j<curChildList.getLength(); j++) {
						curChildNode = curChildList.item(j);
						nodeName = curChildNode.getNodeName();
						if(nodeName.equals(DTD.TEXT)) {
							String cont = XmlDomParser.getTxt(paNode);
							if(cont.contains("")) {
								log.info("ContParser.searchTextChk getSearchArray:" + scDto.getSearchArray() + "");
							}
						}
					}
					
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.searchTextChk Exception:"+ex.toString());
		}
		
		return result;
	}
	
		
	/**
	 * <descinfo> 파싱
	 * @MethodName	: getDescinfoHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 6. 27.
	 * @ModificationHistory	: 
	 * @param psDto, paNode, nDepth
	 * @return
	 */
	public StringBuffer getDescinfoHtml(ParserDto psDto, Node paNode, int nDepth) {
		StringBuffer rtSB = new StringBuffer();
		if (!paNode.hasChildNodes()) {
			log.info("return rtSB : "+rtSB);
			return rtSB;
		}
		
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				log.info("checkVehicleType : false");
				return rtSB;
			}
			
			NamedNodeMap paAttr = paNode.getAttributes();
			String descType = XmlDomParser.getAttributes(paAttr, ATTR.TYPE);
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			
			for (int i=0; i<curList.getLength(); i++) {
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				//자식 노드가 있을 경우에만 실행한다.
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					
					if (nodeName.equals(DTD.PARASEQ)) {
						
						nodeSB.append( this.getParaSeqHtml(psDto, curNode, nDepth+1, descType) );
					}
				}
			}

			/*
			 * 20141126 add FI 이미지(접근및위치자료) 일 경우 <descinfo>안에 잇더라도 접기 HTML 기능 미부여
			 * //joe추후 수정요
			 * DB 등록 모듈 수정요 <descinfo type='filocationfig'>일 경우 FI교범의 접근및위치자료 임을 알수 있도록 속성 추가해야함
			 */
			boolean isFIPic = false;
			if (XmlDomParser.getXmlDocumentNode(paNode, "").indexOf("FI_PIC") > -1) {
				isFIPic = true;
			}
			
			StringBuffer contSB = new StringBuffer();
			
			/**
			 * 원본 데이터에 <descinfo type='filocationfig'>는 FI교범의 접근및위치자료 임
			 * <descinfo type=''> type='title'는 제목줄에 접기/펼치기 기능주고, type=''는 그냥 내용 데이터임 
			 */
			if (isFIPic == true) {
				TITLE_CNT++;
				contSB.append( fiParser.getObject().getAPHtml(psDto, paNode, TITLE_CNT, nodeSB.toString()) );
				
			} else if (descType.equals(VAL.TYPE_TITLE)) {
				TITLE_CNT++;
				//rtSB.append( CSS.getDivTitle(TITLE_CNT+"") );
				//2022 06 07 Park.J.S. : 들여쓰기 문제  해결위해 뎁스 확인 수정
				int nTitleDepth = nDepth;
				try {
					if(nTitleDepth <= 1) {
						nTitleDepth = this.setTitleDepth(XmlDomParser.getAttributes(paNode.getParentNode().getAttributes(), ATTR.TYPE));
						log.info("getPageHtml paType : "+XmlDomParser.getAttributes(paNode.getParentNode().getAttributes(), ATTR.TYPE)+", nTitleDepth : "+nTitleDepth+", nDepth : "+nDepth);
					}
				}catch (Exception e) {
					log.error("들여 쓰기 오류 : "+e.getMessage());
				}
				if (nTitleDepth  == 1) {
					contSB.append(CSS.DIV_CHAPTER);
				} else if (nTitleDepth  == 2) {
					contSB.append(CSS.getDivSection());
				} else if (nTitleDepth  == 3) {
					TASK_CNT = TITLE_CNT;
					contSB.append( CSS.getDivTopic() );
				} else if (nTitleDepth  == 4) {
					contSB.append( CSS.getDivSubTopic() );
					nodeSB.insert(0, CSS.SPAN_SUBTOPIC);
					nodeSB.append(CSS.SPAN_END);
				} else if (nTitleDepth  == 5) {
					contSB.append(CSS.DIV_INTRO);
				} else {
					log.info("Nake Else Title : "+nTitleDepth);
					contSB.append( CSS.getDivSubTopic() );
					nodeSB.insert(0, CSS.SPAN_SUBTOPIC);
					nodeSB.append(CSS.SPAN_END);
				}
				
				String subNode = "";
				if(nodeSB.length() > 6) {
					subNode = nodeSB.substring(nodeSB.length() - 6);
				}
				
				if(subNode.equals("</div>")) {
					contSB.append(nodeSB.insert(nodeSB.length() - 6, this.getMemoHtml(psDto, "")));
				} else {
					contSB.append(nodeSB);
					contSB.append( this.getMemoHtml(psDto, "") );
				}
				
				
				contSB.append(CSS.DIV_END);
				
			} else {
				/**
				 * 한 목차에 <descinfo type='title'> 하위에 <task> 나 <descinfo> 하나만 있는것이 일반적이지만,
				 * FI교범의 보충자료는 <descinfo type='title'><descinfo><task> 이렇게 여러개 올수 잇기에 해당 부분 처리
				 * rtSB.append( CSS.getDivDesc(TITLE_CNT+"") );
				 * rtSB.append(nodeSB);
				 * rtSB.append(CSS.DIV_END);
				 */
				Node siblingNode = XmlDomParser.getNodeFromXPathAPI(paNode, XALAN.FI_SUPPLE_PRECED);
				if (siblingNode == null) {
					contSB.append( CSS.getDivDesc() );
				}
				contSB.append(nodeSB);
				siblingNode = XmlDomParser.getNodeFromXPathAPI(paNode, XALAN.FI_SUPPLE_FOLLOW);
				if (siblingNode == null) {
					contSB.append(CSS.DIV_END);
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//2022 03 03 Park.J.S ADD : FI교범의 타이틀에 Alert이 있을 경우 처리 위해 추가
			//log.info("contSB : "+contSB.toString());
			for (int i=0; i<curList.getLength(); i++) {
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				//자식 노드가 있을 경우에만 실행한다.
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.ALERT)) {
						//log.info("alertParser.getAlertHtml(psDto, curNode) : "+alertParser.getAlertHtml(psDto, curNode));
						contSB.append( alertParser.getObject().getAlertHtml(psDto, curNode) );
					}
				}
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			StringBuffer verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.getObject().endVersionHtml(verSB);
			
			rtSB.append(verSB);
			rtSB.append(contSB);
			rtSB.append(verEndStr);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getDescinfoHtml Exception:"+ex.toString());
		}
		
		log.info("rtSB : " + rtSB.toString());
		
		return rtSB;
	}

	
	/**
	 * 
	 * @MethodName	: getParaSeqHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 6. 27.
	 * @ModificationHistory	: 
	 * @param psDto, paNode, nDepth, descType
	 * @return
	 */
	public StringBuffer getParaSeqHtml(ParserDto psDto, Node paNode, int nDepth, String descType) {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
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
						nodeSB.append( this.getParaHtml(psDto, curNode, nDepth, descType) );
					}
				}
			}
			
			StringBuffer verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.getObject().endVersionHtml(verSB);
			log.info("getParaSeqHtml verSB : "+verSB);
			rtSB.append(verSB);
			rtSB.append(nodeSB);
			log.info("getParaSeqHtml verSB : "+nodeSB.toString());
			rtSB.append(verEndStr);
			log.info("getParaSeqHtml verEndStr : "+verEndStr);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getParaSeqHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 
	 * @MethodName	: getParaHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 6. 27.
	 * @ModificationHistory	: 
	 * @param psDto, paNode, nDepth, descType
	 * @return
	 */
	public StringBuffer getParaHtml(ParserDto psDto, Node paNode, int nDepth, String descType) {
		log.info(this.getClass().getName() + " : getParaHtml");
		StringBuffer rtSB = new StringBuffer();
		
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				log.info("return");
				return rtSB;
			}
			
			Node curNode = null;
			String nodeName		= "";
			String paraText		= "";
			StringBuffer nodeSB	= new StringBuffer();
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++) {
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if((FRAME_BEFORE_YN) && !nodeName.equals(DTD.GRPH) && !nodeName.equals("#text") && !nodeName.equals("para-seq")) {
					FRAME_BEFORE_YN = false;
					FRAME_CLASS_NUM++;
				}

				if(nodeName.equals(DTD.GRPH)) {
					FRAME_BEFORE_YN = true;
				}
				
				psDto.setFrameClassNum(FRAME_CLASS_NUM);
				
				if (curNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				log.info("nodeName : "+nodeName);
				//테이블 등의 데이터 나오기전에 텍스트가 있을 경우 우선 코드 변환하여 포함시킴
				if (!nodeName.equals(DTD.TEXT) && !paraText.equals("")) {
					nodeSB.append( setParaText(psDto, descType, paraText, nDepth) );
					paraText = "";
				}
				
				log.info("nodeSB2 Start : "+nodeName);
				if (nodeName.equals(DTD.PARASEQ)) {
					nodeSB.append( this.getParaSeqHtml(psDto, curNode, (nDepth+1), descType) );
					
				} else if (nodeName.equals(DTD.ALERT)) {
					nodeSB.append( alertParser.getObject().getAlertHtml(psDto, curNode) );
					
				} else if (nodeName.equals(DTD.GRPH)) {
					//메뉴보기, 캡션 플래그 설정 필요
					nodeSB.append( grphParser.getObject().getGrphHtml(psDto, curNode, true) );
					
				} else if (nodeName.equals(DTD.TABLE)) {
					nodeSB.append( tableParser.getObject().getTableHtml(psDto, curNode, nDepth, "") );
					
				} else if (nodeName.equals(DTD.TEXT)) {
					paraText += "<span>" + textParser.getObject().getTextPara(psDto, curNode) + "</span>";
					
					//log.info("===>joe para nDepth="+nDepth+", paraText="+paraText);
				}
				log.info("nodeSB2 Fin : "+nodeSB);
			}
			
			if (!paraText.equals("")) {
				nodeSB.append( this.setParaText(psDto, descType, paraText, nDepth) );
			}
			log.info("nodeSB3 : "+nodeSB);	
			StringBuffer verSB = null;
			String verEndStr = "";
			
			if(psDto.getVersionStatus() == null) {
				log.info("Make Version Start");
				verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
				log.info("verSB : "+verSB);
				verEndStr = verParser.getObject().endVersionHtml(verSB);
				log.info("verEndStr : "+verEndStr);
				rtSB.append(verSB);
				rtSB.append(nodeSB);
				log.info("nodeSB : "+nodeSB.toString());
				rtSB.append(verEndStr);
				log.info("Make Version Fin");
			} else {
				rtSB.append(nodeSB);
			}
			log.info("rtSB4 : "+rtSB);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getParaHtml Exception:"+ex.toString());
		}
		return rtSB;
	}
	
	
	/**
	 * 
	 * @MethodName	: setParaText
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 22.
	 * @ModificationHistory	: 
	 * @param psDto, descType, paraText
	 * @return
	 */
	public StringBuffer setParaText(ParserDto psDto, String descType, String paraText, int nDepth) {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			if (!paraText.equals("")) {
				paraText = CodeConverter.getCodeConverter(psDto, paraText, "", "");
				log.info("paraText : "+paraText);
				/*
				if (nDepth > 2) {
					nDepth = nDepth - 2;
				} else if (nDepth == 2) {
					nDepth = nDepth - 1;
				}
				*/
				
				if (descType.equals(VAL.TYPE_TITLE)) {
					rtSB.append(paraText);
				} else {
					//rtSB.append(CSS.P_PARA1);
					String startDiv = CSS.getSpanPara(nDepth+"");
					
					StringBuffer addClass = new StringBuffer(startDiv);
					startDiv = addClass.toString();
					addClass.insert(13,"frame_" + psDto.getFrameClassNum() + " ");
					
					rtSB.append(startDiv);
					rtSB.append(paraText);
					rtSB.append(CSS.SPAN_END);
					//rtSB.append(CSS.P_END);
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.setParaText Exception:"+ex.toString());
		}
		
		return rtSB;
	}

	
	/**
	 * 
	 * @MethodName	: getTaskHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 6. 27.
	 * @ModificationHistory	: KIM K.S. / 2017. 6. 27.
	 * @param psDto, paNode, nDepth
	 * @return
	 */
	public StringBuffer getTaskHtml(ParserDto psDto, Node paNode, int nDepth) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (!paNode.hasChildNodes()) {
			return rtSB;
		}
		
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals(DTD.STEPSEQ)) {
						
						nodeSB.append( this.getStepSeqHtml(psDto, curNode, nDepth+1) );
						
					} else if (nodeName.equals(DTD.INPUT)) {
						nodeSB.append( inputParser.getObject().getInputHtml(psDto, curNode) );
						
					} else if (nodeName.equals(DTD.FOLLOWON)) {
						//joe 처리할 사항 있음
						nodeSB.append( this.getStepSeqHtml(psDto, curNode, nDepth) );
						if(nodeSB.length() == 0) {
							
							String starTag = CSS.P_STEP1;
							StringBuffer addClass = new StringBuffer(starTag);
							addClass.insert(10,"frame_" + psDto.getFrameClassNum() + " ");
							starTag = addClass.toString();
							
							String str = starTag + TITLE.NONE + CSS.P_END;
							return rtSB.append(str);
						}
					}
				}
			}
			
			StringBuffer contSB = new StringBuffer();
			
			/**
			 * 한 목차에 <descinfo type='title'> 하위에 <task> 나 <descinfo> 하나만 있는것이 일반적이지만,
			 * FI교범의 보충자료는 <descinfo type='title'><descinfo><task> 이렇게 여러개 올수 잇기에 해당 부분 처리
			 * rtSB.append( CSS.getDivTask(TITLE_CNT+"") );
			 * rtSB.append(nodeSB);
			 * rtSB.append(CSS.DIV_END);
			 */
			Node siblingNode = XmlDomParser.getNodeFromXPathAPI(paNode, XALAN.FI_SUPPLE_PRECED);
			if (siblingNode == null) {
				contSB.append( CSS.getDivTask() );
			}
			contSB.append(nodeSB);
			siblingNode = XmlDomParser.getNodeFromXPathAPI(paNode, XALAN.FI_SUPPLE_FOLLOW);
			if (siblingNode == null) {
				contSB.append(CSS.DIV_END);
			}
			
			StringBuffer verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.getObject().endVersionHtml(verSB);
			
			rtSB.append(verSB);
			rtSB.append(contSB);
			rtSB.append(verEndStr);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getTaskHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 
	 * @MethodName	: getStepSeqHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 6. 27.
	 * @ModificationHistory	: 
	 * @param psDto, paNode, nDepth
	 * @return
	 */
	public StringBuffer getStepSeqHtml(ParserDto psDto, Node paNode, int nDepth) {
		log.info("getStepSeqHtml nDepth : "+nDepth);
		StringBuffer rtSB = new StringBuffer();
		
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
			Node curNode = null;
//			NamedNodeMap curAttr = null;
			String nodeName = "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					log.info("getStepSeqHtml nDepth : "+nDepth);
					if (nodeName.equals(DTD.STEP)) {
						nodeSB.append( this.getStepHtml(psDto, curNode, nDepth) );
					}
				}
			}
			
			StringBuffer verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.getObject().endVersionHtml(verSB);
			
			rtSB.append(verSB);
			rtSB.append(nodeSB);
			rtSB.append(verEndStr);
						
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getStepSeqHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 
	 * @MethodName	: getStepHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 6. 27.
	 * @ModificationHistory	: 
	 * @param psDto, paNode, nDepth
	 * @return
	 */
	public StringBuffer getStepHtml(ParserDto psDto, Node paNode, int nDepth) {
		log.info(this.getClass().getName() + " : getStepHtml, nDepth : "+nDepth);
		StringBuffer rtSB = new StringBuffer();
		
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
			Node curNode		= null;
//			NamedNodeMap curAttr = null;
			String nodeName = "";
			String stepText	= "";
			String fiText	= "";
			StringBuffer nodeSB	= new StringBuffer();
			NodeList curList = paNode.getChildNodes();
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//2022 02 24 Park.J.S. : Step 자식 노드가 체크 박스 미사용하는 객체일경우 체크박스 예외처리하기위해 checkFlag 추가
			//                      하단 this.isChecklist(psDto, paNode); 기능상으로 중복되는 부분 존재함
			boolean checkFlag = false;
			for (int i=0; i<curList.getLength(); i++) {
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				log.info("FRAME_BEFORE_YN : "+FRAME_BEFORE_YN+", nodeName : "+nodeName+", DTD.GRPH) : "+DTD.GRPH);
				if((FRAME_BEFORE_YN) && !nodeName.equals(DTD.GRPH) && !nodeName.equals("#text") && !nodeName.equals("step-seq")) {
					FRAME_BEFORE_YN = false;
					FRAME_CLASS_NUM++;
				}

				if(nodeName.equals(DTD.GRPH)) {
					FRAME_BEFORE_YN = true;
				}
				
				log.info("getStepHtml : " + nodeName + "// FRAME_BEFORE_YN : " + FRAME_BEFORE_YN + "// " + FRAME_CLASS_NUM);
				
				psDto.setFrameClassNum(FRAME_CLASS_NUM);
				log.info("curNode.getNodeType() : "+curNode.getNodeType());
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					//log.info("ContParser HtmlParser-------");
					//테이블 등의 데이터 나오기전에 텍스트가 있을 경우 우선 코드 변환하여 포함시킴
					log.info("nodeName : "+nodeName);
					if (!nodeName.equals(DTD.TEXT) && !stepText.equals("")) {
						nodeSB.append( setStepText(psDto, stepText, nDepth) );
						stepText = "";
					}
					
					if (nodeName.equals(DTD.STEPSEQ)) {
						log.info("STEPSEQ ADD "+nDepth+" + 1");
						nodeSB.append( this.getStepSeqHtml(psDto, curNode, (nDepth+1)) );
					} else if (nodeName.equals(DTD.ALERT)) {
						nodeSB.append( alertParser.getObject().getAlertHtml(psDto, curNode) );
						
					} else if (nodeName.equals(DTD.GRPH)) {
						//메뉴보기, 캡션 플래그 설정 필요
						nodeSB.append( grphParser.getObject().getGrphHtml(psDto, curNode, true) );
						
					} else if (nodeName.equals(DTD.TABLE)) {//2022 10 04 Park.J.S. FI Table 처리 추가
						if("FaultStepTableNode".equalsIgnoreCase(XmlDomParser.getAttributes(curNode.getAttributes(), "nodekind"))) {
							log.info("FaultStepTableNode Start");
							fiText += fiParser.getObject().getFITableHtml(psDto, curNode);
							log.info("FaultStepTableNode Fin");
						}else {
							nodeSB.append( tableParser.getObject().getTableHtml(psDto, curNode, nDepth, "") );
						}
					} else if (nodeName.equals(DTD.TEXT)) {
						stepText += textParser.getObject().getTextStep(psDto, curNode);
						checkFlag = true;
					} else {
						log.info("Step append Else Call : "+nodeName);
						boolean addTextFILayer = false;
						boolean addTableFILayer = false;;
						if("node".equalsIgnoreCase(nodeName) && "FaultTextNode".equalsIgnoreCase(XmlDomParser.getAttributes(curNode.getAttributes(), "nodekind"))) {
							addTextFILayer	= true;
						}else if("node".equalsIgnoreCase(nodeName) && "FaultTitleNode".equalsIgnoreCase(XmlDomParser.getAttributes(curNode.getAttributes(), "nodekind"))) {
							addTextFILayer	= true;
						}else if("node".equalsIgnoreCase(nodeName) && "FaultStepTableNode".equalsIgnoreCase(XmlDomParser.getAttributes(curNode.getAttributes(), "nodekind"))) {
							addTableFILayer	= true;
						}
						log.info("addTextFILayer : "+addTextFILayer+", addTableFILayer : "+addTableFILayer);
						for (int j=0; j<curNode.getChildNodes().getLength(); j++) {
							Node subCurNode = curNode.getChildNodes().item(j);
							String subNodeName = subCurNode.getNodeName();
							if (curNode.getNodeType() == Node.ELEMENT_NODE) {
								if (subNodeName.equals(DTD.TEXT)) {
									if(addTextFILayer) {
										fiText += textParser.getObject().getTextStep(psDto, subCurNode);
									}else {
										stepText += textParser.getObject().getTextStep(psDto, subCurNode);
										checkFlag = true;
									}
								}
							}
						}
					}
				}else {
					log.info("curNode.getNodeType() Else Call ==> getNodeType : "+curNode.getNodeType()+", nodeName : "+nodeName);
					//checkFlag = false;
				}
			}
			log.info("stepText1 : "+stepText);
			log.info("nodeSB1 : "+nodeSB.toString());
			//checklist 항목 검사 코드 구현
			String checklistId = this.isChecklist(psDto, paNode);
			log.info("checklistId : "+checklistId+", paNode : "+paNode.getNodeName()+", checkFlag : "+checkFlag);
			if (!checklistId.equals("") && checkFlag) {
				// 2023.05.17 - KTA경우 경고창이 아닌 checkbox 형태로 나타나는 문제 보정, redmine #730 - jingi.kim
				boolean isNotKTACheck = true;
				if ( "KTA".equalsIgnoreCase(ext.getBizCode()) && "".equals(stepText) ) {	isNotKTACheck = false;	}
				if( isNotKTACheck ) {
					stepText = CSS.getInputChecklist(psDto.getToKey(), psDto.getTocoId(), checklistId) + stepText;
					log.info("stepText1_1 : "+stepText);
				}
			}else {//2022 08 23 체크 박스 없는 만큼 띄어쓰기 처리 부모노드가 체크박스일경우 해당
				try {
					String stepPType	= XmlDomParser.getAttributes(paNode.getParentNode().getParentNode().getAttributes(), ATTR.TYPE);
					
					if (stepPType != null && stepPType.equals(VAL.STEP_TYPE_CL)) {
						nDepth = nDepth+1;
					}else {
						//2022 10 03 Park.J.S. 부모의 부모 까지 확인하는 호직 추사
						try {
							String pStepPType	= XmlDomParser.getAttributes(paNode.getParentNode().getParentNode().getParentNode().getParentNode().getAttributes(), ATTR.TYPE);
							if (pStepPType != null && pStepPType.equals(VAL.STEP_TYPE_CL)) {
								log.info("CL Check Parent up Parent");
								nDepth = nDepth+1;
							}
						}catch (Exception e) {
							log.info("CL Check Parent up Parent Error : "+e.getMessage());
						}
					}
				}catch (Exception e) {}
				
			}
			if (!stepText.equals("")) {
				//2022 02 24 Park.J.S. : 중간에 내용 존재할경우 맨 앞에 넣기 위해서 수정
				if("".equals(nodeSB.toString())) {
					nodeSB.append( this.setStepText(psDto, stepText, nDepth) );
				}else {
					String step1 = (this.setStepText(psDto, stepText, nDepth)).toString().replace(CSS.P_END, "");
					String step2 = nodeSB.toString();
					step2 = step2.substring(step2.indexOf(">")+1,step2.length()).replace(CSS.P_END, "");
					String step3 = CSS.P_END;
					log.info("step1 : "+step1);
					log.info("step2 : "+step2);
					log.info("step3 : "+step3);
					//String tempStr = (this.setStepText(psDto, stepText, nDepth)).toString().replace(CSS.P_END, "") + nodeSB.toString() + CSS.P_END;
					String tempStr = step1 + step2 + step3;
					log.info("tempStr : "+tempStr);
					nodeSB = new StringBuffer();
					nodeSB.append(tempStr);
				}
			}
			if (!fiText.equals("")) {
				nodeSB = new StringBuffer();
				nodeSB.append("<div class=\"fi_version_div\">");
				//2022 12 23 Park.J.S. Update : 버전 정보에 불필요 특수 문자 처리하는 내용 추가(FI의 경우 javascript에서 불필요 틀수 문자 처리하는 기능 있음) 
				//nodeSB.append(fiText);
				nodeSB.append(fiText.replaceAll("&amp;#\\d{1,4};", "").replaceAll("&#\\d{1,4};", ""));
				nodeSB.append("</div>");
			}
			log.info("stepText2 : "+stepText);
			log.info("nodeSB2 : "+nodeSB.toString());
			StringBuffer verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.getObject().endVersionHtml(verSB);
			
			rtSB.append(verSB);
			rtSB.append(nodeSB);
			rtSB.append(verEndStr);
			//log.info("contparserrtsb : " + rtSB);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getStepHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 
	 * @MethodName	: setStepText
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 22.
	 * @ModificationHistory	: LIM Y.M. / 2017. 1. 23.
	 * @ModificationHistory	: PARK J.S. / 2021. 10
	 * @param psDto, stepText, nDepth
	 * @return
	 */
	public StringBuffer setStepText(ParserDto psDto, String stepText, int nDepth) {
		log.info("setStepText : "+stepText+", "+nDepth);
		StringBuffer rtSB = new StringBuffer();
		try {	
			if (!stepText.equals("")) {
				stepText = CodeConverter.getCodeConverter(psDto, stepText, "", "");
				log.info("stepText : "+stepText);
				/* 2022 06 02 Park.J.S : Lsam 수정 문제되서 주석 푸후 다른 기종 문제 대비해서 IF 문 처리 LSAM은 해당 스타일도 다름 */
				if(!"LSAM".equalsIgnoreCase(ext.getBizCode())){
					/*
					nDepth = nDepth - 1;
					if(nDepth < 0) {
						nDepth = 1;
					}
					*/
				}
				//rtSB.append(CSS.P_STEP1);
				String startDiv = CSS.getSpanStep(nDepth+"");
				StringBuffer addClass = new StringBuffer(startDiv);
				addClass.insert(10,"frame_" + psDto.getFrameClassNum() + " ");
				/**
				 * 2021 09 13 
				 * parkjs
				 * 들여쓰기 수정
				 */
				try {
					ExpisCommonUtile ecu = ExpisCommonUtile.getInstance();
					//2024.02.19 - 공백으로 시작시 오류 보정 - jingi.kim
				    if(ecu.checkLineBreak(stepText.trim())) {
						addClass.insert(10,"indent_" + nDepth + " ");
					}else {
						log.info("들여쓰기 아님 : "+stepText);
					}
				}catch (Exception e) {
					log.error("indent_ : "+e.getMessage());
				}
				startDiv = addClass.toString();
				rtSB.append(startDiv);
				rtSB.append(stepText);
				rtSB.append(CSS.P_END);
			}else {
			}
			log.info("rtSB : "+rtSB.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.setStepText Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 
	 * @MethodName	: setTitleDepth
	 * @AuthorDate		: LIM Y.M. / 2014. 6. 27.
	 * @ModificationHistory	: 
	 * @param contType
	 * @return
	 */
	public int setTitleDepth(String contType) {
		
		int rtInt = -1;
		
		try {
			contType = contType.toUpperCase();
			
			if (contType.equals(VAL.CHAPTER)) {
				rtInt = 1;
			} else if (contType.equals(VAL.SECTION)) {
				rtInt = 2;
			} else if (contType.equals(VAL.TOPIC)) {
				rtInt = 3;
			} else if (contType.equals(VAL.SUBTOPIC)) {
				rtInt = 4;
			} else if (contType.equals(VAL.INTRO)) {
				rtInt = 5;
//				rtInt = 2;
				
			} else if (contType.equals(VAL.FI_DI)) {
				rtInt = 2;
			} else if (contType.equals(VAL.FI_LR)) {
				rtInt = 2;
			} else if (contType.equals(VAL.FI_DI_DESC)) {
				//rtInt = -1;
				rtInt = 3;
			} else if (contType.equals(VAL.FI_LR_DESC)) {
				//rtInt = -1;
				rtInt = 3;
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.setTitleDepth Exception:"+ex.toString());
		}
		
		return rtInt;
	}
	
	
	/**
	 * 내용 시현 시 목차의 해당하는 메모 표시
	 * @MethodName	: getMemoHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 9. 1.
	 * @ModificationHistory	: 
	 * @param psDto, selectId
	 * @return
	 */
	public StringBuffer getMemoHtml(ParserDto psDto, String selectId) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (psDto.getMemoList() == null) {
			return rtSB;
		}
		
		try {
			String tocoId = psDto.getTocoId();
			if (!StringUtil.checkNull(selectId).equals("")) {
				tocoId = selectId;
			}

			ArrayList<MemoDto> memoList = psDto.getMemoList();
			if (memoList.size() > 0) {
				rtSB.append(" ");
						
				for (int i=0; i<memoList.size(); i++) {
					MemoDto memoDto = (MemoDto) memoList.get(i);
					
					if (psDto.getToKey().equals(memoDto.getToKey()) && tocoId.equals(memoDto.getTocoId())) {
						String memoId				= CSS.DIV_MEMO_ID + memoDto.getMemoSeq();
						String memoCont			= memoDto.getCont().replaceAll(CHAR.NEWLINE, CSS.BR);
						String createUserInfo	= memoDto.getCreateUserInfo() + " (" + memoDto.getCreateUserId() + ")";
						String createDate			= StringUtil.formattedDate(memoDto.getCreateDate(), CSS.DIV_MEMO_DATE);
						String shareYn				= memoDto.getShareYn();
						String userId				= psDto.getUserId();
						/* 2022 10 13 jysi ADD 영문화 작업 */
						String languageType             = psDto.getLanguageType();
						
						String shareType = "";
						if (shareYn.equals(IConstants.MEMOSHARE_YN_YES)) {
							if (userId.equals(memoDto.getCreateUserId())) {
								shareType = IConstants.MEMOSHARE_TYPE2;
							} else {
								shareType = IConstants.MEMOSHARE_TYPE3;
							}
						} else {
							shareType = IConstants.MEMOSHARE_TYPE1;
						}
						
						rtSB.append( CSS.getDivMemo(memoId, memoCont, createUserInfo, createDate, shareType, languageType) );
					}
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getMemoHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 경고 내용만 바로 추출시 호출
	 * @MethodName	: getAlertHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 9. 18.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getAlertHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.SYSTEM)) {
			return rtSB;
		}
		
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
			Node curNode		= null;
			String nodeName = "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.ALERT)) {
					nodeSB.append( alertParser.getObject().getAlertHtml(psDto, curNode) );
				}
			}
			
			StringBuffer verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.getObject().endVersionHtml(verSB);
			
			rtSB.append(verSB);
			rtSB.append(nodeSB);
			rtSB.append(verEndStr);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getAlertHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 그림 목차나 그림(그래픽) 내용만 바로 추출시 호출
	 * @MethodName	: getGrphHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 9. 18.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getGrphHtml(ParserDto psDto, Node paNode) {
		log.info(this.getClass().getName() + " : getGrphHtml");
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.SYSTEM)) {
			return rtSB;
		}
		
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
			Node curNode		= null;
			String nodeName = "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				log.info("curNode : "+curNode.toString());
				if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.GRPH)) {
					nodeSB.append( grphParser.getObject().getGrphHtml(psDto, curNode, true) );
				}
				log.info("nodeSB : "+nodeSB.toString());
			}
			
			StringBuffer verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.getObject().endVersionHtml(verSB);
			
			rtSB.append(verSB);
			rtSB.append(nodeSB);
			rtSB.append(verEndStr);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getGrphHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}


	/**
	 * 표 목차나 표(테이블) 내용만 바로 추출시 호출
	 * @MethodName	: getTableHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 9. 18.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getTableHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.SYSTEM)) {
			return rtSB;
		}
		
		try {
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
			Node curNode		= null;
			String nodeName = "";
			StringBuffer nodeSB	= new StringBuffer();
			
			NodeList curList = paNode.getChildNodes();
			for (int i=0; i<curList.getLength(); i++)
			{
				curNode = curList.item(i);
				nodeName = curNode.getNodeName();
				
				if (curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.TABLE)) {
					nodeSB.append( tableParser.getObject().getTableHtml(psDto, curNode, 0, "") );
				}
			}
			
			StringBuffer verSB = verParser.getObject().checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.getObject().endVersionHtml(verSB);
			
			rtSB.append(verSB);
			rtSB.append(nodeSB);
			rtSB.append(verEndStr);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getTableHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 해당 컨텐츠 내용이 CL(checklist) 인지 검사
	 * @MethodName	: isChecklist
	 * @AuthorDate		: LIM Y.M. / 2017. 1. 31.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public String isChecklist(ParserDto psDto, Node paNode) {
		
		String rtStr = "";
		
		if (paNode == null || psDto == null) {
			return rtStr;
		}
		
		try {
			String stepId		= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.ID);
			String stepType	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TYPE);
			
			if (stepType != null && stepType.equals(VAL.STEP_TYPE_CL)) {
				rtStr = stepId;
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.isChecklist Exception:"+ex.toString());
		}
		
		return rtStr;
	}
	
	/**
	 * 교범 내용(XML DOM) 데이터를 파싱하여 문자열(HTML) 구성
	 * @MethodName	: getPageHtmlXPath
	 * @AuthorDate		: jingi.kim  / 2023. 03. 10. 체크 로직 제거
	 * @ModificationHistory	: 
	 * @param psDto, paElem, titleCnt
	 * @return
	 */
	public StringBuffer getPageHtmlXPath(ParserDto psDto, Element paElem, int titleCnt) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (paElem == null) {
			log.info("getPageHtmlXPath paElem == null return");
			return rtSB;
		}
		
		try {
			
			//아이콘 리스트 추출
			psDto.setIconList( iconParser.getObject().getIconList(paElem) );
			
			int nTitleDepth = -1;
			int nSysDepth = 0;
			
			//목차 제목의 접기/펴기 기능에서 고유 번호 갯수인(TITLE_CNT)를 초기화 하는걸 일부 제한
			//TITLE_CNT = 0;
			TITLE_CNT = titleCnt;
			
			//FI 내용 바로 호출시 수행 
			Node paNode = (Node) paElem;
			NamedNodeMap paAttr = paNode.getAttributes();
			String paType = XmlDomParser.getAttributes(paAttr, ATTR.TYPE);
			log.info("getPageHtmlXPath paType : "+paType);
			nTitleDepth = this.setTitleDepth(paType);
			
			if (verParser.getObject().checkVehicleType(psDto, paNode) == false) {
				log.info("checkVehicleType : false");
				return rtSB;
			}
			
			NodeList descList = XmlDomParser.getNodeListFromXPathAPI(paNode,  "//descinfo");
			log.info("descList . length : " + descList.getLength());
			
			if (descList.getLength() > 0) {
				for (int j=0; j<descList.getLength(); j++) {
					String descName = XmlDomParser.getAttributes(descList.item(j).getAttributes(), ATTR.NAME);
					String descType = XmlDomParser.getAttributes(descList.item(j).getAttributes(), ATTR.TYPE);
					// log.info("Name : " + descName + " // type : " + descType + " ==? " + descType.equalsIgnoreCase("title"));
					
					if ( descType.equalsIgnoreCase("title") ) {
						rtSB.append( CSS.getDivTopic() );
						rtSB.insert(0, CSS.DIV_CONT);
						rtSB.append( textParser.getObject().getTextPara(psDto, descList.item(j)) );
						rtSB.append( CSS.DIV_END );
						continue;
					}
					
					NodeList tblList = XmlDomParser.getNodeListFromXPathAPI(paNode, "//table");
					if ( tblList.getLength() <= 0 ) continue;
					
					rtSB.append( CSS.getDivDesc() );
					rtSB.append( tableParser.getObject().getTableHtmlXPath(psDto, tblList.item(0), 1, "") );
					rtSB.append( CSS.DIV_END );
					
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getPageHtmlXPath Exception:"+ex.toString());
		}
		return rtSB;
	}
	
	/**
	 * 2024-04-29 - <system type="desc"> 일때, GS/JG/WD 구분을 ToKey 에서 추출 - jingi.kim
	 * TODO : regex 로
	 */
	public String getDescTypeByToKey( ParserDto psDto ) {
		String rtn = "";
		if ( "BLOCK2".equalsIgnoreCase(ext.getBizCode()) || "KTA".equalsIgnoreCase(ext.getBizCode()) ) {
			String toKey = psDto.getToKey();
			if ( toKey.length() > 9 ) {
				String spKey = toKey.substring( toKey.length() - 9, toKey.length());
				
				if ( spKey.contains("GS-") ) { rtn = "GS"; };
				if ( spKey.contains("JG-") ) { rtn = "JG"; };
				if ( spKey.contains("WD-") ) { rtn = "WD"; };
			}
		}
		return rtn;
	}
	
}
