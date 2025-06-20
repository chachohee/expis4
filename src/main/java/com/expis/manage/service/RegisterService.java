package com.expis.manage.service;

import com.expis.common.eXPIS3Constants;
import com.expis.domparser.*;
import com.expis.ietm.dao.*;
import com.expis.ietm.dto.*;
import com.expis.ietm.parser.ParserDto;
import com.expis.manage.dao.SystemInfoMapper;
import com.expis.manage.dao.ToInfoMapper;
import com.expis.manage.dao.TreeXContMapper;
import com.expis.manage.dto.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService {

    @Value("${app.expis.bizCode}")
    private String bizCode;
    @Value("${app.expis.dfSysPath}")
    private String dfSysPath;

    private final SystemInfoMapper sysInfoMapper;
    private final TreeXContMapper treeMapper;
    private final ToInfoMapper toInfoMapper;
    private final TocoInfoMapper tocoInfoMapper;
    private final XContAllMapper contAllMapper;
    private final XContGraphicMapper contGrphMapper;
    private final XContAlertMapper contAlertMapper;
    private final XContTableMapper contTableMapper;
    private final XContWCMapper contWCMapper;
    private final XContVersionMapper contVersionMapper;
    private final SearchContMapper scContMapper;
    private final SearchFIMapper scFiMapper;
    private final SearchIPBMapper scIpbMapper;
    private final SearchPartinfoMapper scPartMapper;
    private final SearchWucMapper scWucMapper;
    private final VersionInfoMapper versionInfoMapper;
    private final DBInsertErrorService dbInsertErrorService;


    /**
     * 계통트리(System) XCont(XML Data) DB 등록
     * 동시 여러 건수 등록
     */
    public int insertTreeXCont(RegisterDto regDto, InputStream xmlStream) {

        int rtInt = -1;

        if (xmlStream == null || regDto == null) {
            return rtInt;
        }

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(xmlStream, StandardCharsets.UTF_8))){
            String line;
            while ( (line = reader.readLine()) != null ) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("{} . insertTreeXCont Exception={}", this.getClass(),  e.toString());
        }

        try {
            //System Tree XCont(XML Data) Insert
            ArrayList<TreeXContDto> insertList = new ArrayList<>();
            int insertCnt = 0;
            int i = 0;

            List<String> chunks = splitByCharacters(stringBuilder.toString());

            for ( String chunk : chunks ) {
                TreeXContDto treeDto = new TreeXContDto();
                treeDto.setTreeXth( ++i );
                treeDto.setTreeXcont(chunk);
                treeDto.setStatusKind(eXPIS3Constants.STATUS_KIND_VALID);
                treeDto.setCreateUserId(regDto.getUserId());

                switch (regDto.getIsSysDel()) {
                    case eXPIS3Constants.TREE_KIND_SYSTEM -> {
                        treeDto.setTreeKind(eXPIS3Constants.TREE_KIND_SYSTEM);
                        treeDto.setRefToKey("");
                        treeDto.setRefUserId("");
                    }
                    case eXPIS3Constants.TREE_KIND_TO -> {
                        treeDto.setTreeKind(eXPIS3Constants.TREE_KIND_TO);
                        treeDto.setRefToKey(regDto.getToKey());
                        treeDto.setRefUserId("");
                    }
                    case eXPIS3Constants.TREE_KIND_MYTO -> {
                        treeDto.setTreeKind(eXPIS3Constants.TREE_KIND_MYTO);
                        treeDto.setRefToKey("");
                        treeDto.setRefUserId("");
                    }
                }

                insertList.add(treeDto);
                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                    insertCnt += insertList.size();
                    rtInt = treeMapper.insertAllDao(insertList);
                    insertList = new ArrayList<>();
                    if (rtInt <= 0) { break; }
                }
            }
            if (!insertList.isEmpty()) {
                insertCnt += insertList.size();
                rtInt = treeMapper.insertAllDao(insertList);
                insertList = new ArrayList<>();
            }

            log.info("RegisterService . insertTreeXCont insertList.size={}, rtInt={}", insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            rtInt = -1;
            log.error("RegisterService . insertTreeXCont Exception={}", ex.toString());
        }

        return rtInt;
    }

    // 문자 단위로 자르기
    private List<String> splitByCharacters(String text) {
        List<String> result = new ArrayList<>();
        int length = text.length();
        int start = 0;

        while (start<length) {
//            int end = Math.min(start + chunkSize, length);
            int end = text.offsetByCodePoints(start, Math.min(eXPIS3Constants.REG_MAX_STR_LEN, length-start));

            result.add(text.substring(start, end));
            start = end;
        }
        return result;
    }

    /**
     * 계통트리(System) Info DB 등록
     * 동시 여러 건수 등록
     */
    public int insertSystemInfo(RegisterDto regDto, SysTreeXmlWrapper sysTreeXmlWrapper) {

        int rtInt = -1;

        if (sysTreeXmlWrapper == null || regDto == null) {
            return rtInt;
        }

        try {
            //System Tree Info Insert
            ArrayList<SystemInfoDto> insertList = new ArrayList<>();
            int insertCnt = 0;
            int i = 0;

            List<SystemXmlWrapper> flatList = flatterSysTreeXml(sysTreeXmlWrapper.getSystemList(), "");

            for ( SystemXmlWrapper sys : flatList) {
                SystemInfoDto sysDto = new SystemInfoDto();
                sysDto.setSysId(sys.getId());
                sysDto.setPSysId(sys.getPsysid());
                sysDto.setSysOrd(++i);
                sysDto.setSysName(sys.getName());
                sysDto.setSysSubname(sys.getSubname());
                sysDto.setSysType(sys.getType());
                sysDto.setToKey(sys.getItemid());
                sysDto.setStatusKind(eXPIS3Constants.STATUS_KIND_VALID);
                sysDto.setCreateUserId(regDto.getUserId());

                insertList.add(sysDto);

                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                    insertCnt += insertList.size();

                    //많은 데이타 처리 시에 문제 생겨서 해당부분 수정
                    for (SystemInfoDto systemInfoDto : insertList) {
                        ArrayList<SystemInfoDto> tempList = new ArrayList<>();
                        tempList.add(systemInfoDto);
                        rtInt = sysInfoMapper.insertAllDao(tempList);
                    }

                    insertList = new ArrayList<>();
                }
            }

            if (!insertList.isEmpty()) {
                insertCnt += insertList.size();

//                for(int j=0; j<insertCnt; j++) {
//                    rtInt = sysInfoMapper.insertAllDaoMDB(insertList.get(j));
//                }

                rtInt = sysInfoMapper.insertAllDao(insertList);
                insertList = new ArrayList<>();
            }

            log.info("RegisterService . insertSystemInfo List.size={}, rtInt={}", insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("RegisterService . insertSystemInfo Exception:{}", ex.toString());
        }

        return rtInt;
    }

    /**
     * TO Info DB 등록
     * (SysTree.xml 파일과 System_ToInfo.xml 파일을 합친다고 계획되어 적용)
     * 동시 단일 건수 등록
     */
    public int insertToInfo(RegisterDto regDto, SysTreeXmlWrapper sysTreeXmlWrapper) {

        int rtInt = -1;

        if (sysTreeXmlWrapper == null || regDto == null) {
            return rtInt;
        }

        try {

            rtInt = toInfoMapper.deleteMergeDao();

            //TO Info Insert
            ArrayList<ToInfoDto> insertList = new ArrayList<>();
            int insertCnt = 0;

            List<SystemXmlWrapper> flatList = flatterSysTreeXml(sysTreeXmlWrapper.getSystemList(), "", "TO");

            for ( SystemXmlWrapper sys : flatList) {
                if (sys.getId().isEmpty()) {
                    log.info("RegisterService . insertToInfo ToId is NULL Exception");
                    continue;
                }

                ToInfoDto toDto = getToInfoDto(regDto, sys);

                insertList.add(toDto);

                //log.info("toDto={}",toDto);

                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                    insertCnt += insertList.size();

//                    for(int j=0; j<insertCnt; j++) {
//                        rtInt = toInfoMapper.insertAllDaoMDB(insertList.get(j));
//                    }

                    rtInt = toInfoMapper.insertAllDao(insertList);
                    if (rtInt <= 0) { break; }
                    insertList = new ArrayList<>();
//                    insertCnt = 0;
                }

            }

            if (!insertList.isEmpty()) {
                insertCnt += insertList.size();

//                for(int j=0; j<insertCnt; j++) {
//                    rtInt = toInfoMapper.insertAllDaoMDB(insertList.get(j));
//                }
                rtInt = toInfoMapper.insertAllDao(insertList);
            }

            log.info("RegisterService . insertToInfo List.size={}, rtInt={}", insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("RegisterService . insertToInfo Exception={}",ex.toString());
        }

        return rtInt;
    }

    private static ToInfoDto getToInfoDto(RegisterDto regDto, SystemXmlWrapper sys) {
        ToInfoDto toDto = new ToInfoDto();
        toDto.setToKey(sys.getItemid());
        toDto.setToId(sys.getId());
        toDto.setToName(sys.getName());
        toDto.setToSubname(sys.getSubname());
        toDto.setToType(sys.getType());
        toDto.setToToType(sys.getTotype());
        toDto.setToVehicleType(sys.getVehicletype());
        toDto.setToPart(sys.getPart());
        toDto.setToRevNo(sys.getVersion());			    //SysTree 등록시 SysTree.xml 파일 안에 있음
        toDto.setToRevDate(sys.getVersiondate());		//SysTree 등록시 SysTree.xml 파일 안에 있음
        toDto.setToChgNo(sys.getChangeno());
        toDto.setToChgDate(sys.getChgdate());

        toDto.setToDesc(sys.getSupplement());
        toDto.setToSaveYn(sys.getSaveyn());
        toDto.setToFviewYn(eXPIS3Constants.FVIEW_YN_NO);
        toDto.setStatusKind(eXPIS3Constants.STATUS_KIND_VALID);
        toDto.setCreateUserId(regDto.getUserId());
        toDto.setModifyUserId(regDto.getUserId());
        return toDto;
    }

    // 계통정보 DB 삭제
    public int deleteSystem(RegisterDto regDto) {

        int delInt	= 0;		//삭제건수, 0이어도 오류 아님

        try {
            //1) System Tree XCont(XML data) Delete (tm_tree_xcont delete)
            //2) System Tree Info Delete (tm_sys_info delete)

            TreeXContDto treeDto = new TreeXContDto();
            log.info("deleteSystem : {}", treeDto);
            if (regDto.getIsSysDel().equals(eXPIS3Constants.TREE_KIND_SYSTEM) || regDto.getIsSysDel().equals(eXPIS3Constants.TREE_KIND_TO)) {
                treeDto.setTreeKind(regDto.getIsSysDel());
                if (regDto.getIsSysDel().equals(eXPIS3Constants.TREE_KIND_TO) && !regDto.getToKey().isEmpty()) {
                    treeDto.setRefToKey(regDto.getToKey());
                }
                delInt = treeMapper.deleteDao(treeDto);
            } else {
                delInt = treeMapper.deleteAllDao();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("deleteSystem Exception:{}", ex.toString());
        }

        return delInt;
    }

    /*
     * 전체 프로젝트(TO) DB 삭제
     */
    public int deleteProject(RegisterDto rcDto) {

        int rtInt	= -1;
        int delInt	= -1;		//삭제건수, 0이어도 오류 아님

        try {
            RegisterDto regDto = new RegisterDto();
            regDto.setToKey("");
            regDto.setUserId(rcDto.getUserId());
            regDto.setIsSysDel(eXPIS3Constants.TREE_KIND_TO);

            delInt = this.deleteSystem(regDto);
            log.info("deleteProject  1. rtInt:{}", delInt);

            // 전체 프로젝트 삭제시 트리정보에 save_yn 처리하고 재등록 (tm_tree_xcont update)
            if (delInt >= 0) {
                delInt = this.deleteAllToSaveYn(rcDto);
                log.info(" deleteProject deleteAllToSaveYn  1-1. rtInt:{}", delInt);
            }

            if (delInt >= 0) {
                delInt = this.deleteAllTo(regDto);
                log.info(" deleteProject   2. rtInt:{}", delInt);
            }

            if (delInt >= 0) {
                delInt = this.deleteAllXCont(regDto);
                log.info(" deleteProject   3. rtInt:{}", delInt);
            }

            if (delInt >= 0) {
                delInt = this.deleteAllSearch(regDto);
                log.info(" deleteProject   4. rtInt:{}", delInt);
            }

            if (delInt >= 0) {
                rtInt = 1;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(" deleteProject Exception:{}", ex.toString());
        }

        return rtInt;
    }

    /*
     * 전체 교범 DB 삭제시에 트리 정보에 save_yn 처리하고 재 등록 위함
     */
    public int deleteAllToSaveYn(RegisterDto rcDto) {

        int rtInt	= -1;		//등록건수, 0이면 등록한것이 없는것임
        int delInt	= -1;		//삭제건수, 0이어도 오류 아님
        SysTreeXmlWrapper sysTreeXmlWrapper;

        try {
            //파일이 아닌 기존에 등록한 DB를 읽어서 계통트리(SysTree) 정보를 Dom으로 변환하여 Node로 처리
            sysTreeXmlWrapper = this.getSystemInfoDomFromDB(rcDto);
            if (sysTreeXmlWrapper == null) {
                return rtInt;
            }

            //1. TreeXCont 데이터 읽어와서 Attribute 제거해서 Dom 수정
            List<SystemXmlWrapper> systemXmlList = sysTreeXmlWrapper.getSystemList();
            SystemXmlWrapper systemXmlWrapper = new SystemXmlWrapper();
            for (int i = 0; i < systemXmlList.size(); i++) {
                SystemXmlWrapper item = systemXmlList.get(i);
                if ( item.getType().equalsIgnoreCase("TO") ) {
                    item.setSaveyn("");
                }
            }

            //등록 Core에서 사용하는 컬렉션인 RegisterDto에 값 셋팅
            RegisterDto regDto = new RegisterDto();
            regDto.setToKey("");
            regDto.setUserId(rcDto.getUserId());
            regDto.setIsSysDel(eXPIS3Constants.TREE_KIND_SYSTEM);
            regDto.setDbType(rcDto.getDbType());

            //1. TreeXCont Mapper 호출하여 테이블에서 제거 (tm_tree_xcont delete)
            delInt = this.deleteSystem(regDto);
            log.info(" deleteAllToSaveYn   1.delInt:{}", delInt);

            //2. TreeXCont Mapper 호출하여 테이블에 등록 (tm_tree_xcont insert)
            if (delInt >= 0) {
                XmlMapper xmlMapper = new XmlMapper();
                xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                String xmlString = xmlMapper.writeValueAsString(sysTreeXmlWrapper);
                xmlString = xmlString.replaceAll("SysTreeXmlWrapper", "techinfo");

                rtInt = this.insertTreeXCont(regDto, new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
                if (log.isInfoEnabled()) {
                    log.info(" deleteAllToSaveYn  2.rtInt:"+rtInt);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(" deleteAllToSaveYn Exception:{}", ex.toString());
        }

        return rtInt;
    }

    /*
     * 전체 TO DB 삭제
     */
    public int deleteAllTo(RegisterDto regDto) {

        int delInt	= 0;		//삭제건수, 0이어도 오류 아님

        try {
            //2) TO Info Delete (tm_to_info update)
            //3) TOCO 목차 Info Delete (tm_toco_info delete)
            //4) TO 버전 Info Delete (tm_ver_info delete)

            delInt = toInfoMapper.deleteAllDao();
            log.info(" deleteAllTo   1-2. delInt:{}", delInt);

            if (delInt >= 0) {
                delInt = tocoInfoMapper.deleteAllDao();
                log.info(" deleteAllTo  1-3. delInt:{}", delInt);
            }

            if (delInt >= 0) {
                delInt = versionInfoMapper.deleteAllDao();
                log.info(" deleteAllTo  1-4. delInt:{}", delInt);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(" deleteAllTo Exception:{}", ex.toString());
        }

        return delInt;
    }

    /*
     * 전체 TO 내용 DB 삭제
     */
    public int deleteAllXCont(RegisterDto regDto) {

        int delInt	= 0;		//삭제건수, 0이어도 오류 아님

        try {
            //TOCO 목차내용 XCont(XML Data) Delete (tm_*_xcont delete)
            delInt = contAllMapper.deleteAllDao();
            log.info(" deleteAllXCont  1-1. delInt:{}", delInt);

            if (delInt >= 0) {
                delInt = contAlertMapper.deleteAllDao();
                log.info(" deleteAllXCont  1-2. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = contGrphMapper.deleteAllDao();
                log.info(" deleteAllXCont  1-3. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = contTableMapper.deleteAllDao();
                log.info("  deleteAllXCont  1-4. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = contWCMapper.deleteAllDao();
                log.info("  deleteAllXCont  1-5. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = contVersionMapper.deleteAllDao();
                log.info(" deleteAllXCont  1-6. delInt:{}", delInt);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(" deleteAllXCont Exception:{}", ex.toString());
        }

        return delInt;
    }

    /*
     * 전체 TO 중 검색 내용 DB 삭제
     */
    public int deleteAllSearch(RegisterDto regDto) {

        int delInt	= 0;		//삭제건수, 0이어도 오류 아님

        try {
            //TOCO 검색내용 XCont(XML Data) Delete (tm_*_xcont delete)
            delInt = scContMapper.deleteAllDao();
            log.info(" deleteAllSearch  1-1. delInt:{}", delInt);

            if (delInt >= 0) {
                delInt = scFiMapper.deleteAllDao();
                log.info("deleteAllSearch  1-2. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = scIpbMapper.deleteAllDao();
                log.info("deleteAllSearch  1-3. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = scPartMapper.deleteAllDao();
                log.info("deleteAllSearch  1-4. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = scWucMapper.deleteAllDao();
                log.info("deleteAllSearch  1-5. delInt:{}", delInt);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("deleteAllSearch Exception : {}", regDto.getToKey(), ex);
        }

        return delInt;
    }

    // 단일 TO(교범) DB 삭제
    public int deleteSingleTo(RegisterDto rcDto) {

        int rtInt	= -1;
        int delInt	= -1;		//삭제건수, 0이어도 오류 아님

        try {
            //1) TO 목차 XCont(XML Data) Delete (tm_tree_xcont delete)
            //1-1) TO 목차 XCont(XML Data) save_yn 속성 Delete (tm_tree_xcont update) //2023 01 10 jysi ADD
            //2) TO Info Delete (tm_to_info update)
            //3) TOCO 목차 Info Delete (tm_toco_info delete)
            //4) TO 버전 Info Delete (tm_ver_info delete)
            //5) TOCO 목차내용 XCont(XML Data) Delete (tm_*_xcont delete)
            //2022 08 08 Park.J.S. ADD
            //6) PRINT_INFO DELETE

            RegisterDto regDto = new RegisterDto();
            regDto.setToKey(rcDto.getToKey());
            regDto.setUserId(rcDto.getUserId());
            regDto.setIsSysDel(eXPIS3Constants.TREE_KIND_TO);

            {
                delInt = this.deleteSystem(regDto);
                log.info("deleteSingleTo ===> 1. rtInt:{}", delInt);
            }

            if (delInt >= 0) {
                delInt = this.deleteToSaveYn(rcDto);
                log.info("deleteSingleTo deleteToSaveYn ===> 1-1. rtInt:{}", delInt);
            }

            if (delInt >= 0) {
                delInt = this.deleteTo(regDto);
                log.info("deleteSingleTo ===> 2. rtInt:{}", delInt);
            }

            if (delInt >= 0) {
                delInt = this.deleteXCont(regDto);
                log.info("deleteSingleTo ===> 3. rtInt:{}", delInt);
            }

            if (delInt >= 0) {
                delInt = this.deleteSearch(regDto);
                log.info("deleteSingleTo ===> 4. rtInt:{}", delInt);
            }

            if (delInt >= 0) {
                dbInsertErrorService.deleteDBInserError(regDto);
                log.info("deleteSingleTo ===> 5. rtInt:{}", delInt);
            }

            if (delInt >= 0) {
                DBInsertErrorDto dto = new DBInsertErrorDto();
                dto.setToKey(regDto.getToKey());
                delInt = dbInsertErrorService.deletePrintInfo(dto);
                log.info("deletePrintInfo ===> 6. rtInt:{}", delInt);
            }

            if (delInt >= 0) {
                rtInt = 1;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("deleteSingleTo Exception:{}", ex.toString());
        }

        return rtInt;
    }

    /**
     * 단일 단위로 교범 DB 삭제시에 트리 정보에 save_yn 처리하고 재 등록 위함
     */
    public int deleteToSaveYn(RegisterDto rcDto) {

        int rtInt	= -1;		//등록건수, 0이면 등록한것이 없는것임
        int delInt	= -1;		//삭제건수, 0이어도 오류 아님
        SysTreeXmlWrapper sysTreeXmlWrapper;
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            //파일이 아닌 기존에 등록한 DB를 읽어서 계통트리(SysTree) 정보를 Dom으로 변환하여 Node로 처리
            sysTreeXmlWrapper = this.getSystemInfoDomFromDB(rcDto);
            if (sysTreeXmlWrapper == null) {
                return rtInt;
            }

            //1. TreeXCont 데이터 읽어와서 Attribute 제거해서 Dom 수정
            String findToKey = rcDto.getToKey().replace("%amp;", "&");;
            List<SystemXmlWrapper> systemXmlList = sysTreeXmlWrapper.getSystemList();
            SystemXmlWrapper systemXmlWrapper = new SystemXmlWrapper();
            for (int i = 0; i < systemXmlList.size(); i++) {
                SystemXmlWrapper item = systemXmlList.get(i);
                if ( item.getItemid().equals(findToKey) && item.getType().equalsIgnoreCase("TO") ) {
                    item.setSaveyn("");
                }
            }

//            if ( systemXmlWrapper.getId() != null && !systemXmlWrapper.getId().isEmpty() ) {
//                systemXmlWrapper.setSaveyn("");
//            }

            //등록 Core에서 사용하는 컬렉션인 RegisterDto에 값 셋팅
            RegisterDto regDto = new RegisterDto();
            regDto.setToKey("");
            regDto.setUserId(rcDto.getUserId());
            regDto.setIsSysDel(eXPIS3Constants.TREE_KIND_SYSTEM);
            regDto.setDbType(rcDto.getDbType());

            //1. TreeXCont Mapper 호출하여 테이블에서 제거 (tm_tree_xcont delete)
            delInt = this.deleteSystem(regDto);
            log.info("RegisterComponent.deleteToSaveYn ===> 1.delInt:{}", delInt);

            //2. TreeXCont Mapper 호출하여 테이블에 등록 (tm_tree_xcont insert)
            if (delInt >= 0) {
                String xmlString = xmlMapper.writeValueAsString(sysTreeXmlWrapper);
                xmlString = xmlString.replaceAll("SysTreeXmlWrapper", "techinfo");
                rtInt = this.insertTreeXCont(regDto, new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
                log.info("RegisterComponent.deleteToSaveYn ===> 2.rtInt:{}", rtInt);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("RegisterComponent.deleteToSaveYn Exception: {}", ex.toString());
        }

        return rtInt;
    }

    /**
     * TO DB 삭제
     */
    public int deleteTo(RegisterDto regDto) {

        int delInt	= 0;		//삭제건수, 0이어도 오류 아님

        try {
            //1) TO 목차 XCont(XML Data) Delete (tm_tree_xcont delete)
			/*
			{
				TreeXContDto treeDto = new TreeXContDto();
				treeDto.setTreeKind(IConstants.TREE_KIND_TO);
				treeDto.setRefToKey(regDto.getToKey());

				delInt = treeMapper.deleteDao(treeDto);
				logger.info("deleteTo ===> 1-1. delInt:"+delInt);
			}
			*/
            delInt = 1;

            //2) TO Info Delete (tm_to_info update)
            ToInfoDto toDto = new ToInfoDto();
            toDto.setToKey(regDto.getToKey());
            toDto.setPropertyKind(eXPIS3Constants.TOPROP_YN_SAVE);
            toDto.setToSaveYn(eXPIS3Constants.TOSAVE_YN_NO);
            toDto.setModifyUserId(regDto.getUserId());

            delInt = toInfoMapper.updatePropertyDao(toDto);
            log.info("deleteTo ===> 1-2. delInt:{}", delInt);

            //3) TOCO 목차 Info Delete (tm_toco_info delete)
            if (delInt >= 0) {
                TocoInfoDto tocoDto = new TocoInfoDto();
                tocoDto.setToKey(regDto.getToKey());

                delInt = tocoInfoMapper.deleteDao(tocoDto);
                log.info("deleteTo ===> 1-3. delInt:{}", delInt);
            }

            //4) TO 버전 Info Delete (tm_ver_info delete)
            if (delInt >= 0) {
                VersionInfoDto verDto = new VersionInfoDto();
                verDto.setToKey(regDto.getToKey());

                delInt = versionInfoMapper.deleteDao(verDto);
                log.info("deleteTo ===> 1-4. delInt:{}", delInt);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("deleteTo Exception:{}", ex.toString());
        }

        return delInt;
    }

    /**
     * TO 내용 DB 삭제
     * TOCO 목차내용 XCont(XML Data) Delete (tm_all_xcont delete)
     * - all, alert, graphic, table, wc, version
     */
    public int deleteXCont(RegisterDto regDto) {

        int delInt	= 0;		//삭제건수, 0이어도 오류 아님

        try {
            //TOCO 목차내용 XCont(XML Data) Delete (tm_*_xcont delete)
            XContDto contDto = new XContDto();
            contDto.setToKey(regDto.getToKey());

            {
                delInt = contAllMapper.deleteDao(contDto);
                log.info("deleteXCont ===> 1-1. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = contAlertMapper.deleteDao(contDto);
                log.info("deleteXCont ===> 1-2. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = contGrphMapper.deleteDao(contDto);
                log.info("deleteXCont ===> 1-3. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = contTableMapper.deleteDao(contDto);
                log.info("deleteXCont ===> 1-4. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = contWCMapper.deleteDao(contDto);
                log.info("deleteXCont ===> 1-5. delInt:{}", delInt);
                try {
                    //TODO: bizCode
                    if("KTA".equalsIgnoreCase(bizCode)) {
                        contWCMapper.deleteDaoKTA(contDto);
                    }
                }catch (Exception e) {
                    log.error(e.getMessage(),e);
                }

            }
            if (delInt >= 0) {
                delInt = contVersionMapper.deleteDao(contDto);
                log.info("ContRegister.deleteXCont ===> 1-6. delInt:{}", delInt);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("deleteXCont Exception:{}", ex.toString());
        }

        return delInt;
    }

    /**
     * TO 중 검색 내용 DB 삭제
     * TOCO 검색내용 XCont(XML Data) Delete (tm_sc_cont delete)
     * - cont, fi, ipb, partinfo, wuc
     */
    public int deleteSearch(RegisterDto regDto) {

        int delInt	= 0;		//삭제건수, 0이어도 오류 아님

        try {
            //TOCO 검색내용 XCont(XML Data) Delete (tm_sc_cont delete)
            SearchDto scDto = new SearchDto();
            scDto.setToKey(regDto.getToKey());
            SearchPartinfoDto scPartDto = new SearchPartinfoDto();
            scPartDto.setToKey(regDto.getToKey());

            delInt = scContMapper.deleteDao(scDto);
            log.info("deleteSearch ===> 1-1. delInt:{}", delInt);

            if (delInt >= 0) {
                delInt = scFiMapper.deleteDao(scDto);
                log.info("deleteSearch ===> 1-2. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = scIpbMapper.deleteDao(scPartDto);
                log.info("deleteSearch ===> 1-3. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = scPartMapper.deleteDao(scPartDto);
                log.info("deleteSearch ===> 1-4. delInt:{}", delInt);
            }
            if (delInt >= 0) {
                delInt = scWucMapper.deleteDao(scDto);
                log.info("deleteSearch ===> 1-5. delInt:{}", delInt);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("deleteSearch Exception : {}", regDto.getToKey(), ex);
        }

        return delInt;
    }

    /**
     * 이미지 및 핫스팟 정보 복사를 위해 추가
     * 서버상에 존재하는 업로드 해야하는 실제 이미지위치(imaPath)를 전달 받아서 이미지 타입(fileType)에 따른 경로에 해당 이미지 업로드
     * 중복된 명칭의 파일은 덮어쓰므로 파일 업로드시에 주의
     * 이미지 및 파일레 관한 확장자가 정해지면 해당 확장자 확인하는 로직 추가해서 보안 강화 가능
     * 3 비디오, 4 아이콘, 5 외부참조 파일
     * 6 PDF 파일
     * param imaPath : 서버상 실제 해당 이미지 파일의 위치
     * param fileType : 1 이미지, 2 핫스팟, 3 비디오, 4 아이콘, 5 외부참조 파일 , 6 PDF 파일
     */
    public void copyFiles(File fileFolder, int fileType) {
        File[] sourceFiles =  fileFolder.listFiles();
        if (sourceFiles == null) return;

        for (File sourceFile : sourceFiles) {
            try {
                //log.info(sourceFiles[i].getAbsolutePath());

                String file_ext = "";
                //String source_dir = fileFolder.getAbsolutePath();
                String target_dir;
                switch (fileType) {
                    case 1:
                        target_dir = dfSysPath + "image\\" + sourceFile.getName();
                        break;
                    case 2:
                        target_dir = dfSysPath + "imagedata\\" + sourceFile.getName();
                        file_ext = ".xml";
                        break;
                    case 3:
                        target_dir = dfSysPath + "video\\" + sourceFile.getName();
                        file_ext = ".avi"; //.mp4
                        break;
                    case 4:
                        target_dir = dfSysPath + "icon\\" + sourceFile.getName();
                        break;
                    case 5:
                        target_dir = dfSysPath + "outsidefile\\" + sourceFile.getName();
                        break;
                    case 6:
                        target_dir = dfSysPath + "paperfile\\" + sourceFile.getName();
                        file_ext = ".pdf";
                        break;
                    default:
                        target_dir = "";
                        file_ext = "";
                }
                if (!target_dir.isEmpty()) {
                    Files.createDirectories(Paths.get(target_dir));
                }
                boolean isCopy = true;
                if ( !file_ext.isEmpty()) {
                    if ( sourceFile.toString().toLowerCase().endsWith(file_ext) ) {
                        isCopy = false;
                    }
                }
                if (isCopy) {
                    Path source = Paths.get(sourceFile.getAbsolutePath());
                    Path target = Paths.get(target_dir, sourceFile.toString());
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                }

                /* 폴더 채 진행하는 함수 ( For 루틴 제외. )
                String finalFile_ext = file_ext;
                String finalTarget_dir = target_dir;
                Files.walkFileTree(Paths.get(source_dir), new SimpleFileVisitor<Path>(){
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        boolean isCopy = true;
                        if ( !finalFile_ext.isEmpty()) {
                            if ( file.toString().toLowerCase().endsWith(finalFile_ext) ) {
                                isCopy = false;
                            }
                        }
                        if ( isCopy ) {
                            Path targetPath = Paths.get(finalTarget_dir, file.getFileName().toString());
                            Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        }

                        return FileVisitResult.CONTINUE;
                    }
                });*/

            } catch (Exception e) {
                log.error("File upload Fail : {}", e.getMessage());
            }
        }

    }

    // TO 디렉토리 내 UUID.xml 파일을 XML 로 읽어 DOM으로 반환
    public Document getContInfoDom(RegisterDto rcDto) {

        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            //입력받은 TO 폴더에서 목차의 내용 파일인 UUID.xml를 추출해서 Dom 생성
            String toDirPath = rcDto.getToDirPath();
            String tocoFilePath = toDirPath + "/" + rcDto.getTocoId() + ".xml";
            log.info("getContInfoDom path={}, file={}", rcDto.getToDirPath(), rcDto.getTocoId()+".xml");
            if (toDirPath == null || toDirPath.isEmpty()) {
                return null;
            }

            File tocoFile = new File(tocoFilePath);
            log.info("{} file is exists={}", tocoFilePath, tocoFile.exists());
            if (tocoFile.exists()) {
                //TODO: euc-kr 인코딩 체크 하는 함수임 - 필요한지 확인
                // StringBuffer sbCont = this.getUniCont(tocoFilePath);

                DocumentBuilder builder = factory.newDocumentBuilder();
                doc = builder.parse(tocoFile);
//                if (sbCont != null && !sbCont.toString().equals("")) {
//                    doc = builder.parse(tocoFile);
//                } else {
//                    doc = builder.parse(tocoFile);
//                }
            }

        }  catch (Exception ex) {
            ex.printStackTrace();
            log.info("getContInfoDom Exception:{}", ex.toString());
        }

        return doc;
    }

    //XCont 등록 - 공통
    public int insertXCont(RegisterDto regDto, Document doc, SystemXmlWrapper item) {
        int rtInt = -1;

        rtInt = this.insertAlertXCont(regDto, doc);
        log.info(" insertAlertXCont  rtInt:{}", rtInt);

        if (rtInt >= 0) {
            rtInt = this.insertTableXCont(regDto, doc);
            log.info(" insertTableXCont  rtInt:{}", rtInt);
        }

        if (rtInt >= 0) {
            rtInt = this.insertWCXCont(regDto, doc);
            log.info(" insertWCXCont  rtInt:{}", rtInt);
        }

        return rtInt;
    }

    /*
     * TO 내용(UUID.xml) 중 Alert 관련 XCont(XML Data) DB 등록
     */
    private int insertAlertXCont(RegisterDto regDto, Document doc) {

        int rtInt = 0;		//등록할 Alert 데이터가 없을 수도 있기에 0으로 초기화
        if (doc == null || regDto == null) {
            return rtInt;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile(XALAN.REG_ALERT);
            NodeList alertList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            if (alertList.getLength() <= 0) {
                return rtInt;
            }

            ArrayList<XContDto> insertList = new ArrayList<>();
            int insertCnt = 0;

            for (int loop=0; loop<alertList.getLength(); loop++) {
                Node alertNode = alertList.item(loop);

                if (alertNode != null && alertNode.getNodeName().equals(DTD.ALERT)) {
                    ArrayList<XContDto> tempList = this.insertAlertXContSet(regDto, alertNode);
                    if (tempList != null && !tempList.isEmpty()) {
                        insertList.addAll(tempList);
                    }

                    if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                        rtInt = contAlertMapper.insertAllDao(insertList);
                        insertList = new ArrayList<>();
                        if (rtInt <= 0) { break; }
                    }
                }
            }

            if (!insertList.isEmpty()) {
                rtInt = contAlertMapper.insertAllDao(insertList);
                insertCnt += insertList.size();
                insertList = new ArrayList<>();
            }

            log.info("insertAlertXCont insertList.size():{}, rtInt:{}", insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("ContRegister.insertAlertXCont Exception:{}", ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 Alert 관련 XCont(XML Data) DB 등록 실행
     */
    private ArrayList<XContDto> insertAlertXContSet(RegisterDto regDto, Node alertNode) {

        ArrayList<XContDto> rtList = new ArrayList<>();

        if (alertNode == null || alertNode.getNodeType() != Node.ELEMENT_NODE || regDto == null) {
            return rtList;
        }
        if (!alertNode.getNodeName().equals(DTD.ALERT)) {
            return rtList;
        }

        try {
            Element alertElement = (Element) alertNode;
            String alertId = alertElement.getAttribute(ATTR.ID);
            if (alertId.isEmpty()) {
                return rtList;
            }

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            StringBuilder contSB = new StringBuilder();
            transformer.transform(new DOMSource(alertNode), new StreamResult(stringWriter));
            contSB.append(stringWriter);

            List<String> chunks = splitByCharacters(contSB.toString());

            int i = 0;
            for ( String chunk : chunks ) {
                XContDto contDto = new XContDto();
                contDto.setToKey(regDto.getToKey());
                contDto.setTocoId(regDto.getTocoId());
                contDto.setAltId(alertId);
                contDto.setXth( ++i );
                contDto.setXcont(chunk);
                contDto.setCreateUserId(regDto.getUserId());
                rtList.add(contDto);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("ContRegister.insertAlertXContSet Exception:{}", ex.toString());
        }

        return rtList;
    }

    /**
     * TO목차내용(UUID.xml) 중 Table 관련 XCont(XML Data) DB 등록
     */
    private int insertTableXCont(RegisterDto regDto, Document doc) {

        int rtInt = 0;		//등록할 Table 데이터가 없을 수도 있기에 0으로 초기화
        if (doc == null || regDto == null) {
            return rtInt;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile(XALAN.REG_TABLE);
            NodeList tblList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            if (tblList.getLength() <= 0) {
                return rtInt;
            }

            ArrayList<XContDto> insertList = new ArrayList<>();
            int insertCnt = 0;

            for (int loop=0; loop<tblList.getLength(); loop++) {
                Node tblNode = tblList.item(loop);

                if (tblNode != null && tblNode.getNodeName().equals(DTD.TABLE)) {
                    ArrayList<XContDto> tempList = this.insertTableXContSet(regDto, doc, tblNode);
                    if (tempList != null && !tempList.isEmpty()) {
                        insertList.addAll(tempList);
                    }

                    if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                        rtInt = contTableMapper.insertAllDao(insertList);
                        insertList = new ArrayList<>();
                        if (rtInt <= 0) { break; }
                    }
                }
            }

            if (!insertList.isEmpty()) {
                rtInt = contTableMapper.insertAllDao(insertList);
                insertList = new ArrayList<>();
            }

            log.info("insertTableXCont insertList.size():{}, rtInt:{}", insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("insertTableXCont Exception:{}", ex.getMessage(),ex);
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 Table 관련 XCont(XML Data) DB 등록 실행
     */
    public ArrayList<XContDto> insertTableXContSet(RegisterDto regDto, Document doc, Node tblNode) {

        ArrayList<XContDto> rtList = new ArrayList<>();

        if (tblNode == null || tblNode.getNodeType() != Node.ELEMENT_NODE || regDto == null) {
            return rtList;
        }
        if (!tblNode.getNodeName().equals(DTD.TABLE)) {
            return rtList;
        }

        try {
            Element tblElement = (Element) tblNode;

            String tblId = tblElement.getAttribute(ATTR.ID);
            if (tblId.isEmpty()) {
                return rtList;
            }

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            String tblCaption = this.getTableName(tblNode);

            StringBuilder contSB = new StringBuilder();

            transformer.transform(new DOMSource(tblNode), new StreamResult(stringWriter));
            contSB.append(stringWriter);

            contSB.append( this.getTableIconXml(doc, tblNode) );	//icon list 추가

            List<String> chunks = splitByCharacters(contSB.toString());

            int i = 0;
            for ( String chunk : chunks ) {
                XContDto contDto = new XContDto();
                contDto.setToKey(regDto.getToKey());
                contDto.setTocoId(regDto.getTocoId());
                contDto.setTblId(tblId);
                contDto.setTblCaption(tblCaption);
                contDto.setXth( ++i );
                contDto.setXcont(chunk);
                contDto.setCreateUserId(regDto.getUserId());
                rtList.add(contDto);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("insertTableXContSet Exception:{}", ex.toString());
        }

        return rtList;
    }

    /**
     * Table 엔티티 내에서 테이블명 추출 (<table><rowhddef><entry caption='1'>)
     */
    public String getTableName(Node tblNode) {

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

    /**
     * Table 엔티티 내에서 표 데이터가 icon 이미지로 되 있을경우 추출
     *  <table><rowhddef><entry><text>&#24;</text>, <icon>
     */
    private String getTableIconXml(Document doc, Node tblNode) {

        StringBuilder rtStr = new StringBuilder();

        if (tblNode == null || tblNode.getNodeType() != Node.ELEMENT_NODE) {
            return "";
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            Node textNode;
            NodeList textList = (NodeList) xPath.evaluate(XALAN.REG_TABLE_ICON, tblNode, XPathConstants.NODESET);

            if (textList.getLength() <= 0) {
                return rtStr.toString();
            }

            for (int i=0; i<textList.getLength(); i++) {
                textNode = textList.item(i);

                if (textNode != null && textNode.getNodeType() == Node.ELEMENT_NODE && textNode.getNodeName().equals(DTD.TEXT)) {
                    String textStr = getTextFromNode(textNode);
                    //TODO: CodeConverter
                    textStr = CodeConverter.convertTagToEntity(textStr);

                    if (textStr.isEmpty()) { continue; }

                    String[] arrText = textStr.split(CODE.SYMBOL_SEMI+"");
                    for (int j=0; j<arrText.length; j++) {

                        if (arrText[j].trim().equals(CODE.ECD_ICON) && j < (arrText.length -1)) {
                            String iconId = arrText[j+1].replace(CODE.ECD_ICON, "");
                            Node iconNode = (Node) xPath.evaluate(XALAN.getIconFromIdXPathSql(iconId), doc, XPathConstants.NODE);
                            if (iconNode != null) {
                                transformer.transform(new DOMSource(iconNode), new StreamResult(stringWriter));
                                rtStr.append(stringWriter);
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("ContRegister.getTableIconXml Exception:{}", ex.toString());
        }

        return rtStr.toString();
    }

    /**
     * TO목차내용(UUID.xml) 중 Workcard 관련 XCont(XML Data) DB 등록
     */
    private int insertWCXCont(RegisterDto regDto, Document doc) {

        int rtInt = 0;		//등록할 alert 데이터가 없을 수도 있기에 0으로 초기화
        if (doc == null || regDto == null) {
            return rtInt;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            //<workcards>
            XPathExpression compile = xPath.compile(XALAN.REG_WCS);
            NodeList wcsList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);

            if (wcsList.getLength() <= 0) {
                return rtInt;
            }

            ArrayList<XContDto> insertList = new ArrayList<XContDto>();
            int insertCnt = 0;

            for (int loop=0; loop<wcsList.getLength(); loop++) {
                Node wcsNode = wcsList.item(loop);

                if (wcsNode != null && wcsNode.getNodeName().equals(DTD.WC_WCS)) {
                    //<workcard> 엔티티가 있으면 부대WC, 없으면 야전WC
                    NodeList wcList = wcsNode.getChildNodes();

                    //if는 부대급TO, else는 야전급TO
                    if (wcList.getLength() > 0) {
                        for (int sub=0; sub<wcList.getLength(); sub++) {
                            Node wcNode = wcList.item(sub);
                            Element wcElement = (Element) wcNode;

                            if (wcNode != null && wcNode.getNodeName().equals(DTD.WC_WORKCARD)) {
                                ArrayList<XContDto> tempList = this.insertWCXContSet(regDto, wcNode);
                                if (tempList != null && !tempList.isEmpty()) {
                                    insertList.addAll(tempList);
                                }

                                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                                    rtInt = contWCMapper.insertAllDao(insertList);
                                    insertList = new ArrayList<XContDto>();
                                    if (rtInt <= 0) { break; }
                                }
                                // WC 카드 내 계통별 링크 기능구현 위해 추가
                                if("KTA".equalsIgnoreCase(bizCode)) {
                                    setWcSystemLink(wcNode, regDto.getToKey(), wcElement.getAttribute(ATTR.ID));
                                }
                            }
                        }
                    } else {
                        ArrayList<XContDto> tempList = this.insertWCXContSet(regDto, wcsNode);
                        if (tempList != null && !tempList.isEmpty()) {
                            insertList.addAll(tempList);
                        }

                        if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                            rtInt = contWCMapper.insertAllDao(insertList);
                            insertList = new ArrayList<XContDto>();
                            if (rtInt <= 0) { break; }
                        }
                        // WC 카드 내 계통별 링크 기능구현 위해 추가
                        if("KTA".equalsIgnoreCase(bizCode)) {
                            Element wcsElement = (Element) wcsNode;
                            setWcSystemLink(wcsNode, regDto.getToKey(), wcsElement.getAttribute(ATTR.ID));
                        }
                    }

                }
            }
            if (!insertList.isEmpty()) {
                rtInt = contWCMapper.insertAllDao(insertList);
                insertCnt += insertList.size();
                insertList = new ArrayList<>();
            }

            log.info("insertWCXCont insertList.size():{}, rtInt:{}", insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("insertWCXCont Exception:{}", ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 Workcard 관련 XCont(XML Data) DB 등록 실행
     */
    private ArrayList<XContDto> insertWCXContSet(RegisterDto regDto, Node wcNode) {

        ArrayList<XContDto> rtList = new ArrayList<>();

        if (wcNode == null || wcNode.getNodeType() != Node.ELEMENT_NODE || regDto == null) {
            return rtList;
        }
        if (!wcNode.getNodeName().equals(DTD.WC_WCS) && !wcNode.getNodeName().equals(DTD.WC_WORKCARD)) {
            return rtList;
        }

        try {
            Element wcElement = (Element) wcNode;

            String wcId		= wcElement.getAttribute(ATTR.ID);
            if (wcId.isEmpty()) {
                return rtList;
            }

            String cardNo	= wcElement.getAttribute(ATTR.WC_CARDNO);
            String wcType	= this.getWCTypeCode(wcElement.getAttribute(ATTR.WC_TYPE));
            // WC 카드 내 계통별 링크 기능구현 위해 추가
            String wcSystem	= "";

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            StringBuilder contSB = new StringBuilder();	//<system> 태그로 감싸기
            transformer.transform(new DOMSource(wcNode), new StreamResult(stringWriter));
            contSB.append(stringWriter);
            contSB.insert(0, "<"+DTD.SYSTEM+">");
            contSB.append("</"+DTD.SYSTEM+">");


            List<String> chunks = splitByCharacters(contSB.toString());

            boolean isWCLink = "KTA".equalsIgnoreCase(bizCode) || "KBOB".equalsIgnoreCase(bizCode) || "KICC".equalsIgnoreCase(bizCode) || "MUAV".equalsIgnoreCase(bizCode) || "SENSOR".equalsIgnoreCase(bizCode);
            int i = 0;
            for ( String chunk : chunks ) {
                XContDto contDto = new XContDto();
                contDto.setToKey(regDto.getToKey());
                contDto.setTocoId(wcId);	 //WC TO는 wc_id가 목차_id가 됨
                contDto.setCardNo(cardNo);
                contDto.setWcType(wcType);
                contDto.setXth( ++i );
                contDto.setXcont(chunk);

                // WC 카드 내 계통별 링크 기능구현 위해 추가 ==>  KTA 일경우에만 WcSystem 추가 LSAM 및 다른 디비 영향도 처리 위해
                if(isWCLink) {
                    if(wcSystem.isEmpty()) {
                        log.info("Set wcSystem : {}", wcSystem);
                        NodeList cList = wcNode.getChildNodes();
                        for(int j=0;j<cList.getLength();j++) {
                            Node cNode = cList.item(j);

                            if(cNode.getNodeType() != Node.ELEMENT_NODE) {
                                continue;
                            }

                            Element cElement = (Element) cNode;
                            wcSystem = cElement.getAttribute(ATTR.WC_SYSTEM);
                            log.info("Set wcSystem : {}", wcSystem);

                            if(!wcSystem.isEmpty()) {
                                break;
                            }
                        }
                    }
                    contDto.setWcSystem(wcSystem);
                }
                contDto.setCreateUserId(regDto.getUserId());
                rtList.add(contDto);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("insertWCXContSet Exception:{}", ex.toString());
        }

        return rtList;
    }

    /**
     * 작업카드(WC) 타입을 리터럴로 된 문구를 코드로 변환
     */
    private String getWCTypeCode(String typeValue) {

        if (typeValue == null || typeValue.isEmpty()) {
            return "";
        }

        String rtStr = "";

        String[] arrTypeCode	= {
                "01", "02", "03", "04", "05",
                "06", "07", "08", "09", "10"
        };
        String[] arrTypeValue	= {
                "TYPEA", "TYPEA_IMG", "TYPEB", "TYPEB_IMG", "TYPEB_2",
                "TYPEC", "TYPEC_2", "TYPED", "TYPEE", "TYPEF"
        };

        try {
            for (int i=0; i<arrTypeCode.length; i++) {
                if (typeValue.toUpperCase().equals(arrTypeValue[i])) {
                    rtStr = arrTypeCode[i];
                    break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("ContRegister.getWCType Exception:{}", ex.toString());
        }

        return rtStr;
    }

    /**
     * WC 카드 내 계통별 링크 구현
     */
    private void setWcSystemLink(Node workcardNode,String toKey, String tocoId) {
        try {
            String name			= "";//구분
            String steptime		= "";//인시분
            String steparea		= "";//작업구역
            String system		= "";//계통
            String subsystem	= "";//하부계통
            String cont			= "";//검사내용
            NodeList wstepList = workcardNode.getChildNodes();
            log.info("wstepList : {}", wstepList.getLength());

            for(int j=0; j<wstepList.getLength(); j++) {
                Node wstepNode = wstepList.item(j);
                if(wstepNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                log.info("wstepNode : {}", wstepNode.getNodeName());

                Element wstepElement = (Element) wstepNode;

                ParserDto psDto = new ParserDto();
                psDto.setSearchWord("");
                psDto.setVehicleType("");
//                psDto.setBizSyspath( StringUtil.checkNull( StringUtil.getRemoveSlash(ext.getDF_SYSPATH()) ) );
//                psDto.setBizIetmdata( StringUtil.checkNull( StringUtil.getRemoveSlash(ext.getDF_IETMDATA()) ) );
                steptime	= wstepElement.getAttribute("steptime");
                steparea	= wstepElement.getAttribute("steparea");
                system		= wstepElement.getAttribute("system");
                subsystem	= wstepElement.getAttribute("subsystem");
                cont 		= "";

                NodeList textNodeList = wstepNode.getChildNodes();
                log.info("textNodeList : {}", textNodeList.getLength());
                for(int k=0;k<textNodeList.getLength();k++) {
                    Node textNode = textNodeList.item(k);
                    if(textNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    if("제목".equalsIgnoreCase(wstepElement.getAttribute(ATTR.TYPE))) {
                        if("text".equalsIgnoreCase(textNode.getNodeName())) {
                            name = textNode.getTextContent();
                            name = name.replace(CHAR.CHAR_LT, CHAR.MARK_LT);
                            name = name.replace(CHAR.CHAR_GT, CHAR.MARK_GT);
                            name = name.replace(CODE.ENM_LT, CHAR.MARK_LT);
                            name = name.replace(CODE.ENM_GT, CHAR.MARK_GT);
                            // 머리말기호
                            if (name.contains(CODE.ENM_QUOT) && name.indexOf(CODE.ENM_QUOT) < 3) {
                                // 2022. 02. 16 Park.J.S : 따움표 치환처리 체크 추가
                                boolean isQuotReplace = "T50".equalsIgnoreCase(bizCode) || "FA50".equalsIgnoreCase(bizCode) || "BLOCK2".equalsIgnoreCase(bizCode);
                                if (isQuotReplace) {
                                    if (name.startsWith("")) {// "로 시작할경우
                                        name = name.replace(CODE.ENM_QUOT, CHAR.MARK_DOT);
                                    } else if (name.indexOf("&#13;" + CODE.ENM_QUOT) > 0) {// 줄바꿈후 "일경우
                                        name = name.replace("&#13;" + CODE.ENM_QUOT, "&#13;" + CHAR.MARK_DOT);
                                    } else if (name.indexOf("&amp;#13;" + CODE.ENM_QUOT) > 0) {// 줄바꿈후 "일경우
                                        name = name.replace("&amp;#13;" + CODE.ENM_QUOT, "&amp;#13;" + CHAR.MARK_DOT);
                                    } else if (name.indexOf("<br/>" + CODE.ENM_QUOT) > 0) {// 줄바꿈후 "일경우
                                        name = name.replace("<br/>" + CODE.ENM_QUOT, "<br/>" + CHAR.MARK_DOT);
                                    } else if (name.indexOf("<br>" + CODE.ENM_QUOT) > 0) {// 줄바꿈후 "일경우
                                        name = name.replace("<br>" + CODE.ENM_QUOT, "<br>" + CHAR.MARK_DOT);
                                    }
                                }
                            }
                            //TODO: CodeConverter
                            name = CodeConverter.getCodeConverter(psDto, name, "", "");
                        }
                    }else {
                        if(!"".equalsIgnoreCase(system)) {
                            if("text".equalsIgnoreCase(textNode.getNodeName())) {
                                cont = textNode.getTextContent();
                                cont = cont.replace(CHAR.CHAR_LT, CHAR.MARK_LT);
                                cont = cont.replace(CHAR.CHAR_GT, CHAR.MARK_GT);
                                cont = cont.replace(CODE.ENM_LT, CHAR.MARK_LT);
                                cont = cont.replace(CODE.ENM_GT, CHAR.MARK_GT);
                                // 머리말기호
                                if (cont.contains(CODE.ENM_QUOT) && cont.indexOf(CODE.ENM_QUOT) < 3) {
                                    // 따움표 치환처리 체크 추가
                                    boolean isQuotReplace = "T50".equalsIgnoreCase(bizCode) || "FA50".equalsIgnoreCase(bizCode) || "BLOCK2".equalsIgnoreCase(bizCode);
                                    if (isQuotReplace) {
                                        if (cont.startsWith("")) {// "로 시작할경우
                                            cont = cont.replace(CODE.ENM_QUOT, CHAR.MARK_DOT);
                                        } else if (cont.indexOf("&#13;" + CODE.ENM_QUOT) > 0) {// 줄바꿈후 "일경우
                                            cont = cont.replace("&#13;" + CODE.ENM_QUOT, "&#13;" + CHAR.MARK_DOT);
                                        } else if (cont.indexOf("&amp;#13;" + CODE.ENM_QUOT) > 0) {// 줄바꿈후 "일경우
                                            cont = cont.replace("&amp;#13;" + CODE.ENM_QUOT, "&amp;#13;" + CHAR.MARK_DOT);
                                        } else if (cont.indexOf("<br/>" + CODE.ENM_QUOT) > 0) {// 줄바꿈후 "일경우
                                            cont = cont.replace("<br/>" + CODE.ENM_QUOT, "<br/>" + CHAR.MARK_DOT);
                                        } else if (cont.indexOf("<br>" + CODE.ENM_QUOT) > 0) {// 줄바꿈후 "일경우
                                            cont = cont.replace("<br>" + CODE.ENM_QUOT, "<br>" + CHAR.MARK_DOT);
                                        }
                                    }
                                }
                                //TODO: CodeConverter
                                cont = CodeConverter.getCodeConverter(psDto, cont, "", "");

                                XContWCDto dto = new XContWCDto();
                                dto.setToKey(toKey);
                                dto.setTocoId(tocoId);
                                dto.setName(name);
                                dto.setSteptime(steptime);
                                dto.setSteparea(steparea);
                                dto.setSystem(system);
                                dto.setSubsystem(subsystem);
                                dto.setContent(cont);
                                contWCMapper.insertAllDaoKTA(dto);
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    // TO 내용(UUID.xml) 중 검색 내용 중 FI 관련 XCont(XML Data) DB 등록
    public int insertSpecialFI(RegisterDto regDto, Document doc, SystemXmlWrapper item) {

        int rtInt = 0;

        if (item == null || regDto == null || doc == null) {
            return rtInt;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile(XALAN.REG_FI);
            Node fiRootNode = (Node) compile.evaluate(doc, XPathConstants.NODE);

            if (fiRootNode == null || fiRootNode.getNodeType() != Node.ELEMENT_NODE) {
                log.info("{} is nothing.", XALAN.REG_FI);
                return rtInt;
            }

            Element fiRootElement = (Element) fiRootNode;
            String fiRootType = fiRootElement.getAttribute(ATTR.TYPE);

            /*
              FI(type:FI, FI_FIELD) 부대급/야전급 추출하여 등록
              1. tm_all_xcont  등록 호출
              2. tm_grph_xcont  등록 호출
              3. tm_sc_cont  등록 호출 - FI의 컨텐츠 TEXT를 검색 테이블에 저장
              4. tm_sc_fi 등록 호출
             */

            compile = xPath.compile(XALAN.REG_DESC);
            Node descNode = (Node) compile.evaluate(doc, XPathConstants.NODE);

            if (descNode != null && !fiRootType.equals(VAL.FI_FIELD)) {
                rtInt = insertAllXContTitleDesc(regDto, descNode);
                if(rtInt < 0) {
                    dbInsertErrorService.insertDBInsertError(regDto, "insertAllXContTitleDesc");
                    return -1;
                }
                rtInt = insertScContFI(regDto, descNode, "");
                if(rtInt < 0) {
                    dbInsertErrorService.insertDBInsertError(regDto, "insertScContFI");
                    return -1;
                }
            }

            NodeList sysList = fiRootNode.getChildNodes();
            log.info("sysList.getLength() : {}", sysList.getLength());
            for (int i=0; i<sysList.getLength(); i++) {
                Node sysNode = sysList.item(i);
                if (sysNode == null || sysNode.getNodeType() != Node.ELEMENT_NODE) continue;

                Element sysElement = (Element) sysNode;
                String sysDtd		= sysNode.getNodeName();
                String sysId		= sysElement.getAttribute(ATTR.ID);
                String sysName		= sysElement.getAttribute(ATTR.NAME);
                String sysType		= sysElement.getAttribute(ATTR.TYPE);
                log.info("sysDtd : {} , sysId : {}, sysName : {}, sysType : {}", sysDtd, sysId, sysName, sysType);

                if (sysDtd.equals(DTD.SYSTEM)
                        && (sysType.equals(VAL.FI_DI) || sysType.equals(VAL.FI_LR) || sysType.equals(VAL.FI_AP) || sysType.equals(VAL.FI_DDS))) {

                    rtInt = insertAllXContTitleVirturlWithAlert(regDto, sysType, sysId, sysName, eXPIS3Constants.REG_TITLE_FI, sysNode);
                    if(rtInt < 0) {
                        dbInsertErrorService.insertDBInsertError(regDto, "insertAllXContTitleVirturlWithAlert");
                        return -1;
                    }

                    NodeList subList = sysNode.getChildNodes();
                    for (int j=0; j<subList.getLength(); j++) {
                        Node subNode = subList.item(j);
                        if (subNode == null || subNode.getNodeType() != Node.ELEMENT_NODE) {
                            continue;
                        }
                        Element subElement  = (Element) subNode;
                        String subDtd		= subNode.getNodeName();
                        String subId		= subElement.getAttribute(ATTR.ID);
                        String subType		= subElement.getAttribute(ATTR.TYPE);
                        log.info("subDtd : {}, subId : {}, subType : {}", subDtd, subId, subType);
                        if ((sysType.equals(VAL.FI_DI) || sysType.equals(VAL.FI_LR))
                                && (subDtd.equals(DTD.SYSTEM) && (subType.equals(VAL.FI_DI_DESC) || subType.equals(VAL.FI_LR_DESC)))) {
                            rtInt = insertAllXContFI(regDto, subNode);
                            if(rtInt < 0) {
                                dbInsertErrorService.insertDBInsertError(regDto, "insertAllXContFI");
                                return -1;
                            }
                            rtInt = insertScContFI(regDto, subNode, subId);
                            if(rtInt < 0) {
                                dbInsertErrorService.insertDBInsertError(regDto, "insertScContFI");
                                return -1;
                            }
                            /*
                             * 디비 입력시에 FI 교범 프로세스 예외 경우 등록되게 추가
                             * system 및에 faultinf가 descinfo와 동일 자식일경우 등록 안되는 부분 수정
                             */
                            //TODO: 로직 불필요해 보이는데
//                            try {
//                                if(subNode.getChildNodes().getLength() > 1) {
//                                    ExpisCommonUtile ecu = ExpisCommonUtile.getInstance();
//                                    NodeList subListChildNods = subNode.getChildNodes();
//                                    logger.info("subListChildNods : "+subListChildNods.getLength());
//                                    for (int k=0; k<subListChildNods.getLength(); k++) {
//                                        Node subChildNode = ecu.setIconElement((Element)subNode,subListChildNods.item(k),false);
//                                        //Node subChildNode = subListChildNods.item(k);
//                                        if (subChildNode == null) {
//                                            logger.info("subChildNode continue");
//                                            continue;
//                                        }
//                                        String subChildDtd		= subChildNode.getNodeName();
//                                        String subChildId		= XmlDomParser.getAttributes(subChildNode.getAttributes(), ATTR.ID);
//                                        String subChildType		= XmlDomParser.getAttributes(subChildNode.getAttributes(), ATTR.TYPE);
//                                        logger.info("subChildDtd	: "+subChildDtd+",	subChildId	: "+subChildId+",	subChildType	: "+subChildType);
//                                        if("faultinf".equalsIgnoreCase(subChildDtd)) {
//                                            rtInt = insertAllXContFI(regDto, subChildNode);
//                                            if(rtInt < 0) {
//                                                successFlag = false;
//                                                errStr = "insertAllXContFI";
//                                                return -1;
//                                            }
//                                            rtInt = insertScContFI(regDto, subChildNode, subChildId);
//                                            if(rtInt < 0) {
//                                                successFlag = false;
//                                                errStr = "insertScContFI";
//                                                return -1;
//                                            }
//                                        }
//                                    }
//                                }
//                            }catch (Exception e) {
//                                logger.info("e : "+e.getMessage());
//                                e.printStackTrace();
//                                successFlag = false;
//                                errStr = e.getMessage();
//                                return -1;
//                            }
                        } else if (sysType.equals(VAL.FI_AP) && subDtd.equals(DTD.DESC)) {
                            //NodeList grphList = XmlDomParser.getNodeListFromXPathAPI(subNode, XALAN.REG_FI_AP_GRPH);
                            compile = xPath.compile(XALAN.REG_FI_AP_GRPH);
                            NodeList grphNodeList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
                            for (int k=0; k<grphNodeList.getLength(); k++) {
                                Node grphNode = grphNodeList.item(k);
                                Element grphElement = (Element) grphNode;
                                String grphId = grphElement.getAttribute(ATTR.ID);
                                String grphName	= grphElement.getAttribute(ATTR.NAME);

                                rtInt = insertAllXContTitleVirturl(regDto, grphNode, grphId, grphName, eXPIS3Constants.REG_TITLE_FIPIC);
                                if(rtInt < 0) {
                                    dbInsertErrorService.insertDBInsertError(regDto, "insertAllXContTitleVirturl");
                                    return -1;
                                }
                                rtInt = insertScContFI(regDto, grphNode, grphId);
                                if(rtInt < 0) {
                                    dbInsertErrorService.insertDBInsertError(regDto, "insertScContFI");
                                    return -1;
                                }
                            }

                        } else if (sysType.equals(VAL.FI_DDS) && subDtd.equals(DTD.FI_FAULTINF)) {
                            rtInt = insertAllXContFI(regDto, subNode);
                            if(rtInt < 0) {
                                dbInsertErrorService.insertDBInsertError(regDto, "insertAllXContFI");
                                return -1;
                            }
                            rtInt = insertScContFI(regDto, subNode, subId);
                            if(rtInt < 0) {
                                dbInsertErrorService.insertDBInsertError(regDto, "insertScContFI");
                                return -1;
                            }

                        } else if (sysType.equals(VAL.FI_DDS) && subDtd.equals(DTD.TASK)) {
                            //joe 20150113 고장탐구절차 최상위 항목에 “경고”, “주의”, “주기” 문구 추가되도록 수정
                            //상단 2022 03 02 Park.J.S : rtInt = contRegister.insertAllXContTitleVirturlWithAlert(regDto, sysType, sysId, sysName, IConstants.REG_TITLE_FI, sysNode); 에서 처리함
                        }else {
                            log.info("Else Pass Not insert : {}", subNode);
                        }
                    }

                }else {

                    try {
                        log.info("fiRootNodeType : {}", fiRootType);
                        if (VAL.FI_FIELD.equalsIgnoreCase(fiRootType)){
                            log.info("야전급 교범 처리 입니다. 그외 로직에서 호출될 경우 문제가 발생 할수 있습니다.");

                            NodeList nodeList = doc.getChildNodes();
                            Node systemNode = null;
                            log.info("야전급 nodeList.getLength() : {}", nodeList.getLength());
                            for(int j=0;j<nodeList.getLength();j++) {
                                Node tempNode = nodeList.item(j);
                                log.info("야전급 tempNode : {}", tempNode.getNodeName());
                                if(tempNode.getNodeType() == Node.ELEMENT_NODE ) {
                                    if(tempNode.getNodeName().equalsIgnoreCase(DTD.SYSTEM)) {
                                        systemNode = tempNode;
                                        break;
                                    }
                                }
                            }

                            log.info("야전급 systemNode : {}", systemNode);
                            rtInt = this.insertAllXContFI(regDto, systemNode);
                            log.info("야전급 교범 insertAllXContFI : {}", rtInt);
                            if(rtInt < 0) {
                                dbInsertErrorService.insertDBInsertError(regDto, "insertAllXContFI");
                                return -1;
                            }
                            break;
                        }
                    }catch (Exception e) {
                        log.error(e.getMessage(),e);
                    }
                }

            }

            if (rtInt >= 0) {
                rtInt = insertGrphXCont(regDto, doc);
            }else {
                dbInsertErrorService.insertDBInsertError(regDto, "insertGrphXCont");
                return -1;
            }

            //TODO : Checking - Element toElem
            if (rtInt >= 0) {
                rtInt = insertScFI(regDto, fiRootNode, doc);
            }else {
                dbInsertErrorService.insertDBInsertError(regDto, "insertScFI");
                return -1;
            }

            if (rtInt == 0) {
                rtInt = 1;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("insertSpecialFI Exception={}", ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 IPB교범의 XCont(XML Data) DB 등록 실행
     * //TODO: 주석 안 맞음
     */
    private int insertAllXContTitleDesc(RegisterDto regDto, Node descNode) {

        int rtInt = 0;		//등록할 타이틀 내용은 데이터가 없을 수도 있기에 0으로 초기화

        if (regDto == null || descNode == null) {
            return rtInt;
        }

        try {
            String tocoId		= "";
            String tocoType	= "";
            StringBuilder contSB = new StringBuilder();

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            Node sysNode = descNode.getParentNode();
            if (sysNode != null) {
//                Element sysElement = (Element) sysNode;
//                tocoId		= sysElement.getAttribute(ATTR.ID);
//                tocoType	= sysElement.getAttribute(ATTR.TYPE);
//                String veType = sysElement.getAttribute(ATTR.VEHICLETYPE);
//                String attr = ATTR.TYPE + "='" + tocoType + "' " + ATTR.ID + "='" + tocoId + "' " + ATTR.VEHICLETYPE + "='" + veType + "'";
//                contSB.append(XmlDomParser.setSystemRoot(descNode, attr, "", ""));

                transformer.transform(new DOMSource(sysNode), new StreamResult(stringWriter));
                contSB.append(stringWriter);
            }

            //icon 포함 - jingi.kim
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            NodeList iconNodeList = (NodeList) xPath.evaluate(XALAN.ICON_LIST, sysNode, XPathConstants.NODESET);

            if ( iconNodeList != null && iconNodeList.getLength() > 0 ) {
                for ( int i=0; i<iconNodeList.getLength(); i++ ) {
                    Node iconNode = iconNodeList.item(i);
                    transformer.transform(new DOMSource(iconNode), new StreamResult(stringWriter));
                    contSB.append(stringWriter);
                }

                contSB.insert(0, CHAR.TAG_START + DTD.EXPISROOT + CHAR.TAG_END);
                contSB.append(CHAR.TAG_SL_START + DTD.EXPISROOT + CHAR.TAG_END);
            }

            if (!contSB.isEmpty()) {
                ArrayList<XContDto> insertList = new ArrayList<>();
                int i = 0;
                List<String> chunks = splitByCharacters(contSB.toString());

                for ( String chunk : chunks ) {
                    XContDto contDto = new XContDto();
                    contDto.setToKey(regDto.getToKey());
                    contDto.setTocoId(regDto.getTocoId());
                    contDto.setXth( ++i );
                    contDto.setXcont(chunk);
                    contDto.setCreateUserId(regDto.getUserId());
                    insertList.add(contDto);

                    if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                        rtInt = contAllMapper.insertAllDao(insertList);
                        insertList = new ArrayList<>();
                        if (rtInt <= 0) { break; }
                    }
                }
                if (!insertList.isEmpty()) {
                    rtInt = contAllMapper.insertAllDao(insertList);
                    insertList = new ArrayList<>();
                }
            }

            log.info("{} . insertAllXContTitleDesc rtInt: {}", this.getClass(), rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{} . insertAllXContTitleDesc Exception:{}", this.getClass(), ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 검색 내용 중 FI의 Contents 전체 관련 XCont(XML Data) DB 등록
     * FI일 경우에는 상위 partinfo의 id가 목차id가 됨 (<partinfo id=""><partbase><text />)
     */
    private int insertScContFI(RegisterDto regDto, Node contNode, String tocoId) {

        int rtInt = 0;		//등록할 검색 내용 데이터가 없을 수도 있기에 0으로 초기화

        if (contNode == null || regDto == null) {
            return rtInt;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            NodeList textNodeList = (NodeList) xPath.evaluate(XALAN.REG_FI_TEXT, contNode, XPathConstants.NODESET);
            if (textNodeList == null || textNodeList.getLength() <= 0) {
                return rtInt;
            }

            ArrayList<SearchDto> insertList = new ArrayList<>();
            int insertCnt = 0;
            StringBuilder contSB = new StringBuilder();

            for (int loop=0; loop<textNodeList.getLength(); loop++) {
                Node textNode = textNodeList.item(loop);
                if (textNode != null && textNode.getNodeName().equals(DTD.TEXT)) {
                    String cont = textNode.getTextContent().trim();
                    //TODO: CondeConverter 처리 방안
                    cont = CodeConverter.deleteAllCode(cont);
                    if (cont.isEmpty()) { continue; }

                    if (!contSB.isEmpty()) {
                        contSB.append(CHAR.SEARCH_TEXT);
                    }
                    contSB.append(cont);

                    if (contSB.length() > eXPIS3Constants.REG_CHECK_STR_LEN || loop == (textNodeList.getLength() - 1)) {
                        SearchDto scDto = new SearchDto();
                        scDto.setToKey(regDto.getToKey());
                        scDto.setTocoId(tocoId);
                        scDto.setCont(contSB.toString());
                        scDto.setCreateUserId(regDto.getUserId());
                        insertList.add(scDto);

                        contSB = new StringBuilder();
                    }
                }
            }

            if ( !insertList.isEmpty() ) {
                //속도 이슈로 인해 최대치 넘어갈경우 분할 처리하도록 수정
                if(insertList.size() >= eXPIS3Constants.REG_MAXIMUM){
                    for(int j=0; j<insertList.size(); j = j+eXPIS3Constants.REG_MAXIMUM) {
                        ArrayList<SearchDto> tempInsertList = new ArrayList<>();
                        if(j+eXPIS3Constants.REG_MAXIMUM < insertList.size()) {
                            tempInsertList.addAll(insertList.subList(j, j+eXPIS3Constants.REG_MAXIMUM));
                        }else{
                            tempInsertList.addAll(insertList.subList(j, insertList.size()));
                        }
                        rtInt = scContMapper.insertAllDao(tempInsertList);
                    }
                }else {
                    rtInt = scContMapper.insertAllDao(insertList);
                }
            }

            log.info("{} . insertScCont insertList.size():{} , rtInt:{}", this.getClass(), insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{} . insertScCont Exception:{}", this.getClass(),ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * <pre>
     * FI(고장탐구절차) 최상위 항목에 “경고”, “주의”, “주기” 문구 추가가 필요할 경우
     */
    private int insertAllXContTitleVirturlWithAlert(RegisterDto regDto, String sysType, String tocoId, String tocoName, int getType, Node contNode) {
        int rtInt = 0;
        if (regDto == null || tocoId == null || contNode == null) {
            return rtInt;
        }

        StringBuilder contentBuffer = new StringBuilder();
        try {

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            NodeList alertNodeList = (NodeList) xPath.evaluate(XALAN.REG_ALERT, contNode, XPathConstants.NODESET);
            if (alertNodeList == null || alertNodeList.getLength() <= 0) {
                return rtInt;
            }

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            for (int loop=0; loop<alertNodeList.getLength(); loop++) {
                Node alertNode = alertNodeList.item(loop);
                transformer.transform(new DOMSource(alertNode), new StreamResult(stringWriter));
                contentBuffer.append(stringWriter);
            }

            String tocoType	= "";
            String grphStr		= "";
            StringBuffer contSB = new StringBuffer();

            if (getType == eXPIS3Constants.REG_TITLE_FI) {
                tocoType = VAL.INTRO;
            } else if (getType == eXPIS3Constants.REG_TITLE_IPB) {
                tocoType = VAL.TOPIC;
            }  else if (getType == eXPIS3Constants.REG_TITLE_FIPIC) {
                tocoType = VAL.TOPIC;
                //grphNode 가 넘어오지 않음 필요시에 넘겨서 처리 필요
                //grphStr = XmlDomParser.getXmlDocumentNode(grphNode, "");
            }

            contSB.append("<system id='").append(tocoId).append("' itemid='' type='").append(tocoType).append("' version='' name='").append(tocoName).append("' >");
            contSB.append("<descinfo id='' itemid='' type='title' ref='' style='' status='a' version='' name='").append(tocoName).append("' >");
            contSB.append("<para-seq id='' name='' itemid='' type='' ref='' style='' status='a' version=''>");
            contSB.append("<para id='' name='' itemid='' type='title' ref='' style='' status='a' version=''>");

            if (getType == eXPIS3Constants.REG_TITLE_FIPIC) {
                contSB.append(grphStr);
            } else {
                contSB.append("<text id='' name='' itemid='' type='' ref='' style='' status='a' version=''>").append(tocoName).append("</text>");
            }
            contSB.append("</para></para-seq>");
            contSB.append(contentBuffer);
            contSB.append("</descinfo>");
            contSB.append("</system>");

            if (!contSB.isEmpty()) {
//                rtInt = insertAllXContExec(regDto.getToKey(), tocoId, regDto.getUserId(), contSB, regDto.getDbType());
                ArrayList<XContDto> insertList = new ArrayList<>();
                int i = 0;
                List<String> chunks = splitByCharacters(contSB.toString());

                for ( String chunk : chunks ) {
                    XContDto contDto = new XContDto();
                    contDto.setToKey(regDto.getToKey());
                    contDto.setTocoId(regDto.getTocoId());
                    contDto.setXth( ++i );
                    contDto.setXcont(chunk);
                    contDto.setCreateUserId(regDto.getUserId());
                    insertList.add(contDto);

                    if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                        rtInt = contAllMapper.insertAllDao(insertList);
                        insertList = new ArrayList<>();
                        if (rtInt <= 0) { break; }
                    }
                }
                if (!insertList.isEmpty()) {
                    rtInt = contAllMapper.insertAllDao(insertList);
                    insertList = new ArrayList<>();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{} . insertAllXContTitleVirturl Exception:{}", this.getClass(), ex.toString());
            return -1;
        }
        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 FI교범의 XCont(XML Data) DB 등록 실행
     */
    private int insertAllXContFI(RegisterDto regDto, Node sysNode) {

        int rtInt = 0;		//등록할 검색용 FI 데이터가 없을 수도 있기에 0으로 초기화

        if (sysNode == null || sysNode.getNodeType() != Node.ELEMENT_NODE || regDto == null) {
            return rtInt;
        }

        try {
            //tocoId 누락 문제 보완 - jingi.kim
            String tocoId = regDto.getTocoId();
            StringBuilder contSB = new StringBuilder();

            Element sysElement = (Element) sysNode;
            String sysDtd	= sysNode.getNodeName();
            String sysType = sysElement.getAttribute(ATTR.TYPE);
            if ( (sysDtd.equals(DTD.SYSTEM) && (sysType.equals(VAL.FI_DI_DESC) || sysType.equals(VAL.FI_LR_DESC)))
                    || sysDtd.equals(DTD.FI_FAULTINF) ) {
                tocoId = sysElement.getAttribute(ATTR.ID);
            }else if(sysDtd.equals(DTD.SYSTEM) && sysType.equalsIgnoreCase(VAL.SUBTOPIC)){
                log.info("야전급");
                tocoId = sysElement.getAttribute(ATTR.ID);
            }

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(sysNode), new StreamResult(stringWriter));

            contSB.append(stringWriter);

            if (!contSB.isEmpty()) {
//                rtInt = insertAllXContExec(regDto.getToKey(), tocoId, regDto.getUserId(), contSB, regDto.getDbType());
                ArrayList<XContDto> insertList = new ArrayList<>();
                int i = 0;
                List<String> chunks = splitByCharacters(contSB.toString());

                for ( String chunk : chunks ) {
                    XContDto contDto = new XContDto();
                    contDto.setToKey(regDto.getToKey());
                    contDto.setTocoId(tocoId);
                    contDto.setXth( ++i );
                    contDto.setXcont(chunk);
                    contDto.setCreateUserId(regDto.getUserId());
                    insertList.add(contDto);

                    if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                        rtInt = contAllMapper.insertAllDao(insertList);
                        insertList = new ArrayList<>();
                        if (rtInt <= 0) { break; }
                    }
                }
                if (!insertList.isEmpty()) {
                    rtInt = contAllMapper.insertAllDao(insertList);
                    insertList = new ArrayList<>();
                }
            }

            log.info("{} . insertAllXContFI rtInt:{}", this.getClass(), rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{} . insertAllXContFI Exception:{}", this.getClass(), ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 IPB교범의 XCont(XML Data) DB 등록 실행
     */
    public int insertAllXContTitleVirturl(RegisterDto regDto, Node grphNode, String tocoId, String tocoName, int getType) {

        int rtInt = 0;		//등록할 타이틀 내용은 데이터가 없을 수도 있기에 0으로 초기화

        if (regDto == null || tocoId == null) {
            return rtInt;
        }

        try {
            String tocoType	= "";
            String grphStr		= "";
            StringBuilder contSB = new StringBuilder();

            if (getType == eXPIS3Constants.REG_TITLE_FI) {
                tocoType = VAL.INTRO;
            } else if (getType == eXPIS3Constants.REG_TITLE_IPB) {
                tocoType = VAL.TOPIC;
            }  else if (getType == eXPIS3Constants.REG_TITLE_FIPIC) {
                tocoType = VAL.TOPIC;

                StringWriter stringWriter = new StringWriter();
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.transform(new DOMSource(grphNode), new StreamResult(stringWriter));
                grphStr = stringWriter.toString();
            }

            contSB.append("<system id='").append(tocoId).append("' itemid='' type='").append(tocoType).append("' version='' name='").append(tocoName).append("' >");
            contSB.append("<descinfo id='' itemid='' type='title' ref='' style='' status='a' version='' name='").append(tocoName).append("' >");
            contSB.append("<para-seq id='' name='' itemid='' type='' ref='' style='' status='a' version=''>");
            contSB.append("<para id='' name='' itemid='' type='title' ref='' style='' status='a' version=''>");

            if (getType == eXPIS3Constants.REG_TITLE_FIPIC) {
                contSB.append(grphStr);
            } else {
                contSB.append("<text id='' name='' itemid='' type='' ref='' style='' status='a' version=''>").append(tocoName).append("</text>");
            }

            contSB.append("</para></para-seq></descinfo>");
            contSB.append("</system>");

            if (!contSB.isEmpty()) {
//                rtInt = insertAllXContExec(regDto.getToKey(), tocoId, regDto.getUserId(), contSB, regDto.getDbType());
                ArrayList<XContDto> insertList = new ArrayList<>();
                int i = 0;
                List<String> chunks = splitByCharacters(contSB.toString());

                for ( String chunk : chunks ) {
                    XContDto contDto = new XContDto();
                    contDto.setToKey(regDto.getToKey());
                    contDto.setTocoId(regDto.getTocoId());
                    contDto.setXth( ++i );
                    contDto.setXcont(chunk);
                    contDto.setCreateUserId(regDto.getUserId());
                    insertList.add(contDto);

                    if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                        rtInt = contAllMapper.insertAllDao(insertList);
                        insertList = new ArrayList<>();
                        if (rtInt <= 0) { break; }
                    }
                }
                if (!insertList.isEmpty()) {
                    rtInt = contAllMapper.insertAllDao(insertList);
                    insertList = new ArrayList<>();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("{} . insertAllXContTitleVirturl Exception:{}", this.getClass(), ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 Graphic 관련 XCont(XML Data) DB 등록
     */
    private int insertGrphXCont(RegisterDto regDto, Document doc) {

        int rtInt = 0;		//등록할 Graphic 데이터가 없을 수도 있기에 0으로 초기화

        if (doc == null || regDto == null) {
            return rtInt;
        }

        try {
            ArrayList<XContDto> insertList = new ArrayList<>();
            int insertCnt = 0;

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile(XALAN.REG_GRPH);
            NodeList grphNodeList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);

            if (grphNodeList.getLength() <= 0) {
                return rtInt;
            }

            for (int loop=0; loop<grphNodeList.getLength(); loop++) {
                Node grphNode = grphNodeList.item(loop);
                Element grphElement = (Element) grphNode;

                if (grphNode != null && grphNode.getNodeName().equals(DTD.GRPH)) {
                    String extPtr	 = grphElement.getAttribute(ATTR.GRPH_PATH);
                    if (extPtr.isEmpty()) { continue; }

                    ArrayList<XContDto> tempList = this.insertGrphXContSet(regDto, grphNode, "");
                    if (tempList != null && !tempList.isEmpty()) {
                        insertList.addAll(tempList);
                    }
                }
            }

            if (!insertList.isEmpty()) {
                // 속도 이슈로 인해 최대치 넘어갈경우 분할 처리하도록 수정
                if(insertList.size() >= eXPIS3Constants.REG_MAXIMUM){
                    for(int j=0; j<insertList.size(); j = j+eXPIS3Constants.REG_MAXIMUM) {
                        ArrayList<XContDto> tempInsertList = new ArrayList<>();
                        if(j+eXPIS3Constants.REG_MAXIMUM < insertList.size()) {
                            tempInsertList.addAll(insertList.subList(j, j+eXPIS3Constants.REG_MAXIMUM));
                        }else{
                            tempInsertList.addAll(insertList.subList(j, insertList.size()));
                        }
                        rtInt = contGrphMapper.insertAllDao(tempInsertList);
                    }
                }else {
                    rtInt = contGrphMapper.insertAllDao(insertList);
                }

                insertCnt += insertList.size();
                insertList = new ArrayList<>();
            }

            log.info("{} . insertGrphXCont insertList.size():{} , rtInt:{}", this.getClass(), insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{} . insertGrphXCont Exception:{}", this.getClass(), ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 Graphic 관련 XCont(XML Data) DB 등록 실행
     */
    private ArrayList<XContDto> insertGrphXContSet(RegisterDto regDto, Node grphNode, String pinfoId) {

        ArrayList<XContDto> rtList = new ArrayList<>();

        if (grphNode == null || grphNode.getNodeType() != Node.ELEMENT_NODE || regDto == null) {
            return rtList;
        }
        if (!grphNode.getNodeName().equals(DTD.GRPH)) {
            return rtList;
        }

        try {
            Element grphElement = (Element) grphNode;
            String grphId = grphElement.getAttribute(ATTR.ID);
            String grphName = grphElement.getAttribute(ATTR.GRPH_PATH);
            if (grphId.isEmpty() || grphName.isEmpty()) {
                return rtList;
            }

            String tocoId			= regDto.getTocoId();
            String grphCaption	= this.getGrphCaption(grphNode);
            String grphType		= eXPIS3Constants.GRPH_TYPE_COMM;

            //부모 노드가 partinfo 일 경우 이미지타입을 ipb img로 표기
            if (grphNode.getParentNode() != null && grphNode.getParentNode().getNodeName().equals(DTD.IPB_PARTINFO)) {
                grphType = eXPIS3Constants.GRPH_TYPE_IPB;
                if (!pinfoId.isEmpty()) {
                    tocoId = pinfoId;	//부대IPB에서 호기분리시 그래픽의 목차ID(toco_id)를 p_d_refid 로 찾은 원본 <partinfo id="">를 사용
                } else { //IPB에서 본 이미지
                    tocoId = ((Element)grphNode.getParentNode()).getAttribute(ATTR.ID);
                }
            }

            //실제 이미지 파일명
            grphName = grphName.replace("\\", "/");
            int idx = grphName.indexOf("/");
            if (idx > -1) {
                grphName = grphName.substring(idx+1);
            }

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(grphNode), new StreamResult(stringWriter));

            ArrayList<XContDto> insertList = new ArrayList<>();
            int i = 0;
            List<String> chunks = splitByCharacters(String.valueOf(stringWriter));

            for ( String chunk : chunks ) {
                XContDto contDto = new XContDto();
                contDto.setToKey(regDto.getToKey());
                contDto.setTocoId(tocoId);
                contDto.setGrphId(grphId);
                contDto.setGrphCaption(grphCaption);
                contDto.setGrphType(grphType);
                contDto.setFileOrgName(grphName);
                contDto.setXth(++i);
                contDto.setXcont(chunk);
                contDto.setCreateUserId(regDto.getUserId());
                rtList.add(contDto);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{} . insertGrphXContSet Exception:{}", this.getClass(), ex.toString());
        }

        return rtList;
    }

    /**
     * Grphprim 엔티티 내에서 이미지명 추출 (<grphprim><text>)
     */
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
            ex.printStackTrace();
            log.error("{} . getGrphCaption Exception:{}", this.getClass(), ex.toString());
        }

        return rtStr;
    }


    /**
     * TO목차내용(UUID.xml) 중 검색 내용 중 FI 관련 XCont(XML Data) DB 등록
     */
    private int insertScFI(RegisterDto regDto, Node fiRootNode, Document doc) {

        int rtInt = 0;		//등록할 FI(fi_code) 데이터가 없을 수도 있기에 0으로 초기화

        if (fiRootNode == null || regDto == null) {
            return rtInt;
        }

        try {
            NodeList fiList;

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression compile = xPath.compile(XALAN.REG_FI_DILR);
            fiList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            rtInt = this.insertScFI(regDto, fiList, eXPIS3Constants.REG_FI_DILR, doc);

            compile = xPath.compile(XALAN.REG_FI_DDS);
            fiList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            rtInt = this.insertScFI(regDto, fiList, eXPIS3Constants.REG_FI_DDS, doc);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{} . insertScFI Exception :{}", this.getClass(), ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 검색 내용 중 FI 관련 XCont(XML Data) DB 등록 실행
     */
    private int insertScFI(RegisterDto regDto, NodeList fiList, int getType, Document doc) {

        int rtInt = 0;		//등록할 FI(fi_code) 데이터가 없을 수도 있기에 0으로 초기화

        if (fiList == null || fiList.getLength() == 0 || regDto == null) {
            return rtInt;
        }
        if (getType != eXPIS3Constants.REG_FI_DILR && getType != eXPIS3Constants.REG_FI_DDS) {
            return rtInt;
        }

        try {
            ArrayList<SearchDto> insertList = new ArrayList<>();
            int insertCnt		= 0;

            String fiId;
            String fiCode;

            for (int loop=0; loop<fiList.getLength(); loop++) {
                Node fiNode = fiList.item(loop);
                Element fiElement = (Element) fiNode;

                fiId = "";
                fiCode = "";
                if (getType == eXPIS3Constants.REG_FI_DILR) {
                    if (fiNode != null && fiNode.getNodeName().equals(DTD.FI_NODE)) {
                        fiId = fiElement.getAttribute(ATTR.LINK_TOCO_ID);
                        //TODO: CondeConverter
                        fiCode = CodeConverter.deleteAllCode(fiNode.getTextContent());
                    }
                }
                if (getType == eXPIS3Constants.REG_FI_DDS) {
                    if (fiNode != null && fiNode.getNodeName().equals(DTD.FI_FAULTINF)) {
                        fiId = fiElement.getAttribute(ATTR.ID);
                        fiCode = fiElement.getAttribute(ATTR.NAME);
                    }
                }

                if (fiId.isEmpty() && fiCode.isEmpty()) {
                    log.info("{} . insertScFI Contents is NULL Exception", this.getClass());
                    continue;
                }

                ArrayList<String> fiCodeList = this.getFiCode(doc, fiId);
                for ( String fcd : fiCodeList ) {
                    if ( "".equals(fcd) ) continue;

                    SearchDto scDto = new SearchDto();
                    scDto.setToKey(regDto.getToKey());
                    scDto.setTocoId(fiId);		//FI TO는 fi_id가 목차_id가 됨
                    scDto.setFiCode(fcd);
                    scDto.setCreateUserId(regDto.getUserId());
                    insertList.add(scDto);
                    insertCnt++;
                }

                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                    rtInt = scContMapper.insertAllDao(insertList);
                    if(rtInt < 0) {
                        dbInsertErrorService.insertDBInsertError(regDto, "insertScFI");
                    }
                    rtInt = scFiMapper.insertAllDao(insertList);
                    if(rtInt < 0) {
                        dbInsertErrorService.insertDBInsertError(regDto, "insertScFI");
                    }

                    insertList = new ArrayList<>();
                    if (rtInt <= 0) { break; }
                }
            }

            if (!insertList.isEmpty()) {
                rtInt = scContMapper.insertAllDao(insertList);
                if(rtInt < 0) {
                    dbInsertErrorService.insertDBInsertError(regDto, "insertScFI");
                }
                rtInt = scFiMapper.insertAllDao(insertList);
                if(rtInt < 0) {
                    dbInsertErrorService.insertDBInsertError(regDto, "insertScFI");
                }

                insertList = new ArrayList<>();
            }
            log.info("{} . insertScFI insertList.size():{} , rtInt:{}", this.getClass(), insertCnt, rtInt);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{} . insertScFI Exception : {}", this.getClass(), ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * 고장탐구 검색을 위한 데이터 tm_sc_fi 테이블에 추가 - jingi.kim
     *  	- expis3.xml의 sssn = 34-54-00 과 name = AE 를 조합해서 34-54-AE 와 같이 ficode를 생성
     * 		- itemid 값이 AA-AD 와 같은 경우 (34-54-AA, 34-54-AD) 2건으로 처리
     */
    private ArrayList<String> getFiCode(Document doc, String fid) {
        ArrayList<String> rtList = new ArrayList<>();

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression compile = xPath.compile(XALAN.REG_SYSTEM+"[@id='"+ fid +"']");
            Node systemNode = (Node) compile.evaluate(doc, XPathConstants.NODE);
            if (systemNode == null || systemNode.getNodeType() != Node.ELEMENT_NODE) {
                return rtList;
            }

            Element systemElement = (Element) systemNode;
            String attrName = systemElement.getAttribute(ATTR.NAME).toUpperCase();
            String attrSssn = systemElement.getAttribute(ATTR.IPB_SSSN).toUpperCase();
            if (attrName.isEmpty() || attrSssn.isEmpty()) {
                return rtList;
            }
            if ( attrSssn.length() < 6 ) {
                return rtList;
            }

            String fCodePre = attrSssn.substring(0, 5);

            String[] arrName = attrName.split("-");
            for( String nm : arrName) {
                rtList.add(fCodePre +"-"+ nm);
            }

        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }

        return rtList;
    }

    /**
     * TO목차내용(UUID.xml) 중 검색 내용 중 IPB 관련 XCont(XML Data) DB 등록
     * 부대급			airpartinfo			tm_sc_ipb, tm_sc_partinfo에 등록
     * 야전급			""							tm_sc_partinfo에 등록, ipb-code값이 없음
     * 부대급피규어	airpartinfo_figure
     * 부대급KF16	PAGE_airpartinfo	tm_sc_ipb, tm_sc_partinfo에 등록
     */
    public int insertSpecialIPB(RegisterDto regDto, Document doc, SystemXmlWrapper item) {

        int rtInt = 0;		//등록할 검색용 IPB 데이터가 없을 수도 있기에 0으로 초기화
        if (item == null || regDto == null || doc == null) {
            log.info("document is null");
            return rtInt;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile(XALAN.REG_IPB);
            Node pinfoNode = (Node) compile.evaluate(doc, XPathConstants.NODE);

            if (pinfoNode == null || pinfoNode.getNodeType() != Node.ELEMENT_NODE || !pinfoNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
                log.info("{} is nothing.", XALAN.REG_IPB);
                return rtInt;
            }

            /*
             * 부대급 IPB(type:airpartinfo) 추출하여 등록, 야전급 IPB(type:'') 추출하여 등록
             * 1) 부대급만 ipb_code가 있기에 insertScIPBExecIpbinfo() 실행
             * 2) Partinfo 등록시에도 부대급과 야전급 상이하기에 insertScIPBExecPartinfo() 처리 방법 약간 다름
             * 3) 부대급만 호기분리 있기에 그래픽 등록시 insertGrphXContIPB() 처리 방법 약간 다름
             */
            int ipbType = eXPIS3Constants.IPB_TYPE_UNIT;
            Element pInfoElement = (Element) pinfoNode;
            String pinfoType = pInfoElement.getAttribute(ATTR.TYPE).toUpperCase();
            if (pinfoType.equals(VAL.PINFO_TYPE_UNIT)  || pinfoType.equals(VAL.PINFO_TYPE_KF16)) {
                ipbType = eXPIS3Constants.IPB_TYPE_UNIT;
            } else if (pinfoType.equals(VAL.PINFO_TYPE_FIG)) {

            } else if (pinfoType.isEmpty()) {
                ipbType = eXPIS3Constants.IPB_TYPE_FIELD;
            }
            /*
             * 1. tm_all_xcont  등록 호출
             * 2. tm_grph_xcont  등록 호출
             * 3. tm_sc_cont  등록 호출 - IPB의 컨텐츠 TEXT를 검색 테이블에 저장
             * 4. tm_sc_ipbinfo 등록 호출 (부대급IPB에만 적용, 야전IPB는 ipb-code 값이 없음)
             * 5. tm_sc_partinfo 등록 호출
             */

            //Dom 재정비
            doc = this.setIPBAttributes(regDto, doc, ipbType);
            //등록
            NodeList ipbList = (NodeList) xPath.evaluate(XALAN.REG_IPB_UNIT, doc, XPathConstants.NODESET);
            if (ipbList.getLength() > 0) {
                Node descNode = (Node) xPath.evaluate(XALAN.REG_DESC, doc, XPathConstants.NODE);
                if (descNode != null) {
                    rtInt = this.insertAllXContTitleDesc(regDto, descNode);
                    if(rtInt < 0) {
                        dbInsertErrorService.insertDBInsertError(regDto, "insertAllXContTitleDesc");
                        return -1;
                    }
                }
                rtInt = this.insertAllXContIPB(regDto, ipbList);
                if(rtInt < 0) {
                    dbInsertErrorService.insertDBInsertError(regDto, "insertAllXContIPB");
                    return -1;
                }
                rtInt = this.insertScContIPB(regDto, ipbList);
                if(rtInt < 0) {
                    dbInsertErrorService.insertDBInsertError(regDto, "insertScContIPB");
                    return -1;
                }
                rtInt = this.insertGrphXContIPB(regDto, doc, ipbType);
                if(rtInt < 0) {
                    dbInsertErrorService.insertDBInsertError(regDto, "insertGrphXContIPB");
                    return -1;
                }
                if (ipbType == eXPIS3Constants.IPB_TYPE_UNIT) {
                    rtInt = this.insertScIPBIpbinfo(regDto, ipbList);
                    if(rtInt < 0) {
                        dbInsertErrorService.insertDBInsertError(regDto, "insertScIPBIpbinfo");
                        return -1;
                    }
                }
                rtInt = this.insertScIPBPartinfo(regDto, ipbList, ipbType);
                if(rtInt < 0) {
                    dbInsertErrorService.insertDBInsertError(regDto, "insertScIPBPartinfo");
                    return -1;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("{} . insertSpecialIPB Exception:{}", this.getClass(), ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * 	setIPBAttributes
     * <partinfo childpart=''> [부대IPB]교범중에 IPB코드가 영문으로 끝나도 하위부품이 없는경우가 있기에 하위부품 계산
     * <partbase filename=''> [부대IPB]그래픽 처리 (멀티 시트의 그래픽일 경우 기능 추가)
     * <partinfo grphref=''> [야전IPB]그래픽 처리 (멀티 시트의 그래픽일 경우 기능 추가)
     * <partbase name=''> name이 있으면 그대로, 없으면 등록시 <text> 추출하여 Attr 추가
     */
    private Document setIPBAttributes(RegisterDto regDto, Document doc, int ipbType) {
        log.info("CALL IN setIPBAttributes");
        Document rtDoc = null;
        if (doc == null || regDto == null) {
            return null;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile(XALAN.REG_IPB_UNIT);
            NodeList pInfoList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);

            if (pInfoList.getLength() <= 0) {
                return null;
            }
            for (int i=0; i<pInfoList.getLength(); i++) {
                Node pinfoNode = pInfoList.item(i);
                log.info("setIPBAttributes pinfoList {} / {}", i, pInfoList.getLength());
                if (ipbType == eXPIS3Constants.IPB_TYPE_UNIT) {
                    //1. <partinfo childpart=''>	등록시 Attr 추가, 자식노드 갯수 세어서 추가
                    if (pinfoNode.hasChildNodes()) {
                        NodeList childList = (NodeList) xPath.evaluate(XALAN.REG_IPB_PINFO_CHILD, pinfoNode, XPathConstants.NODESET);
                        Element tmpElem = (Element) pinfoNode;
                        tmpElem.setAttribute(ATTR.IPB_CHILDPART, childList.getLength()+"");
                    }
                    //2. <partbase filename=''>	등록시 Attr 추가, <grphprim> 추출해서 (부대IPB는 <partbase><grphprim>으로 되어있음)
                    if (pinfoNode.getParentNode() != null && pinfoNode.getParentNode().getNodeName().equals(DTD.IPB_PARTINFO)) {
                        Node pbaseNode = (Node) xPath.evaluate(XALAN.REG_IPB_PBASE_CHILD, pinfoNode, XPathConstants.NODE);
                        if (pbaseNode != null) {
                            StringBuilder xpathSql = new StringBuilder();
                            StringBuilder graphArr = new StringBuilder();

                            //20201020 LSAM
                            NodeList graphChildList = (NodeList) xPath.evaluate("./grphprim[@ref!='']", pinfoNode, XPathConstants.NODESET);
                            if (graphChildList.getLength() > 0) {
                                for (int j=0; j<graphChildList.getLength(); j++) {
                                    Element meElement = (Element) graphChildList.item(j);
                                    String graphRef = meElement.getAttribute(ATTR.GRPH_REF);
                                    String externalPtr = meElement.getAttribute("external-ptr");
                                    if (externalPtr.isEmpty()) {
                                        if (graphRef.contains(",")) {
                                            String[] arrRef = graphRef.split(",");
                                            for (String s : arrRef) {
                                                xpathSql.append((!xpathSql.isEmpty()) ? " or " : "");
                                                xpathSql.append("@id='").append(s).append("'");
                                            }
                                        } else {
                                            xpathSql.append((!xpathSql.toString().isEmpty()) ? " or " : "");
                                            xpathSql.append("@id='").append(graphRef).append("'");
                                        }
                                    }
                                }
                            }
                            if (!xpathSql.toString().isEmpty()) {
                                NodeList graphSiblList = (NodeList) xPath.evaluate(XALAN.getGrphFromRefXPathSql(xpathSql.toString()), pinfoNode, XPathConstants.NODESET);
                                if (graphSiblList.getLength() > 0) {
                                    for (int j=0; j<graphSiblList.getLength(); j++) {
                                        graphArr.append((!graphArr.isEmpty()) ? "|" : "");
                                        graphArr.append(this.getGrphPath(graphSiblList.item(j), ""));
                                    }
                                }
                                if (!graphArr.toString().isEmpty()) {
                                    Element tmpElem = (Element) pbaseNode;
                                    tmpElem.setAttribute(ATTR.IPB_FILENAME, graphArr.toString());
                                }
                            }
                        }
                    }

                } else if (ipbType == eXPIS3Constants.IPB_TYPE_FIELD) {
                    //3. <partinfo grphref=''> 야전IPB는 그래픽 정보가 partinfo안에 grphref로 되어있음
                    Node pbaseNode = (Node) xPath.evaluate(XALAN.REG_IPB_PBASE_CHILD, pinfoNode, XPathConstants.NODE);
                    if (pbaseNode != null) {
                        Element pinfoElement = (Element) pinfoNode;
                        String graphRef = pinfoElement.getAttribute(ATTR.IPB_GRPHREF);
                        if (!graphRef.isEmpty()) {
                            StringBuilder xpathSql = new StringBuilder();
                            StringBuilder graphArr = new StringBuilder();
                            if (graphRef.contains(",")) {
                                String[] arrRef = graphRef.split(",");
                                for (String s : arrRef) {
                                    xpathSql.append((!xpathSql.isEmpty()) ? " or " : "");
                                    xpathSql.append("@id='").append(s).append("'");
                                }
                            } else {
                                xpathSql.append((!xpathSql.toString().isEmpty()) ? " or " : "");
                                xpathSql.append("@id='").append(graphRef).append("'");
                            }
                            NodeList graphSiblList = (NodeList) xPath.evaluate(XALAN.getGrphFromRefXPathSql(xpathSql.toString()), pinfoNode, XPathConstants.NODESET);
                            if (graphSiblList.getLength() > 0) {
                                for (int j=0; j<graphSiblList.getLength(); j++) {
                                    graphArr.append((!graphArr.isEmpty()) ? "|" : "");
                                    graphArr.append(this.getGrphPath(graphSiblList.item(j), ""));
                                }
                            }
                            if (!graphArr.toString().isEmpty()) {
                                Element tmpElem = (Element) pbaseNode;
                                tmpElem.setAttribute(ATTR.IPB_FILENAME, graphArr.toString());
                            }
                        }
                    }
                }
            }
            //4. <partbase name=''>	name이 있으면 그대로, 없으면 등록시 <text> 추출하여 Attr 추가
            compile = xPath.compile(XALAN.REG_IPB_PBASE);
            NodeList pBaseList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            if (pBaseList.getLength() > 0) {
                for (int i=0; i<pBaseList.getLength(); i++) {
                    Node pbaseNode = pBaseList.item(i);
                    Element pbaseElement = (Element) pbaseNode;
                    StringBuilder pbaseName = new StringBuilder(pbaseElement.getAttribute(ATTR.NAME));
                    NodeList textList = (NodeList) xPath.evaluate(XALAN.REG_IPB_TEXT, pbaseNode, XPathConstants.NODESET);
                    if (pbaseName.isEmpty()) {
                        for (int j=0; j<textList.getLength(); j++) {
                            String tmpName = this.getTextFromNode(textList.item(j));
                            //TODO: CodeConverter 처리
                            tmpName = CodeConverter.deleteAllCode(tmpName);
                            pbaseName.append(tmpName);
                        }
                        if (!pbaseName.toString().isEmpty()) {
                            Element tmpElem = (Element) pbaseNode;
                            tmpElem.setAttribute(ATTR.NAME, pbaseName.toString());
                        }
                    } else {
                        for (int j=textList.getLength()-1; j>-1; j--) {
                            pbaseNode.removeChild(textList.item(j));
                        }
                    }
                }
            }

            rtDoc = doc;

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{} . setIPBAttributes Exception:{}", this.getClass(), ex.toString());
        }

        return rtDoc;
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
    /**
     * 이미지 노드에서 그래픽의 경로 추출
     */
    private String getGrphPath(Node imgNode, String bizPath) {

        String rtStr = "";

        if (imgNode == null) {
            return rtStr;
        }

        try {
            Element imgElement = (Element) imgNode;
            String grphPath = imgElement.getAttribute(ATTR.GRPH_PATH);
            if (grphPath.contains(VAL.IMG_FD_IPBIMAGE)) {
                grphPath = grphPath.substring((VAL.IMG_FD_IPBIMAGE.length()+1));

            } else if (grphPath.contains(VAL.IMG_FD_IMAGE)) {
                grphPath = grphPath.substring((VAL.IMG_FD_IMAGE.length()+1));
            } else if (grphPath.contains(VAL.IMG_FD_FOLDER)) {
                grphPath = grphPath.substring((grphPath.indexOf(VAL.IMG_FD_FOLDER)+1));
            }

            if ( !bizPath.isEmpty() ) {
                rtStr = bizPath + "/" + eXPIS3Constants.GRPH_PATH_IMAGE + "/" +grphPath;
            } else {
                rtStr = grphPath;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("{} . getGrphPath Exception:{}", this.getClass(), ex.toString());
        }

        return rtStr;
    }

    /**
     * TO목차내용(UUID.xml) 중 IPB교범의 XCont(XML Data) DB 등록 실행
     */
    private int insertAllXContIPB(RegisterDto regDto, NodeList pinfoList) {

        int rtInt = 0;		//등록할 검색용 IPB 부대급 데이터가 없을 수도 있기에 0으로 초기화

        if (pinfoList == null || pinfoList.getLength() == 0 || regDto == null) {
            return rtInt;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            String tocoId = "";
            StringBuffer contSB = new StringBuffer();

            for (int loop=0; loop<pinfoList.getLength(); loop++) {
                Node pinfoNode = pinfoList.item(loop);
                Element pinfoElement = (Element) pinfoNode;

                if(pinfoNode != null && pinfoNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
                    //자식노드가 잇을 경우에만 해당 partinfo 노드를 목차로 추가함
                    NodeList childList = (NodeList) xPath.evaluate(XALAN.REG_IPB_PINFO_CHILD, pinfoNode, XPathConstants.NODESET);
                    NodeList childList2 = (NodeList) xPath.evaluate(XALAN.REG_IPB_PBASE_CHILD, pinfoNode, XPathConstants.NODESET);

                    if (childList.getLength() > 0) {
                        tocoId = pinfoElement.getAttribute(ATTR.ID);
                        transformer.transform(new DOMSource(pinfoNode), new StreamResult(stringWriter));
                        contSB.append(stringWriter);
                        if (!contSB.isEmpty()) {
                            rtInt = this.insertAllXContExec(regDto.getToKey(), tocoId, regDto.getUserId(), contSB, regDto.getDbType());
                        }
                    } else if (childList2.getLength() > 0) {
                        tocoId = pinfoElement.getAttribute(ATTR.ID);
                        transformer.transform(new DOMSource(pinfoNode), new StreamResult(stringWriter));
                        contSB.append(stringWriter);
                        if (!contSB.isEmpty()) {
                            rtInt = this.insertAllXContExec(regDto.getToKey(), tocoId, regDto.getUserId(), contSB, regDto.getDbType());
                        }
                    }
                }
            }

            log.info("insertAllXContIPB rtInt:{}", rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("insertAllXContIPB Exception:{}", ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) XCont(XML Data) DB 등록 실행
     */
    private int insertAllXContExec(String toKey, String tocoId, String userId, StringBuffer contSB, String dbType) {

        int rtInt = -1;

        if (contSB == null || toKey == null || tocoId == null || userId == null) {
            return rtInt;
        }
        log.info("insertAllXContExec contSB : {}", contSB);
        log.info("dbType={}", dbType);

        StringBuilder stringBuilder = new StringBuilder();
        try {
            //1) TO 목차내용 XCont(XML data) Insert
            ArrayList<XContDto> insertList = new ArrayList<>();
            int insertCnt = 0;
            int i = 0;

            List<String> chunks = splitByCharacters(contSB.toString());

            for ( String chunk : chunks ) {
                XContDto contDto = new XContDto();
                contDto.setToKey(toKey);
                contDto.setTocoId(tocoId);
                contDto.setXth( ++i );
                contDto.setXcont(chunk);
                contDto.setCreateUserId(userId);
                insertList.add(contDto);

                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                    rtInt = contAllMapper.insertAllDao(insertList);
                    insertList = new ArrayList<>();
                    if (rtInt <= 0) { break; }
                }
            }
            if (!insertList.isEmpty()) {
                rtInt = contAllMapper.insertAllDao(insertList);
                insertList = new ArrayList<>();
            }
            if (rtInt > 0) {
                rtInt = insertCnt;
            }

            log.info("insertAllXContExec insertList.size():{}, rtInt:{}", insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("insertAllXContExec Exception:{}", ex.toString());
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 검색 내용 중 IPB의 Contents 전체 관련 XCont(XML Data) DB 등록
     * IPB일 경우에는 상위 partinfo의 id가 목차id가 됨 (<partinfo id=""><partbase><text />)
     * partbase 밑에  text 노드의 값을 name=''으로 추가하기 전에 처리할 경우 사용
     */
    private int insertScContIPB(RegisterDto regDto, NodeList pinfoList) {

        int rtInt = 0;		//등록할 검색용 IPB 부대급 데이터가 없을 수도 있기에 0으로 초기화

        if (pinfoList == null || pinfoList.getLength() == 0 || regDto == null) {
            return rtInt;
        }

        try {
            String tocoId = "";

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            ArrayList<SearchDto> insertList = new ArrayList<>();
            int insertCnt = 0;
            StringBuilder contSB;

            for (int loop=0; loop<pinfoList.getLength(); loop++) {
                Node pinfoNode = pinfoList.item(loop);
                Element pinfoElement = (Element) pinfoNode;
                if (pinfoNode == null || !pinfoNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
                    continue;
                }

                //자식노드가 잇을 경우에만 해당 partinfo 노드를 목차로 추가함
                NodeList childList = (NodeList) xPath.evaluate(XALAN.REG_IPB_PBASE_CCHILD, pinfoNode, XPathConstants.NODESET);
                if (childList.getLength() > 0) {
                    tocoId = pinfoElement.getAttribute(ATTR.ID);

                    for (int sub=0; sub<childList.getLength(); sub++) {
                        contSB = new StringBuilder();
                        Node pbaseNode = childList.item(sub);
                        Element pbaseElement = (Element) pbaseNode;
                        if (pbaseNode == null || !pbaseNode.getNodeName().equals(DTD.IPB_PARTBASE)) {
                            continue;
                        }

                        String cont = pbaseElement.getAttribute(ATTR.NAME);
                        contSB.append(cont);

                        List<String> chunks = splitByCharacters(contSB.toString());

                        for ( String chunk : chunks ) {
                            SearchDto scDto = new SearchDto();
                            scDto.setToKey(regDto.getToKey());
                            scDto.setTocoId(tocoId);
                            scDto.setCont(chunk);
                            scDto.setCreateUserId(regDto.getUserId());
                            insertList.add(scDto);

                            if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                                rtInt = scContMapper.insertAllDao(insertList);
                                insertList = new ArrayList<>();
                                if (rtInt <= 0) { break; }
                            }
                        }
                    }
                }
            }
            if (!insertList.isEmpty()) {
                rtInt = scContMapper.insertAllDao(insertList);
                insertList = new ArrayList<>();
            }

            log.info("insertScContIPBAfter insertList.size():{}, rtInt:{}", insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("insertScContIPBAfter Exception : {}", regDto.getToKey(), ex);
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 IPB교범의 Graphic 관련 XCont(XML Data) DB 등록
     */
    private int insertGrphXContIPB(RegisterDto regDto, Document doc, int ipbType) {

        int rtInt = 0;		//등록할 Graphic 데이터가 없을 수도 있기에 0으로 초기화

        if (doc == null || regDto == null) {
            return rtInt;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            ArrayList<XContDto> insertList = new ArrayList<>();
            int insertCnt = 0;

            //기본 이미지 등록 (<grphprim external-ptr!=''>)
            XPathExpression compile = xPath.compile(XALAN.REG_GRPH);
            NodeList grphList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            if (grphList.getLength() <= 0) {
                return rtInt;
            }

            for (int loop=0; loop<grphList.getLength(); loop++) {
                Node grphNode = grphList.item(loop);
                Element grphElement = (Element) grphNode;

                if (grphNode != null && grphNode.getNodeName().equals(DTD.GRPH)) {
                    String extPtr	 = grphElement.getAttribute(ATTR.GRPH_PATH);
                    if (extPtr.isEmpty()) {
                        continue;
                    }

                    ArrayList<XContDto> tempList = this.insertGrphXContSet(regDto, grphNode, "");
                    if (tempList != null && !tempList.isEmpty()) {
                        insertList.addAll(tempList);
                    }

                    if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                        //속도 이슈로 인해 최대치 넘어갈경우 분할 처리하도록 수정
                        if(insertList.size() >= eXPIS3Constants.REG_MAXIMUM){
                            for(int j=0; j<insertList.size(); j = j+eXPIS3Constants.REG_MAXIMUM) {
                                ArrayList<XContDto> tempInsertList = new ArrayList<>();
                                if(j+eXPIS3Constants.REG_MAXIMUM < insertList.size()) {
                                    tempInsertList.addAll(insertList.subList(j, j+eXPIS3Constants.REG_MAXIMUM));
                                }else{
                                    tempInsertList.addAll(insertList.subList(j, insertList.size()));
                                }
                                rtInt = contGrphMapper.insertAllDao(tempInsertList);
                            }
                        }else {
                            rtInt = contGrphMapper.insertAllDao(insertList);
                        }
                        //rtInt = contGrphMapper.insertAllDao(insertList);

                        insertCnt += insertList.size();
                        insertList = new ArrayList<>();
                        if (rtInt <= 0) { break; }
                    }
                }
            }

            if (!insertList.isEmpty()) {
                rtInt = contGrphMapper.insertAllDao(insertList);
                insertCnt += insertList.size();
                insertList = new ArrayList<>();
            }


            //호기분리 이미지 등록 (<partinfo p_d_refid!=''>) : 부대IPB이면서 호기분리 이미지 있을 경우
            if (ipbType == eXPIS3Constants.IPB_TYPE_UNIT) {
                compile = xPath.compile(XALAN.REG_IPB_PDREFID);
                NodeList pinfoList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
                if (pinfoList.getLength() <= 0) {
                    return rtInt;
                }

                for (int loop=0; loop<pinfoList.getLength(); loop++) {
                    Node pinfoNode = pinfoList.item(loop);
                    Element pinfoElement = (Element) pinfoNode;

                    if (pinfoNode != null && pinfoNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
                        String pinfoId = pinfoElement.getAttribute(ATTR.ID);
                        String pdRefid = pinfoElement.getAttribute(ATTR.IPB_PDREFID);
                        if (pinfoId.isEmpty() || pdRefid.isEmpty()) {
                            continue;
                        }

                        grphList = (NodeList) xPath.evaluate(XALAN.getGrphFromPartIdXPathSql(pdRefid), doc, XPathConstants.NODESET);
                        for (int sub=0; sub<grphList.getLength(); sub++) {
                            Node grphNode = grphList.item(sub);
                            Element grphElement = (Element) grphNode;

                            if (grphNode != null && grphNode.getNodeName().equals(DTD.GRPH)) {
                                String extPtr = grphElement.getAttribute(ATTR.GRPH_PATH);
                                if (extPtr.isEmpty()) {
                                    continue;
                                }

                                ArrayList<XContDto> tempList = this.insertGrphXContSet(regDto, grphNode, pinfoId);
                                if (tempList != null && !tempList.isEmpty()) {
                                    insertList.addAll(tempList);
                                }

                                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                                    // 속도 이슈로 인해 최대치 넘어갈경우 분할 처리하도록 수정
                                    if(insertList.size() >= eXPIS3Constants.REG_MAXIMUM){
                                        for(int j=0; j<insertList.size(); j = j+eXPIS3Constants.REG_MAXIMUM) {
                                            ArrayList<XContDto> tempInsertList = new ArrayList<XContDto>();
                                            if(j+eXPIS3Constants.REG_MAXIMUM < insertList.size()) {
                                                tempInsertList.addAll(insertList.subList(j, j+eXPIS3Constants.REG_MAXIMUM));
                                            }else{
                                                tempInsertList.addAll(insertList.subList(j, insertList.size()));
                                            }
                                            rtInt = contGrphMapper.insertAllDao(tempInsertList);
                                        }
                                    }else {
                                        rtInt = contGrphMapper.insertAllDao(insertList);
                                    }
                                    //rtInt = contGrphMapper.insertAllDao(insertList);

                                    insertCnt += insertList.size();
                                    insertList = new ArrayList<>();
                                    if (rtInt <= 0) { break; }
                                }
                            }
                        }
                    }
                }
                log.info("insertGrphXContIPB rtInt : {}", rtInt);
                if (!insertList.isEmpty()) {
                    rtInt = contGrphMapper.insertAllDao(insertList);
                    insertCnt += insertList.size();
                    insertList = new ArrayList<>();
                }
                log.info("insertGrphXContIPB rtInt : {}", rtInt);
            }
            log.info("insertGrphXContIPB (B) insertList.size():{}, rtInt:{}", insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("insertGrphXContIPB Exception:{}", ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 검색 내용 중 IPB(ipb_code) 관련 XCont(XML Data) DB 등록 실행
     */
    //@Transactional(propagation = Propagation.REQUIRED, isolation=Isolation.DEFAULT, rollbackFor={Exception.class, SQLException.class}, readOnly=false)
    private int insertScIPBIpbinfo(RegisterDto regDto, NodeList ipbList) {
        log.info("CALL insertScIPBIpbinfo : {}, {}", regDto.getToKey(), regDto.getTocoId());
        int rtInt = 0;		//등록할 검색용 IPB 부대급 데이터가 없을 수도 있기에 0으로 초기화

        if (ipbList == null || ipbList.getLength() == 0) {
            log.info("insertScIPBIpbinfo Return : {}, {}", regDto.getToKey(), regDto.getTocoId());
            return rtInt;
        }

        try {
            ArrayList<SearchPartinfoDto> insertList = new ArrayList<>();
            int insertCnt		= 0;

            String ipbId		= "";
            String ipbCode	= "";

            for (int loop=0; loop<ipbList.getLength(); loop++) {
                Node ipbNode = ipbList.item(loop);
                Element ipbElement = (Element) ipbNode;

                if (ipbNode == null || !ipbNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
                    continue;
                }

                ipbId = ipbElement.getAttribute(ATTR.ID);
                ipbCode = ipbElement.getAttribute(ATTR.IPB_IPBCODE);

                //ipbcode 자리수 제한
                if (!ipbCode.isEmpty() && (ipbCode.length() == 2 || ipbCode.charAt(ipbCode.length()-1) > CHAR.REG_IPB_CODE)) {
                } else {
                    ipbCode = "";
                }
                if (ipbId.isEmpty() || ipbCode.isEmpty()) {
                    log.info("insertScIPBExecIpbinfo Contents is NULL");
                    //continue;
                    log.info("Pass continue");
                }

                SearchPartinfoDto scDto = new SearchPartinfoDto();
                scDto.setToKey(regDto.getToKey());
                scDto.setTocoId(ipbId);		//IPB TO는 ipb_id가 목차_id가 됨
                scDto.setIpbCode(ipbCode);
                scDto.setCreateUserId(regDto.getUserId());
                insertList.add(scDto);

                insertCnt++;

                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                    // 속도 이슈로 인해 최대치 넘어갈경우 분할 처리하도록 수정
                    if(insertList.size() >= eXPIS3Constants.REG_MAXIMUM){
                        for(int j=0; j<insertList.size(); j = j+eXPIS3Constants.REG_MAXIMUM) {
                            ArrayList<SearchPartinfoDto> tempInsertList = new ArrayList<SearchPartinfoDto>();
                            if(j+eXPIS3Constants.REG_MAXIMUM < insertList.size()) {
                                tempInsertList.addAll(insertList.subList(j, j+eXPIS3Constants.REG_MAXIMUM));
                            }else{
                                tempInsertList.addAll(insertList.subList(j, insertList.size()));
                            }
                            rtInt = scPartMapper.insertAllDao(tempInsertList);
                        }
                    }else {
                        rtInt = scPartMapper.insertAllDao(insertList);
                    }
                    //rtInt = scPartinfoMapper.insertAllDao(insertList);

                    insertList = new ArrayList<>();
                    if (rtInt <= 0) { break; }
                }
            }

            if (!insertList.isEmpty()) {
                rtInt = scPartMapper.insertAllDao(insertList);
                insertList = new ArrayList<>();
            }

            log.info("insertScIPBIpbinfo insertList.size():{}, rtInt:{}", insertCnt, rtInt);

        } catch (Exception ex) {
            //ex.printStackTrace();
            log.error("SearchRegister.insertScIPBIpbinfo Exception : {}", regDto.getToKey(), ex);
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 검색 내용 중 IPB(Partinfo) 관련 XCont(XML Data) DB 등록 실행
     * IPB KTA 일경우 일력 ATT 수정
     */
    private int insertScIPBPartinfo(RegisterDto regDto, NodeList ipbList, int ipbType) {
        log.info("insertScIPBPartinfo 중 검색 내용 중 IPB(Partinfo) 관련 XCont(XML Data) DB 등록 실행");

        int rtInt = 0;		//등록할 검색용 IPB 부대급 데이터가 없을 수도 있기에 0으로 초기화

        if (ipbList == null || ipbList.getLength() == 0 || regDto == null) {
            log.info("insertScIPBPartinfo 중 검색 내용 중 IPB(Partinfo) 관련 XCont(XML Data) DB 등록  취소 됨...............");
            return rtInt;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            ArrayList<SearchPartinfoDto> insertList = new ArrayList<>();
            int insertCnt		= 0;

            String ipbId		= "";
            String indexNo		= "";
            String grphNo		= "";
            String ipbCode		= "";
            String partNo		= "";
            String partName		= "";
            String nsn			= "";
            String cage			= "";
            String rdn			= "";
            String upa			= "";
            String smr			= "";
            String stdMngt		= "";
            String wuc			= "";				// 작업단위부호 추가 - jingi.kim

            for (int loop=0; loop<ipbList.getLength(); loop++) {
                Node ipbNode = ipbList.item(loop);
                Element ipbElement = (Element) ipbNode;

                if (!ipbNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
                    continue;
                }

                ipbId		= ipbElement.getAttribute(ATTR.ID);
                indexNo		= ipbElement.getAttribute(ATTR.IPB_INDEXNUM);

                if(!"KTA".equalsIgnoreCase(bizCode)){
                    grphNo	= ipbElement.getAttribute(ATTR.IPB_GRPHNUM);
                }else {
                    grphNo	= ipbElement.getAttribute(ATTR.IPB_GRPHNUM2);
                }
                ipbCode		= ipbElement.getAttribute(ATTR.IPB_IPBCODE);
                rdn			= ipbElement.getAttribute(ATTR.IPB_RDN);
                upa			= ipbElement.getAttribute(ATTR.IPB_UNITSPER);
                stdMngt		= ipbElement.getAttribute(ATTR.IPB_STD);
                wuc			= ipbElement.getAttribute(ATTR.IPB_WUC);			//2023.12.07 - 작업단위부호 추가 - jingi.kim

                Node pbaseNode = (Node) xPath.evaluate("./child::partbase", ipbNode, XPathConstants.NODE);
                if (pbaseNode != null && pbaseNode.getNodeName().equals(DTD.IPB_PARTBASE)) {
                    Element pbaseElement = (Element) pbaseNode;

                    partNo		= pbaseElement.getAttribute(ATTR.IPB_PARTNUM);
                    partName	= pbaseElement.getAttribute(ATTR.NAME);
                    nsn			= pbaseElement.getAttribute(ATTR.IPB_NSN);
                    cage		= pbaseElement.getAttribute(ATTR.IPB_CAGE);
                    smr			= pbaseElement.getAttribute(ATTR.IPB_SMR);
                    if (partName.isEmpty()) {
                        partName = pbaseNode.getTextContent().trim();
                    }
                }

                if (ipbId.isEmpty()) {
                    log.info("SearchRegister.insertScIPBPartinfo Contents is NULL ipbId");
                    continue;
                }
                //부대IPB는 품목번호(indexnum) 0이면 검색 테이블에 미등록, 야전IPB는 품목번호 0이어도 등록
                if (ipbType == eXPIS3Constants.IPB_TYPE_UNIT && indexNo.isEmpty()) {
                    log.info("SearchRegister.insertScIPBPartinfo IPB Unit & index_no is NULL ipbType");
                    continue;
                }

                String parentTocoId = "";
                if (ipbType == eXPIS3Constants.IPB_TYPE_FIELD && ipbNode.getParentNode() != null) {
                    Node paNode = ipbNode.getParentNode();
                    Element paElement = (Element) paNode;
                    parentTocoId = paElement.getAttribute(ATTR.ID);
                }
                // 부대급도 목차_id로 설정 안할경우 조회가 안됨
                if (ipbType == eXPIS3Constants.IPB_TYPE_UNIT && ipbNode.getParentNode() != null) {
                    Node paNode = ipbNode.getParentNode();
                    Element paElement = (Element) paNode;
                    parentTocoId = paElement.getAttribute(ATTR.ID);
                }

                SearchPartinfoDto scDto = new SearchPartinfoDto();
                scDto.setToKey(regDto.getToKey());
                scDto.setTocoId(ipbId);		//IPB TO는 ipb_id가 목차_id가 됨

                // 야전IPB일 경우는, 목차_id로 지정
                log.info("ipbType : {}, IConstants.IPB_TYPE_FIELD : " + eXPIS3Constants.IPB_TYPE_FIELD, ipbType);
                if (ipbType == eXPIS3Constants.IPB_TYPE_FIELD) {
                    scDto.setTocoId(parentTocoId);
                }
                // 부대급도 목차_id로 설정 안할경우 조회가 안됨
                if (ipbType == eXPIS3Constants.IPB_TYPE_UNIT) {
                    log.info("2021 08 13 parkjs 부대급도 목차_id로 설정  안할경우 조회가 안됨 : {}", parentTocoId);
                    scDto.setTocoId(parentTocoId);
                }
                scDto.setIndexNo(indexNo);
                scDto.setGrphNo(grphNo);
                scDto.setIpbCode(ipbCode);
                scDto.setPartNo(partNo);
                scDto.setPartName(partName);
                scDto.setNsn(nsn);
                scDto.setCage(cage);
                scDto.setRdn(rdn);
                scDto.setUpa(upa);
                scDto.setSmr(smr);
                scDto.setStdMngt(stdMngt);
                scDto.setCreateUserId(regDto.getUserId());
                // 건바이건 등록하도록 수정
                //System.out.println("insertList1 : "+scDto.getTocoId()+", "+scDto.getPartNo()+", "+scDto.getPartName()+", "+scDto.getNsn()+", "+scDto.getRdn());
                //rtInt = scPartMapper.insertAllDaoMDB(scDto);
                scDto.setWuc(wuc); 			// 작업단위부호 추가 - jingi.kim
                insertList.add(scDto);

                insertCnt++;

                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                    //2022 04 07 속도 이슈로 인해 최대치 넘어갈경우 분할 처리하도록 수정
                    if(insertList.size() >= eXPIS3Constants.REG_MAXIMUM){
                        for(int j=0; j<insertList.size(); j = j+eXPIS3Constants.REG_MAXIMUM) {
                            ArrayList<SearchPartinfoDto> tempInsertList = new ArrayList<>();
                            if(j+eXPIS3Constants.REG_MAXIMUM < insertList.size()) {
                                tempInsertList.addAll(insertList.subList(j, j+eXPIS3Constants.REG_MAXIMUM));
                            }else{
                                tempInsertList.addAll(insertList.subList(j, insertList.size()));
                            }
                            rtInt = scPartMapper.insertAllDao(tempInsertList);
                        }
                    }else {
                        rtInt = scPartMapper.insertAllDao(insertList);
                    }
                    //rtInt = scPartMapper.insertAllDao(insertList);

                    insertList = new ArrayList<>();
                    if (rtInt <= 0) { break; }
                }
            }

            if (!insertList.isEmpty()) {
                rtInt = scPartMapper.insertAllDao(insertList);
                insertList = new ArrayList<>();
            }
            log.info("SearchRegister.insertScIPBPartinfo insertList.size():{}, rtInt:{}", insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("SearchRegister.insertScIPBPartinfo Exception : {}", regDto.getToKey(), ex);
            return -1;
        }

        //2024.10.21 - T50 부품정보에서 특정 부품값이 검색되지 않아, 메서드 추가
        ScIPBPartinfoExtraData(regDto, ipbList, ipbType);
        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 검색 내용 중 IPB(Partinfo) 관련 XCont(XML Data) DB 등록 실행
     * 부품정보에서 특정 부품값이 검색되지 않아, 메서드 추가
     */
    private void ScIPBPartinfoExtraData(RegisterDto regDto, NodeList ipbList, int ipbType) {

        int rtInt = 0;

        //parentId값을 넣기위한 list
        HashSet<String> parentsList = new HashSet<>();
        //partinfo값을 넣기위한 list
        HashSet<String> partinfoList = new HashSet<>();

        String ipbId		= "";
        String stdMngt		= "";
        String p_d_refid    = "";
        String parentTocoId = ""; //부품상위 id값을 담기위한 변수

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            for (int loop=0; loop<ipbList.getLength(); loop++) {
                Node ipbNode = ipbList.item(loop);
                Element ipbElement = (Element) ipbNode;

                ipbId		= ipbElement.getAttribute(ATTR.ID);
                stdMngt		= ipbElement.getAttribute(ATTR.IPB_STD);

                Node pbaseNode = (Node) xPath.evaluate("./child::partbase", ipbNode, XPathConstants.NODE);
                if (pbaseNode != null && pbaseNode.getNodeName().equals(DTD.IPB_PARTBASE)) {
                    if(!ipbElement.getAttribute(ATTR.IPB_STD).isEmpty()) {

                        //paAttr를 이용해 트리 추출 (./child::partbase)

                        Node paNode = ipbNode.getParentNode();
                        Element paElement = (Element) paNode;
                        parentTocoId = paElement.getAttribute(ATTR.ID);

                        String previousValue = "";
                        if(!parentTocoId.equals(previousValue)) {
                            //parentTocoId 값 리스트로 값 저장
                            parentsList.add(parentTocoId);
                            previousValue = parentTocoId;
                        }

                        if(!stdMngt.equals(previousValue)) {
                            //stdMngt 값 리스트로 값 저장
                            partinfoList.add(stdMngt);
                            previousValue = stdMngt;
                        }
                    }
                    //ipbAttr를 이용해 트리 추출 (전체 트리)
                    ipbId	= ipbElement.getAttribute(ATTR.ID);
                    p_d_refid	= ipbElement.getAttribute(ATTR.IPB_PDREFID);

                    if(!ipbElement.getAttribute(ATTR.IPB_PDREFID).isEmpty()) {
                        //test(parentTocoId의 리스트 값)과  p_d_refid 값 일치
                        for(String parents : parentsList) {
                            if(parents.equals(p_d_refid)) {
                                for(String part : partinfoList) {
                                    ArrayList<SearchPartinfoDto> insertList = new ArrayList<>();
                                    SearchPartinfoDto scDto = new SearchPartinfoDto();
                                    scDto.setToKey(regDto.getToKey());
                                    scDto.setTocoId(ipbId);
                                    scDto.setStdMngt(part);
                                    insertList.add(scDto);
                                    rtInt = scPartMapper.insertAllDao(insertList);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("SearchRegister.insertScIPBPartinfo Exception : {}", regDto.getToKey(), ex);
        }
    }

    /**
     * 2023.11.29 - IPB교범에서 작업단위부호 데이터 구성 - jingi.kim
     */
    public int insertIPBScWucList(RegisterDto regDto, Document doc, SystemXmlWrapper item) {
        int rtInt = 0;
        ArrayList<SearchDto> searchDtoList = new ArrayList<>();

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile(XALAN.REG_IPB_UNIT);
            NodeList ipbList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);

            if (ipbList.getLength() <= 0) return rtInt;

            for (int i = 0; i < ipbList.getLength(); i++) {
                Node piNode = ipbList.item(i);
                Element piElement = (Element) piNode;

                if (!piNode.getNodeName().equalsIgnoreCase(DTD.IPB_PARTINFO)) continue;

                HashMap<String, String> prMap = this.isAvaliablePartId(piNode, item);
                String prId = prMap.get("id");
                String prName = prMap.get("name");
                //String partId = this.isAvaliablePartId(piNode);
                String partName = piElement.getAttribute(ATTR.NAME);
                String wucode = piElement.getAttribute(ATTR.IPB_WUC);
                if ("".equalsIgnoreCase(wucode)) continue;

                String[] arrCode = wucode.split(",");
                for (String wuc : arrCode) {
                    String wucCode = wuc.trim();
                    if ("".equalsIgnoreCase(wucCode)) continue;
                    if (!Pattern.matches("\\w{4,5}", (wucCode))) continue;

                    SearchDto scDto = new SearchDto();
                    scDto.setToKey(regDto.getToKey());
                    scDto.setTocoId(prId);
                    scDto.setWucCode(wucCode);
                    scDto.setWucName(partName);
                    scDto.setCont(prName);
                    scDto.setCreateUserId(regDto.getUserId());
                    searchDtoList.add(scDto);
                }
            }
        } catch (Exception e) {
            log.error("insertIPBScWucList", e);
        }

        if (!searchDtoList.isEmpty()) {
            rtInt = this.insertScWuc(searchDtoList);
        }

        return rtInt;
    }

    /**
     * 2023.12.01 - IPB교범에서 partinfo 테그의 id 중 유효한 값 추출 - jingi.kim
     * 			- partinfo 테그의 id 값중 expis3.xml의 system 테그의 id 값과 같을 경우 리턴
     */
    private HashMap<String, String> isAvaliablePartId(Node partinfoNode, SystemXmlWrapper item) {
        HashMap<String, String> rtnMap = new HashMap<>();

        if ( ! partinfoNode.getNodeName().equalsIgnoreCase(DTD.IPB_PARTINFO) ) return rtnMap;

        Element partinfoElement = (Element) partinfoNode;
        String partId = partinfoElement.getAttribute(ATTR.ID);
        if ("".equalsIgnoreCase(partId)) return rtnMap;

        /*NodeList toSysList = XmlDomParser.getNodeListFromXPathAPI(this.toElem, "//"+DTD.SYSTEM+"[@id='"+partId+"']");
        if ( toSysList.getLength() > 0 ) {
            rtnMap.put("id", partId);

            Node toSysNode = toSysList.item(0);
            String toSysName = XmlDomParser.getAttributes(toSysNode.getAttributes(), ATTR.NAME);
            rtnMap.put("name", toSysName);
        }*/
        if ( item.getId().equals(partId) ) {
            rtnMap.put("id", partId);
            rtnMap.put("name", item.getName());
        }

        Node paNode = partinfoNode.getParentNode();
        Element paElement = (Element) paNode;
        if ( ! paNode.getNodeName().equalsIgnoreCase(DTD.IPB_PARTINFO) ) return rtnMap;

        String paPartId = paElement.getAttribute(ATTR.ID);
        if ("".equalsIgnoreCase(paPartId)) return rtnMap;

        /*NodeList paSysList = XmlDomParser.getNodeListFromXPathAPI(this.toElem, "//"+DTD.SYSTEM+"[@id='"+paPartId+"']");
        if ( paSysList.getLength() > 0 ) {
            rtnMap.put("id", paPartId);

            Node paSysNode = paSysList.item(0);
            String paSysName = XmlDomParser.getAttributes(paSysNode.getAttributes(), ATTR.NAME);
            rtnMap.put("name", paSysName);
        }*/
        if ( item.getId().equals(paPartId) ) {
            rtnMap.put("id", paPartId);
            rtnMap.put("name", item.getName());
        }

        return rtnMap;
    }

    /**
     * 2023.11.29 - 작업단위부호 WUC 추가 - jingi.kim
     */
    public int insertScWuc( ArrayList<SearchDto> wucList ) {
        int rtInt = 1;

        ArrayList<SearchDto> insertList = new ArrayList<>();
        try {
            for ( int i=0 ; i<wucList.size(); i++ ) {
                insertList.add(wucList.get(i));

                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                    // 속도 이슈로 인해 최대치 넘어갈경우 분할 처리하도록 수정
                    rtInt = scWucMapper.insertAllDao(insertList);

                    insertList = new ArrayList<>();
                    if (rtInt <= 0) { break; }
                }
            }

            if (!insertList.isEmpty()) {
                rtInt = scWucMapper.insertAllDao(insertList);
            }
        } catch( Exception e ) {
            e.printStackTrace();
            rtInt = -1;
        }

        return rtInt;
    }

    //WorkCard 관련 정보 등록
    public int insertWorkCard(RegisterDto regDto, Document doc, SystemXmlWrapper item) {
        int rtInt = -1;

        /*
         * 야전급 WC 처리 위해서
         */
        try {
            Node firstNode = doc.getFirstChild();
            for(int i=0;i<doc.getChildNodes().getLength();i++) {
                if (doc.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                    firstNode = doc.getChildNodes().item(i);
                    break;
                }
            }
            Element firstElement = (Element) firstNode;
            String sysType	= firstElement.getAttribute(ATTR.TYPE);
            String sysId	= firstElement.getAttribute(ATTR.ID);
            String sysName	= firstElement.getAttribute(ATTR.NAME);
            log.info("WC Type Check sysType : {}, sysId : {}, sysName : {}, firstNode : {}", sysType, sysId, sysName, firstNode.getNodeName());

            if(DTD.SYSTEM.equalsIgnoreCase(firstNode.getNodeName()) && VAL.WC_FIELD.equalsIgnoreCase(sysType)) {
                log.info("야전급 WC 입니다. 전체 system을 등록 합니다.");
                rtInt = this.insertAllXCont(regDto, doc);
                log.info("insertAllXCont rtInt:{}", rtInt);
            }else {
                rtInt = this.insertAllXContWC(regDto, doc);
                log.info("insertAllXContWC rtInt:{}", rtInt);
            }
        }catch (Exception e) {
            rtInt = this.insertAllXContWC(regDto, doc);
            log.info("insertAllXContWC rtInt:{}", rtInt);
        }
        if (rtInt >= 0) {
            rtInt = this.insertGrphXCont(regDto, doc);
            log.info("insertGrphXCont rtInt:{}", rtInt);
        }

        if (rtInt >= 0) {
            rtInt = this.insertScCont(regDto, doc);
            log.info(" insertScCont rtInt:{},", rtInt);
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) XCont(XML Data) DB 등록
     * 동시 여러 건수 등록
     */
    private int insertAllXCont(RegisterDto regDto, Document doc) {

        int rtInt = -1;

        if (doc == null || regDto == null) {
            log.info("Return null");
            return rtInt;
        }

        try {
            StringBuffer contSB = new StringBuffer();

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
            contSB.append(stringWriter);

            if (!contSB.isEmpty()) {
                rtInt = insertAllXContExec(regDto.getToKey(), regDto.getTocoId(), regDto.getUserId(), contSB, regDto.getDbType());
            }
            log.info("ContRegister.insertAllXCont rtInt:{}", rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("ContRegister.insertAllXCont Exception:{} ==> {}", ex.toString(), regDto.getTocoId(), ex);
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 WC교범의 XCont(XML Data) DB 등록 실행
     */
    private int insertAllXContWC(RegisterDto regDto, Document doc) {
        int rtInt = -1;

        if (doc == null || regDto == null) {
            log.info("CALL insertAllXContWC return null");
            return rtInt;
        }

        try {
            Node contNode = doc.cloneNode(true);

            //WC내용은 tm_wc_xcont 라는 테이블에 별도로 등록되기에, 장 내용을 등록시에는 workcards빼고 descinfo, task만 등록되도록 처리
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile(XALAN.REG_WCS);
            NodeList wcsList = (NodeList) compile.evaluate(contNode, XPathConstants.NODESET);

            if (wcsList.getLength() > 0) {
                for (int i=(wcsList.getLength()-1); i>=0; i--) {
                    Node wcsNode = wcsList.item(i);
                    Node paNode = wcsNode.getParentNode();
                    paNode.removeChild(wcsNode);
                }
            }

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            StringBuffer contSB = new StringBuffer();
            transformer.transform(new DOMSource(contNode), new StreamResult(stringWriter));
            contSB.append(stringWriter);
            if (!contSB.isEmpty()) {
                rtInt = insertAllXContExec(regDto.getToKey(), regDto.getTocoId(), regDto.getUserId(), contSB, regDto.getDbType());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("insertAllXContWC Exception:{}", ex.toString());
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 검색 내용 중 Contents 전체 관련 XCont(XML Data) DB 등록
     */
    private int insertScCont(RegisterDto regDto, Document doc) {

        int rtInt = 0;		//등록할 검색 내용 데이터가 없을 수도 있기에 0으로 초기화

        if (doc == null || regDto == null) {
            return rtInt;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile(XALAN.REG_TEXT);
            NodeList textList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            if (textList.getLength() <= 0) {
                return rtInt;
            }

            ArrayList<SearchDto> insertList = new ArrayList<>();
            int insertCnt = 0;
            StringBuilder contSB = new StringBuilder();

            for (int loop=0; loop<textList.getLength(); loop++) {
                Node textNode = textList.item(loop);

                if (textNode != null && textNode.getNodeName().equals(DTD.TEXT)) {
                    String cont = this.getTextFromNode(textNode);
                    //TODO: CodeConverter 처리
                    cont = CodeConverter.deleteAllCode(cont);
                    //Contents 내용 중 마지막 텍스트가 ""일 경우 검색 내용 데이터가 등록되지 않는 현상 수정
                    //if (cont.equals("")) {
                    if (cont.isEmpty() && contSB.length() <= eXPIS3Constants.REG_CHECK_STR_LEN && loop != (textList.getLength()-1)) {
                        continue;
                    }

                    if (!contSB.isEmpty() && !cont.isEmpty()) {
                        contSB.append(CHAR.SEARCH_TEXT);
                    }
                    contSB.append(cont);
                }
            }

            List<String> chunks = splitByCharacters(contSB.toString());

            for ( String chunk : chunks ) {
                StringBuffer buffer = new StringBuffer(chunk);
                ArrayList<SearchDto> tempList = this.insertScContSet(regDto, buffer, "");
                insertList.addAll(tempList);

                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                    rtInt = scContMapper.insertAllDao(insertList);
                    insertList = new ArrayList<>();
                    if (rtInt <= 0) { break; }
                }
            }

            if (!insertList.isEmpty()) {
                rtInt = scContMapper.insertAllDao(insertList);
                insertCnt += insertList.size();
                insertList = new ArrayList<>();
            }
            log.info("insertScCont insertList.size():{}, rtInt:{}", insertCnt, rtInt);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("SearchRegister.insertScCont Exception : {}", regDto.getToKey(), ex);
            return -1;
        }

        return rtInt;
    }

    /**
     * TO목차내용(UUID.xml) 중 검색 내용 중 Contents 전체 관련 XCont(XML Data) DB 등록 실행
     */
    private ArrayList<SearchDto> insertScContSet(RegisterDto regDto, StringBuffer contSB, String tocoId) {

        ArrayList<SearchDto> rtList = new ArrayList<SearchDto>();

        if (contSB == null || contSB.isEmpty() || regDto == null) {
            return rtList;
        }

        try {
            int maxStrLength	= eXPIS3Constants.REG_MAX_STR_LEN - 300;
            int maxLoop			= contSB.length() / maxStrLength + 1;

            if (tocoId.isEmpty()) {
                tocoId = regDto.getTocoId();
            }

            for (int i=0; i<maxLoop; i++) {
                String cont = "";
                maxStrLength	= eXPIS3Constants.REG_MAX_STR_LEN - 300;
                int lastIdx = -1;

                if (contSB.length() < maxStrLength) {	//마지막일 때는 문자열 끊지않음
                    maxStrLength = contSB.length();
                    cont = contSB.substring(0, maxStrLength);
                } else {
                    cont = contSB.substring(0, maxStrLength);

                    //검색용 Text 테이블은 순서(xth)가 없기 때문에 단어가 중간에 끊어지는 방지위해 공백(" ")으로 끊음
                    lastIdx = cont.lastIndexOf(" ");
                    if (!cont.isEmpty() && lastIdx > 0) {
                        cont = contSB.substring(0, lastIdx);
                        maxStrLength = lastIdx;
                    }

                    if (cont.isEmpty()) {
                        log.info("insertScContSet Contents is NULL Exception");
                        continue;
                    }
                }

                SearchDto scDto = new SearchDto();
                scDto.setToKey(regDto.getToKey());
                scDto.setTocoId(tocoId);
                scDto.setCont(cont);
                scDto.setCreateUserId(regDto.getUserId());
                rtList.add(scDto);

                if (!contSB.isEmpty() && contSB.length() > maxStrLength) {
                    contSB = new StringBuffer(contSB.substring(maxStrLength, contSB.length()));
                }
                if (contSB.length() <= 0) {
                    break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("SearchRegister.insertScContSet Exception : {}", regDto.getToKey(), ex);
        }

        return rtList;
    }

    //일반적인 교범 등록
    public int insertDefaultToCo(RegisterDto regDto, Document doc, SystemXmlWrapper systemXmlWrapper) {
        int rtInt = -1;

        rtInt = this.insertAllXCont(regDto, doc);
        log.info(" insertAllXCont rtInt:{}", rtInt);

        if (rtInt >= 0) {
            rtInt = this.insertGrphXCont(regDto, doc);
            log.info(" insertGrphXCont  rtInt:{}", rtInt);
        }
        if (rtInt >= 0) {
            rtInt = this.insertScCont(regDto, doc);
            log.info(" insertScCont  rtInt:{}", rtInt);
        }

        return rtInt;
    }

    /**
     * JG 타입의 준비사항내  "필수교환 품목 및 소모성 물자 : consum" , "기타요건 : othercond" 테이블의 내용이 검색 되도록 SCCont DB 등록
     * 	: jingi.kim / 2024.02.13.
     */
    public int insertScContJG(RegisterDto regDto, Document doc) {

        int rtInt = 0;		//등록할 검색 내용 데이터가 없을 수도 있기에 0으로 초기화

        if (doc == null || regDto == null) {
            return rtInt;
        }

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile("//"+DTD.IN_CONSUM);
            NodeList consumList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            if (consumList.getLength() <= 0) {
                return rtInt;
            }

            ArrayList<SearchDto> insertList = new ArrayList<>();
            int insertCnt = 0;
            StringBuilder contSB = new StringBuilder();

            for (int loop=0; loop<consumList.getLength(); loop++) {
                Node consumNode = consumList.item(loop);
                if ( consumNode == null || !consumNode.getNodeName().equalsIgnoreCase(DTD.IN_CONSUM) ) continue;

                Element consumElement = (Element) consumNode;

                String conGovstd = consumElement.getAttribute(ATTR.IN_GOVSTD);
                if (!conGovstd.isEmpty()) {
                    contSB.append(conGovstd);
                    contSB.append(CHAR.SEARCH_TEXT);
                }

                String conPartnum = consumElement.getAttribute(ATTR.IN_PARTNUM);
                if (!conPartnum.isEmpty()) {
                    contSB.append(conPartnum);
                    contSB.append(CHAR.SEARCH_TEXT);
                }

                String conQuantity = consumElement.getAttribute(ATTR.IN_QUANTITY);
                if (!conQuantity.isEmpty()) {
                    contSB.append(conQuantity);
                    contSB.append(CHAR.SEARCH_TEXT);
                }

                // partbase
                NodeList partList = consumNode.getChildNodes();
                if (partList.getLength() > 0) {
                    for ( int p=0; p<partList.getLength(); p++ ) {
                        Node partNode = partList.item(p);
                        if ( partNode == null || !partNode.getNodeName().equalsIgnoreCase(DTD.IN_PARTBASE) ) continue;

                        Element partElement = (Element) partNode;

                        String partName = partElement.getAttribute(ATTR.IN_NAME);
                        if (!partName.isEmpty()) {
                            contSB.append(partName);
                            contSB.append(CHAR.SEARCH_TEXT);
                        }

                        String partNum = partElement.getAttribute(ATTR.IN_PARTNUM);
                        if (!partNum.isEmpty()) {
                            contSB.append(partNum);
                            contSB.append(CHAR.SEARCH_TEXT);
                        }

                        String partCage = partElement.getAttribute(ATTR.IN_CAGE);
                        if (!partCage.isEmpty()) {
                            contSB.append(partCage);
                            contSB.append(CHAR.SEARCH_TEXT);
                        }
                    }
                }
            }


            // othercond
            compile = xPath.compile(XALAN.INXALAN_OTHERCOND);
            NodeList othercondList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            if (othercondList.getLength() > 0) {
                for (int lp=0; lp<othercondList.getLength(); lp++) {
                    Node othercondNode = othercondList.item(lp);
                    if ( othercondNode == null || !othercondNode.getNodeName().equalsIgnoreCase(DTD.IN_OTHERCOND) ) continue;

                    Element othercondElement = (Element) othercondNode;

                    String othercondItem = othercondElement.getAttribute(ATTR.IN_OTH_ITEM);
                    if (!othercondItem.isEmpty()) {
                        contSB.append(othercondItem);
                        contSB.append(CHAR.SEARCH_TEXT);
                    }

                    String othercondDesc = othercondElement.getAttribute(ATTR.IN_OTH_DESC);
                    if (!othercondDesc.isEmpty()) {
                        contSB.append(othercondDesc);
                        contSB.append(CHAR.SEARCH_TEXT);
                    }
                }
            }

            if (!contSB.isEmpty()) {
                List<String> chunks = splitByCharacters(contSB.toString());

                for ( String chunk : chunks ) {
                    StringBuffer buffer = new StringBuffer(chunk);
                    ArrayList<SearchDto> tempList = this.insertScContSet(regDto, buffer, "");
                    insertList.addAll(tempList);

                    if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                        rtInt = scContMapper.insertAllDao(insertList);
                        insertList = new ArrayList<>();
                        if (rtInt <= 0) { break; }
                    }
                }
            }

            if (!insertList.isEmpty()) {
                rtInt = scContMapper.insertAllDao(insertList);
                insertCnt += insertList.size();
                insertList = new ArrayList<>();
            }

            log.info("insertScContJG insertList.size() : {} ,  rtInt:{}", insertCnt, rtInt);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("SearchRegister.insertScContJG Exception : {}", regDto.getToKey(), ex);
            return -1;
        }

        return rtInt;
    }

    // KTA, 색인 업데이트 기능
    // 국가재고번호 검색, 참고지정번호 검색, 부품정보 검색 관련 함수
    // 국가재고번호, 부품정보는 교범 등록시 속성의 값을 검색용 테이블로 구성하므로 실질적으로 참고지정번호의 데이터만 매핑
    public int updateIPBSearchForKTA(Document doc, RegisterDto regDto) {

        if ( doc == null ) return -1;
        if ( !"eXPISInfo".equalsIgnoreCase(doc.getNodeName()) ) return -1;
        if ( !doc.hasChildNodes() ) return -1;

        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression compile = xPath.compile(XALAN.REG_SYSTEM);
            NodeList nodeList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            if ( nodeList.getLength() != 0) return -1;

            compile = xPath.compile(".//part");
            NodeList partList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            for (int i=0; i<partList.getLength(); i++) {
                Node part = partList.item(i);
                Element partElement = (Element) part;

                String ref		= partElement.getAttribute("ref");
                String figno	= partElement.getAttribute("figno");
                String inum 	= partElement.getAttribute("inum");
                String num 		= partElement.getAttribute("num");
                String nsn		= partElement.getAttribute("nsn");

                SearchPartinfoDto scPartDto = new SearchPartinfoDto();
                scPartDto.setIpbCode(ref);
                scPartDto.setIndexNo(inum);
                scPartDto.setGrphNo(figno);
                scPartDto.setPartNo(num);
                scPartDto.setNsn(nsn);

                if (!nsn.isEmpty()) {
                    //System.out.println("국가 재고 번호 색인 Pass");
                }else if(!ref.isEmpty() && !figno.isEmpty() && !inum.isEmpty() && !num.isEmpty()) {
                    // 참고 지정번호
                    scPartDto.setIpbCode(ref);
                    scPartDto.setIndexNo(inum);
                    scPartDto.setGrphNo(figno);
                    scPartDto.setPartNo(num);
                    scPartDto.setNsn(nsn);
                    log.info("ref : {}, inum : {}, , figno : {}, , num : {}, nsn : {}", ref, inum, figno, num, nsn);
                    scContMapper.updateIPBSearchForKTA(scPartDto);
                }else if(!figno.isEmpty() && !inum.isEmpty() && !num.isEmpty()) {
                    // 부품번호 색인
                    scPartDto.setPartNo(num);
                    scPartDto.setGrphNo(figno);
                    scPartDto.setIpbCode(ref);
                    scPartDto.setIndexNo(inum);
                    scPartDto.setNsn(nsn);
                }
            }

        } catch (Exception e) {
            log.error("updateIPBSearch Exception", e);
        }

        return 1;
    }

    /**
     * 그림목차/동영상목차 생성
     */
    public HashMap<String, Node> createGrphToco(RegisterDto regDto, Document doc, Node grphTocoNode, Node videoTocoNode) {

        HashMap<String, Node> rtMap = new HashMap<String, Node>();

        if (doc == null || regDto == null) {
            return rtMap;
        }

        try {
            String listName = "";
            String listType = "";

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression compile = xPath.compile(XALAN.REG_TOCO_COMM);
            Node firstSysNode = (Node) compile.evaluate(doc, XPathConstants.NODE);
            Element firstSysElement = (Element) firstSysNode;
            if (firstSysNode != null) {
                listName = firstSysElement.getAttribute(ATTR.NAME);
                listType = firstSysElement.getAttribute(ATTR.TYPE);
            }

            compile = xPath.compile(XALAN.REG_GRPH);
            NodeList grphList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            if (grphList == null || grphList.getLength() <= 0) {
                return rtMap;
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
                    if (extPtr.isEmpty() || grphId.isEmpty() || grphName.isEmpty()) {
                        if(!"fi_mainimg".equals(type)) { continue; }
                    }

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
                    Element grphElem = doc.createElement(DTD.SYSTEM);
                    grphElem.setAttribute(ATTR.ID,		grphId);
                    grphElem.setAttribute(ATTR.NAME,	grphName);
                    grphElem.setAttribute(ATTR.TYPE,	VAL.TOCOLIST_GRPH);
                    if (paNodeNm.equals(DTD.IPB_PARTINFO)) {
                        grphElem.setAttribute(ATTR.SYS_TOCO_ID,			paId);
                        grphElem.setAttribute(ATTR.SYS_TOCO_NAME,		paName);
                        grphElem.setAttribute(ATTR.SYS_TOCO_TYPE,		VAL.TOCOLIST_IPB);
                    } else {
                        grphElem.setAttribute(ATTR.SYS_TOCO_ID,			regDto.getTocoId());
                        grphElem.setAttribute(ATTR.SYS_TOCO_NAME,		listName);
                        grphElem.setAttribute(ATTR.SYS_TOCO_TYPE,		listType);
                    }

                    int lastIndex = extPtr.lastIndexOf(".");
                    String extName = (lastIndex == -1) ? "" : extPtr.substring(lastIndex+1);
                    log.info("extName : {} => {}", extName, VAL.TOCOLIST_VIDEO_EXT.indexOf(extName.toUpperCase()));
                    if (VAL.TOCOLIST_VIDEO_EXT.contains(extName.toUpperCase())) {
                        //동영상인 경우 type을 Grph가 아닌 Video를 사용하도록 변경
                        grphElem.setAttribute(ATTR.TYPE, VAL.TOCOLIST_VIDEO);
                        videoTocoNode.appendChild(grphElem);
                    } else {
                        grphTocoNode.appendChild(grphElem);
                    }
                }
            }

            rtMap.put(VAL.TOCOLIST_GRPH, grphTocoNode);
            rtMap.put(VAL.TOCOLIST_VIDEO, videoTocoNode);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("TreeRegister.createGrphToco Exception:{}", ex.toString());
        }

        return rtMap;
    }

    /**
     * 표목차 생성
     */
    public Node createTableToco(RegisterDto regDto, Document doc, Node tableTocoNode) {

        if (doc == null || regDto == null) {
            return tableTocoNode;
        }

        try {
            String listName = "";
            String listType = "";

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression compile = xPath.compile(XALAN.REG_TOCO_COMM);
            Node firstSysNode = (Node) compile.evaluate(doc, XPathConstants.NODE);
            Element firstSysElement = (Element) firstSysNode;
            if (firstSysNode != null) {
                listName = firstSysElement.getAttribute(ATTR.NAME);
                listType = firstSysElement.getAttribute(ATTR.TYPE);
            }

            compile = xPath.compile(XALAN.REG_TABLE);
            NodeList tableList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            if (tableList == null || tableList.getLength() <= 0) {
                return tableTocoNode;
            }

            for (int loop=0; loop<tableList.getLength(); loop++) {
                Node tableNode = tableList.item(loop);
                Element tableElement = (Element) tableNode;

                String tableId		= "";
                String tableName	= "";
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
                        Element tableElem = doc.createElement(DTD.SYSTEM);
                        tableElem.setAttribute(ATTR.SYS_ID,         tableId);
                        tableElem.setAttribute(ATTR.SYS_NAME,       tableName);
                        tableElem.setAttribute(ATTR.SYS_TYPE,       VAL.TOCOLIST_TABLE);
                        tableElem.setAttribute(ATTR.SYS_TOCO_ID,    regDto.getTocoId());
                        tableElem.setAttribute(ATTR.SYS_TOCO_NAME,  listName);
                        tableElem.setAttribute(ATTR.SYS_TOCO_TYPE,  listType);

                        tableTocoNode.appendChild(tableElem);
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("TreeRegister.createTableToco Exception:{}", ex.toString());
        }

        return tableTocoNode;
    }

    /**
     * 단일 단위로 교범 DB 등록시에 트리 정보에 save_yn 처리하고 재 등록 위함
     */
    public int updateToSaveYn(RegisterDto rcDto) {

        int rtInt	= -1;		//등록건수, 0이면 등록한것이 없는것임
        int delInt	= -1;		//삭제건수, 0이어도 오류 아님
        SysTreeXmlWrapper sysTreeXmlWrapper;

        try {
            //파일이 아닌 기존에 등록한 DB를 읽어서 계통트리(SysTree) 정보를 Dom으로 변환하여 Node로 처리
            sysTreeXmlWrapper = this.getSystemInfoDomFromDB(rcDto);
            log.info("DB Insert Start : {}", rcDto.getToKey());
            if (sysTreeXmlWrapper == null) {
                return rtInt;
            }

            //TODO: 하위 노드는 검사하지 않는게 맞는가?
            //1. TreeXCont 데이터 읽어와서 Attribute 추가해서 Dom 수정
            List<SystemXmlWrapper> systemXmlList = sysTreeXmlWrapper.getSystemList();
            for (int i = 0; i < systemXmlList.size(); i++) {
                SystemXmlWrapper item = systemXmlList.get(i);
                if ( item.getType().equalsIgnoreCase("TO") ) {
                    item.setSaveyn(eXPIS3Constants.TOSAVE_YN_YES);
                }
            }

            //등록 Core에서 사용하는 컬렉션인 RegisterDto에 값 셋팅
            RegisterDto regDto = new RegisterDto();
            regDto.setToKey("");
            regDto.setUserId(rcDto.getUserId());
            regDto.setIsSysDel(eXPIS3Constants.TREE_KIND_SYSTEM);
            regDto.setDbType(rcDto.getDbType());

            //1. TreeXCont Mapper 호출하여 테이블에 등록 (tm_tree_xcont delete)
            delInt = this.deleteSystem(regDto);
            log.info("updateToSaveYn ===> rtInt:{}", rtInt);

            //2. TreeXCont Mapper 호출하여 테이블에 등록 (tm_tree_xcont insert)
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            if (delInt >= 0) {
                String xmlString = xmlMapper.writeValueAsString(sysTreeXmlWrapper);
                xmlString = xmlString.replaceAll("SysTreeXmlWrapper", "techinfo");
                rtInt = this.insertTreeXCont(regDto, new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
                log.info("updateToSaveYn ===> rtInt:{}", rtInt);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("updateToSaveYn Exception:{}", ex.toString());
        }

        return rtInt;
    }

    /**
     * 계통트리(SysTree) 정보를 DB에서 XML 로 읽어 DOM으로 반환
     */
    public SysTreeXmlWrapper getSystemInfoDomFromDB(RegisterDto rcDto) {

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SysTreeXmlWrapper sysTreeXmlWrapper = new SysTreeXmlWrapper();
        StringBuilder contSB = new StringBuilder();

        try {
            ArrayList<TreeXContDto> treeList = null;

            //1. DB에서 데이터 추출하는 MyBatis의 DAO 처리 모듈 호출 -> Tree XML Contents 추출 -> DOM으로 생성
            TreeXContDto treeDto = new TreeXContDto();
            treeDto.setTreeKind(eXPIS3Constants.TREE_KIND_SYSTEM);
            treeList = treeMapper.selectListDao(treeDto);

            if (treeList != null && !treeList.isEmpty()) {
                for (TreeXContDto rsDto : treeList) {
                    contSB.append(rsDto.getTreeXcont());
                }
            }

            if (!contSB.isEmpty()) {
                sysTreeXmlWrapper = xmlMapper.readValue(new ByteArrayInputStream(contSB.toString().getBytes(StandardCharsets.UTF_8)), SysTreeXmlWrapper.class);
            }

        }  catch (Exception ex) {
            ex.printStackTrace();
            log.error("RegisterCollector.getSystemInfoDomFromDB Exception:{}", ex.toString());
        }

        return sysTreeXmlWrapper;
    }

    /**
     * TO Info DB 수정
     * 동시 단일 건수 등록
     */
    public int updateToInfo(RegisterDto regDto, ExpisXmlWrapper expisXmlWrapper) {

        int rtInt = -1;

        if (expisXmlWrapper == null || regDto == null) {
            return rtInt;
        }

        try {
            //원판 및 마지막 버전 정보 추출
            String	toChgNo			= "";
            String	toChgDate		= "";
            VersionXmlWrapper verNode		= expisXmlWrapper.getVersionList().getLast();
            if (verNode != null) {
                toChgNo		= Optional.ofNullable(verNode.getChangeno()).orElse("");
                toChgDate	= Optional.ofNullable(verNode.getChgdate()).orElse("");
            }

            //ToInfoDto 에 데이터 셋팅하여 DB 수정
            ToInfoDto toDto = new ToInfoDto();
            toDto.setToKey(regDto.getToKey());
            if (!toChgNo.isEmpty() && !toChgDate.isEmpty()) {
                toDto.setToChgNo(toChgNo);
                toDto.setToChgDate(toChgDate);
            }
            toDto.setToSaveYn(eXPIS3Constants.TOSAVE_YN_YES);
            toDto.setModifyUserId(regDto.getUserId());

            rtInt = toInfoMapper.updateDao(toDto);


            log.info("ToRegister.updateToInfo rtInt:{}", rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("ToRegister.updateToInfo Exception:{}", ex.toString());
        }

        return rtInt;
    }

    /**
     * TO목차(eXPIS.xml) Info DB 등록
     * 동시 여러 건수 등록
     */
    public int insertTocoInfo(RegisterDto regDto, ExpisXmlWrapper expisXmlWrapper) {

        int rtInt = -1;

        if (expisXmlWrapper == null || regDto == null) {
            return rtInt;
        }

        SystemXmlWrapper sysNode;
        List<SystemXmlWrapper> sysList;

        try {
            //2) TOCO 목차 Info Insert
            ArrayList<TocoInfoDto> insertList = new ArrayList<>();
            int insertCnt = 0;

            String	tocoId					= "";
            String	pTocoId					= "";
            String	tocoName				= "";
            String	tocoType				= "";
            String	tocoVehicleType			= "";
            String	tocoRefId				= "";
            String	tocoSecurityCode		= "";
            String	tocoStatusCode			= "";
            String	tocoSssnNo				= "";
            String	tocoDummy				= "";
            String	tocoVerId				= "";
            String	tocoChgNo				= "";
            String	tocoRevNo				= "";

            sysList		= flatterSysTreeXml(expisXmlWrapper.getSystemList(), "");
            for (int i=0; i<sysList.size(); i++) {
                sysNode = sysList.get(i);

                tocoId		= sysNode.getId();
                SystemXmlWrapper parentNode = null;
                for ( SystemXmlWrapper systemXmlWrapper : expisXmlWrapper.getSystemList() ) {
                    parentNode = systemXmlWrapper.findParentOf(sysNode);
                    if ( parentNode != null ) break;
                }

                if (parentNode != null && !tocoId.isEmpty()) {
                    pTocoId	= Optional.ofNullable(parentNode.getId()).orElse("0");
                } else {
                    pTocoId	= "0";
                }
                tocoName				= Optional.ofNullable(sysNode.getName()).orElse("");
                tocoType				= this.convertTocoType(sysNode.getType());
                tocoVehicleType			= Optional.ofNullable(sysNode.getVehicletype()).orElse("").toUpperCase();
                tocoRefId				= Optional.ofNullable(sysNode.getRef()).orElse("");
                tocoSecurityCode		= Optional.ofNullable(sysNode.getSecurity()).orElse("");
                tocoStatusCode			= Optional.ofNullable(sysNode.getStatus()).orElse("");
                tocoSssnNo				= Optional.ofNullable(sysNode.getSssn()).orElse("");
                tocoDummy				= Optional.ofNullable(sysNode.getDummy()).orElse("");
                tocoVerId				= Optional.ofNullable(sysNode.getVersion()).orElse("");

                //<system type='TO' vehicletype="System">으로 되있기에 빈값 변환
                if (tocoType.equals(eXPIS3Constants.STOCO_TYPE_TO)) {
                    tocoVehicleType = "";
                }
                // tocoVehicleType 없을 경우 부모 객체 상속 받는 로직 추가
                try {
                    if(tocoVehicleType.isEmpty()) {//tocoVehicleType 없는 경우
                        if ( parentNode != null ) {
                            parentNode = sysNode;
                            for(int j=0;j<10;j++){					//부모의 상위까지 가기 위해 for문 처리
                                parentNode = parentNode.findParentOf(parentNode);
                                if ( parentNode == null ) break;

                                tocoVehicleType	= Optional.ofNullable(parentNode.getVehicletype()).orElse("").toUpperCase();
                                log.info("Check Parent Node VEHICLETYPE[{}] : {}", j, tocoVehicleType);
                                if(!tocoVehicleType.isEmpty()) break;
                            }
                        }
                    }
                }catch (Exception e) {
                    log.error("Check Parent Node Error",e);
                }
                if (tocoVerId != null && !tocoVerId.isEmpty()) {
                    List<VersionXmlWrapper> versionList = expisXmlWrapper.getVersionList();
                    for (VersionXmlWrapper item : versionList) {
                        if (item.getId().equalsIgnoreCase(tocoVerId)) {
                            tocoChgNo = Optional.ofNullable(item.getChangeno()).orElse("");
                            tocoRevNo = Optional.ofNullable(item.getRevision()).orElse("");
                        }
                    }
                }

                if (tocoId == null || tocoId.isEmpty()) {
                    log.info("insertTocoInfo TocoId is NULL Exception");
                    continue;
                }

                TocoInfoDto tocoDto = new TocoInfoDto();
                tocoDto.setToKey(regDto.getToKey());
                tocoDto.setTocoId(tocoId);
                tocoDto.setPTocoId(pTocoId);
                //tocoDto.setTocoOrd( (i+1) );
                tocoDto.setTocoOrd( Long.parseLong(i+1+"") );
                tocoDto.setTocoName(tocoName);
                tocoDto.setTocoType(tocoType);
                tocoDto.setTocoVehicleType(tocoVehicleType);
                tocoDto.setTocoRefId(tocoRefId);
                tocoDto.setTocoSecurityCode(tocoSecurityCode);
                tocoDto.setTocoStatusCode(tocoStatusCode);
                tocoDto.setTocoSssnNo(tocoSssnNo);
                tocoDto.setTocoDummy(tocoDummy);
                tocoDto.setTocoVerId(tocoVerId);
                tocoDto.setTocoChgNo(tocoChgNo);
                tocoDto.setTocoRevNo(tocoRevNo);
                tocoDto.setStatusKind( eXPIS3Constants.STATUS_KIND_VALID );
                tocoDto.setCreateUserId( regDto.getUserId() );

                insertList.add(tocoDto);

                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                    rtInt = tocoInfoMapper.insertAllDao(insertList);

                    insertCnt += insertList.size();
                    insertList = new ArrayList<>();
                    if (rtInt <= 0) { break; }
                }
            }

            if (!insertList.isEmpty()) {
                rtInt = tocoInfoMapper.insertAllDao(insertList);

                insertCnt += insertList.size();
                insertList = new ArrayList<>();;
            }

            log.info("insertTocoInfo insertList.size():{}, rtInt:{}", insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.debug("insertTocoInfo Exception:{}", ex.toString());
        }

        return rtInt;
    }

    /**
     * System Type 이 문자열로 된것을 코드로 변환
     */
    public String convertTocoType(String typeName) {

        String rtStr = "";

        if (typeName == null || typeName.isEmpty()) {
            return rtStr;
        }

        /**
         * 교범목차(eXPIS.xml) 에 대한 <system type=''> 값 DB등록 모듈 시 필요
         */
        Map<String, String> sToCoMap = new HashMap<>();
        sToCoMap.put(VAL.SYS_TYPE_TO, eXPIS3Constants.STOCO_TYPE_TO);
        sToCoMap.put(VAL.INTRO, eXPIS3Constants.STOCO_TYPE_INTRO);
        sToCoMap.put(VAL.CHAPTER, eXPIS3Constants.STOCO_TYPE_CHAPTER);
        sToCoMap.put(VAL.SECTION, eXPIS3Constants.STOCO_TYPE_SECTION);
        sToCoMap.put(VAL.TOPIC, eXPIS3Constants.STOCO_TYPE_TOPIC);
        sToCoMap.put(VAL.SUBTOPIC, eXPIS3Constants.STOCO_TYPE_SUBTOPIC);
        sToCoMap.put(VAL.TASK, eXPIS3Constants.STOCO_TYPE_TASK);
        sToCoMap.put(VAL.WC_UNIT, eXPIS3Constants.STOCO_TYPE_WC_UNIT);
        sToCoMap.put(VAL.WC_FIELD, eXPIS3Constants.STOCO_TYPE_WC_FIELD);
        sToCoMap.put(VAL.IPB_UNIT, eXPIS3Constants.STOCO_TYPE_IPB_UNIT);
        sToCoMap.put(VAL.IPB_FIELD, eXPIS3Constants.STOCO_TYPE_IPB_FIELD);
        sToCoMap.put(VAL.IPB_ROOT, eXPIS3Constants.STOCO_TYPE_IPB_ROOT);
        sToCoMap.put(VAL.FI_DI, eXPIS3Constants.STOCO_TYPE_FI_DI);
        sToCoMap.put(VAL.FI_DI_DESC, eXPIS3Constants.STOCO_TYPE_FI_DIDESC);
        sToCoMap.put(VAL.FI_FIA, eXPIS3Constants.STOCO_TYPE_FI_FIA);
        sToCoMap.put(VAL.FI_LR, eXPIS3Constants.STOCO_TYPE_FI_LR);
        sToCoMap.put(VAL.FI_LR_DESC, eXPIS3Constants.STOCO_TYPE_FI_LRDESC);
        sToCoMap.put(VAL.FI_AP, eXPIS3Constants.STOCO_TYPE_FI_AP);
        sToCoMap.put(VAL.FI_FIPIC, eXPIS3Constants.STOCO_TYPE_FI_FIPIC);
        sToCoMap.put(VAL.FI_DDS, eXPIS3Constants.STOCO_TYPE_FI_DDS);
        sToCoMap.put(VAL.FI_FIB, eXPIS3Constants.STOCO_TYPE_FI_FIB);

        try {
            typeName = typeName.toUpperCase();

            rtStr = sToCoMap.getOrDefault(typeName, typeName);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("SystemRegister.convertSysType Exception:{}", ex.toString());
        }

        return rtStr;
    }

    /**
     * TO목차(eXPIS.xml) Info 에서 버전정보(<version>) DB 등록
     * 동시 여러 건수 등록
     */
    public int insertVerInfo(RegisterDto regDto, ExpisXmlWrapper expisXmlWrapper) {

        int rtInt = 0;		//등록할 Version 데이터가 없을 수도 있기에 0으로 초기화

        if (expisXmlWrapper == null || regDto == null) {
            return rtInt;
        }

        VersionXmlWrapper verNode	= null;
        List<VersionXmlWrapper> verList		= null;

        try {
            //TOCO 버전 Info Insert
            ArrayList<VersionInfoDto> insertList = new ArrayList<>();
            int insertCnt = 0;

            String	verId		= "";
            String	chgNo		= "";
            String	chgDate	= "";
            String	revNo		= "";
            String	revDate	= "";

            verList		= expisXmlWrapper.getVersionList();
            for (VersionXmlWrapper versionXmlWrapper : verList) {
                verNode = versionXmlWrapper;

                verId = Optional.ofNullable(verNode.getId()).orElse("");
                chgNo = Optional.ofNullable(verNode.getChangeno()).orElse("");
                chgDate = Optional.ofNullable(verNode.getChgdate()).orElse("");
                revNo = Optional.ofNullable(verNode.getRevision()).orElse("");
                revDate = Optional.ofNullable(verNode.getRevdate()).orElse("");

                if (verId.isEmpty()) {
                    log.info("insertVerInfo VersionId is NULL Exception");
                    continue;
                }

                VersionInfoDto verDto = new VersionInfoDto();
                verDto.setToKey(regDto.getToKey());
                verDto.setVerId(verId);
                verDto.setChgNo(chgNo);
                verDto.setChgDate(chgDate);
                verDto.setRevNo(revNo);
                verDto.setRevDate(revDate);
                verDto.setStatusKind(eXPIS3Constants.STATUS_KIND_VALID);
                verDto.setCreateUserId(regDto.getUserId());

                insertList.add(verDto);

                if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                    rtInt = versionInfoMapper.insertAllDao(insertList);

                    insertCnt += insertList.size();
                    insertList = new ArrayList<>();
                    if (rtInt <= 0) {
                        break;
                    }
                }
            }

            if (!insertList.isEmpty()) {
                rtInt = versionInfoMapper.insertAllDao(insertList);

                insertCnt += insertList.size();
                insertList = new ArrayList<>();
            }

            log.info("insertVerInfo insertList.size():{}, rtInt:{}", insertCnt, rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("ToRegister.insertVerInfo Exception:{}", ex.toString());
        }

        return rtInt;
    }

    /**
     * TO 디렉토리 내 oldversiondata/UUID.xml 파일을 XML 로 읽어 DOM으로 반환
     */
    public Document getVerInfoDom(String verFilePath) {

        Document		doc		= null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        if (verFilePath == null || verFilePath.isEmpty()) {
            return null;
        }

        try {
            //입력받은 TO 폴더에서 oldversiondata 하위의 버전내용 파일인 UUID.xml를 추출해서 Dom 생성
            File verFile	= new File(verFilePath);
            if (verFile.exists()) {
                StringBuffer sbCont = this.getUniCont(verFilePath); //Uni Code 포함되었는지 검사

                DocumentBuilder builder = factory.newDocumentBuilder();

                if (sbCont != null && !sbCont.toString().isEmpty()) {
                    doc = builder.parse(sbCont.toString());
                } else {
                    doc = builder.parse(verFile);
                }
            }

        }  catch (Exception ex) {
            ex.printStackTrace();
            log.info("RegisterCollector.getVerInfoDom Exception:{}", ex.toString());
        } finally {
        }

        return doc;
    }

    /**
     * 전달받은 파일 내 내용에 Uni Code (® 등) 가 포함되어 있는지 검사
     */
    public StringBuffer getUniCont(String filePath) {

        StringBuffer	rtSB	= null;
        FileReader	fr			= null;
        BufferedReader br	= null;

        if (filePath == null || filePath.isEmpty()) {
            return null;
        }

        try {
            fr	= new FileReader(filePath);
            File file = new File(filePath);
            StringBuffer sbCont = new StringBuffer();

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "euc-kr"));
            String line = "";
            while ((line = br.readLine()) != null) {
                //sbCont.append(line).append("\r\n");
                sbCont.append(line);
            }
            br.close();
            fr.close();

            /*
             * 문서에 ksc5601의 업데이트문자인 ®이 포함돼있을 경우 처리
             * XML파일을 스트링으로 읽어서 검사하여, 해당문자가 있으면 대체문자로 치환후 DOM 재구성
             * '®'의 유니코드인 '0x00AE'로 검사 -> 임시 대체문자인 '&#regr;'로 치환, 후에 HTML 태그인 '&#X00ae;'로 대체
             */
            int idx = sbCont.toString().indexOf(0x00AE); //®
            if (idx > -1) {
                sbCont = new StringBuffer(sbCont.toString().replaceAll("[\\u00AE]", "&amp;#regr;"));
                rtSB = sbCont;
            }
        }  catch (Exception ex) {
            ex.printStackTrace();
            log.info("RegisterCollector.getUniCont Exception:{}", ex.toString());
        } finally {
            if (br != null) { try{br.close(); } catch(IOException e) {} }
            if (fr != null) { try{fr.close(); } catch(IOException e) {} }
        }

        return rtSB;
    }

    /**
     * 버전 정보 등록시에 버전 파일 내에 아이콘 리스트 추출
     */
    public StringBuffer getIconXml(Document doc) {

        StringBuffer rtSB = new StringBuffer();

        if (doc == null) {
            return rtSB;
        }

        try {
            //전달받은 oldversiondata 하위 버전내용 파일 UUID.xml Dom에서 icon List를 추출(Contents 단위마다 추가위함)
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            XPathExpression compile = xPath.compile(XALAN.REG_ICON);
            NodeList iconList = (NodeList) compile.evaluate(doc, XPathConstants.NODESET);
            Node iconNode		= null;

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            for (int i=0; i<iconList.getLength(); i++) {
                iconNode = iconList.item(i);
                String contSB = "";
                transformer.transform(new DOMSource(iconNode), new StreamResult(stringWriter));
                rtSB.append(contSB);
            }

        }  catch (Exception ex) {
            ex.printStackTrace();
            log.info("RegisterCollector.getIconXml Exception:{}", ex.toString());
        } finally {
        }

        return rtSB;
    }

    /**
     * TO 내 버전내용(oldversiondata/UUID.xml) XCont(XML Data) DB 등록
     * 동시 여러 건수 등록, 버전정보는 변경내용(xcont) 없어도 등록해야함
     */
    public int insertVerXCont(RegisterDto regDto, ExpisXmlWrapper expisXmlWrapper, Document doc, StringBuffer iconSB) {

        int rtInt = -1;

        if (doc == null || regDto == null) {
            return rtInt;
        }

        try {
            String	tocoId		= "";
            String	contId		= "";
            String	verId		= "";
            String	verName		= "";
            Node subNode		= null;

            NodeList contList	= doc.getChildNodes();
            log.info("insertVerXCont contList : {}", contList.getLength());
            if (contList.getLength() <= 0) {
                return rtInt;
            }

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            StringWriter stringWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            for (int i=0; i<contList.getLength(); i++) {
                subNode = contList.item(i);
                Element subElement = (Element) subNode;

                log.info("insertVerXCont subNode : {}", subNode);
                String uuid = "";
                if (subNode != null && subNode.getNodeType() == Node.ELEMENT_NODE && !subNode.getNodeName().equals(DTD.ICON)) {
                    uuid = subElement.getAttribute(ATTR.ID);
                } else {
                    rtInt = 0;
                }
                log.info("insertVerXCont uuid : {}", uuid);
                if (uuid.isEmpty()) { continue; }

                //<system> 태그로 감싸기
                StringBuilder contSB = new StringBuilder();
                transformer.transform(new DOMSource(subNode), new StreamResult(stringWriter));
                contSB.append("<"+DTD.SYSTEM+">");
                contSB.append(stringWriter);
                contSB.append(iconSB.toString());
                contSB.append("</"+DTD.SYSTEM+">");
                log.info("insertVerXCont contSB : {}", contSB);

                int maxStrLength	= eXPIS3Constants.REG_MAX_STR_LEN;
                int maxLoop			= contSB.length() / maxStrLength + 1;
                int insertCnt			= 0;

                //1) TO 버전내용 XCont(XML data) Insert
                ArrayList<XContDto> insertList = new ArrayList<>();

                List<String> chunks = splitByCharacters(contSB.toString());

                for ( String chunk : chunks ) {
                    tocoId = this.getVerInfo(1, regDto.getVerFilePath());
                    contId = subElement.getAttribute(ATTR.ID);
                    String verNo = this.getVerInfo(3, regDto.getVerFilePath());
                    Node verNode = (Node) xPath.evaluate(XALAN.getVerFromNoXPathSql(verNo), doc, XPathConstants.NODE);
                    Element verElement = (Element) verNode;
                    if (verNode != null) {
                        verId = verElement.getAttribute(ATTR.ID);
                    } else {
                        verId = "";
                    }
                    verName	= this.getVerInfo(2, regDto.getVerFilePath());
                    log.info("insertVerXCont verId : {}, regDto.getToKey : {}, tocoId : {}, contId : {}, verName : {}, insertCnt : {}, chunk : {}, regDto.getUserId : {}", verId, regDto.getToKey(), tocoId, contId, verName, insertCnt, chunk, regDto.getUserId());

                    if (!verId.isEmpty()) {
                        insertCnt++;

                        XContDto contDto = new XContDto();
                        contDto.setToKey(regDto.getToKey());
                        contDto.setTocoId(tocoId);
                        contDto.setContId(contId);
                        contDto.setVerId(verId);
                        contDto.setVerName(verName);
                        contDto.setXth( (insertCnt) );
                        contDto.setXcont(chunk);
                        contDto.setCreateUserId(regDto.getUserId());
                        insertList.add(contDto);
                    }

                    if (insertList.size() >= eXPIS3Constants.REG_MAX_LIST_LARGE) {
                        rtInt = contVersionMapper.insertAllDao(insertList);
                        insertList = new ArrayList<>();
                        if (rtInt <= 0) { break; }
                    }
                }
                if (!insertList.isEmpty()) {
                    insertCnt += insertList.size();
                    rtInt = contVersionMapper.insertAllDao(insertList);
                    insertList = new ArrayList<>();
                }
                if (rtInt > 0) {
                    rtInt = insertCnt;
                }
                log.info("insertVerXCont insertList.size():{}, rtInt:{}", insertCnt, rtInt);
            } //for end

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("insertVerXCont Exception:{}", ex.toString());
        }

        return rtInt;
    }

    /**
     * 버전내용 파일에서 versionName 추출
     * ex) 0b4f8fa57769413c90fcd5102e1afb21_C3.xml -> C3
     *   fileName (1 : toco_id, 2 : ver_name, 3 : ver_name에서 C제거)
     */
    public String getVerInfo(int argType, String fileName) {

        String rtStr = "";
        log.info("getVerInfo argType : {}, fileName : {}", argType, fileName);
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        try {
            String cutA	= "_";
            String cutB	= ".";
            int idxVer		= fileName.lastIndexOf(cutA);
            int idxDot		= fileName.lastIndexOf(cutB);

            if (idxVer > -1 && idxVer < idxDot && idxDot < fileName.length()) {
                if (argType == 1) {
                    rtStr = fileName.substring(0, idxVer);
                } else if (argType == 2) {
                    rtStr = fileName.substring(idxVer+1, idxDot);
                } else if (argType == 3) {
                    rtStr = fileName.substring(idxVer+1, idxDot).replaceAll(VAL.VER_PREFIX, "");
                }
            }

            // rtStr = StringUtil.checkNull(rtStr);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("ContRegister.getVerInfo Exception:{}", ex.toString());
        }

        return rtStr;
    }

    /**
     * 계통/TO 목록 데이터를 DB에서 추출하는 DAO 처리하여 String(문자열)로 반환
     */
    public StringBuffer getToTreeStrUpdate (TreeXContDto treeDto) {

        StringBuffer rtSB = new StringBuffer();

        StringBuilder rtSA = new StringBuilder();
        Element toElem;
        Document toDoc;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();

        try {

            //1. DB에서 데이터 추출하는 MyBatis의 DAO 처리 모듈 호출

            //1) DTO 객체에 값 셋팅
            //2) DAO에서 DB Data 추출
            ArrayList<TreeXContDto> treeList = treeMapper.selectListDao(treeDto);

            //3) Contents 추출
            if (treeList != null && !treeList.isEmpty()) {
                for (TreeXContDto rsDto : treeList) {
                    rtSB.append(rsDto.getTreeXcont());
                }
            }

            // 그림/표 목차 재정렬 ( rtSB.length() 시작부터 종료까지)
            if (!rtSB.isEmpty()) {

                TocoInfoDto dto = new TocoInfoDto();
                dto.setToKey(treeDto.getRefToKey());

                ArrayList<TocoInfoDto> tocoDto = tocoInfoMapper.selectTocoDto(dto);
                // MARIA,ORACLE,MSSQL 모두 자바에서 목차 정렬되도록 변경
                tocoDto = this.getSortedTocoDto(tocoDto);

                if ( !rtSB.toString().contains("<"+DTD.SYSTEM+">") ) {
                    rtSB.insert(0, "<"+DTD.SYSTEM+">");
                    rtSB.append("</"+DTD.SYSTEM+">");
                }

                DocumentBuilder builder = factory.newDocumentBuilder();
                toDoc = builder.parse(new ByteArrayInputStream(rtSB.toString().getBytes(StandardCharsets.UTF_8)));

                toElem = toDoc.getDocumentElement();

                NodeList tocoList = (NodeList) xPath.evaluate(XALAN.REG_SYSTEM, toElem, XPathConstants.NODESET);

                Node tocoNode;
                String tocoId;
                String tocoListName;
                String tocoListType;
                String tocoListUuid;
                String tocoName;
                String tocotype;
                String tocoBuff;
                String diffBuff;
                boolean isTocoId = false;

                for (TocoInfoDto rsDto : tocoDto) {

                    for (int i=0; i<tocoList.getLength(); i++) {
                        if (tocoList.getLength() == 0) { break; }

                        tocoNode 		= tocoList.item(i);
                        Element tocoElement = (Element) tocoNode;

                        tocoId	 		= tocoElement.getAttribute(ATTR.SYS_ID);
                        tocoListName 	= tocoElement.getAttribute(ATTR.SYS_TOCO_NAME);
                        tocoListType 	= tocoElement.getAttribute(ATTR.SYS_TOCO_TYPE);
                        tocoListUuid 	= tocoElement.getAttribute(ATTR.SYS_TOCO_ID);
                        tocoName 		= tocoElement.getAttribute(ATTR.SYS_NAME);
                        tocotype 		= tocoElement.getAttribute(ATTR.SYS_TYPE);

                        //문자 치환
                        //TODO: Condeconverter
                        tocoListName = CodeConverter.convertEntityToTag(tocoListName);
                        tocoName = CodeConverter.convertEntityToTag(tocoName);

                        if (tocoId.isEmpty()) { continue; }

                        if( tocoId.equals(rsDto.getTocoId())) {

                            //그림목차 재정렬
                            if( tocoId.equals("GrphToco")) {

                                if(!isTocoId) { // 그림목차, 표 목차 중 그림목차 처음 오게 될 경우 이전 xml 정보 move 처리
                                    diffBuff = rsDto.getTocoId() + "\">";
                                    tocoBuff = rtSB.substring(0, rtSB.lastIndexOf(diffBuff)+10 );
                                    isTocoId = true;
                                }
                                else { // 그림목차, 표 목차 중 그림목차 처음이 아닐 경우 </system> 닫아주고 그림 목차 설정
                                    tocoBuff = "</system> <system id=\""+ tocoId + "\" name=\"" + tocoName + "\" type=\"" + tocotype+ "\">";
                                }

                                rtSA.append(tocoBuff);
                            }
                            // 동영상목차 재정렬 추가
                            //동영상목차 재정렬
                            else if( tocoId.equals("VideoToco")) {

                                if(!isTocoId) { // 그림목차, 동영상목차, 표목차 중 동영상목차 처음 오게 될 경우 이전 xml 정보 move 처리
                                    diffBuff = rsDto.getPTocoId() + "\">";
                                    tocoBuff = rtSB.substring(0, rtSB.lastIndexOf(diffBuff)+11 );
                                    isTocoId = true;
                                }
                                else { // 그림목차, 동영상목차, 표목차 중 동영상목차 처음이 아닐 경우 </system> 닫아주고 동영상 목차 설정
                                    tocoBuff = "</system> <system id=\""+ tocoId + "\" name=\"" + tocoName + "\" type=\"" + tocotype+ "\">";
                                }

                                rtSA.append(tocoBuff);
                            }
                            else if( tocoId.equals("TableToco")) {

                                if(!isTocoId) { // 그림목차, 표 목차 중 표 목차가 처음 오게 될 경우 이전 xml 정보 move 처리
                                    diffBuff = rsDto.getTocoId() + "\">";
                                    tocoBuff = rtSB.substring(0, rtSB.lastIndexOf(diffBuff)+11 );
                                    isTocoId = true;

                                } else { // 그림목차, 표 목차 중 표목차 처음이 아닐 경우 </system> 닫아주고 표 목차 설정
                                    tocoBuff = "</system><system id=\""+ tocoId + "\" name=\"" + tocoName + "\" type=\"" + tocotype+ "\">";
                                }

                                rtSA.append(tocoBuff);
                            }
                            else {

                                if( !tocoListUuid.equals(rsDto.getPTocoId())) continue;

                                if( tocotype.equals("Grph") || tocotype.equals("Table") || tocotype.equals("Video") ){

                                    tocoBuff = "<system id=\""+ tocoId + "\" listname=\"" + tocoListName + "\" listtype=\"" + tocoListType + "\" listuuid=\"" + tocoListUuid + "\" name=\"" + tocoName + "\" type=\"" + tocotype+ "\"/>";
                                    rtSA.append(tocoBuff);
                                }
                            }
                        }
                    }
                }
                log.info("getToTreeStrUpdate before :{}", rtSB);
                //그림목차, 표 목차의 재 정렬이 있을 경우 리턴(rtSB) 값 수정
                if(!rtSA.isEmpty()) {
                    tocoBuff = "</system></eXPISInfo>";
                    rtSA.append(tocoBuff);

                    //그림목차, 표목차 이전 정보 삭제
                    rtSB.delete(0, rtSB.length());

                    //재정렬한 그림목차, 표목차로 MOVE
                    rtSB.append(rtSA);
                }
                log.info("getToTreeStrUpdate after :{}", rtSB);
            }

        }  catch (Exception ex) {
            ex.printStackTrace();
            log.error("TreeCollector.getToTreeStr Exception:{}", ex.toString());
        }

        return rtSB;
    }

    /**
     * DB에서 추출한 그림목차, 표목차 정렬하여 반환(MSSQL)
     */
    public ArrayList<TocoInfoDto> getSortedTocoDto(ArrayList<TocoInfoDto> tocoDto) {

        ArrayList<TocoInfoDto> sortDto = new ArrayList<>();    //정렬하여 새로 담을 DTO리스트
        List<String> sortedContentsName = new ArrayList<>();        //정렬 기준이 되는 목차명 리스트

        String contentsSortName = "";  //정렬 기준 목차명
        String sortTocoId = "";        //목차 ID
        String sortPTocoId = "";	   //그림목차,표목차 구분

        try {
            for (TocoInfoDto srDto : tocoDto) {
                contentsSortName = srDto.getContentsSortName();
                sortTocoId = srDto.getTocoId();
                sortPTocoId = srDto.getPTocoId();

                // 정렬 기준 목차명 공백 제거
                if (contentsSortName.startsWith(" ")) contentsSortName = contentsSortName.substring(1);

                //목차명 앞의 숫자 단위 맞추기
                if (contentsSortName.indexOf('.') == -1) {

                } else if (contentsSortName.matches("그림(.*)")) {
                    if (contentsSortName.indexOf('-') == 4) {
                        contentsSortName =  contentsSortName.substring(0, 2) + "0" + contentsSortName.substring(2);
                    } else if (contentsSortName.indexOf('-') == 3) {
                        contentsSortName =  contentsSortName.substring(0, 2) + "00" + contentsSortName.substring(2);
                    } else if (contentsSortName.indexOf('.') == 5) {
                        contentsSortName =  contentsSortName.substring(0, 2) + "0" + contentsSortName.substring(2, 5) + ". " + contentsSortName.substring(6);
                    } else if (contentsSortName.indexOf('.') == 4) {
                        contentsSortName =  contentsSortName.substring(0, 2) + "00" + contentsSortName.substring(2, 4) + ". " + contentsSortName.substring(5);
                    } else if (contentsSortName.indexOf('.') == 3) {
                        contentsSortName =  contentsSortName.substring(0, 2) + "000" + contentsSortName.substring(2, 3) + ". " + contentsSortName.substring(4);
                    }
                } else if (contentsSortName.matches("표\\(그림(.*)")) {
                    if (contentsSortName.indexOf('-') == 5) {
                        contentsSortName =  contentsSortName.substring(0, 4) + "00" + contentsSortName.substring(4);
                    } else if (contentsSortName.indexOf('-') == 6) {
                        contentsSortName =  contentsSortName.substring(0, 4) + "0" + contentsSortName.substring(4);
                    }
                } else if (contentsSortName.matches("표(.*)")) {
                    if (contentsSortName.indexOf('-') == 3) {
                        contentsSortName =  contentsSortName.substring(0, 1) + "00" + contentsSortName.substring(1);
                    } else if (contentsSortName.indexOf('-') == 2) {
                        contentsSortName =  contentsSortName.substring(0, 1) + "000" + contentsSortName.substring(1);
                    } else if (contentsSortName.indexOf('.') == 4) {
                        contentsSortName =  contentsSortName.substring(0, 1) + "00" + contentsSortName.substring(1, 4) + ". " + contentsSortName.substring(5);
                    } else if (contentsSortName.indexOf('.') == 3) {
                        contentsSortName =  contentsSortName.substring(0, 1) + "000" + contentsSortName.substring(1, 3) + ". " + contentsSortName.substring(4);
                    } else if (contentsSortName.indexOf('.') == 2) {
                        contentsSortName =  contentsSortName.substring(0, 1) + "0000" + contentsSortName.substring(1, 2) + ". " + contentsSortName.substring(3);
                    }
                } else {
                    contentsSortName =  contentsSortName.substring(0, (contentsSortName.lastIndexOf('.') == -1) ? 0 : contentsSortName.lastIndexOf('.')+1) + " " + contentsSortName.substring((contentsSortName.lastIndexOf('.') == -1) ? 0 : contentsSortName.lastIndexOf('.')+1);
                }

                //목차명 괄호 안의 숫자 단위 맞추기
                if (contentsSortName.substring((contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+1, (contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+2).matches("[0-9]")) {
                    if ((contentsSortName.lastIndexOf('/') - contentsSortName.lastIndexOf('(')) == 2) {
                        contentsSortName = contentsSortName.substring(0, (contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+1) + "00" + contentsSortName.substring((contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+1);
                    } else if ((contentsSortName.lastIndexOf('/') - contentsSortName.lastIndexOf('(')) == 3) {
                        contentsSortName = contentsSortName.substring(0, (contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+1) + "0" + contentsSortName.substring((contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+1);
                    }
                } else if (contentsSortName.substring((contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+1, (contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+2).equals("표")) {
                    if ((contentsSortName.lastIndexOf(')') - contentsSortName.lastIndexOf('(')) == 3) {
                        contentsSortName = contentsSortName.substring(0, (contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+2) + "00" + contentsSortName.substring((contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+2);
                    } else if ((contentsSortName.lastIndexOf(')') - contentsSortName.lastIndexOf('(')) == 4) {
                        contentsSortName = contentsSortName.substring(0, (contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+2) + "0" + contentsSortName.substring((contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+2);
                    }
                } else if ((contentsSortName.lastIndexOf(')') - contentsSortName.lastIndexOf('(')) >= 7) {
                    if (contentsSortName.substring((contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+1, (contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+6).equals("Sheet")) {
                        if ((contentsSortName.lastIndexOf(')') - contentsSortName.lastIndexOf('(')) == 7) {
                            contentsSortName = contentsSortName.substring(0, (contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+6) + "00" + contentsSortName.substring((contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+6);
                        } else if ((contentsSortName.lastIndexOf(')') - contentsSortName.lastIndexOf('(')) == 8) {
                            contentsSortName = contentsSortName.substring(0, (contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+6) + "0" + contentsSortName.substring((contentsSortName.lastIndexOf('(') == -1) ? 0 : contentsSortName.lastIndexOf('(')+6);
                        }
                    }
                }

                //목차명 앞 '-' 뒤 숫자 단위 맞추기
                if (contentsSortName.substring((contentsSortName.indexOf('.') == -1) ? 0 : contentsSortName.indexOf('.')+1, (contentsSortName.indexOf('.') == -1) ? 0 : contentsSortName.indexOf('.')+4).matches("^\\s[0-9]\\.$")) {
                    contentsSortName = contentsSortName.substring(0, (contentsSortName.indexOf('.') == -1) ? 0 : contentsSortName.indexOf('.')+2) + "0" + contentsSortName.substring((contentsSortName.indexOf('.') == -1) ? 0 : contentsSortName.indexOf('.')+2);
                } else if (contentsSortName.substring((contentsSortName.indexOf('-') == -1) ? 0 : contentsSortName.indexOf('-')+1, (contentsSortName.indexOf('-') == -1) ? 0 : contentsSortName.indexOf('-')+2).matches("[0-9]")) {
                    if ((contentsSortName.indexOf('.') - contentsSortName.indexOf('-')) == 2) {
                        contentsSortName = contentsSortName.replace("-", "-00");
                    } else if ((contentsSortName.indexOf('.') - contentsSortName.indexOf('-')) == 3) {
                        contentsSortName = contentsSortName.replace("-", "-0");
                    }
                }

                // 동영상목차 정렬 추가, 제목에 '그림','표' 없는 경우 정렬 추가
				/*그림목차=01, 그림목차리스트(제목에 '그림'없음=02, 순번표기형식 '1-1.'=03, '1.'=04), 동영상목차=05, 동영상목차리스트=06,
				  표목차=07, 표목차리스트(제목에 '표'없음=08, 순번표기형식 '1-1.'=09, '1.'=10)*/
                if (sortTocoId.equals("GrphToco")) {contentsSortName = "01";}
                else if (sortPTocoId.equals("GrphToco")) {
                    if (!contentsSortName.contains("그림")) {
                        contentsSortName = "02" + contentsSortName;
                    } else {
                        if (contentsSortName.substring(0, (contentsSortName.indexOf('.') == -1) ? 0 : contentsSortName.indexOf('.')+1).indexOf('-') > -1) {
                            contentsSortName = "03" + contentsSortName;
                        } else {
                            contentsSortName = "04" + contentsSortName;
                        }
                    }
                }
                else if (sortTocoId.equals("VideoToco")) {contentsSortName = "05";}
                else if (sortPTocoId.equals("VideoToco")) {
                    contentsSortName = "06" + contentsSortName;
                }
                else if (sortTocoId.equals("TableToco")) {contentsSortName = "07";}
                else if (sortPTocoId.equals("TableToco")) {
                    if (!contentsSortName.contains("표")) {
                        contentsSortName = "08" + contentsSortName;
                    } else {
                        if (contentsSortName.substring(0, (contentsSortName.indexOf('.') == -1) ? 0 : contentsSortName.indexOf('.')+1).indexOf('-') > -1) {
                            contentsSortName = "09" + contentsSortName;
                        } else {
                            contentsSortName = "10" + contentsSortName;
                        }
                    }
                }

                //logger.info("정렬 전 결과값 : " + contentsSortName);
                srDto.setContentsSortName(contentsSortName);
                srDto.setPTocoId(srDto.getUuid());
                sortedContentsName.add(contentsSortName);
            }

            sortedContentsName.sort(null);        //오름차순 정렬
            //logger.info("정렬 후 결과값 : " + sortedContentsName.toString());

            //정렬된 목차명 기준으로 새 DTO 생성
            // sortedContentsName리스트에 중복값이 존재할 경우 제외하도록 변경
            for (int i =0; i<sortedContentsName.size(); i++) {
                if (sortedContentsName.size() == 0) { break; }

                for (TocoInfoDto srrDto : tocoDto) {
                    if (srrDto.getContentsSortName().equals(sortedContentsName.get(i))) {
                        sortDto.add(srrDto);
                        srrDto.setContentsSortName("");
                        break;
                    } else {
                        continue;
                    }
                }
            }

        } catch(Exception ex) {
            ex.printStackTrace();
            log.error("TreeCollector.getSortedTocoDto Exception:{}", ex.toString());
        }

        return sortDto;
    }

    public List<TocoInfoDto> selectUpdateToList(TocoInfoDto dto) {
        return tocoInfoMapper.selectUpdateToList(dto);
    }

    public void updateVehicleType(TocoInfoDto toInfoDto) {
        tocoInfoMapper.updateVehicleType(toInfoDto);
    }

    //1) System Tree XCont(XML data) Delete (tm_tree_xcont delete)
    //2) System Tree Info Delete (tm_sys_info delete)
    public int deleteSystree(RegisterDto regDto) {

        int rtInt	= -1;
        int delInt	= -1;		//삭제건수, 0이어도 오류 아님

        try {
            regDto.setToKey("");

            //1) System Tree XCont(XML data) Delete (tm_tree_xcont delete)
            //2) System Tree Info Delete (tm_sys_info delete)

            TreeXContDto treeDto = new TreeXContDto();
            if (regDto.getIsSysDel().equals(eXPIS3Constants.TREE_KIND_SYSTEM) || regDto.getIsSysDel().equals(eXPIS3Constants.TREE_KIND_TO)) {
                treeDto.setTreeKind(regDto.getIsSysDel());
                if (regDto.getIsSysDel().equals(eXPIS3Constants.TREE_KIND_TO) && !regDto.getToKey().isEmpty()) {
                    treeDto.setRefToKey(regDto.getToKey());
                }
                delInt = treeMapper.deleteDao(treeDto);
            } else {
                delInt = treeMapper.deleteAllDao();
            }

            if (delInt >= 0) {
                SystemInfoDto sysDto = new SystemInfoDto();
                delInt = sysInfoMapper.deleteDao(sysDto);
            }

//            log.info("deleteSystree rtInt .1={}", rtInt);
            //if (delInt >= 0) {
                rtInt = 1;
            //}
//            log.info("deleteSystree rtInt .2={}", rtInt);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("Exception={}",ex.toString());
        }

//        log.info("deleteSystree rtInt={}", rtInt);
        return rtInt;
    }

    public SysTreeXmlWrapper setSystemSaveYN(SysTreeXmlWrapper sysTreeXmlWrapper) {

        ArrayList<String> dtoList = treeMapper.selectInsertTmList();

        systemListLoop(sysTreeXmlWrapper.getSystemList(), dtoList, 0);

        return sysTreeXmlWrapper;
    }

    private void systemListLoop(List<SystemXmlWrapper> systemXmlWrapperList, ArrayList<String> dtoList, int depth) {
        if ( depth > 99 ) return;

        for (SystemXmlWrapper sys : systemXmlWrapperList) {
            boolean exists = dtoList.stream()
                    .anyMatch(dto -> dto.equals(sys.getItemid()));
            if (exists) {
                log.info("match {}", sys.getItemid());
                sys.setSaveyn(eXPIS3Constants.TOSAVE_YN_YES);
            }
            if (sys.getSystemList() != null && !sys.getSystemList().isEmpty()) {
                systemListLoop(sys.getSystemList(), dtoList, depth++);
            }
        }
    }

    private List<SystemXmlWrapper> flatterSysTreeXml(List<SystemXmlWrapper> systemList, String pSysId) {
        List<SystemXmlWrapper> flatList = new ArrayList<>();
        for (SystemXmlWrapper sys : systemList) {
            sys.setPsysid(pSysId);
            flatList.add(sys);

            //log.info("sys={}", sys);

            if (sys.getSystemList() != null && !sys.getSystemList().isEmpty()) {
                flatList.addAll( flatterSysTreeXml(sys.getSystemList(), sys.getId()) );
            }
        }
        return flatList;
    }

    private List<SystemXmlWrapper> flatterSysTreeXml(List<SystemXmlWrapper> systemList, String pSysId, String targetType) {
        List<SystemXmlWrapper> flatList = new ArrayList<>();
        for (SystemXmlWrapper sys : systemList) {
            if (sys.getType().equalsIgnoreCase(targetType)) {
                sys.setPsysid(pSysId);
                flatList.add(sys);
                //log.info("targetType={}, sys={}", targetType, sys);
            }

            if (sys.getSystemList() != null && !sys.getSystemList().isEmpty()) {
                flatList.addAll( flatterSysTreeXml(sys.getSystemList(), sys.getId(), targetType) );
            }
        }
        return flatList;
    }
}
