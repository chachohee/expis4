package com.expis.common;

import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.domparser.CHAR;
import com.expis.domparser.XmlDomParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;

/**
 * EXPIS UTILE 모음
 */
@Slf4j
public class ExpisCommonUtile {

	private static ExpisCommonUtile instance;
	
	/**
	 * 2022 02 17 동시에 여러 곳에서 요청할수 있어서 instance화 처리
	 * @return
	 */
	public static ExpisCommonUtile getInstance() {
		if(instance == null){
			instance =  new ExpisCommonUtile();
		}
		return instance;
	}
	
	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	ExternalFileEx ext = ctx.getBean("extConf",ExternalFileEx.class);
	
	/**
	 * <pre>
	 * 2021 11 01
	 * 들여쓰기 설정 여부 리턴
	 * 현재 설정된 규칙
	 * 1. 첫번쩨 ' '(띄어쓰기) 직전 문자가 ) 일경우
	 * 2. 첫번쩨 ' '(띄어쓰기) 직전 문자가 .(중간점) 일경우
	 * 3. 첫번쩨 '&nbsp;' 직전 문자가 ) 일경우
	 * 4. 첫번쩨 '&nbsp;' 직전 문자가 .(중간점) 일경우
	 * 5. 'ㆍ'로 시작할 경우
	 * 2022 07 25 Update
	 * 1. 첫번쩨 ' '(띄어쓰기) 직전 문자가 ) 일경우 직전 글자 안에 (가 없을때
	 * </pre>
	 * @param checkTxt
	 * @return
	 */
	public boolean checkLineBreak(String checkTxt) {
		try {
			//문단에 글 없이 줄바꿈만 여러 줄 되어있는 경우의 조건 추가
			if(checkTxt == null || "".equals(checkTxt)) {log.info("checkTxt is null"); return false;}
			//줄바꿈이 있고 해당 안에 들여쓰기가 있을경우 중복된 들여쓰기로 인식 들여 쓰기 안함 추후 문제될경우 <p> 태그로 감싸든지 추가 보완 로직 필요
			if(checkTxt.contains("<br />")) {
				String[] tempStrArry = checkTxt.split("<br />");
				for(int i=1;i<tempStrArry.length;i++) {
					if(checkLineBreak(tempStrArry[i])) {
						log.info("이중 들여쓰기 됨 : "+checkTxt);
						return false;
					}
				}
			}
			//띄어쓰기 없는 문장이 ")"로 끝날 때 제외하도록 조건 추가 => 2023.04.28 jysi EDIT : 조건 변경
			if(checkTxt.split(" ")[0].substring(checkTxt.split(" ")[0].length()-1, checkTxt.split(" ")[0].length()).equals(")") && (checkTxt.split(" ").length > 1)) {
				if(checkTxt.split(" ")[0].contains("(")) {
					//2022 10 03 Park.J.S. Update LSAM 이외에도 발견되서 전체 반영되게 로직 수정함 
					if(checkTxt.startsWith("(") && checkTxt.indexOf(")") <= 3){
						log.info("들여쓰기 설정됨 '(n)' : "+checkTxt.indexOf(")"));
						return true;
					}else {
						log.info("들여쓰기 미 설정됨 '()'안 글 : "+checkTxt.indexOf(")"));
					}
				}else {
					log.info("들여쓰기 설정됨 ')'");
					return true;
				}
			}else if(checkTxt.split(" ")[0].substring(checkTxt.split(" ")[0].length()-1, checkTxt.split(" ")[0].length()).equals(".")) {
				//영어단에 혹은 약어에 . 붙는 경우 들여쓰기 막기 위해 예외 처리 추가 ex)MK. KR-16LF 사출 좌석은 지상 수평 정지 상태(0 ft, 0 kt)에 AA. AB. 등 문제 발생할 경우 예외 처리 필요 => 현재 까지 해당 요건 KTA 에서만 존재 함
				if( checkTxt.split(" ")[0].length() > 2 && checkTxt.substring(0,checkTxt.split(" ")[0].length()-1).matches("^[a-zA-Z]*$")) {
					log.info("들여쓰기 아님 : "+checkTxt);
				}else {
					log.info("들여쓰기 설정됨 '.'");
					return true;
				}
			}else if(checkTxt.indexOf("&nbsp;") > 0 && checkTxt.split("&nbsp;")[0].substring(checkTxt.split("&nbsp;")[0].length()-1, checkTxt.split("&nbsp;")[0].length()).equals(")")) {
				log.info("들여쓰기 설정됨 ')&nbsp;'");
				return true;
			//문장 끝에 .&nbsp; 가 있는 경우 들여쓰기 하지않도록 조건 추가
			//split[0]가 글머리기호가 아닌 경우 들여쓰기 하지 않도록 길이제한 추가
			}else if(checkTxt.indexOf("&nbsp;") > 0 && checkTxt.split(".&nbsp;")[0].length()<3 && checkTxt.split("&nbsp;")[0].substring(checkTxt.split("&nbsp;")[0].length()-1, checkTxt.split("&nbsp;")[0].length()).equals(".") && (checkTxt.lastIndexOf(".&nbsp;")+7)!=checkTxt.length()) {
				log.info("들여쓰기 설정됨 '.&nbsp;'");
				return true;
			}else if(checkTxt.startsWith("ㆍ")) {
				log.info("들여쓰기 설정됨 startsWith 'ㆍ'");
				return true;
			}else {
				/**
				 * 2022 09 29 BLOCK2의 경우 치환 처리되는 '"' 관련 들여쓰기 설정
				 */
				String firstMark = CHAR.MARK_DOT + "&nbsp;";
				if("BLOCK2".equalsIgnoreCase(ext.getBizCode()) && checkTxt.indexOf(firstMark) == 0) {
					log.info("들여쓰기 설정됨 Block2 firstMark '"+firstMark+"'");
					return true;
				}
				log.info("들여쓰기 아님 : "+checkTxt);
				
			}
		}catch (Exception e) {
			log.error("checkLineBreak error : "+e.getMessage());
		}
		return false;
	}
	/**
	 * <pre>
	 * 들여쓰기 설정 여부 리턴 AlertParser 에서 stepFlag 사용함으로 함수 추가 실질적으로는 상단 함수[checkLineBreak(String checkTxt)] 사용중
	 * </pre>
	 * @param stepFlag
	 * @param checkTxt
	 * @return
	 */
	public boolean checkLineBreak(boolean stepFlag, String checkTxt) {
		if(!stepFlag) {
			return false;
		}else {
			return checkLineBreak(checkTxt);
		}
	}
	
	/**
	 * <pre>
	 * 아이콘 정보가 상위 객체에 있어서 아이콘 관련 정보를 받아오지 못할경우 상위 객체의 아이콘 정보를 찾아서 자신에게 추가 해서 리턴
	 * </pre> 
	 * @param domElement 원래 아이콘 정보를 가지고 있는 최상의 객체
	 * @param inNode 아이콘 정보를 추가로 담을 객체
	 * @param imgPathFlag 아이콘정보 이미지 경로 풀경로 처리 여부
	 */
	public Node setIconElement(Element domElement, Node inNode, boolean imgPathFlag) {
		try{
			NodeList list = inNode.getChildNodes();
			for(int i=0;i<list.getLength();i++){
				NodeList textNodeList = XmlDomParser.getNodeListFromXPathAPI(list.item(i), ".//text[contains(text(),'&#24;') or contains(text(),'&amp;#24;')]");
				for (int k=0; k<textNodeList.getLength(); k++) {
					Node textNode = textNodeList.item(k);
					String iconText = XmlDomParser.getTxt(textNode);
					iconText = XmlDomParser.replace(iconText, "&amp;", "&");
					if (!iconText.equals("")) {
						String[] arrText = iconText.split(";");
						for (int m=0; m<arrText.length; m++) {
							if (arrText[m].trim().equals("&#24") && m < (arrText.length-1)) {
								String iconid = XmlDomParser.replace(arrText[m+1],"&#","");
								Node iconNode = XmlDomParser.getNodeFromXPathAPI(domElement, ".//icon[@iconid='"+iconid+"']");
								if (iconNode != null) {
									if(imgPathFlag) {
										((Element)iconNode).setAttribute("filename",ext.getDF_IETMDATA()+((Element)iconNode).getAttribute("filename"));
										inNode.appendChild(iconNode);
									}else {
										inNode.appendChild(iconNode);
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			log.info("Icon Node Add Exception : "+e.getMessage());
		}
		return inNode;
	}

	/**
	 * <pre>
	 * String or File Path to Document
	 * </pre>
	 * @param xmlDoc
	 * @param xmlType 0 : XML 문서가 FILE에 있는 경우, 1 : XML 문서가 BUFFER에 있는 경우(UTF-8), 2 : XML 문서가 BUFFER에 있는 경우(euc-kr)
	 * @return
	 * @throws Exception
	 */
	public Document createDomTree(String xmlDoc, int xmlType) throws  Exception {
		Document doc = null;
		
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.
			newInstance();
			docBuilderFactory.setValidating(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			docBuilder.setErrorHandler(new DefaultHandler());
			
			if (xmlType==0) {		// XML 문서가 FILE에 있는 경우
				doc = docBuilder.parse(new File(xmlDoc));
			}else if (xmlType==1) {	// XML 문서가 BUFFER에 있는 경우
				byte[] xmlDocByte = xmlDoc.getBytes("UTF-8");
					ByteArrayInputStream bais = new ByteArrayInputStream(xmlDocByte);
					InputSource inputSource = new InputSource(bais);
					doc = docBuilder.parse(inputSource.getByteStream());
				}
			else if (xmlType==2) {	// XML 문서가 BUFFER에 있는 경우
				byte[] xmlDocByte = xmlDoc.getBytes("euc-kr");
				ByteArrayInputStream bais = new ByteArrayInputStream(xmlDocByte);
				InputSource inputSource = new InputSource(bais);
				doc = docBuilder.parse(inputSource.getByteStream());
			}else {
				throw new Exception("해당 xmlType이 존재하지 않습니다. (" + xmlType + ")");
			}
		}catch (Exception e) {
			log.info("createDomTree Error : " + e.getMessage());
		}
		
		return doc;
	}
	/**
	 * <pre>
	 * '"'를 '•'로 치환해야 하는지 판단용
	 * 현재 webpath.properties BIZ_CODE 기준 T50, FA50, BLOCK2 만 치환중
	 * </pre>
	 * @return
	 */
	public boolean checkQUOTReplace() {
		if ("T50".equalsIgnoreCase(ext.getBizCode()) || "FA50".equalsIgnoreCase(ext.getBizCode()) || "BLOCK2".equalsIgnoreCase(ext.getBizCode())) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * <pre>
	 * FI 다이어그램을 메뉴로 표시할지 바로 보일지 판단하해서 리턴
	 * 현재 webpath.properties BIZ_CODE 기준 T50, FA50, BLOCK2 만 치환중
	 * </pre>
	 * @return
	 */
	public boolean checkFiMenuView() {
		if ("T50".equalsIgnoreCase(ext.getBizCode()) || "FA50".equalsIgnoreCase(ext.getBizCode()) || "BLOCK2".equalsIgnoreCase(ext.getBizCode())) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 파일을 XML 로 읽어 DOM으로 반환
	 */
	public Element getSystemInfoDom(String sysFilePath) {
		
		Document doc = null;
		Element rtElem = null;
		
		try {
			if (sysFilePath == null || sysFilePath.equals("")) {
				return rtElem;
			}
			
			File sysFile = new File(sysFilePath);
			if (sysFile.exists() == true) {
				doc = XmlDomParser.createDomTree(sysFilePath, 0);
				rtElem = doc.getDocumentElement();
			}
		}  catch (Exception ex) {
			ex.printStackTrace();
			log.info("getSystemInfoDom Exception:"+ex.toString());
		} finally {
		}
		
		return rtElem;
	}

	/**
	 * <pre>
	 * xml을 String 으로 변화 하기위해 추가
	 * </pre>
	 * @param node
	 * @return
	 */
	public String xmlToString(Node node) {
		TransformerFactory tft = TransformerFactory.newInstance();
		StringWriter sw = new StringWriter();
		try {
			Transformer tsf = tft.newTransformer();
			tsf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			tsf.transform(new DOMSource(node) ,new StreamResult(sw));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} 
		return sw.getBuffer().toString();
	}
	
	public static void main(String[] args) {
		String testStr ="a. Pressure gauge - 20~30 psi.&#13;b. Mask Leakage 점검 - TEST MASK 선택.&#13;c. Flow Blinker 점검 - Black and White., 3";
		ExpisCommonUtile test = new ExpisCommonUtile();
		test.checkLineBreak(testStr);
	}
	
	/**
	 * Node 로깅 하기 위해 추가
	 * @param node
	 * @return
	 * @throws TransformerException
	 */
	public static String nodeToString(Node node) throws TransformerException {
	    StringWriter buf = new StringWriter();
	    Transformer xform = TransformerFactory.newInstance().newTransformer();
	    xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    xform.transform(new DOMSource(node), new StreamResult(buf));
	    return(buf.toString());
	}

	/**
	 * String replace 안되는 경우 많아서 subString 사용하는 함수 별도 추가 코드 미완성
	 * @param str         : 대상 문자열
	 * @param findStr     : 찾아야하는 문자열
	 * @param replaceStr  : 바꾸는 문자열
	 * @return
	 */
	public static String replaceStrUseSubString(String str, String findStr, String replaceStr) {
		return str;
	}
}
