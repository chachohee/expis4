package com.expis.util;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * [Java XML Document Parser]
 *
 * @FileName			: XmlDomParser.java
 */
public class XmlDomParser {

	public XmlDomParser() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * XML 문자열 데이터로 DOM 생성
	 *
	 * @MethodName	: createDomTree
	 * @param xmlDoc
	 * @param xmlType (0 : XML 문서가 File 에 있는 경우,  1 : Buffer 에 있는 경우,  2 : Buffer 에 있는 경우)
	 * @return
	 * @throws Exception
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
//
//	public static Document createDomText(String xmlDoc, int xmlType) throws Exception {
//		Document doc = null;
//
//		try {
//			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//			dbf.setValidating(true);
//			DocumentBuilder dbu = dbf.newDocumentBuilder();
//			dbu.setErrorHandler(new DefaultHandler());
//			if (xmlType == 0) {
//				doc = dbu.parse(new File(xmlDoc));
//			} else if (xmlType == 1 || xmlType == 2) {
//				String lang = "UTF-8";
//				if (xmlType == 2) {
//					lang = "EUC-KR";
//				}
//				byte[] bXmlDoc = xmlDoc.getBytes(lang);
//				ByteArrayInputStream bais = new ByteArrayInputStream(bXmlDoc);
//				InputSource is = new InputSource(bais);
//				doc = dbu.parse(is.getByteStream());
//			} else {
//				throw new Exception("XML TYPE is no data. (" + xmlType + ")");
//			}
//
//		} catch (SAXParseException saxex) {
//			return doc;
//		} catch (SAXException saxex) {
//			Exception e = saxex.getException();
//			( (e == null) ? saxex : e).printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} catch (Throwable t) {
//			t.printStackTrace();
//		} finally {
//		}
//
//		return doc;
//	}
//
//
	/**
	 * DOM 내 Node 에서 특정 조건의 NodeList 추출
	 *
	 * @MethodName	: getNodeListFromXPathAPI
	 * @param argNode
	 * @param str
	 * @return
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
		} finally {
		}

		return rtList;
	}
//
//
//	/**
//	 * DOM 내 Node 에서 특정 조건의 단일 Node 추출
//	 *
//	 * @MethodName	: getNodeFromXPathAPI
//	 * @AuthorDate		: LIM Y.M. / 2013. 10. 14.
//	 * @ModificationHistory	:
//	 * @param argNode
//	 * @param str
//	 * @return
//	 */
//	public static Node getNodeFromXPathAPI(Node argNode, String str) {
//
//		Node rtNode = null;
//
//		try {
//			rtNode = XPathAPI.selectSingleNode(argNode, str);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//		}
//
//		return rtNode;
//	}
//
//
	/**
	 * DOM 내 Node 에서 속성인 Attributes Value 값 추출
	 *
	 * @MethodName	: getAttributes
	 * @AuthorDate		: LIM Y.M. / 2013. 10. 15.
	 * @ModificationHistory	:
	 * @param argNnm
	 * @param name
	 * @return
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
		} finally {
		}

		return rtStr;
	}
	
}

