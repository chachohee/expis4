package com.expis.ietm.facade;

import com.expis.common.eXPIS3Constants;
import com.expis.domparser.ATTR;
import com.expis.domparser.VAL;
import com.expis.domparser.eXPIS3Converter;
import com.expis.ietm.dao.*;
import com.expis.ietm.dto.*;
import com.expis.ietm.parser.FaultInfoParser;
import com.expis.ietm.service.ContService;
import com.expis.ietm.service.MemoService;
import com.expis.ietm.service.VersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class ContFacade {

    @Value("${app.expis.dfSysPath}")
    private String DF_SYSPATH;
    @Value("${app.expis.dfIetmData}")
    private String DF_IETMDATA;

    private final ContService contService;
    private final MemoService memoService;
    private final VersionService versionService;

    private final TocoInfoMapper tocoMapper;
    private final FaultInfoParser fiParser;
    private final XContAllMapper allMapper;
    private final XContAllMapper alertMapper;
    private final XContWCMapper wcMapper;
    private final XContTableMapper tableMapper;
    private final XContGraphicMapper grphMapper;

    /**
     * 교범 내용
     */
    public Map<String, Object> getTocoContents(XContDto contDto) {
        Map<String, Object> rtMap = new HashMap<>();
        if(contDto == null) {
            rtMap.put("rtSB", "");
            return rtMap;
        }

        StringBuffer rtSB = new StringBuffer();

        try {

            String ietmdata = "";
            String syspath = "";

            syspath = DF_SYSPATH;
            ietmdata = DF_IETMDATA;
            log.info("sys path : {}", syspath);

            //내용 시현 목차 타입에 따라 다르게 설정
            String vContKind = contDto.getViewContKind();

            TocoInfoDto tocoDto = tocoMapper.selectDetailDao(contDto.getToKey(), contDto.getTocoId());

            //경고-프레임보기에서 경고창 && 그래픽 && 테이블이 아니면
            if (!vContKind.equals(eXPIS3Constants.VCONT_KIND_ALERT)
                    && !vContKind.equals(eXPIS3Constants.VCONT_KIND_GRPH) && !vContKind.equals(eXPIS3Constants.VCONT_KIND_TABLE)) {

                log.info(" - tocoDto : {}", tocoDto);
                if (tocoDto != null) {
                    String tocoType = tocoDto.getTocoType();
                    String pTocoId = tocoDto.getPTocoId();
                    String tocoRefId = tocoDto.getTocoRefId();
                    String tocoId = tocoDto.getTocoId();
                    String fiType = tocoDto.getTocoType(); //fi 타입추가 (EX:28..30) - jsh -
                    log.info("tocoType : {}", tocoType);

                    String tocoTypeWord = eXPIS3Converter.getTocoTypeWordFromCode(tocoType);
                    rtMap.put("tocoType", tocoTypeWord);
                    rtMap.put("fiType", fiType);

                    if ("IPB".equalsIgnoreCase(tocoTypeWord)) {
                        contDto.setViewContKind(eXPIS3Constants.VCONT_KIND_IPB);
                    }
                    if ("FI".equalsIgnoreCase(tocoTypeWord)) {
                        contDto.setViewContKind(eXPIS3Constants.VCONT_KIND_FI);
                        contDto.setContId(tocoId);
                        if (tocoType.equals(eXPIS3Constants.TOCO_TYPE_FI_FIA)) {
                            contDto.setTocoId(pTocoId); //결함식별>조종자>하위목차 선택시 상위 부모 목차로 추출해야함
                        }
                    }
                    if ("WC".equalsIgnoreCase(tocoTypeWord)) {
                        contDto.setViewContKind(eXPIS3Constants.VCONT_KIND_WC);
                        if (!tocoRefId.isEmpty()) {
                            contDto.setArrRefId(tocoRefId.split(","));
                        }
                    }

                    //해당 목차의 메모 리스트 추출
                    if (!contDto.getTocoId().isEmpty()) {
                        MemoDto memoDto = new MemoDto();
                        memoDto.setToKey(contDto.getToKey());
                        memoDto.setTocoId(contDto.getTocoId());
                        memoDto.setCreateUserId(contDto.getUserId());

                        ArrayList<MemoDto> memoList;
                        memoList = memoService.selectSingleMemo(memoDto);
                        log.info("getTocoContents memoList : {}", memoList.toString());

                        rtMap.put("memoList", memoList);
                    }
                }
            }

            /*
             * 1. DB에서 데이터 추출하는 MyBatis의 DAO 처리 모듈 호출
             * 2. 스트링으로 된 XML 데이터를 W3C DOM으로 생성하는 모듈 호출
             * 3. XML 데이터를 W3C DOM으로 생성하는 모듈 호출
             * 4. DOM 데이터를 HTML로 파싱하는 모듈 호출
             * 01 : 내용 본문	- getPageHtml()
             * 02 : 경고			- getAlertHtml()
             * 03 : 그래픽		- getGrphHtml()
             * 04 : 표(테이블)	- getTableHtml()
             * 05 : IPB		- getPageHtml()
             * 06 : WC		- getPageHtml()
             * 07 : FI			- getPageHtml()
             */
            String nodeType = "";
            String tocoName = "";

            switch (contDto.getViewContKind()) {
                case eXPIS3Constants.VCONT_KIND_ALERT:
                    log.info("contComponet Alert!!");
                    contDto.setAltId(contDto.getContId());
                    rtSB.append( contService.getAlertCont(contDto).toString() );

                    nodeType = "ALERT";
                    break;
                case eXPIS3Constants.VCONT_KIND_GRPH:
                    log.info("contComponent Grph!! Start");
                    contDto.setGrphId(contDto.getContId());
                    rtSB.append( contService.getGrphCont(contDto).toString() );

                    tocoName = tocoMapper.selectTocoNameDao(contDto.getToKey(), contDto.getTocoId());

                    nodeType = "GRAPH";
                    break;
                case eXPIS3Constants.VCONT_KIND_TABLE:
                    log.info("contComponent Table!!");
                    contDto.setTblId(contDto.getContId());
                    rtSB.append( contService.getTableCont(contDto).toString() );

                    tocoName = tocoMapper.selectTocoNameDao(contDto.getToKey(), contDto.getTocoId());

                    nodeType = "TABLE";
                    break;
                case eXPIS3Constants.VCONT_KIND_WC:
                    log.info("contComponent WC!!");
                    rtSB.append( contService.getWCCont(contDto).toString() );

                    nodeType = "WORKCARD";
                    if ("KT1".equalsIgnoreCase(contDto.getBizCode())
                            || "KA1".equalsIgnoreCase(contDto.getBizCode())
                            || "KT100".equalsIgnoreCase(contDto.getBizCode())) {
                        nodeType = "KTAWORKCARD";
                    }
                    break;

                default:
                    log.info("contComponent default!! IPB, WC, FI, Etc..... : {}", contDto.getViewContKind());
                    if(contDto.getToKey().contains("FI") || contDto.getToKey().contains("FR")) {
                        rtSB.append(contService.getFICont(contDto));
                        break;
                    }

                    rtSB.append(contService.getAllCont(contDto).toString());

                    break;
            }

            if (nodeType.isEmpty()) {
                nodeType = contService.getFirstNodeType(rtSB, contDto.getBizCode(), contDto.getToKey(), tocoDto != null ? tocoDto.getTocoName() : null);
            }

            //IPB인 경우 system 테그 추가 - jingi.kim
            if ("IPB".equalsIgnoreCase(nodeType) || eXPIS3Constants.VCONT_KIND_IPB.equals(contDto.getViewContKind())) {
                rtSB.insert(0, "<system>");
                rtSB.append("</system>");

                Map<String, String> figureData = contService.getIPBFigureData(rtSB);
                String partInfoJson = contService.replacePartInfoJson(contDto);

                rtMap.put("imageData", figureData);
                rtMap.put("replacePart", partInfoJson);

                //image Data json 리턴
                if (figureData != null && !figureData.toString().isEmpty()) {
                    Map<String, Object> imageDataJson = contService.getIPBFigureJson(figureData);
                    rtMap.put("imageDataJson", imageDataJson);
                }

                //IPB 테이블 열 너비 조절
                Map<String, String> ipbTableWidths = new HashMap<>();
                if (contDto.getIpbType() == "01") {
                    // 영어교범 처리
                    if ((contDto.getLanguageType() != null && "en".equalsIgnoreCase(contDto.getLanguageType()))) {
                        ipbTableWidths.put("ipbcode", "5");
                        ipbTableWidths.put("graphicnum", "5");
                        ipbTableWidths.put("indexnum", "7");
                        ipbTableWidths.put("partnum", "15");
                        ipbTableWidths.put("nsn", "10");
                        ipbTableWidths.put("cage", "10");
                        ipbTableWidths.put("name", "30");
                        ipbTableWidths.put("unitsper", "5");
                        ipbTableWidths.put("usablon", "5");
                        ipbTableWidths.put("smr", "8");
                        ipbTableWidths.put("rdn", "5");
                    } else {
                        ipbTableWidths.put("ipbcode", "5");
                        ipbTableWidths.put("graphicnum", "5");
                        ipbTableWidths.put("indexnum", "7");
                        ipbTableWidths.put("partnum", "15");
                        ipbTableWidths.put("nsn", "10");
                        ipbTableWidths.put("cage", "10");
                        ipbTableWidths.put("name", "30");
                        ipbTableWidths.put("unitsper", "5");
                        ipbTableWidths.put("usablon", "5");
                        ipbTableWidths.put("smr", "8");
                        ipbTableWidths.put("rdn", "5");
                    }

                    if ("LSAM".equalsIgnoreCase(contDto.getBizCode()) || "KICC".equalsIgnoreCase(contDto.getBizCode()) || "MUAV".equalsIgnoreCase(contDto.getBizCode()) || "SENSOR".equalsIgnoreCase(contDto.getBizCode())) {
                        ipbTableWidths.put("ipbcode", "1");
                        ipbTableWidths.put("graphicnum", "3");
                        ipbTableWidths.put("indexnum", "3");
                        ipbTableWidths.put("partnum", "15");
                        ipbTableWidths.put("nsn", "13");
                        ipbTableWidths.put("cage", "6");
                        ipbTableWidths.put("name", "auto");
                        ipbTableWidths.put("unitsper", "4");
                        ipbTableWidths.put("usablon", "3");
                        ipbTableWidths.put("smr", "6");
                        ipbTableWidths.put("rdn", "5");
                    }
                // T.O 교범
                } else {
                    // 영어교범 처리
                    if ((contDto.getLanguageType() != null && "en".equalsIgnoreCase(contDto.getLanguageType()))) {
                        if (!"KTA".equalsIgnoreCase(contDto.getBizCode())) {
                            ipbTableWidths.put("ipbcode", "5");
                            ipbTableWidths.put("graphicnum", "5");
                            ipbTableWidths.put("indexnum", "3");
                            ipbTableWidths.put("partnum", "7");
                            ipbTableWidths.put("nsn", "6");
                            ipbTableWidths.put("cage", "4");
                            ipbTableWidths.put("level", "4");
                            ipbTableWidths.put("name", "auto");
                            ipbTableWidths.put("unitsper", "5");
                            ipbTableWidths.put("retrofit", "4");
                            ipbTableWidths.put("usablon", "3");
                            ipbTableWidths.put("smr", "4");
                            ipbTableWidths.put("rdn", "4");
                            ipbTableWidths.put("workunitcode", "4");
                            ipbTableWidths.put("partsource", "3");
                            ipbTableWidths.put("refdata", "3");
                            ipbTableWidths.put("reftono", "5");
                            ipbTableWidths.put("kai_std", "5");
                            ipbTableWidths.put("sssn", "4");
                        } else {
                            ipbTableWidths.put("ipbcode", "9.39");
                            ipbTableWidths.put("graphicnum", "6.78");
                            ipbTableWidths.put("indexnum", "3.39");
                            ipbTableWidths.put("partnum", "7.82");
                            ipbTableWidths.put("nsn", "6.78");
                            ipbTableWidths.put("cage", "4.43");
                            ipbTableWidths.put("level", "4.43");
                            ipbTableWidths.put("name", "auto");
                            ipbTableWidths.put("unitsper", "5.63");
                            ipbTableWidths.put("retrofit", "5.63");
                            ipbTableWidths.put("usablon", "3.39");
                            ipbTableWidths.put("smr", "4.43");
                            ipbTableWidths.put("rdn", "4.43");
                            ipbTableWidths.put("workunitcode", "4.43");
                            ipbTableWidths.put("partsource", "3.39");
                            ipbTableWidths.put("refdata", "3.39");
                            ipbTableWidths.put("reftono", "5.63");
                            ipbTableWidths.put("kai_std", "5.63");
                            ipbTableWidths.put("sssn", "4.43");

                        }
                    } else {
                        if (!"KTA".equalsIgnoreCase(contDto.getBizCode())) { // 현재 이 조건만 4버전에 맞게 너비 수정함
                            ipbTableWidths.put("ipbcode", "0.8");
                            ipbTableWidths.put("graphicnum", "2.94");
                            ipbTableWidths.put("indexnum", "3.71");
                            ipbTableWidths.put("partnum", "7.25");
                            ipbTableWidths.put("nsn", "5.5");
                            ipbTableWidths.put("cage", "3.4");
                            ipbTableWidths.put("level", "3.71");
                            ipbTableWidths.put("name", "auto");
                            ipbTableWidths.put("unitsper", "5.88");
                            ipbTableWidths.put("retrofit", "5.00");
                            ipbTableWidths.put("usablon", "3.71");
                            ipbTableWidths.put("smr", "5.88");
                            ipbTableWidths.put("rdn", "5.00");
                            ipbTableWidths.put("workunitcode", "5.00");
                            ipbTableWidths.put("partsource", "4.66");
                            ipbTableWidths.put("refdata", "3.82");
                            ipbTableWidths.put("reftono", "11.35");
                            ipbTableWidths.put("kai_std", "7.00");
                            ipbTableWidths.put("sssn", "5.00");

                        } else {
                            ipbTableWidths.put("ipbcode", "9.39");
                            ipbTableWidths.put("graphicnum", "6.78");
                            ipbTableWidths.put("indexnum", "3.39");
                            ipbTableWidths.put("partnum", "7.82");
                            ipbTableWidths.put("nsn", "6.78");
                            ipbTableWidths.put("cage", "4.43");
                            ipbTableWidths.put("level", "4.43");
                            ipbTableWidths.put("name", "auto");
                            ipbTableWidths.put("unitsper", "5.63");
                            ipbTableWidths.put("retrofit", "5.63");
                            ipbTableWidths.put("usablon", "3.39");
                            ipbTableWidths.put("smr", "4.43");
                            ipbTableWidths.put("rdn", "4.43");
                            ipbTableWidths.put("workunitcode", "4.43");
                            ipbTableWidths.put("partsource", "3.39");
                            ipbTableWidths.put("refdata", "3.39");
                            ipbTableWidths.put("reftono", "5.63");
                            ipbTableWidths.put("kai_std", "5.63");
                            ipbTableWidths.put("sssn", "4.43");
                        }
                        if ("KBOB".equalsIgnoreCase(contDto.getBizCode())) {
                            ipbTableWidths.put("rdn", "4.43");
                        }
                    }
                }
                rtMap.put("ipbTableWidths", ipbTableWidths);
            }

            if ( !tocoName.isEmpty() ) {
                rtMap.put("tocoName", tocoName);
            }
            rtMap.put("rtSB", rtSB);
            rtMap.put("nodeType", nodeType);

        } catch (Exception ex) {
            //ex.printStackTrace();
            rtMap.put("rtSB", "");
            log.error("getTocoContents Exception: {}", ex.toString());
        }

        return rtMap;
    }

    public Map<String, Object> getVersionStaus(String xmlString, XContDto contDto) {
        Map<String, Object> rtMap = new HashMap<>();
        if(xmlString.isEmpty()) {
            return rtMap;
        }

        Map<String, Object> dataMap = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            NodeList verNodeList = (NodeList) xPath.evaluate("//*[@version and normalize-space(@version) != '']", document, XPathConstants.NODESET);

            String lastVersionId = versionService.getLastVersionInfo(contDto);
            for (int i=0; i<verNodeList.getLength(); i++) {
                Node verNode = verNodeList.item(i);
                if (verNode == null) continue;

                Element verElement = (Element) verNode;

                String changeBasis = verElement.getAttribute("changebasis");
                String contId = verElement.getAttribute(ATTR.ID);
                String level = verElement.getAttribute(ATTR.SYS_TOCO_lEVEL);
                String statusCode = verElement.getAttribute(ATTR.SYS_TOCO_STATUSCD);
                String tpStatusCode = verElement.getAttribute(ATTR.SYS_TOCO_TP_STATUSCD);
                String versionId = verElement.getAttribute(ATTR.SYS_TOCO_VERID);
                String styleName = "";
                String parentLevel = "";
                Node parentNode = verNode.getParentNode();
                if ( parentNode != null ) {
                    parentLevel = ((Element) parentNode).getAttribute(ATTR.SYS_TOCO_lEVEL);
                }

                // BLOCK2, IPB교범에서 품목번호 0번에 변경바가 표시되지 않도록
                if ( parentLevel.isEmpty() && !level.isEmpty() && ("T50".equalsIgnoreCase(contDto.getBizCode()) || "FA50".equalsIgnoreCase(contDto.getBizCode())) ) {
                    statusCode = "";
                }

                // TP(보충판) 변경바 : 초록색
                if ("1".equalsIgnoreCase(tpStatusCode)) {
                    styleName = "TP";
                } else if (statusCode.equals(VAL.CONT_STATUS_APPEND) && !versionId.isEmpty() || statusCode.equals(VAL.CONT_STATUS_UPDATE)) {
                    styleName = "UPDATE";
                } else {
                    statusCode = "";
                }

                // 일반 변경바 인지 최신(마지막) 변경바 인지 구분화 하여 style 지정
                if ( !lastVersionId.isEmpty() && lastVersionId.equals(versionId) ) {
                    styleName = "LAST";
                }

                VersionForm versionForm = new VersionForm();
                versionForm.setToKey(contDto.getToKey());
                versionForm.setTocoId(contDto.getTocoId());
                versionForm.setContId(contId);
                versionForm.setStatus(statusCode);
                versionForm.setStyle(styleName);
                versionForm.setVersionId(versionId);

                dataMap.put(contId, versionForm);
            }

        } catch (Exception e) {
            return rtMap;
        }

        rtMap.put("versionData", dataMap);
        return rtMap;
    }
}
