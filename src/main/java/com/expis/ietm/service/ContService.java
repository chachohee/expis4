package com.expis.ietm.service;

import com.expis.common.eXPIS3Constants;
import com.expis.domparser.CSS;
import com.expis.domparser.DTD;
import com.expis.ietm.dao.*;
import com.expis.ietm.dto.ReplacePartDto;
import com.expis.ietm.dto.TocoInfoDto;
import com.expis.ietm.parser.FIParser;
import com.expis.ietm.parser.FaultInfoParser;
import com.expis.ietm.dto.XContDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
@RequiredArgsConstructor
public class ContService {

    @Value("${app.expis.dfSysPath}")
    private String DF_SYSPATH;
    @Value("${app.expis.dfIetmData}")
    private String DF_IETMDATA;

    private final TocoInfoMapper tocoMapper;
    private final FaultInfoParser fiParser;
    private final FIParser fiXconParser;
    private final XContAllMapper allMapper;
    private final XContAllMapper alertMapper;
    private final XContWCMapper wcMapper;
    private final XContTableMapper tableMapper;
    private final XContGraphicMapper grphMapper;

    /**
     * replacePart 정보 반환
     */
    public String replacePartInfo(XContDto contDto) {

        String partXmlString = "";
        //TODO: 경로 하드코딩 제거
        String partPath = "C:\\EXPIS3\\Expis3_client\\webapps\\ExpisWeb\\" + DF_IETMDATA + "\\startpage\\replacePart.xml";
        try (FileInputStream fis = new FileInputStream(partPath)) {
//            return StreamUtils.copyToString(fis, StandardCharsets.UTF_8);
            partXmlString = StreamUtils.copyToString(fis, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
            //throw new RuntimeException(e);
        }

        if ( !partXmlString.isEmpty() ) {
            int startIndex = partXmlString.indexOf("<replace>");
            if (startIndex != -1) {
                partXmlString = partXmlString.substring(startIndex);
            } else {
                log.info("No <replace>");
            }
        }
        return partXmlString;
    }
    /**
     * replacePart JSON 정보 반환
     */
    public String replacePartInfoJson(XContDto contDto) {

        String partXmlString = "";
        String partInfoJson = "";
        //TODO: 경로 하드코딩 제거
        String partPath = "C:\\EXPIS3\\Expis3_client\\webapps\\ExpisWeb\\" + DF_IETMDATA + "\\startpage\\replacePart.xml";
        try (FileInputStream fis = new FileInputStream(partPath)) {
            partXmlString = StreamUtils.copyToString(fis, StandardCharsets.UTF_8);

            // XML → Java 객체
            XmlMapper xmlMapper = new XmlMapper();
            ReplacePartDto partInfo = xmlMapper.readValue(partXmlString, ReplacePartDto.class);

            // Java 객체 → JSON
            ObjectMapper jsonMapper = new ObjectMapper();
            partInfoJson = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(partInfo);

            log.info("partInfoJson : {}", partInfoJson);

        } catch (IOException e) {
            return "";
            //throw new RuntimeException(e);
        }

        if ( !partXmlString.isEmpty() ) {
            int startIndex = partXmlString.indexOf("<replace>");
            if (startIndex != -1) {
                partXmlString = partXmlString.substring(startIndex);
            } else {
                log.info("No <replace>");
            }
        }
        return partInfoJson;
    }

    /**
     * 교범 내용 XML 데이터 받아서 DOM으로 반환
     */
    public StringBuilder getAllCont(XContDto contDto) {
        StringBuilder contSB = new StringBuilder();

        try {
            ArrayList<XContDto> paramList = new ArrayList<XContDto>();
            ArrayList<XContDto> contList = new ArrayList<XContDto>();

            //1. DB에서 데이터 추출하는 MyBatis의 DAO 처리 모듈 호출 -> Contents 추출 -> DOM으로 생성
            if (contDto.getOutputMode().equals(eXPIS3Constants.OPT_OUMODE_MULTI)) {
                if(contDto.getVehicleType() == null) {
                    contDto.setVehicleType("");
                }
                paramList = allMapper.selectMultiListDao(contDto);

                //2024.11.18 - Type에 맞는 데이터만 추출하도록 보완 - jingi.kim
                for (XContDto xContDto : paramList) {
                    if (xContDto.getTocoVehicleType().equals("1")) {
                        contList.add(xContDto);
                    }
                }
                //log.info("contList : {}", contList);
                //20170110 add WC교범의 장/절 클릭시 하위 작업카드 호출이 필요한 경우
//                ArrayList<XContDto> wcList = wcMapper.selectAllMultiListDao(contDto);
//                log.info("wcList : {}", wcList.size());
//                if (wcList.size() > 0) {
//                    paramList = new ArrayList<XContDto>();
//                    contList = new ArrayList<XContDto>();
//                    paramList = allMapper.selectWCMultiListDao(contDto);
//
//                    //2024.11.18 - Type에 맞는 데이터만 추출하도록 보완 - jingi.kim
//                    for(int i=0; i<paramList.size(); i++) {
//                        if(paramList.get(i).getTocoVehicleType().equals("1")) {
//                            contList.add(paramList.get(i));
//                        }
//                    }
//                    contList.addAll(wcList);
//                }
            } else {
                contList = allMapper.selectListDao(contDto);
            }
            //log.info("contList : {}", contList);
            if (contList != null && !contList.isEmpty()) {
                for (XContDto rsDto : contList) {
                    contSB.append(rsDto.getXcont());
                }
            }
            //log.info("contSB.append : {}", contSB.toString());
            if (!contSB.isEmpty()) {
                if (contDto.getOutputMode().equals(eXPIS3Constants.OPT_OUMODE_MULTI)) {
                    contSB.insert(0, "<eXPISMultiInfo>");
                    contSB.append("</eXPISMultiInfo>");
                }
            }
        }  catch (Exception ex) {
            //ex.printStackTrace();
            log.error("{} . getAllContDom Exception:{}", this.getClass(), ex.toString());
        } finally {

        }

        return contSB;
    }

    /**
     * 교범 내용 중 경고 XML 데이터
     */
    public StringBuilder getAlertCont(XContDto contDto) {

        StringBuilder contSB = new StringBuilder();

        try {
            ArrayList<XContDto> contList = alertMapper.selectListDao(contDto);
            if (contList != null && !contList.isEmpty()) {
                for (XContDto rsDto : contList) {
                    contSB.append(rsDto.getXcont());
                }
            }

        }  catch (Exception ex) {
            //ex.printStackTrace();
            log.error("{} . getAlertCont Exception:{}", this.getClass(), ex.toString());
        }

        return contSB;
    }

    /**
     * 교범 내용 중 그림(그래픽) XML 데이터
     */
    public StringBuilder getGrphCont(XContDto contDto) {

        StringBuilder contSB = new StringBuilder();

        try {
            //2023 02 07 Park.J.S. ADD : fileOrgName 처음은 없이 조회
            String fileOrgName = contDto.getFileOrgName();
            contDto.setFileOrgName(null);
            log.info("fileOrgName : "+fileOrgName);
            ArrayList<XContDto> contList = grphMapper.selectListDao(contDto);
            if (contList != null && !contList.isEmpty()) {
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
                if (contList != null && !contList.isEmpty()) {
                    for (XContDto rsDto : contList) {
                        contSB.append(rsDto.getXcont());
                    }
                }
                contDto.setTocoId(tocoId);
            }

        }  catch (Exception ex) {
            //ex.printStackTrace();
            log.error("{} . getGrphCont Exception:{}",this.getClass(), ex.toString());
        } finally {

        }

        return contSB;
    }

    /**
     * 교범 내용 중 표(테이블) XML 데이터
     */
    public StringBuilder getTableCont(XContDto contDto) {

        StringBuilder contSB = new StringBuilder();

        try {
            ArrayList<XContDto> contList = tableMapper.selectListDao(contDto);
            if (contList != null && !contList.isEmpty()) {
                for (XContDto rsDto : contList) {
                    contSB.append(rsDto.getXcont());
                }
            }

        }  catch (Exception ex) {
            //ex.printStackTrace();
            log.error("{}, getTableCont Exception:{}", this.getClass(), ex.toString());
        }

        return contSB;
    }

    /**
     * 교범 내용 WorkCard XML 데이터
     */
    public StringBuilder getWCCont(XContDto contDto) {

        StringBuilder contSB = new StringBuilder();

        try {
            ArrayList<XContDto> contList = null;
            if (contDto.getOutputMode().equals(eXPIS3Constants.OPT_OUMODE_MULTI)) {
                contList = wcMapper.selectMultiListDao(contDto);
            } else {
                contList = wcMapper.selectListDao(contDto);
            }

            if (contList != null && !contList.isEmpty()) {
                for (XContDto rsDto : contList) {
                    contSB.append(rsDto.getXcont());
                }
            }

            if (!contSB.isEmpty()) {
                contSB.insert(0, "<eXPISInfo>");
                contSB.append("</eXPISInfo>");
            }

        }  catch (Exception ex) {
            //ex.printStackTrace();
            log.error("{} . getWCCont Exception:{}", this.getClass(), ex.toString());
        } finally {

        }

        return contSB;
    }

    public Map<String, String> getIPBFigureData(StringBuffer stringBuffer) {
        Map<String, String> rtnMap = new HashMap<>();
        try {
            InputSource source = new InputSource(new StringReader(stringBuffer.toString()));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(source);
            document.getDocumentElement().normalize();

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression compile = xPath.compile("//grphprim[@external-ptr]");
            NodeList nodeList = (NodeList) compile.evaluate(document, XPathConstants.NODESET);
            log.info("nodeList.getLength(): {}", nodeList.getLength());

            String imgFileName = "";
            for ( int i=0; i<nodeList.getLength(); i++ ) {
                Node cnode = nodeList.item(i);
                if ( cnode == null ) continue;

                NamedNodeMap attributes = cnode.getAttributes();
                Node item = attributes.getNamedItem("external-ptr");

                imgFileName = item.getNodeValue();
                if (imgFileName.isEmpty()) continue;

                log.info("image file name: {}", imgFileName);
                imgFileName = Paths.get(imgFileName).getFileName().toString();
                log.info("image file name: {}", imgFileName);
                if (imgFileName.lastIndexOf(".") != -1) {
                    imgFileName = imgFileName.substring(0, imgFileName.lastIndexOf("."));
                }
                log.info("image file name: {}", imgFileName);

                //TODO: 경로 하드코딩
                String imgDataPath = "C:\\EXPIS3\\Expis3_client\\webapps\\ExpisWeb\\" + DF_IETMDATA + "\\imagedata\\" + imgFileName + ".xml";
                try (FileInputStream fis = new FileInputStream(imgDataPath)) {
                    rtnMap.put(imgFileName, StreamUtils.copyToString(fis, StandardCharsets.UTF_8).trim());
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            //throw new RuntimeException(e);
        }

        return rtnMap;
    }

    /**
     * imageData json 객체 생성
     */
    public Map<String, Object> getIPBFigureJson(Map<String, String> figureData) {
        String rawInput = figureData.toString();
        Map<String, String> xmlMap = splitEntries(rawInput);
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, String> entry : xmlMap.entrySet()) {
            String key = entry.getKey();
            String xml = entry.getValue();

            JSONObject parsed = parseXmlToJson(xml);
            // JSONObject를 Map으로 변환해서 넣기
            result.put(key, parsed.toMap());
        }

        return result;
    }

    private static Map<String, String> splitEntries(String raw) {
        Map<String, String> result = new LinkedHashMap<>();

        raw = raw.trim().replaceAll("^\\{", "").replaceAll("}$", "");

        // ',' 로 나누기 (XML 안의 , 무시)
        StringBuilder buffer = new StringBuilder();
        int depth = 0;
        List<String> entries = new ArrayList<>();

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);

            if (c == '<') depth++;
            if (c == '>') depth--;
            if (c == ',' && depth <= 0) {
                entries.add(buffer.toString());
                buffer.setLength(0);
            } else {
                buffer.append(c);
            }
        }
        if (buffer.length() > 0) entries.add(buffer.toString());

        for (String entry : entries) {
            int eqIdx = entry.indexOf('=');
            if (eqIdx != -1) {
                String key = entry.substring(0, eqIdx).trim();
                String value = entry.substring(eqIdx + 1).trim();

                // XML 헤더 제거
                value = value.replaceAll("^<\\?xml[^>]*\\?>", "").trim();

                // 닫는 태그 자동 보정
                if (!value.matches(".*</\\w+>$")) {
                    Matcher m = Pattern.compile("<(\\w+)(\\s|>)").matcher(value);
                    if (m.find()) {
                        String tagName = m.group(1);
                        value += "</" + tagName + ">";
                    }
                }

                result.put(key, value);
            }
        }

        return result;
    }

    private static JSONObject parseXmlToJson(String xml) {
        JSONObject json = new JSONObject();
        JSONArray infos = new JSONArray();

        try {
            // 1. XML 선언 제거
            xml = xml.replaceAll("(?s)<\\?xml.*?\\?>", "");

            // 2. 잘못된 이중 닫힘 제거 (셀프 클로징 + 닫는 태그가 같이 있을 때)
            xml = xml.replaceAll("<SystemInfo([^>]*)/>(\\s*</SystemInfo>)", "<SystemInfo$1></SystemInfo>");

            // 3. 정상 셀프 클로징 태그 → 여는+닫는 형태로 변환
            xml = xml.replaceAll("<SystemInfo([^>]*)/>", "<SystemInfo$1></SystemInfo>");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            Element root = doc.getDocumentElement();

            String width = root.getAttribute("width");
            String height = root.getAttribute("height");
            if (width != null && !width.isEmpty()) {
                json.put("width", width);
            } else {
                json.put("width", "");
            }
            if (height != null && !height.isEmpty()) {
                json.put("height", height);
            } else {
                json.put("height", "");
            }

            NodeList infoList = root.getElementsByTagName("info");

            // <info> 태그가 있을 경우만 처리
            if (infoList != null && infoList.getLength() > 0) {
                for (int i = 0; i < infoList.getLength(); i++) {
                    Element info = (Element) infoList.item(i);
                    JSONObject infoObj = new JSONObject();


                    NamedNodeMap attrs = info.getAttributes();
                    for (int j = 0; j < attrs.getLength(); j++) {
                        Attr attr = (Attr) attrs.item(j);
                        infoObj.put(attr.getName(), attr.getValue());
                    }

                    infos.put(infoObj);
                }
                // <infos>가 비어 있지 않으면, infos 배열을 json에 추가
                json.put("infos", infos);
            } else {
                // <info> 태그가 없을 경우, infos 배열을 빈 배열로 추가
                json.put("infos", new JSONArray());
            }

        } catch (Exception e) {
            e.printStackTrace();
            json.put("error", e.getMessage());
            json.put("raw", xml);
        }

        return json;
    }

    public String getFirstNodeType(StringBuffer stringBuffer, String bizCode, String toKey, String tocoName) {
        try {
            InputSource source = new InputSource(new StringReader(stringBuffer.toString()));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(source);
            document.getDocumentElement().normalize();

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression compile = xPath.compile("//partinfo[1]");
            Node partNode = (Node) compile.evaluate(document, XPathConstants.NODE);
            if ( partNode != null ) { return "IPB"; }

            compile = xPath.compile("//"+ DTD.TASK +"/"+ DTD.INPUT +"/"+ DTD.IN_REQCOND);
            Node inputNode = (Node) compile.evaluate(document, XPathConstants.NODE);
            if (inputNode != null) { return "REQCOND"; }

            String firstNodeType = "";

            compile = xPath.compile("//system[1]");
            Node firstNode = (Node) compile.evaluate(document, XPathConstants.NODE);
            if (firstNode != null) {
                NamedNodeMap attributes = firstNode.getAttributes();
                Node nodeType = attributes.getNamedItem("type");

                firstNodeType = nodeType.getNodeValue();
            }

            /* *
             * 2025.04.21. - JG 정비절차 (책자교범) - minhee Yun
             * 2025.05.07 - 기존 조건식 추가 - jingi.kim
             * 2025.06.12 - BLOCK2 계열 전용 조건식 변경 - jingi.kim
             * */

            if (toKey.contains("JG-")) {
                if (bizCode.contains("T50") || bizCode.contains("FA50") || bizCode.contains("KA1") || bizCode.contains("KT1") || bizCode.contains("KT100")) {
                    compile = xPath.compile("//"+ DTD.SYSTEM +"/"+ DTD.TASK +"/"+ DTD.STEPSEQ +"/"+ DTD.STEP);
                    Node stepNode = (Node) compile.evaluate(document, XPathConstants.NODE);
                    if (stepNode != null) {
                        if (!"Introduction".equalsIgnoreCase(firstNodeType)
                                && !"Chapter".equalsIgnoreCase(firstNodeType)
                                && !"Section".equalsIgnoreCase(firstNodeType)) {
                            if (tocoName != null && tocoName.split("\\.").length >= 3) {
                                return "STEPSEQ";
                            } else {
                                return firstNodeType;
                            }
                        }
                    }
                }
            }

            if (bizCode.contains("T50") || bizCode.contains("FA50")) {
                if (toKey.contains("-21FI-00-")) {
                    compile = xPath.compile("//"+ DTD.SYSTEM +"/"+ DTD.DESC);
                    NodeList descNodeList = (NodeList) compile.evaluate(document, XPathConstants.NODESET);
                    compile = xPath.compile("//"+ DTD.SYSTEM +"/"+ DTD.TASK);
                    NodeList taskNodeList = (NodeList) compile.evaluate(document, XPathConstants.NODESET);
                    if (descNodeList.getLength() > 2 && taskNodeList.getLength() >= 1) {
                        return "FIELDSTEP";
                    }
                }
            }

            return firstNodeType;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return "";
    }

    /**
     * 교범 내용 중 그림(그래픽) XML 데이터
     */
    public Map<String, Object> getSingleGrphCont(XContDto xContDto) {
        Map<String, Object> rtnMap = new HashMap<>();
        StringBuffer rtSB = new StringBuffer();

        // 이미지 cont select
        XContDto grphCont = grphMapper.selectDetailDao(xContDto);
        // 이미지 cont 해당 목차 tocoName select
        String tocoName = getTocoName(grphCont.getToKey(), grphCont.getTocoId());

        // 이미지 cont append
        StringBuffer xcont = rtSB.append(grphCont.getXcont());

        rtnMap.put("rtSB", xcont.toString());
        rtnMap.put("nodeType", "image"); // nodeType이 따로 존재하지않아 image로 적용
        rtnMap.put("tocoLink", CSS.getTocoLink(
                grphCont.getTocoId(),
                tocoName,
                xContDto.getLanguageType())
        );
        return rtnMap;
    }

    /**
     * 교범 내용 중 FI XML 데이터 - jsh
     */
    public String getFICont(XContDto contDto)   {
        try {
            StringBuffer rtSB = new StringBuffer();
            String fiParserCont = fiXconParser.fiParser(contDto);

            //FI 교범이지만, type이 DI_DESC 경우에만 fiparser 실행 - jsh
            if(fiParserCont == null || fiParserCont.isEmpty()){
                StringBuffer append = rtSB.append(getAllCont(contDto).toString());
                return append.toString();
            }
            return fiParserCont;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 2025.04.22 - osm
     * - 임시 생성 (로직 리팩토링 시 제거할지 판단)
     * - table 단일 조회
     * - 해당목차 기능에 대한 테이블 toKey, tocoId를 가져오기 위해 생성
     */
    public XContDto getSingleTableCont(XContDto xContDto) {
        return tableMapper.selectDetailDao(xContDto);
    }

    /**
     * 2025.04.22 - osm
     * - TocoName 조회 메서드 분리
     * - 해당 메서드 호출 시 매개변수 값을 명확히 하기 위해 XContDto가 아닌 각 필요한 값을 매개변수로 지정
     */
    public String getTocoName(String toKey, String tocoId) {
        return tocoMapper.selectTocoNameDao(toKey, tocoId);
    }

    /**
     * 2025.06.02
     * - WC 카드 내 계통 별 링크 조회
     */
    public List<XContDto> viewWCLink(XContDto xContDto) {
        return wcMapper.viewWCLinkDao(xContDto);
    }
}
