package com.expis.ietm.parser;

import com.expis.domparser.XmlDomParser;
import com.expis.ietm.dao.XContAllMapper;
import com.expis.ietm.dto.XContDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
@RequiredArgsConstructor

public class FIParser {
    private final XContAllMapper allMapper;

    // fi xml 파싱 -jsh-
    public String fiParser(XContDto contDto) throws Exception{
        StringBuffer rtSB = new StringBuffer();

        // 1. Xml 문자열 생성
        String xcont = generateXmlFromXCont(contDto);
        if (xcont == null || xcont.isBlank() || !xcont.contains("DI_DESC")) {
            return "";
        }

        // 2. 문자열 -> Document 변환
        Document xmlDoc = parseXmlString(xcont);

        // 3. system 태그 추가
        NodeList systemList = xmlDoc.getElementsByTagName("system");
        Element systemElement = (Element) systemList.item(0);
        appendSystemTag(systemElement, rtSB);

        // 4. desinfo 태그 추가
        NodeList descList = xmlDoc.getElementsByTagName("descinfo");
        appendTag(descList, rtSB, null);

        // 5. faultinf 태그 추가
        NodeList faultList  = xmlDoc.getElementsByTagName("faultinf");
        appendTag(faultList, rtSB, node -> {
            String fiId = XmlDomParser.getAttributes(node.getAttributes(), "id");
            return fiId != null && fiId.equals(contDto.getContId());
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

            return nodeToString(rtElem);
        }
        return null;
    }

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
}
