package com.expis.domparser;

import com.expis.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.UUID;

/**
 * [Java XML Document Parser]
 */
@RequiredArgsConstructor
public class XmlDomParser {

	/**
	 * XML 문자열 데이터로 DOM 생성
	 */
	public static Document createDomTree(String xmlDoc, int xmlType) throws Exception {
		Document doc = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(true);
			DocumentBuilder dbu = dbf.newDocumentBuilder();
			dbu.setErrorHandler(new DefaultHandler());
			if (xmlType == 0) {
				doc = dbu.parse(new File(xmlDoc));
			} else if (xmlType == 1 || xmlType == 2) {
				String lang = "UTF-8";
				if (xmlType == 2) {
					lang = "EUC-KR";
				}
				byte[] bXmlDoc = xmlDoc.getBytes(lang);
				ByteArrayInputStream bais = new ByteArrayInputStream(bXmlDoc);
				InputSource is = new InputSource(bais);
				doc = dbu.parse(is.getByteStream());
			} else {
				throw new Exception("XML TYPE is no data. (" + xmlType + ")");
			}

		} catch (SAXParseException saxex) {
			System.err.println("XmlDomParser.createDomTree() Exception. id : "
					+ saxex.getSystemId() + ", line : " + saxex.getLineNumber());
			saxex.printStackTrace();
		} catch (SAXException saxex) {
			Exception e = saxex.getException();
			( (e == null) ? saxex : e).printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
		}

		return doc;
	}

	/**
	 * DOM 내 Node 에서 특정 조건의 NodeList 추출
	 */
	public static NodeList getNodeListFromXPathAPI(Node argNode, String str) {

		NodeList rtList = null;

		try {

			// XPath 객체 생성
			XPath xPath = XPathFactory.newInstance().newXPath();
			// XPath 쿼리 실행
			XPathExpression expr = xPath.compile(str);
			rtList = (NodeList) expr.evaluate(argNode, javax.xml.xpath.XPathConstants.NODESET);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtList;
	}

	/**
	 * DOM 내 Node 에서 속성인 Attributes Value 값 추출
	 */
	public static String getAttributes(NamedNodeMap argNnm, String name) {

		String rtStr = "";

		try {
			if (argNnm != null && argNnm.getNamedItem(name) != null) {
				rtStr = argNnm.getNamedItem(name).getNodeValue();
				rtStr = StringUtil.checkNull(rtStr);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtStr;
	}

	/**
	 * DOM 내 Node 에서 Text 인 값만 추출
	 */
	public static String getTxt(Node argNode) {
//		LinkParser linkParser = new LinkParser();
//		ParserDto psDto = new ParserDto();
		StringBuffer rtStr = new StringBuffer();
		String text = "";
		try {
			if (argNode.getNodeType() == Node.TEXT_NODE) {
				rtStr.append(CodeConverter.replaceQuotes(((org.w3c.dom.Text)argNode).getData()));
			} else {
				NodeList nList = argNode.getChildNodes();
				for (int i=0; i<nList.getLength(); i++) {
					text = getTxt(nList.item(i));
					rtStr.append(text);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtStr.toString();
	}

	/**
	 * 문자 치환 기능 추가
	 */
	public static String replace(String str, String n1, String n2) {
		int itmp = 0;
		if (str==null || str.equals("")) return "";

		String tmp = str;
		StringBuffer sb = new StringBuffer();
		sb.append("");
		while (tmp.indexOf(n1)>-1){
			itmp = tmp.indexOf(n1);
			sb.append(tmp.substring(0,itmp));
			sb.append(n2);
			tmp = tmp.substring(itmp+n1.length());
		}
		sb.append(tmp);
		return sb.toString();
	}

	/**
	 * DOM 내 Node 에서 특정 조건의 단일 Node 추출
	 */
	public static Node getNodeFromXPathAPI(Node argNode, String str) {
		Node rtNode = null;

		try {

			// XPath 객체 생성
			XPath xPath = XPathFactory.newInstance().newXPath();
			// XPath 쿼리 실행
			XPathExpression expr = xPath.compile(str);
			rtNode = (Node) expr.evaluate(argNode, javax.xml.xpath.XPathConstants.NODE);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtNode;
	}

	/**
	 * DOM 내 Node 에서 특정 조건의 NodeList 추출
	 * 한단계 아래까지 확인해서 추가함
	 */
	public static NodeList getNodeListAllFromXPathAPI(Node argNode, String str) {

		NodeList rtList = null;

		try {

			// XPath 객체 생성
			XPath xPath = XPathFactory.newInstance().newXPath();
			// XPath 쿼리 실행
			XPathExpression expr = xPath.compile(str);

			// XPath 표현식을 주어진 노드에 적용
			rtList = (NodeList) expr.evaluate(argNode, XPathConstants.NODESET);

			// 자식 노드들을 가져와서 재귀적으로 처리
			NodeList tempList = argNode.getChildNodes();
			for (int i = 0; i < tempList.getLength(); i++) {
				if (expr.evaluate(tempList.item(i), XPathConstants.NODESET) != null) {
					NodeList tempRtList = (NodeList) expr.evaluate(tempList.item(i), XPathConstants.NODESET);
					rtList = tempRtList;
				}
			}

		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}

		return rtList;
	}

	/**
	 * DOM 데이터를 XML 문자열로 변환
	 */
	public static String getXmlDocumentNode(Node argNode, String indent) {

		StringBuffer rtStr = new StringBuffer();
		String idAttrCheck = "";

		if (argNode == null) {
			return rtStr.toString();
		}

		try {
			String nodeName = argNode.getNodeName();

			switch (argNode.getNodeType())
			{
				case Node.ELEMENT_NODE :
					rtStr.append(CHAR.TAG_START);
					rtStr.append(nodeName);
					if (argNode.hasAttributes()) {
						NamedNodeMap nnm = argNode.getAttributes();
						for (int i=0; i<nnm.getLength(); i++) {
							Attr attr = (Attr)nnm.item(i);
							//Attribute의 name과 value 값을 넣어준다.
							rtStr.append(' ').append(attr.getName()).append('=').append(CHAR.CHAR_QUOT).append(CodeConverter.convertEntityToTag(attr.getValue())).append(CHAR.CHAR_QUOT);
							if(attr.getName().equals("id")) {
								idAttrCheck = "Y";
							}
						}
					}

					if(indent.equals("systree")) {
						if(nodeName.equals("system") && !idAttrCheck.equals("Y")) {
							rtStr.append(' ').append("id").append('=').append(CHAR.CHAR_QUOT).append(UUID.randomUUID().toString().replaceAll("-", "")).append(CHAR.CHAR_QUOT);
						}
					}

					if (!argNode.hasChildNodes()) {
						rtStr.append(CHAR.TAG_SL_END);
						break;
					} else {
						rtStr.append(CHAR.TAG_END);
						NodeList nList = argNode.getChildNodes();
						for (int i=0; i<nList.getLength(); i++) {
							rtStr.append(getXmlDocumentNode(nList.item(i), indent));
						}
					}
					rtStr.append(CHAR.TAG_SL_START).append(nodeName).append(CHAR.TAG_END);
					break;

				case Node.TEXT_NODE :
					rtStr.append(CodeConverter.convertEntityToTag(((org.w3c.dom.Text)argNode).getData()));
					break;

				default :
					break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return rtStr.toString();
	}
}
