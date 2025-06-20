package com.expis.manage.facade;

import com.expis.common.eXPIS3Constants;
import com.expis.domparser.*;
import com.expis.ietm.dto.TocoInfoDto;
import com.expis.manage.dto.ExpisXmlWrapper;
import com.expis.manage.dto.RegisterDto;
import com.expis.manage.dto.SystemXmlWrapper;
import com.expis.manage.service.RegisterService;
import com.expis.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Component
public class ToFasade {

    private final RegisterService registerService;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Value("${app.expis.bizCode}")
    private String bizCode;
    @Value("${app.expis.ietmType}")
    private String ietmType;
    @Value("${app.expis.databaseType}")
    private String databaseType;
    @Value("${app.expis.dfIetmData}")
    private String ietmDataPath;

    private final List<SystemXmlWrapper> grphNodeList;
    private final List<SystemXmlWrapper> videoNodeList;
    private final List<SystemXmlWrapper> tableNodeList;

    // 교범 등록
    public boolean insertSingleTo(RegisterDto rcDto) {

        try {
            int rtInt = 0;

            String decodeDir = rcDto.getToDirPath();

            //입력받은 TO 폴더에서 목차 파일인 eXPIS.xml 추출해서 Dom 생성
            String toFilePath = decodeDir + "/" + eXPIS3Constants.FILE_NAME_TO;

            File toFile = new File(toFilePath);
            if (!toFile.exists()) {
                return false;
            }

            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ExpisXmlWrapper expisXmlWrapper = null;
            expisXmlWrapper = xmlMapper.readValue(toFile, ExpisXmlWrapper.class);

            List<SystemXmlWrapper> systemList = expisXmlWrapper.getSystemList();

            // 상위 노드 수동 설정
//            if ( !systemList.isEmpty() ) {
//                for ( SystemXmlWrapper system : systemList ) {
//                    if ( system.getSystemList() == null ) continue;;
//                    for ( SystemXmlWrapper childSystem : system.getSystemList() ) {
//                        if ( childSystem.getParent() != null ) continue;;
//                        childSystem.setParent(system);
//                    }
//                }
//            }

            if (systemList.isEmpty()) {
                // pdf 파일만 존재 할수 있어서 해당 경우 확인함
                try{
                    log.info("pdfFiles check Start");
                    String dirPath = rcDto.getToDirPath();
                    File pdfFiles = new File(dirPath+"/paperfile");
                    log.info("pdfFiles ={}", pdfFiles);
                    if(pdfFiles.exists() && pdfFiles.isDirectory()) {
                        log.info("pdfFiles folder file copy");
                        registerService.copyFiles(pdfFiles, 6);
                    }
                    log.info("pdfFiles check Fin");
                }catch (Exception e) {
                    log.info("pdfFiles check Error :{}", e.getMessage());
                }
                return false;
            }

            //1. TO Tree XCont(XML data)/Info Delete (tm_tree_xcont/tm_to_info delete)
            rtInt = registerService.deleteSingleTo(rcDto);
            log.info("==> 1.deleteSingleTo :{}", rtInt);

            List<SystemXmlWrapper> flatterSystemXmlList = flatterSystemXmlList(systemList, "");

            log.info("system length={}", systemList.size());
            //병렬처리
            List<CompletableFuture<Void>> futures = flatterSystemXmlList.stream()
                    .map(item -> CompletableFuture.runAsync( ()-> saveDatabase(item, rcDto), executor ))
                    .toList();

            //모든작업 대기
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            afterSaveDatabase(expisXmlWrapper, rcDto);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private void saveDatabase(SystemXmlWrapper item, RegisterDto rcDto) {

        if ( item == null ) return;
        if ( "GrphToco".equalsIgnoreCase(item.getId()) ) return;
        if ( "VideoToco".equalsIgnoreCase(item.getId()) ) return;
        if ( "TableToco".equalsIgnoreCase(item.getId()) ) return;

        log.info(" saveDatabase start");
        //log.info(" item={}, rcDto={}", item, rcDto);
        log.info(" item.id={}, rcDto={}", item.getId(), rcDto);

        int rtInt = 0;

        //파일을 읽어서 TO목차 내용(UUID.xml) 정보를 Dom으로 변환하여 Node로 처리
        RegisterDto regDto = new RegisterDto();
        regDto.setToKey(rcDto.getToKey());
        regDto.setToDirPath(rcDto.getToDirPath());
        regDto.setUserId(rcDto.getUserId());
        regDto.setTocoId(item.getId());
        Document doc = registerService.getContInfoDom(regDto);
        log.info("doc is null={}", (doc == null));
        if ( doc == null ) return;

        //그림목차/동영상목차/표목차 생성
        putExtraNodeList(regDto, doc);

        //2. TO 내용 등록
        //XCont 괸련 Mapper 호출하여 테이블에 등록 (tm_*_xcont insert)
        String tocoType	= getTocoType(doc);
        boolean isFixWUC = isFixTokeyWUC(rcDto.getToKey());		                    // 작업단위부호 (WUC) 검색되도록 교범 고정 - jingi.kim
        boolean isJGReq	= isTocoJGReq(doc);						                    // JG 교범 준비사항의 필수교환품목 검색 누락 보정 - jingi.kim

        rtInt = registerService.insertXCont(regDto, doc, item);
        log.info(" insertToCo - rtInt={}", rtInt);

        switch (tocoType) {
            case VAL.FI_UNIT:
                rtInt = registerService.insertSpecialFI(regDto, doc, item);
                log.info(" insertSpecialFI - rtInt={}", rtInt);
                break;
            case VAL.IPB_UNIT:
                rtInt = registerService.insertSpecialIPB(regDto, doc, item);
                log.info(" insertSpecialIPB - rtInt:{}", rtInt);

                // 작업단위부호 (WUC) 검색되도록 교범 정보 추가 - jingi.kim
                if (isFixWUC) {
                    rtInt = registerService.insertIPBScWucList(regDto, doc, item);
                    log.info("insertSpecialIPB - insertIPBScWucList - rtInt:{}", rtInt);
                }
                break;
            case VAL.WC_UNIT:
                rtInt = registerService.insertWorkCard(regDto, doc, item);
                log.info(" insertWorkCard - rtInt={}", rtInt);
                // 작업단위부호 (WUC) 검색되도록 교범 정보 추가 - jingi.kim
                if (isFixWUC) {
                    rtInt = registerService.insertIPBScWucList(regDto, doc, item);
                    log.info("insertWorkCard - insertIPBScWucList - rtInt:{}", rtInt);
                }
                break;
            default:
                rtInt = registerService.insertDefaultToCo(regDto, doc, item);
                log.info("insertDefaultToCo - rtInt={}", rtInt);
                // 작업단위부호 (WUC) 검색되도록 교범 정보 추가 - jingi.kim
                if (isFixWUC) {
                    rtInt = registerService.insertIPBScWucList(regDto, doc, item);
                    log.info(" insertDefaultToCo - insertIPBScWucList - rtInt:{}", rtInt);
                }
                // JG 교범의 준비사항  검색 되도록 정보 추가 - jingi.kim
                if (isJGReq) {
                    rtInt = registerService.insertScContJG(regDto, doc);
                    log.info(" insertScContJG -  rtInt : {}", rtInt);
                }
                break;
        }

    }

    private void afterSaveDatabase(ExpisXmlWrapper expisXmlWrapper, RegisterDto rcDto) {

        //그림/동영상/표 목차 node 추가
        appendExtraNodes(expisXmlWrapper);

        int rtInt = 0;
        //3. TO 트리, 정보, 목차, 버전목록 등록
        // 교범 목차만 있고 본문 파일이 없을 경우도, 등록은 해야함
        if (expisXmlWrapper != null) {
            RegisterDto regToDto = new RegisterDto();
            regToDto.setToKey(rcDto.getToKey());
            regToDto.setUserId(rcDto.getUserId());
            regToDto.setDbType(rcDto.getDbType());
            regToDto.setIsSysDel(eXPIS3Constants.TREE_KIND_TO);

            //TreeXCont Mapper 호출하여 테이블에 수정 (tm_tree_xcont update)
            rtInt = registerService.updateToSaveYn(rcDto);
            log.info("afterSaveDatabase - updateToSaveYn ===> rtInt:{}", rtInt);


            //TreeXCont Mapper 호출하여 테이블에 등록 (tm_tree_xcont insert)
            try {
                XmlMapper xmlMapper = new XmlMapper();
                xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                String xmlString = null;
                xmlString = xmlMapper.writeValueAsString(expisXmlWrapper);
                xmlString = xmlString.replaceAll("ExpisXmlWrapper", "eXPISInfo");

                rtInt = registerService.insertTreeXCont(regToDto, new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
                log.info("afterSaveDatabase - insertTreeXCont ===> rtInt:{}", rtInt);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            //ToInfo Mapper 호출하여 테이블에 수정 (tm_to_info update)
            if (rtInt >= 0) {
                rtInt = registerService.updateToInfo(regToDto, expisXmlWrapper);
                log.info("afterSaveDatabase -  updateToInfo ===> rtInt:{}", rtInt);
            }

            //TocoInfo Mapper 호출하여 테이블에 등록 (tm_toco_info insert)
            if (rtInt >= 0) {
                rtInt = registerService.insertTocoInfo(regToDto, expisXmlWrapper);
                log.info("afterSaveDatabase -  insertTocoInfo ===> rtInt:{}", rtInt);
            }

            //VersionInfo Mapper 호출하여 테이블에 등록 (tm_ver_info insert)
            if (rtInt >= 0) {
                rtInt = registerService.insertVerInfo(regToDto, expisXmlWrapper);
                log.info("afterSaveDatabase -  insertVerInfo ===> rtInt:{}", rtInt);
            }
        }

        log.info("afterSaveDatabase -  Insert Version Info expisXmlWrapper : {}, rtInt : {}", expisXmlWrapper, rtInt);
        //4. 버전 내용 등록
        if (expisXmlWrapper != null && rtInt >= 0) {
            Document verDoc     = null;
            StringBuffer iconSB	= null;

            //TO의 oldversion/UUID.xml 파일 읽어서 버전내용(Cont) DB 등록
            String verFileDir = rcDto.getToDirPath() + "/" + eXPIS3Constants.AUTH_VERSION;
            log.info("afterSaveDatabase -  Insert Version Info verFileDir : {}", verFileDir);
            String[] verFileList = null;
            File verFilePath = new File(verFileDir);
            if (verFilePath.exists()) {
                verFileList = verFilePath.list();
                log.info("afterSaveDatabase -  Insert Version Info verFileList.length : {}", verFileList.length);
                if (verFileList.length > 0) {
                    for (int i=0; i<verFileList.length; i++) {
                        //TODO: StringUtil
                        String extName = StringUtil.getExtName(verFileList[i]);
                        log.info("afterSaveDatabase -  Insert Version Info verDoc url  : {}/{}", verFileDir, verFileList[i]);
                        if (!extName.equalsIgnoreCase(eXPIS3Constants.FILE_EXTNAME)) {
                            log.info("afterSaveDatabase -  Insert Version Info extName not equalsIgnoreCase");
                            continue;
                        }
                        try {
                            verDoc = registerService.getVerInfoDom(verFileDir + "/" + verFileList[i]);
                            iconSB = registerService.getIconXml(verDoc);
                            log.info("afterSaveDatabase -  Insert Version Info verDoc : {}", verDoc);

                            if (verDoc != null && verDoc.hasChildNodes() && verDoc.getChildNodes().getLength() > 0) {
                                RegisterDto regVerDto = new RegisterDto();
                                regVerDto.setToKey(rcDto.getToKey());
                                regVerDto.setUserId(rcDto.getUserId());
                                regVerDto.setVerFilePath(verFileList[i]);
                                regVerDto.setDbType(rcDto.getDbType());
                                log.info("afterSaveDatabase -  Insert Version Info verDoc : {}", verDoc);

                                //Version XCont 괸련 Mapper 호출하여 테이블에 등록 (tm_ver_xcont insert)
                                rtInt = registerService.insertVerXCont(regVerDto, expisXmlWrapper, verDoc, iconSB);
                                log.info("afterSaveDatabase -  insertVerXCont ===> 4.({}) rtInt:{}, verFile={}", i, rtInt, verFileList[i]);

                                //if (rtInt < 0) { break; }
                            }

                        }catch (Exception e) {
                            log.error("afterSaveDatabase - insertSingleTo Insert Version Info Error ",e);
                        }
                    }
                }

            }
        }


        //5. 이미지 및 정보 파일 이동
        try {
            //업로드 폴더위취
            String dirPath = rcDto.getToDirPath();
            File imgPath = new File(dirPath+"/Image");
            File imgPath2 = new File(dirPath+"/Images");
            File ipbPath = new File(dirPath+"/IPBImage");
            File pngPath = new File(dirPath+"/png");
            File hotPath = new File(dirPath+"/ImageInfo");
            File videoPath = new File(dirPath+"/video");
            File videoPath2 = new File(dirPath+"/Video");
            File iconPath = new File(dirPath+"/ICon");
            File iconPath2 = new File(dirPath+"/Icon");
            File iconPath3 = new File(dirPath+"/icon");
            File outSideFiles = new File(dirPath+"/OutSideFiles");
            File pdfFiles = new File(dirPath+"/paperfile");

            log.info("hotPath : {}", hotPath);
            log.info("ipbPath : {}", ipbPath);
            log.info("imaPath : {}", imgPath);
            log.info("videoPath : {}", videoPath);
            log.info("videoPath2 : {}", videoPath2);
            log.info("iconPath : {}", iconPath);
            log.info("iconPath2 : {}", iconPath);
            log.info("iconPath3 : {}", iconPath);
            log.info("outSideFiles : {}", outSideFiles);
            log.info("pdfFiles : {}", pdfFiles);

            if(imgPath.exists() && imgPath.isDirectory()) {
                log.info("Image folder file copy");
                registerService.copyFiles(imgPath, 1);
            }
            if(imgPath2.exists() && imgPath2.isDirectory()) {
                log.info("Images folder file copy");
                registerService.copyFiles(imgPath2, 1);
            }
            if(ipbPath.exists() && ipbPath.isDirectory()) {
                log.info("IPBImage folder file copy");
                registerService.copyFiles(ipbPath, 1);
            }
            if(pngPath.exists() && pngPath.isDirectory()) {
                log.info("png folder file copy");
                registerService.copyFiles(ipbPath, 1);
            }
            if(hotPath.exists() && hotPath.isDirectory()) {
                log.info("ImageInfo folder file copy");
                registerService.copyFiles(hotPath, 2);
            }
            if(videoPath.exists() && videoPath.isDirectory()) {
                log.info("video folder file copy");
                registerService.copyFiles(videoPath, 3);
            }
            if(videoPath2.exists() && videoPath2.isDirectory()) {
                log.info("video folder file copy");
                registerService.copyFiles(videoPath2, 3);
            }
            if(iconPath.exists() && iconPath.isDirectory()) {
                log.info("iconPath folder file copy");
                registerService.copyFiles(iconPath, 4);
            }
            if(iconPath2.exists() && iconPath2.isDirectory()) {
                log.info("iconPath2 folder file copy");
                registerService.copyFiles(iconPath2, 4);
            }
            if(iconPath3.exists() && iconPath3.isDirectory()) {
                log.info("iconPath3 folder file copy");
                registerService.copyFiles(iconPath3, 4);
            }
            if(outSideFiles.exists() && outSideFiles.isDirectory()) {
                log.info("outSideFiles folder file copy");
                registerService.copyFiles(outSideFiles, 5);
            }
            if(pdfFiles.exists() && pdfFiles.isDirectory()) {
                log.info("pdfFiles folder file copy");
                registerService.copyFiles(pdfFiles, 6);
            }
        }catch (Exception e) {
            log.error("파일 처리중 예외 상황이 발생 했습니다. {}", e.getMessage(), e);
        }

        if (rtInt == 0) {
            rtInt = 1;
        }
        // tm_toco_info 테이블에  TOCO_VEHICLE_TYPE 없는 경우 처리 위해 추가
        try{
            int tempIdx = 0;
            boolean whileFlag = true;
            while(whileFlag) {
                TocoInfoDto dto = new TocoInfoDto();
                dto.setToKey(rcDto.getToKey());
                List<TocoInfoDto> updateList =  registerService.selectUpdateToList(dto);
                if(updateList != null && !updateList.isEmpty()) {
                    log.info("tm_toco_info Update Data  is exist : {}", updateList.size());
                    for (TocoInfoDto tocoInfoDto : updateList) {
                        registerService.updateVehicleType(tocoInfoDto);
                    }
                }else {
                    log.info("tm_toco_info Update Data is not exist");
                    whileFlag = false;
                }
                //무한 루프 방지 위해 추가.
                tempIdx++;
                if(tempIdx > 1000) {
                    whileFlag = false;
                }
            }
        }catch (Exception e) {
            log.error(e.getMessage(),e);
        }


        // KTA 전용 처리 - 색인 검색 업데이트
        try {
            List<SystemXmlWrapper> flatterSystemXmlList = flatterSystemXmlList( Objects.requireNonNull(expisXmlWrapper).getSystemList(), "");

            for (SystemXmlWrapper item : flatterSystemXmlList) {
                if (item.getType().contains("Index")) {
                    RegisterDto regDto = new RegisterDto();
                    regDto.setToKey(rcDto.getToKey());
                    regDto.setToDirPath(rcDto.getToDirPath());
                    regDto.setUserId(rcDto.getUserId());
                    regDto.setTocoId(item.getId());
                    Document doc = registerService.getContInfoDom(regDto);

                    registerService.updateIPBSearchForKTA(doc, rcDto);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private void appendExtraNodes(ExpisXmlWrapper expisXmlWrapper) {
        if (expisXmlWrapper == null) return;
        boolean isSuccess = false;
        if ("T50".equalsIgnoreCase(bizCode)) { isSuccess = true; }
        if ("FA50".equalsIgnoreCase(bizCode)) { isSuccess = true; }
        if ("LSAM".equalsIgnoreCase(bizCode)) { isSuccess = true; }
        if ("KBOB".equalsIgnoreCase(bizCode)) { isSuccess = true; }
        if ("KICC".equalsIgnoreCase(bizCode)) { isSuccess = true; }
        if ("MUAV".equalsIgnoreCase(bizCode)) { isSuccess = true; }
        if ("SENSOR".equalsIgnoreCase(bizCode)) { isSuccess = true; }
        if ( !isSuccess ) return;

        try {
            List<SystemXmlWrapper> systemList = expisXmlWrapper.getSystemList();

            //1.2. 그림/동영상/표목차 생성
            if (!grphNodeList.isEmpty()) {
                SystemXmlWrapper grphSystem = new SystemXmlWrapper();
                grphSystem.setId("GrphToco");
                grphSystem.setName("그림목차");
                grphSystem.setType("GrphToco");
                grphSystem.setSystemList( sortExtraNode(grphNodeList) );
                systemList.add(grphSystem);
            }
            if (!videoNodeList.isEmpty()) {
                SystemXmlWrapper videoSystem = new SystemXmlWrapper();
                videoSystem.setId("VideoToco");
                videoSystem.setName("동영상목차");
                videoSystem.setType("VideoToco");
                videoSystem.setSystemList( sortExtraNode(videoNodeList) );
                systemList.add(videoSystem);
            }
            if (!tableNodeList.isEmpty()) {
                SystemXmlWrapper tableSystem = new SystemXmlWrapper();
                tableSystem.setId("TableToco");
                tableSystem.setName("표목차");
                tableSystem.setType("TableToco");
                tableSystem.setSystemList( sortExtraNode(tableNodeList) );
                systemList.add(tableSystem);
            }

        }catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    // graph/video/table node 정보 추출해서 global 목록에 추가
    private void putExtraNodeList(RegisterDto regDto, Document doc) {
        if (doc == null || regDto == null) {
            return;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression compile = xPath.compile(XALAN.REG_TOCO_COMM);
            Node firstSysNode = (Node) compile.evaluate(doc, XPathConstants.NODE);

            compile = xPath.compile(XALAN.REG_GRPH);
            NodeList grphList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            if (grphList != null && grphList.getLength() > 0) {
                addGrphNode(regDto, grphList, firstSysNode);
            }

            compile = xPath.compile(XALAN.REG_TABLE);
            NodeList tableList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            if (tableList != null && tableList.getLength() > 0) {
                addTableNode(regDto, tableList, firstSysNode);
            }

        } catch (Exception e) {
            log.error("createGrphToco Exception:{}", e.toString());
        }
    }

    private void addGrphNode(RegisterDto regDto, NodeList grphList, Node firstNode) {
        try {
            String listName = "";
            String listType = "";

            if (firstNode != null) {
                Element firstSysElement = (Element) firstNode;
                listName = firstSysElement.getAttribute(ATTR.NAME);
                listType = firstSysElement.getAttribute(ATTR.TYPE);
            }

            for (int loop=0; loop<grphList.getLength(); loop++) {
                Node grphNode = grphList.item(loop);
                Element grphElement = (Element) grphNode;

                if (grphNode != null && grphNode.getNodeName().equals(DTD.GRPH)) {
                    //그래픽 노드에서 정보 추출
                    String grphId 			= grphElement.getAttribute(ATTR.ID);
                    String grphName			= this.getGrphCaption(grphNode);
                    String extPtr			= grphElement.getAttribute(ATTR.GRPH_PATH);
                    //fi 이미지 이미지 리스트에 안들어 가는 현상 수정 하기 위해 추가
                    String type				= grphElement.getAttribute(ATTR.TYPE);
                    String vehicleType = grphElement.getAttribute(ATTR.VEHICLETYPE);
                    if (extPtr.isEmpty() || grphId.isEmpty() || grphName.isEmpty()) {
                        if(!type.isEmpty() && !"fi_mainimg".equals(type)) { continue; }
                    }

                    //2025.04.16 - grphName 이 없을 경우 제외 - jingi.kim
                    if (grphName.isEmpty()) continue;

                    //IPB 도해 이미지일 경우에는 해당목차가 단순 상위 노드가 아닌 partinfo이기에 추출방법 상이
                    String paNodeNm	= "";
                    String paId			= "";
                    String paName		= "";
                    Node paNode = grphNode.getParentNode();
                    Element paElement = (Element) paNode;
                    if (paNode != null) {
                        paNodeNm	= paNode.getNodeName();
                        paId		= paElement.getAttribute(ATTR.ID);
                        paName		= paElement.getAttribute(ATTR.NAME);
                    }

                    //그림목차 해당 노드 생성하여 추가
                    SystemXmlWrapper grphSystem = new SystemXmlWrapper();
                    grphSystem.setId(grphId);
                    grphSystem.setName(grphName);
                    grphSystem.setType(VAL.TOCOLIST_GRPH);
                    grphSystem.setVehicletype(vehicleType);
                    if (paNodeNm.equals(DTD.IPB_PARTINFO)) {
                        grphSystem.setListuuid(paId);
                        grphSystem.setListname(paName);
                        grphSystem.setListtype(VAL.TOCOLIST_IPB);
                    } else {
                        grphSystem.setListuuid(regDto.getTocoId());
                        grphSystem.setListname(listName);
                        grphSystem.setListtype(listType);
                    }

                    int lastIndex = extPtr.lastIndexOf(".");
                    String extName = (lastIndex == -1) ? "" : extPtr.substring(lastIndex+1);
                    log.info("extName : {} => {}", extName, VAL.TOCOLIST_VIDEO_EXT.indexOf(extName.toUpperCase()));
                    if (VAL.TOCOLIST_VIDEO_EXT.contains(extName.toUpperCase())) {
                        //동영상인 경우 type을 Grph가 아닌 Video를 사용하도록 변경
                        grphSystem.setType(VAL.TOCOLIST_VIDEO);
                        grphSystem.setPsysid("VideoToco");
                        videoNodeList.add(grphSystem);
                    } else {
                        grphSystem.setPsysid("GrphToco");
                        grphNodeList.add(grphSystem);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void addTableNode(RegisterDto regDto, NodeList tableList, Node firstNode) {
        try {
            String listName = "";
            String listType = "";

            if (firstNode != null) {
                Element firstSysElement = (Element) firstNode;
                listName = firstSysElement.getAttribute(ATTR.NAME);
                listType = firstSysElement.getAttribute(ATTR.TYPE);
            }

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression compile;

            for (int loop=0; loop<tableList.getLength(); loop++) {
                Node tableNode = tableList.item(loop);
                Element tableElement = (Element) tableNode;

                String tableId		= "";
                String tableName	= "";
                String vehicleType = tableElement.getAttribute(ATTR.VEHICLETYPE);
                boolean isCaption	= false;

                if (tableNode != null && tableNode.getNodeName().equals(DTD.TABLE)) {

                    compile = xPath.compile(XALAN.REG_TABLE_CAPTION);
                    Node entryNode = (Node) compile.evaluate(tableNode, XPathConstants.NODE);

                    //표제목이 <table><rowhddef><entry caption='1'>인것 외에 <table name=''>일 경우에도 표목차 추가
                    if (entryNode != null) {
                        tableId         = tableElement.getAttribute(ATTR.ID);
                        tableName	    = this.getTableName(tableNode);
                        isCaption = true;
                    } else {
                        tableId         = tableElement.getAttribute(ATTR.ID);
                        tableName       = tableElement.getAttribute(ATTR.NAME);
                        if (!tableName.isEmpty() && tableNode.hasChildNodes()) {
                            isCaption = true;
                        }
                    }
                    if (tableId.isEmpty() || tableName.isEmpty()) { continue; }

                    if (isCaption) {
                        //표목차 해당 노드 생성하여 추가
                        SystemXmlWrapper tableSystem = new SystemXmlWrapper();
                        tableSystem.setId(tableId);
                        tableSystem.setName(tableName);
                        tableSystem.setPsysid("TableToco");
                        tableSystem.setType(VAL.TOCOLIST_TABLE);
                        tableSystem.setListuuid(regDto.getTocoId());
                        tableSystem.setListname(listName);
                        tableSystem.setListtype(listType);
                        tableSystem.setVehicletype(vehicleType);
                        tableNodeList.add(tableSystem);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private String getGrphCaption(Node grphNode) {

        String rtStr = "";

        if (grphNode == null || grphNode.getNodeType() != Node.ELEMENT_NODE) {
            return "";
        }

        try {
            String captionMenu = "";
            Element grphElement = (Element) grphNode;

            //<grphprim><text>에서 이미지명 추출
            NodeList childList = grphNode.getChildNodes();
            if (childList.getLength() > 0) {
                for (int i=0; i<childList.getLength(); i++) {

                    Node childNode = childList.item(i);
                    if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    if (childNode.getNodeName().equals(DTD.TEXT)) {
                        String captionStr = childNode.getTextContent().trim();
                        captionMenu = captionStr;

                        if (!captionStr.isEmpty()) {
                            //TODO: CondeConverter
                            captionMenu = CodeConverter.deleteAllCode(captionMenu);
                            captionMenu = CodeConverter.convertEntityToTag(captionMenu);
                        }
                    }
                }
            }

            //이미지명을 text 태그 안에서 추출하는데, 없을 경우 <grphprim name=''>으로 대체 추출
            if (captionMenu.isEmpty()) {
                captionMenu = grphElement.getAttribute(ATTR.NAME);
            }

            rtStr = captionMenu;

        } catch (Exception ex) {
            log.error("{} . getGrphCaption Exception:{}", this.getClass(), ex.toString());
            return "";
        }

        return rtStr;
    }

    /*
     * Table 엔티티 내에서 테이블명 추출 (<table><rowhddef><entry caption='1'>)
     */
    private String getTableName(Node tblNode) {

        StringBuilder rtStr = new StringBuilder();

        if (tblNode == null || tblNode.getNodeType() != Node.ELEMENT_NODE) {
            return "";
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            Node entryNode = (Node) xPath.evaluate(XALAN.REG_TABLE_CAPTION, tblNode, XPathConstants.NODE);
            if (entryNode == null || entryNode.getNodeType() != Node.ELEMENT_NODE) {
                return rtStr.toString();
            }

            Node textNode;
            NodeList textList = entryNode.getChildNodes();
            for (int i=0; i<textList.getLength(); i++) {
                textNode = textList.item(i);

                if (textNode != null && textNode.getNodeType() == Node.ELEMENT_NODE && textNode.getNodeName().equals(DTD.TEXT)) {
                    String textStr = this.getTextFromNode(textNode);
                    //TODO: CondeConverter 처리
                    textStr = CodeConverter.deleteAllCode(textStr);

                    rtStr.append(textStr);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("getTableName Exception:{}", ex.toString());
        }

        return rtStr.toString();
    }

    private List<SystemXmlWrapper> sortExtraNode(List<SystemXmlWrapper> nodeList) {
        return nodeList.stream()
                .filter(item -> item.getName() != null)
                .sorted(Comparator.comparing(item -> extractAllNumber(item.getName()), lexCompare()))
                .toList();
    }

    private static List<Integer> extractAllNumber(String name) {
        Matcher matcher = Pattern.compile("\\d+").matcher(name);
        List<Integer> numbers = new ArrayList<>();
        while (matcher.find()) {
            numbers.add(Integer.parseInt(matcher.group()));
        }
        return numbers;
    }

    private static Comparator<List<Integer>> lexCompare() {
        return (a,b) -> {
            int len = Math.max(a.size(), b.size());
            for( int i=0; i<len; i++ ) {
                int ai = i < a.size() ? a.get(i) : 0;
                int bi = i < b.size() ? b.get(i) : 0;
                if ( ai != bi ) return Integer.compare(ai, bi);
            }
            return 0;
        };
    }

    private String getTextFromNode(Node node) {
        StringBuilder textContent = new StringBuilder();
        if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.ELEMENT_NODE) {
            textContent.append(node.getTextContent().trim()).append(" ");
        }

        NodeList childNodes = node.getChildNodes();
        for ( int i=0; i<childNodes.getLength(); i++ ) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.ELEMENT_NODE) {
                textContent.append(child.getTextContent().trim()).append(" ");
            }
        }
        return textContent.toString().trim();
    }

    //ToCO Type
    private String getTocoType(Document document) {

        String rtType = "default";

        if (document == null) {
            return rtType;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression compile = xPath.compile(XALAN.REG_FI);
            NodeList nodeList = (NodeList) compile.evaluate(document, XPathConstants.NODESET);
            if (nodeList != null && nodeList.getLength() > 0) {
                return VAL.FI_UNIT;
            }

            compile = xPath.compile(XALAN.REG_IPB);
            nodeList = (NodeList) compile.evaluate(document, XPathConstants.NODESET);
            if (nodeList != null && nodeList.getLength() > 0) {
                return VAL.IPB_UNIT;
            }

            compile = xPath.compile(XALAN.REG_WCS);
            nodeList = (NodeList) compile.evaluate(document, XPathConstants.NODESET);
            if (nodeList != null && nodeList.getLength() > 0) {
                return VAL.WC_UNIT;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("isTocoFI Exception:{}", ex.toString());
        }

        return rtType;
    }

    /**
     * 작업단위부호에 사용되는 교범인지 여부 확인 - jingi.kim
     *  - BLOCK2 기준, 교범 번호 FIX
     */
    private boolean isFixTokeyWUC( String tokey ) {

        if ( tokey.equalsIgnoreCase("1A-50A-06") || tokey.equalsIgnoreCase("1A-50A-6") ) 	{ 	return true; 	}
        if ( tokey.equalsIgnoreCase("1T-50A-06") || tokey.equalsIgnoreCase("1T-50A-6") ) 	{ 	return true; 	}
        if ( tokey.equalsIgnoreCase("1A-50A-6WC-1") || tokey.equalsIgnoreCase("1A-50A-6WC-2") || tokey.equalsIgnoreCase("1A-50A-6WC-4") ) 	{ 	return true; 	}
        if ( tokey.equalsIgnoreCase("1T-50A-6WC-1") || tokey.equalsIgnoreCase("1T-50A-6WC-2") || tokey.equalsIgnoreCase("1T-50A-6WC-4") ) 	{ 	return true; 	}
        if ( tokey.contains("1A-50A-4-") ) 	{ 	return true; 	}
        if ( tokey.contains("1T-50A-4-") ) 	{ 	return true; 	}

        return false;
    }

    /**
     * 2024.02.14 - JG교범의 준비사항 여부 확인 - jingi.kim
     */
    private boolean isTocoJGReq(Document doc) {
        if (doc == null) {
            return false;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression compile = xPath.compile("//"+ DTD.TASK +"/"+ DTD.INPUT +"/"+ DTD.IN_REQCOND);
            NodeList reqcondNode = (NodeList) compile.evaluate(doc, XPathConstants.NODE);
            if (reqcondNode != null) {	return true;	}

            compile = xPath.compile("//"+ DTD.TASK +"/"+ DTD.INPUT +"/"+ DTD.IN_PERSON);
            NodeList personNode = (NodeList) compile.evaluate(doc, XPathConstants.NODE);
            if (personNode != null) {	return true;	}

            compile = xPath.compile("//"+ DTD.TASK +"/"+ DTD.INPUT +"/"+ DTD.IN_EQUIP);
            NodeList equipNode = (NodeList) compile.evaluate(doc, XPathConstants.NODE);
            if (equipNode != null) {	return true;	}

            compile = xPath.compile("//"+ DTD.TASK +"/"+ DTD.INPUT +"/"+ DTD.IN_CONSUM);
            NodeList consumNode = (NodeList) compile.evaluate(doc, XPathConstants.NODE);
            if (consumNode != null) {	return true;	}

            compile = xPath.compile("//"+ DTD.TASK +"/"+ DTD.INPUT +"/"+ DTD.IN_ALERT);
            NodeList alertNode = (NodeList) compile.evaluate(doc, XPathConstants.NODE);
            if (alertNode != null) {	return true;	}

            compile = xPath.compile("//"+ DTD.TASK +"/"+ DTD.INPUT +"/"+ DTD.IN_OTHERCOND);
            NodeList otherNode = (NodeList) compile.evaluate(doc, XPathConstants.NODE);
            if (otherNode != null) {	return true;	}

            compile = xPath.compile("//"+ DTD.TASK +"/"+ DTD.INPUT +"/"+ DTD.IN_LINKINFO);
            NodeList linkinNode = (NodeList) compile.evaluate(doc, XPathConstants.NODE);
            if (linkinNode != null) {	return true;	}
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("isTocoJGReq Exception:{}", ex.toString());
        }

        return false;
    }

    //TODO: RegisterService 와 함수 중복
    private List<SystemXmlWrapper> flatterSystemXmlList(List<SystemXmlWrapper> systemList, String pSysId) {
        List<SystemXmlWrapper> flatList = new ArrayList<>();
        for (SystemXmlWrapper sys : systemList) {
            sys.setPsysid(pSysId);
            flatList.add(sys);

            //log.info("sys={}", sys);

            if (sys.getSystemList() != null && !sys.getSystemList().isEmpty()) {
                flatList.addAll( flatterSystemXmlList(sys.getSystemList(), sys.getId()) );
            }
        }
        return flatList;
    }

}
