package com.expis.manage.controller;

import com.expis.common.CommonConstants;
import com.expis.common.config.datasource.DataSourceContextHolder;
import com.expis.common.eXPIS3Constants;
import com.expis.domparser.DTD;
import com.expis.domparser.VAL;
import com.expis.domparser.XALAN;
import com.expis.ietm.dto.TocoInfoDto;
import com.expis.login.interceptor.Login;
import com.expis.manage.dto.*;
import com.expis.manage.facade.ToFasade;
import com.expis.manage.service.DBInsertErrorService;
import com.expis.manage.service.RegisterService;
import com.expis.manage.service.SystemInfoService;
import com.expis.user.dto.UserDto;
import com.expis.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 2024.12.09 - 관리자 메뉴 - TO관리 서비스 - jingi.kim
 * 변경 이력:
 *
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/manage")
class RegisterManagerController {

    private final SystemInfoService systemInfoService;
    private final RegisterService registerService;
    private final DBInsertErrorService dbInsertErrorService;
    private final ToFasade toFasade;
    private final MessageSource messageSource;

    @Value("${app.expis.ietmType}")
    private String ietmType;
    @Value("${app.expis.databaseType}")
    private String databaseType;
    @Value("${app.expis.dfIetmData}")
    private String ietmDataPath;


    //메뉴 : T.O.관리 - SysTree파일등록
    @GetMapping("/systreeWrite.do")
    public String systree(@PathVariable String bizCode,
                          @Login UserDto userDto,
                          Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaRegister");
        model.addAttribute("secondId", "systree");
        model.addAttribute("userDto", userDto);

        log.info("bizCode={}",bizCode);
        DataSourceContextHolder.setDataSource(bizCode);
        int sysInfoCount = systemInfoService.getSysInfoCount();
        model.addAttribute("sysCount", sysInfoCount);
        log.info("sysInfoCount={}",sysInfoCount);

        return "manage/manageHome";
    }

    //메뉴 : T.O.관리 - 저작TO등록
    @GetMapping("/singletoWrite.do")
    public String register(@PathVariable String bizCode,
                           @Login UserDto userDto,
                           Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaRegister");
        model.addAttribute("secondId", "register");
        model.addAttribute("userDto", userDto);
        model.addAttribute("ietmType", ietmType);
        log.info("ietmType={}",ietmType);

        return "manage/manageHome";
    }

    //SysTree 파일 업로드
    @PostMapping("/systreeInsert.do")
    public ResponseEntity<String> sysTreeInsert(@PathVariable String bizCode,
                                                @Login UserDto userDto,
                                                @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("file is empty");
        }

        try {
            DataSourceContextHolder.setDataSource(bizCode);

            int rtInt = 0;

            InputStream inputStream = file.getInputStream();

            //등록시 사용하는 컬렉션인 RegisterDto에 값 셋팅
            RegisterDto regDto = new RegisterDto();
            regDto.setUserId(userDto.getUserId());
            regDto.setDbType(databaseType);
            regDto.setIsSysDel(eXPIS3Constants.TREE_KIND_SYSTEM);

            byte[] streamData = inputStream.readAllBytes();

            // System Tree XCont(XML data)/Info Delete (tm_tree_xcont/tm_sys_info delete)
            if ( !"".equalsIgnoreCase(Arrays.toString(streamData)) ) {
                rtInt = registerService.deleteSystree(regDto);
            }

            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            SysTreeXmlWrapper sysTreeXmlWrapper = xmlMapper.readValue(new ByteArrayInputStream(streamData), SysTreeXmlWrapper.class);

            // 이미 등록된 교범에 saveYN 체크
            sysTreeXmlWrapper = registerService.setSystemSaveYN(sysTreeXmlWrapper);

            String xmlString = xmlMapper.writeValueAsString(sysTreeXmlWrapper);
            xmlString = xmlString.replaceAll("SysTreeXmlWrapper", "techinfo");

            // TreeXCont Mapper 호출하여 테이블에 등록 (tm_tree_xcont insert)
            if (rtInt > 0) {
                rtInt = registerService.insertTreeXCont(regDto, new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
            }

            // SystemInfo Mapper 호출하여 테이블에 등록 (tm_sys_info insert)
            if (rtInt > 0) {
                rtInt = registerService.insertSystemInfo(regDto, sysTreeXmlWrapper);
            }

            // ToInfo Mapper 호출하여 테이블에 등록  (tm_to_info insert)
            if (rtInt > 0) {
                rtInt = registerService.insertToInfo(regDto, sysTreeXmlWrapper);
            }

            if (rtInt > 0) {
                return ResponseEntity.ok("File uploaded successfully.");
            } else {
                return ResponseEntity.status(500).body("Failed to upload file.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload file:"+e.getMessage());
        }
    }

    // 전체 계통 및 프로젝트(TO) 삭제
    @RequestMapping("/systreeDelete.do")
    public ResponseEntity<?> sysTreeDelete(@PathVariable String bizCode,
                                           @Login UserDto userDto) throws Exception {

        Map<String, String> responseMap = new HashMap<>();
        int rtInt = -1;

        try {
            DataSourceContextHolder.setDataSource(bizCode);

            //컬렉션에 값 셋팅
            RegisterDto rcDto = new RegisterDto();
            rcDto.setUserId(userDto.getUserId());

            //전체 프로젝트 삭제 Core 호출
            rtInt = registerService.deleteSystree(rcDto);

            responseMap.put("returnResult", String.valueOf(rtInt));

            log.info("==> systreeDelete() result={}", rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(" systreeDelete Exception:{}", ex.toString());
        }

        responseMap.put("status", "success");
        return ResponseEntity.ok(responseMap);
    }

    /*
      프로젝트 전체 TO 등록
     */
//    @RequestMapping("/projectWrite.do")
//    public ModelAndView projectWrite(HttpServletRequest request
//            , @RequestParam(value = "active", defaultValue="register") String active) throws Exception {
//
//        ModelAndView mav = null;
//
//        try {
//            VariableAspect.setting(request);
//
//            mav = new ModelAndView("layout.admin.register.project");
//            mav.addObject("active", active);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            logger.info("RegisterController.projectWrite Exception:" + ex.toString());
//        }
//
//        return mav;
//    }

    // 프로젝트 전체 TO 등록 리스트
    @RequestMapping("/projectList.do")
    public ResponseEntity<?> projectList(@PathVariable String bizCode,
                                         @Login UserDto userDto,
                                         @RequestParam(value = "to_dir_path", defaultValue = "") String toDirPath,
                                         HttpServletRequest request) throws Exception {

        String fileName = "";
        String filePath = "";

        Map<String, String> responseMap = new HashMap<>();
        log.info("to_dir_path : {}", toDirPath);

        if(toDirPath.isEmpty()) {
            responseMap.put("fileDirError", "error");
            responseMap.put("status", "fail");
            responseMap.put("message", "File not found.");
            return ResponseEntity.ok(responseMap);
        }

        File path = new File(toDirPath);
        String[] list = path.list();
        if ( list == null || list.length == 0 ) {
            responseMap.put("status", "fail");
            responseMap.put("message", "File not found.");
            return ResponseEntity.ok(responseMap);
        }

        DataSourceContextHolder.setDataSource(bizCode);

        RegisterDto rcDto = new RegisterDto();

        dbInsertErrorService.deleteDBInserError(rcDto);

        boolean isSuccess = false;
        for(int i = 0; i < list.length; i++) {
            try {
                fileName = list[i];
                filePath = toDirPath + "\\" + fileName;
                log.info("ToKey : {} , toDirPath : {}", fileName , filePath);
                if(!new File(filePath).isDirectory()) {
                    log.info("This file is not directory : {}", filePath);
                    continue;
                }
                log.info("Directory pass : {}", filePath);

                //컬렉션에 값 셋팅
                rcDto = new RegisterDto();
                log.info("singleToInsert : {} \\ {}", toDirPath, fileName);
                File toDir = new File(toDirPath + "\\" + fileName);
                if (!toDir.exists() ) {
                    log.info("toDir.exists() : {}", toDir.exists());
                    continue;
                }

                rcDto.setToKey(fileName);
                rcDto.setToDirPath(toDirPath + "\\" + fileName);
                rcDto.setUserId(userDto.getUserId());
                rcDto.setDbType(databaseType);

                log.info("rcDto != null : {}, StringUtil.checkNull(rcDto.getToDirPath()).equals(\"\") : {}", rcDto != null, StringUtil.checkNull(rcDto.getToDirPath()).equals(""));
                //TO 등록 Core 호출
                if (!rcDto.getToDirPath().isEmpty()) {
                    isSuccess = toFasade.insertSingleTo(rcDto);
                    log.info("insertSingleTo result={}", isSuccess);
                }

                // DBCP 방식으로 교체후 1분에서 5초로 수정
                //Time_Wait 이슈로 인해 10초정도 텀을 주고 다음 요청 진행함 : 해당 이슈 관련 DB Property 설정 법 찾아서 수정 필요함 현재 수정시에 시간 부족으로 미설정
                for(int j=0;j<5;j++) {
                    Thread.sleep(1000);
                    log.info("Insert Wait[{}] : {}", j, rcDto.getToKey());
                }

            }catch (Exception e) {
                log.error("Insert Project Error : {}", e.getMessage());
                try {Thread.sleep(1000*60);}catch (Exception ex) {log.error("Exception Thread Sleep error : "+ex.getMessage());}
            }
        }

        // 프로젝트 정상 등록 여부 확인
        try {
            rcDto = new RegisterDto();
            List<DBInsertErrorDto> errorList = dbInsertErrorService.selectDBInsertError(rcDto);
            log.info("errorList : {}", errorList);
            if(errorList != null && !errorList.isEmpty()) {
                responseMap.put("errorList", errorList.toString());
                responseMap.put("errorListCount", String.valueOf(errorList.size()));
            }
        }catch (Exception e) {
            log.error("프로젝트 정상 등록 여부 확인 Error : {}", e.getMessage(), e);
        }

        responseMap.put("result", (isSuccess ? "success" : "fail") );
        responseMap.put("status", "success");

        return ResponseEntity.ok(responseMap);
    }

    // 전체 프로젝트(TO) 삭제
    @RequestMapping("/projectDelete.do")
    public ResponseEntity<?> projectDelete(@PathVariable String bizCode,
                                           @Login UserDto userDto,
                                           @RequestParam(value = "active", defaultValue="register") String active,
                                           @RequestParam(value = "file_processing", defaultValue="") String fileProcessing,
                                           HttpServletRequest request) throws Exception {

        int rtInt = -1;
        Map<String, String> responseMap = new HashMap<>();

        try {
            DataSourceContextHolder.setDataSource(bizCode);

            String webdataPath = ietmDataPath;

            //컬렉션에 값 셋팅
            RegisterDto rcDto = new RegisterDto();
            rcDto.setUserId(userDto.getUserId());

            //전체 프로젝트 삭제 Core 호출
            rtInt = registerService.deleteProject(rcDto);

            // 파일 처리(저장된 교범이미지 삭제) 로직 추가
            if(rtInt >= 0 && fileProcessing.equals("chk")) {
                try {
                    if(webdataPath.isEmpty()) {
                        log.info("webdataPath is empty");
                        responseMap.put("status", "success");
                        responseMap.put("message", "web Data Path is Empty.");
                        return ResponseEntity.ok(responseMap);
                    }

                    File iconFolder = new File(webdataPath, "icon");
                    File imageFolder = new File(webdataPath, "image");

                    if ( !(iconFolder.exists() && iconFolder.isDirectory()) && !(imageFolder.exists() && imageFolder.isDirectory()) ) {
                        responseMap.put("status", "success");
                        responseMap.put("message", "Image Resource Path is Empty.");
                        return ResponseEntity.ok(responseMap);
                    }

                    boolean success = true;
                    for (File file : Objects.requireNonNull(iconFolder.listFiles())) {
                        if (file.isFile()) {
                            success &= file.delete();
                        }
                    }
                    for (File file : Objects.requireNonNull(imageFolder.listFiles())) {
                        if (file.isFile()) {
                            success &= file.delete();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("projectDelete file processing Exception: {}", e.toString());
                    rtInt = -2;
                }
            }

            responseMap.put("returnResult", String.valueOf(rtInt));
            log.info(" projectDelete() result={}", rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(" projectDelete Exception:{}", ex.toString());
        }

        responseMap.put("status", "success");
        return ResponseEntity.ok(responseMap);
    }

    // 패키지  insert
    @RequestMapping("/packageInsert.do")
    public ResponseEntity<?> packageList(@PathVariable String bizCode,
                                         @Login UserDto userDto,
                                         @RequestParam(value = "to_dir_path", defaultValue = "") String toDirPath,
                                         HttpServletRequest request) throws Exception {

        Map<String, String> responseMap = new HashMap<>();
        log.info("to_dir_path : {}", toDirPath);
        String fileName = "";
        String filePath = "";

        if(!toDirPath.endsWith(".zip")) {
            responseMap.put("status", "fail");
            responseMap.put("message", "Failed to upload file");
            return ResponseEntity.ok(responseMap);
        }

        DataSourceContextHolder.setDataSource(bizCode);

        RegisterDto rcDto = new RegisterDto();
        dbInsertErrorService.deleteDBInserError(rcDto);

        File zipfile = new File(toDirPath);
        File targetDir = new File(toDirPath.split("[.]")[0]+"\\");

        if (targetDir.exists() && targetDir.length()>0) {
            boolean dirDeleted = FileSystemUtils.deleteRecursively(targetDir);
        }

        this.unZip(zipfile.getAbsolutePath(), targetDir.getAbsolutePath());
        toDirPath = toDirPath.split("[.]")[0]+"\\";
        //이하 [projectList] 동일 로직
        File path = new File(toDirPath);
        String[] list = path.list();

        boolean isSuccess = false;
        for(int i = 0; i < list.length; i++) {
            try {
                fileName = list[i];
                filePath = toDirPath + "\\" + fileName;
                log.info("packageList - ToKey : {} , toDirPath : {}", fileName, filePath);
                if(fileName.equals("packagelist") || !new File(filePath).isDirectory()) {
                    log.info("This file is not directory : {}", filePath);
                    continue;
                }
                log.info("Directory pass : {}", filePath);

                //컬렉션에 값 셋팅
                rcDto = new RegisterDto();

                log.info("singletoInsert : {} \\ {}", toDirPath, fileName);
                File toDir = new File(toDirPath + "\\" + fileName);
                if (!toDir.exists() ) {
                    log.info("toDir.exists() : {}", toDir.exists());
                    continue;
                }
                rcDto.setToKey(fileName);
                rcDto.setToDirPath(toDirPath + "\\" + fileName);
                rcDto.setUserId(userDto.getUserId());
                rcDto.setDbType(databaseType);

                log.info("rcDto : {}", rcDto);
                log.info("rcDto.getToDirPath().isEmpty : {}", rcDto.getToDirPath().isEmpty());
                //TO 등록
                if (!rcDto.getToDirPath().isEmpty()) {
                    isSuccess = toFasade.insertSingleTo(rcDto);
                    log.info("insertSingleTo result={}", isSuccess);
                }

            }catch (Exception e) {
                log.error("Insert Project Error : {}", e.getMessage());
                Thread.sleep(1000*60);
            }
        }
        responseMap.put("returnResult", (isSuccess ? "success" : "fail") );

        responseMap.put("status", "success");
        return ResponseEntity.ok(responseMap);
    }

    // zip 압축 해제
    private void unZip(String zipFilePath, String targetDirectory) throws IOException {
        Path targetDirPath = Paths.get(targetDirectory);
        if (!Files.exists(targetDirPath)) {
            Files.createDirectories(targetDirPath);
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry;
            while ( (zipEntry = zipInputStream.getNextEntry()) != null ) {
                Path entryPath = targetDirPath.resolve(zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(Files.newOutputStream(entryPath))) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ( (len = zipInputStream.read(buffer)) > 0 ) {
                            bufferedOutputStream.write(buffer, 0, len);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
        }
    }

    // 단일 TO 등록
    @PostMapping("/singleToInsert.do")
    public ResponseEntity<?> singleToInsert(@PathVariable String bizCode,
                                            @Login UserDto userDto,
                                            @RequestParam("to_key") String toKey,
                                            @RequestParam("to_dir_path") String toDirPath) {

        Map<String, String> responseMap = new HashMap<>();
        boolean isSuccess = false;

        try {
            DataSourceContextHolder.setDataSource(bizCode);

            String userId	= userDto.getUserId();
            if (userId.isEmpty() || toDirPath.isEmpty()) {
                responseMap.put("status", "fail");
                responseMap.put("message", "Failed to upload file");
                return ResponseEntity.ok(responseMap);
            }

            log.info("toDirPath={}, URLDecode={}", toDirPath, URLDecoder.decode(toDirPath, StandardCharsets.UTF_8));
            String decodeDir = URLDecoder.decode(toDirPath, StandardCharsets.UTF_8);
            Path path = Path.of(decodeDir);
            if ( !Files.exists(path)) {
                responseMap.put("status", "fail");
                responseMap.put("message", "File not found.");
                return ResponseEntity.ok(responseMap);
            }

            //컬렉션에 값 셋팅
            RegisterDto rcDto = new RegisterDto();
            rcDto.setToKey(toKey);
            rcDto.setToDirPath(decodeDir);
            rcDto.setUserId(userId);
            rcDto.setDbType(databaseType);

            log.info("rcDto : {}", rcDto);

            // 등록여부 초기화
            dbInsertErrorService.deleteDBInserError(rcDto);

            // 교범 등록
            isSuccess = toFasade.insertSingleTo(rcDto);
            log.info("Insert result={}", isSuccess);


            // 프로젝트 정상 등록 여부 확인
            List<DBInsertErrorDto> errorList = dbInsertErrorService.selectDBInsertError(rcDto);
            log.info("errorList ={}", errorList);
            if(errorList != null && !errorList.isEmpty()) {
                responseMap.put("errorList", errorList.toString());
                responseMap.put("errorListCount", String.valueOf(errorList.size()));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("singletoInsert Exception:{}", ex.toString());
        }

        responseMap.put("result", (isSuccess ? "success" : "fail") );
        responseMap.put("status", "success");
        return ResponseEntity.ok(responseMap);
    }

    /*
      단일 TO 삭제
     */
    @RequestMapping("/singletoDelete.do")
    public ResponseEntity<?> singletoDelete(@PathVariable String bizCode,
                                            @Login UserDto userDto,
                                            @RequestParam(value = "active", defaultValue="register") String active,
                                            @RequestParam(value = "to_key", defaultValue = "") String toKey,
                                            HttpServletRequest request) throws Exception {

        Map<String, String> responseMap = new HashMap<>();
        int rtInt = -1;

        try {
            //세션 정보
            HttpSession session = request.getSession();
            String userId	= StringUtil.checkNull((String) session.getAttribute("SS_USER_ID"));

            //컬렉션에 값 셋팅
            RegisterDto rcDto = new RegisterDto();
            rcDto.setToKey(toKey);
            rcDto.setUserId(userId);


            //TO 삭제 Core 호출
            if (!toKey.isEmpty()) {
                rtInt = registerService.deleteSingleTo(rcDto);
            }

            responseMap.put("returnResult", String.valueOf(rtInt));

            log.info(" singletoDelete() result={}", rtInt);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info(" singletoDelete Exception:{}", ex.toString());
        }

        responseMap.put("status", "success");
        return ResponseEntity.ok(responseMap);
    }



    /*
      L-SAM Project Cover Update
     */
//    @RequestMapping("/projectCoverInsert.do")
//    public ModelAndView projectCoverInsert(HttpServletRequest request
//            ,@RequestParam(value = "to_dir_path", defaultValue = "") String toDirPath) throws Exception {
//        HttpSession session = request.getSession();
//        String userId = StringUtil.checkNull((String) session.getAttribute("SS_USER_ID"));
//
//        Map<String, Object> model = new HashMap<String, Object>();
//        logger.info("to_dir_path : " + toDirPath);
//        String fileName = "";
//        String filePath = "";
//        int rtInt = -1;
//
//        if(toDirPath.equals("")) {
//            model.put("fileDirError", "error");
//        } else {
//            File path = new File(toDirPath);
//            String[] list = path.list();
//            RegisterClientDto rcDto = new RegisterClientDto();
//            for (int i = 0; i < list.length; i++) {
//                try {
//                    fileName = list[i];
//                    filePath = toDirPath + "\\" + fileName;
//                    logger.info("ToKey : " + fileName);
//                    logger.info("toDirPath : " + filePath);
//                    if (new File(filePath).isDirectory()) {
//                        logger.info("This is directory : " + filePath);
//                        continue;
//                    }
//                    logger.info("Directory pass : " + filePath);
//                    if("coverFile.xlsx".equalsIgnoreCase(fileName)) {
//                        logger.info("This is Cover Data File : " + filePath);
//                        File file = new File(filePath);
//                        XSSFWorkbook workbook = null;
//                        try {
//                            //2021 10 29
//                            //Park js
//                            // 엑셀 파일을 읽기 위해 엑셀 파일 갖고 오기 불필요 소스 제거 및 로직 수정
//                            workbook = new XSSFWorkbook(file);
//                            XSSFSheet sheet = workbook.getSheetAt(0); // 첫번째 Sheet를 갖고 오기 위해 0으로 세팅
//                            int rows = sheet.getPhysicalNumberOfRows();	// row 수 갖고 오기
//                            if(rows >= 2) {
//                                FileMaster fileMethod = new FileMaster();
//                                int fileSeq = 0;
//                                // 3row부터 실데이터이므로 3row부터 읽어준다
//                                logger.info("rows : "+rows);
//                                for(int rowIndex=2; rowIndex<=rows; rowIndex++) {
//                                    //dto.setCreateUserId("admin");
//                                    XSSFRow rowData = sheet.getRow(rowIndex); // row 읽기
//                                    logger.info("rowData : "+rowData.getCell(1)+", "+rowData.getCell(2)+", "+rowData.getCell(3)+", "+rowData.getCell(4)+", "+rowData.getCell(5)+", "+rowData.getCell(6)+", "+rowData.getCell(7));
//                                    CoverManageDTO coverManageDto = new CoverManageDTO();
//                                    FileMasterDTO fileDto = new FileMasterDTO();
//                                    File imageFile = new File(toDirPath + "\\" + rowData.getCell(6));
//                                    if(imageFile.exists() && imageFile.isFile()) {
//                                        fileDto = fileMethod.fileUpload(fileDto, imageFile, mainFilePath, "coverImg", IConstants.FILE_UPLOAD_UUID);
//                                        fileSeq = fileMasterService.insertFile(fileDto);
//                                        coverManageDto.setCoverImg(fileSeq);
//                                    }
//                                    //기존에 등록된 정보가 없을경우 신규 생성후 업데이트 로직상 불필요 호출이나 자주 호출죄지 않으며 기존 로직 확용 위해 해달 프로세스 사용
//                                    CoverManageDTO coverDto = systemOptionService.getCoverCont(rowData.getCell(7)+"");
//                                    if(coverDto == null) {
//                                        CoverManageDTO cover = new CoverManageDTO();
//                                        cover.setToKey(rowData.getCell(7)+"");
//                                        cover.setCreateUserId(userId);
//                                        systemOptionService.insertCoverCont(cover);
//                                    }
//
//                                    //logger.info("coverDto : "+coverDto.getCoverSubTitle()+", "+coverDto.getCoverDistCont()+", "+coverDto.getCoverTitle()+", "+coverDto.getCoverWarnningCont()+", "+coverDto.getCoverCont()+", "+coverDto.getCoverImg());
//                                    //coverDto : 부제2, 발간물   등록번호, HD-683K, 부제1, 개정일, 2
//                                    //rowData : 발간물   등록번호, 개정일, 부제1, HD-683K, 부제2, ICON-0001.png, null
//                                    //2022 11 22 jysi EDIT : SubTitle(부제2)에 부제1이 들어가서 수정
//                                    coverManageDto.setToKey(rowData.getCell(7)+"");
//                                    coverManageDto.setCoverTitle(rowData.getCell(4)+"");
//                                    coverManageDto.setCoverSubTitle(rowData.getCell(5)+"");
//                                    coverManageDto.setCoverDistCont(rowData.getCell(1)+"");
//                                    coverManageDto.setCoverWarnningCont(rowData.getCell(3)+"");
//                                    coverManageDto.setCoverCont(rowData.getCell(2)+"");
//                                    coverManageDto.setModifyUserId(userId);
//                                    systemOptionService.coverUpdate(coverManageDto);
//                                }
//                            }
//                            rtInt++;
//                        }catch (Exception e) {
//                            rtInt = -1;
//                        }finally {
//                            workbook.close();
//                        }
//                    }else {
//                        logger.info("This is Not Cover Data File : " + filePath);
//                    }
//
//                } catch (Exception e) {
//                    logger.error("projectCoverInsert Error : " + e.getMessage());
//                }
//            }
//            model.put("returnResult", rtInt);
//        }
//
//        return new ModelAndView(ajaxJsonView, model);
//    }

}
