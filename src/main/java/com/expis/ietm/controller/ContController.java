package com.expis.ietm.controller;

import com.expis.common.CommonConstants;
import com.expis.common.config.datasource.DataSourceContextHolder;
import com.expis.common.eXPIS3Constants;
import com.expis.common.ext.ExternalFileEx;
import com.expis.domparser.CSS;
import com.expis.domparser.TITLE;
import com.expis.ietm.dto.TocoInfoDto;
import com.expis.ietm.dto.VersionInfoDto;
import com.expis.ietm.dto.XContDto;
import com.expis.ietm.facade.ContFacade;
import com.expis.ietm.service.ContService;
import com.expis.ietm.service.VersionService;
import com.expis.login.interceptor.Login;
import com.expis.manage.dao.SystemOptionMapper;
import com.expis.user.dto.UserDto;
import com.expis.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/ietm")
public class ContController {

    private final ContFacade contFacade;
    private final SystemOptionMapper systemOptionService;
    private final ContService contService;
    private final VersionService versionService;

    /**
     * IETM 상단 - 교범 기본 정보
     */
    @RequestMapping("/topInfo.do")
    public ResponseEntity<?> topInfo(@PathVariable String bizCode,
                                     @RequestParam("to_key") String toKey,
                                     @RequestParam("toco_id") String tocoId,
                                     @Login UserDto userDto,
                                     HttpServletRequest request) throws Exception {

        Map<String, String> responseMap = new HashMap<>();

        try {
            DataSourceContextHolder.setDataSource(bizCode);

            String userId = userDto.getUserId();
            String pTocoId = "";
            String toTitle = "";
            String tocoSssnNoP = "";
            String toHVerDate = "";
            String subName = "";
            String tpinfo = ""; //2024.01.24 - KTA계열일때 tpinfo 추가 - JSH
            String versiondate = ""; //2024.02.14 - KTA계열일때 versiondate 추가 - JSH

            XContDto xcontDto = new XContDto();
            TocoInfoDto tocoDto = new TocoInfoDto();
            tocoDto.setToKey(toKey);
            tocoDto.setTocoId(tocoId);
            tocoDto.setCreateUserId(userId);
//            String toVerDate = toService.getToVersionDate(toKey);
//            String tocoSssnNo = toService.getTocoSssnNo(tocoDto);
            String toVerDate = "2025.01.01";
            String tocoSssnNo = "0";

            //by ejkim 2022.10.06 발간일 변경 (system_toinfo.xml에 있는 versiondate )
//            CoverManageDTO coverDto = systemOptionService.getCoverCont(toKey);
//            if( coverDto !=null ) {
//                //2023.07.10 jysi EDIT : LSAM, NLS일때 발간일은 표지정보(coverCont)에서 가져오도록 수정
//                if(request.getSession().getAttribute("GV_BIZ_CODE").equals("LSAM") || request.getSession().getAttribute("GV_BIZ_CODE").equals("NLS")){
//                    toHVerDate = coverDto.getCoverCont();
//                }else if(request.getSession().getAttribute("GV_BIZ_CODE").equals("KTA")) { //2024.01.24 - KTA계열일때 tpInfo 추가 - JSH
//                    subName = coverDto.getCoverSubTitle();
//                    tpinfo = coverDto.getTpinfo(); //2024.01.24 - KTA계열일때 tpinfo 추가 - JSH
//                    versiondate  = coverDto.getVersiondate(); //2024.02.14 - KTA계열일때 versiondate 추가 - JSH
//                }else {
//                    toHVerDate = coverDto.getCoverVerDate();
//                }
//                //2024.10.15 - 발간일을 표지정보에서 입력한 날짜 표시되도록 추가 - jingi.kim
//                if( request.getSession().getAttribute("GV_BIZ_CODE").equals("KICC") ) {
//                    toHVerDate = coverDto.getCoverCont();
//                }
//            }

            //by ejkim 2022.10.06 상위목차 sssno를 가져오기 위해 추
            if( "".equals(tocoSssnNo) || "-".equals(tocoSssnNo)) {

//                TocoInfoDto prTocoDto = contService.getParentTocoSssnNo(tocoDto);
//                if(prTocoDto != null) {
//                    tocoSssnNoP = prTocoDto.getTocoSssnNo();
//                }
            }

//            TocoInfoDto rtTocoDto = contService.getToInfo(tocoDto);
//            if(rtTocoDto != null) {
//                toTitle = rtTocoDto.getTocoName();
//                pTocoId = rtTocoDto.getPTocoId();
//            }
            xcontDto.setToKey(toKey);

            xcontDto.setTocoId(tocoId);
//            int ipbChk = contService.selectIPBCount(xcontDto);
            int ipbChk = 0;

            responseMap.put("pTocoId", pTocoId);		//2023.08.09 - jingi.kim
            responseMap.put("toVerDate", toVerDate);
            responseMap.put("tocoSssnNo", tocoSssnNo);
            responseMap.put("tocoSssnNoParent", tocoSssnNoP);
            responseMap.put("toTitle", toTitle);
            responseMap.put("ipbChk", String.valueOf(ipbChk));
            responseMap.put(CommonConstants.BIZ_CODE, bizCode);
            responseMap.put("toHVerDate", toHVerDate == null ? "" : toHVerDate); //by ejkim 2022.10.05 add
            responseMap.put("subName", subName);
            responseMap.put("tpinfo" , tpinfo); //2024.01.24 - KTA계열일때 tpinfo 추가 - JSH
            responseMap.put("versiondate" , versiondate); //2024.01.24 - KTA계열일때 versiondate 추가 - JSH

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("ContController.topInfo Exception:{}", ex.toString());
        }

        responseMap.put("status", "success");
        return ResponseEntity.ok(responseMap);
    }

    /**
     * 교범 내용
     */
    @RequestMapping("/toCont.do")
    public ResponseEntity<?> toContAjax(@PathVariable String bizCode,
                                        @Login UserDto userDto,
                                        HttpServletRequest request, HttpSession session) throws Exception {

//        Map<String, String> responseMap = new HashMap<>();
        Map<String, Object> responseMap = new HashMap<>();

        Map<String, Object> rtMap = new HashMap<String, Object>();
        Map<String, Object> verMap = new HashMap<String, Object>();
        String returnData = "";
        int xmlTagChk = 0;

        try {
            DataSourceContextHolder.setDataSource(bizCode);

            //파라미터 정보
            String toKey = Optional.ofNullable(request.getParameter("to_key")).orElse("");
            String tocoId = Optional.ofNullable(request.getParameter("toco_id")).orElse("");
            String contId = Optional.ofNullable(request.getParameter("cont_id")).orElse("");
            String searchWord = Optional.ofNullable(request.getParameter("search_word")).orElse("");
            String viewContKind = Optional.ofNullable(request.getParameter("vcont_kind")).orElse(eXPIS3Constants.VCONT_KIND_CONT);
            String optOutputMode = Optional.ofNullable(request.getParameter("output_mode")).orElse("");
            String optViewMode = Optional.ofNullable(request.getParameter("view_mode")).orElse("");
            String languageType = Optional.ofNullable(request.getParameter("language_type")).orElse("");

            //TODO: 옵션 정보 처리 필요 - 기존 처럼 세션 or Else
            //세션 정보
            HttpSession hs 			= request.getSession();
            String leftLhs 			= StringUtil.checkNull((String) hs.getAttribute("SS_LEFTLHS"));
            String userId			= StringUtil.checkNull((String) hs.getAttribute("SS_USER_ID"));
//			String optViewMode			= StringUtil.checkNull((String) hs.getAttribute("SS_OPT_VIEW_MODE"));
            String optFiMode		= StringUtil.checkNull((String) hs.getAttribute("SS_OPT_FI_MODE"));
            String optFontSize		= StringUtil.checkNull((String) hs.getAttribute("SS_OPT_FONT_SIZE"));
            String optFontFamily	= StringUtil.checkNull((String) hs.getAttribute("SS_OPT_FONT_FAMILY"));
            String arrIPBCols		= StringUtil.checkNull((String) hs.getAttribute(eXPIS3Constants.SS_ARR_IPB_COLS));
            String vehicleType		= StringUtil.checkNull((String) hs.getAttribute("SS_VEHICLE_TYPE"));
            String ipbType          = StringUtil.checkNull((String) hs.getAttribute("ipbType"));

            if (optOutputMode.equals("")) {
                optOutputMode	= eXPIS3Constants.OPT_OUMODE_MULTI;
            }
            if (optViewMode.equals("")) {
                optViewMode		= eXPIS3Constants.OPT_VMODE_PAGE;
            }
            if (optFiMode.equals("")) {
                optFiMode		= eXPIS3Constants.OPT_FIMODE_ALL;
            }

            //2024.03.27 - Topic, SubTopic 일 경우에만 MULTI 가능하도록 추가 - jingi.kim
            //2024.04.16 - BLOCK2, KTA만 적용하도록 제한 - jingi.kim
            if ( "BLOCK2".equalsIgnoreCase(bizCode) || "KTA".equalsIgnoreCase(bizCode) ) {
            } else {
                optOutputMode = eXPIS3Constants.OPT_OUMODE_SINGLE; //20190703 add LYM
            }

            //DTO에 값 셋팅
            XContDto contDto = new XContDto();
            contDto.setBizCode(bizCode);
            contDto.setUserId(userId);
            contDto.setToKey(toKey);
            contDto.setTocoId(tocoId);
            contDto.setContId(contId);
            contDto.setSearchWord(searchWord);					//검색어
            contDto.setViewContKind(viewContKind);				//목차타입
            contDto.setOutputMode(optOutputMode);				//탐색범위
            contDto.setVehicleType(vehicleType);				//형상정보
            contDto.setLanguageType(languageType);              //IPB 테이블 열 너비르 위해 추가
            contDto.setIpbType(ipbType);                        //IPB 테이블 열 너비르 위해 추가


            rtMap = contFacade.getTocoContents(contDto);
            returnData = rtMap.get("rtSB").toString();

//            xmlTagChk = (int) rtMap.get("xmlTagChk");

            if(returnData == null) {
                returnData = "";
            }
            if ( !returnData.isEmpty() ) {
                verMap = contFacade.getVersionStaus(returnData, contDto);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("ContController.toContAjax Exception:{}", ex.toString());
        }

        responseMap.put("status", "success");
        responseMap.put("returnData", returnData);

        if(session.getAttribute("CL_KEY") != null){
            responseMap.put("chk", session.getAttribute("CL_KEY"));
        }

        if ( rtMap.get("tocoType") != null && !rtMap.get("tocoType").toString().isEmpty() ) {
            responseMap.put("tocoType", rtMap.get("tocoType").toString());
        }
        if ( rtMap.get("nodeType") != null && !rtMap.get("nodeType").toString().isEmpty() ) {
            responseMap.put("nodeType", rtMap.get("nodeType").toString());
        }
        if ( rtMap.get("tocoName") != null && !rtMap.get("tocoName").toString().isEmpty() ) {
            responseMap.put("tocoName", rtMap.get("tocoName").toString());
        }
        if ( rtMap.get("memoList") != null && !rtMap.get("memoList").toString().isEmpty() ) {
            responseMap.put("memoList", rtMap.get("memoList"));
        }
        if ( rtMap.get("fiType") != null && !rtMap.get("fiType").toString().isEmpty()){
            responseMap.put("fiType", rtMap.get("fiType").toString());
        }
        if ( rtMap.get("imageData") != null && !rtMap.get("imageData").toString().isEmpty() ) {
            responseMap.put("imageData", rtMap.get("imageData").toString());
        }
        if ( rtMap.get("imageDataJson") != null && !rtMap.get("imageDataJson").toString().isEmpty() ) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(rtMap.get("imageDataJson"));
            responseMap.put("imageDataJson", jsonString);
        }
        if ( rtMap.get("replacePart") != null && !rtMap.get("replacePart").toString().isEmpty() ) {
            responseMap.put("replacePart", rtMap.get("replacePart").toString());
        }
        if ( rtMap.get("ipbTableWidths") != null && !rtMap.get("ipbTableWidths").toString().isEmpty() ) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(rtMap.get("ipbTableWidths"));
            responseMap.put("ipbTableWidths", jsonString);
            log.info("ipbTableWidths : {}", jsonString);
        }
        if ( verMap.get("versionData") != null && !verMap.get("versionData").toString().isEmpty() ) {
            responseMap.put("versionData", new ObjectMapper().writeValueAsString(verMap.get("versionData")));
        }

        return ResponseEntity.ok(responseMap);
    }


    /**
     * 교범 내용
     */
    @RequestMapping("/replacePartInfo.do")
    public ResponseEntity<?> replacePartInfo(@PathVariable String bizCode,
                                             @Login UserDto userDto,
                                             HttpServletRequest request) {

        Map<String, String> responseMap = new HashMap<>();
        String returnData = "";

        try {
            DataSourceContextHolder.setDataSource(bizCode);

            //DTO에 값 셋팅
            XContDto contDto = new XContDto();
            contDto.setBizCode(bizCode);

            returnData = contService.replacePartInfo(contDto);

            if(returnData == null) {
                returnData = "";
            }

        } catch (Exception ex) {
            //ex.printStackTrace();
            log.info("{} . replacePartInfo Exception:{}", this.getClass(), ex.toString());
        }

        responseMap.put("status", "success");
        responseMap.put("replacePart", returnData);

        return ResponseEntity.ok(responseMap);
    }

    /**
     * 팝업창 (공통)
     */
    @GetMapping("/contPopup.do")
    public String contPopup(@PathVariable String bizCode,
                            @RequestParam(value = "toKey", required = false) String toKey,
                            @RequestParam(value = "grphId", required = false) String grphId,
                            @RequestParam(value = "tocoId", required = false) String tocoId,
                            @RequestParam(value = "system", required = false) String system,
                            @RequestParam(value = "tblId", required = false) String tblId,
                            @RequestParam(value = "contId", required = false) String contId,
                            @RequestParam(value = "verId", required = false) String versionId,
                            @RequestParam(value = "verStatus", required = false) String versionStatus,
                            @RequestParam(value = "changeBasis", required = false) String changeBasis,
                            Model model
    ) {
        log.info("[GET] Open Popup");

        // 타켓이 되는 html에 파라미터 값을 변수화하기 위해 추가 (필요 시 추가 작성) - osm
        model.addAttribute("toKey", toKey);
        model.addAttribute("grphId", grphId);                // 이미지
        model.addAttribute("tocoId", tocoId);
        model.addAttribute("system", system);                // WC 교범 BizCode 분기
        model.addAttribute("tblId", tblId);                  // 테이블 ID
        model.addAttribute("contId", contId);                // 버전바 contId
        model.addAttribute("versionId", versionId);          // 버전바 ID
        model.addAttribute("versionStatus", versionStatus);  // 버전바 상태
        model.addAttribute("changeBasis", changeBasis);
        return "ietm/fragments/popup/contPopup";
    }

    /**
     * 팝업창 데이터
     * 2025.06.17 - osm
     * - 이미지 팝업, WC계통 팝업
     * - (임시) 그림목차, 표목차
     * - 버전바 팝업
     *   - WC교범 버전바 이미지타입 TITLE로 처리하도록 임시 구현
     */
    @GetMapping("/contOpener.do")
    public ResponseEntity<?> contOpener(@PathVariable String bizCode,
                                        @RequestParam(value = "toKey", required = false) String toKey,
                                        @RequestParam(value = "grphId", required = false) String grphId,
                                        @RequestParam(value = "tblId", required = false) String tblId,
                                        @RequestParam(value = "system", required = false) String system,
                                        @RequestParam(value = "contId", required = false) String contId,
                                        @RequestParam(value = "verId", required = false) String verId,
                                        @RequestParam(value = "verStatus", required = false) String verStatus,
                                        @RequestParam(value = "changebasis", required = false) String changebasis
    ) {
        log.info("[GET] ContOpener");

        Map<String, Object> responseMap = new HashMap<>();
        try {
            DataSourceContextHolder.setDataSource(bizCode);
            XContDto xcontDto = new XContDto();
            xcontDto.setToKey(toKey);
            xcontDto.setGrphId(grphId);
            xcontDto.setTblId(tblId);
            xcontDto.setWcSystem(system);
            xcontDto.setContId(contId);
            xcontDto.setVerId(verId);
            xcontDto.setVerStatus(verStatus);
            xcontDto.setChangebasis(changebasis);

            if (grphId != null && !grphId.isEmpty()) {
                Map<String, Object> grphCont = contService.getSingleGrphCont(xcontDto);      //grphId != null && !grphId.isEmpty() 일때, 여기 통과 못함

                responseMap.put("returnData", grphCont.get("rtSB").toString());
                responseMap.put("tocoLink", grphCont.get("tocoLink").toString());
                responseMap.put("nodeType", grphCont.get("nodeType").toString());

            } else if (tblId != null && !tblId.isEmpty()) {
                XContDto getSingleTableCont = contService.getSingleTableCont(xcontDto);

                responseMap.put("returnData", contService.getTableCont(xcontDto).toString());
                responseMap.put("tocoLink", CSS.getTocoLink(
                        getSingleTableCont.getTocoId(),
                        contService.getTocoName(getSingleTableCont.getToKey(), getSingleTableCont.getTocoId()),
                        xcontDto.getLanguageType()
                ));
                responseMap.put("nodeType", "table");

            } else if (system != null && !system.isEmpty()) {
                List<XContDto> xContDto = contService.viewWCLink(xcontDto);

                responseMap.put("returnData", xContDto);

            } else if (verId != null && !verId.isEmpty()) {
                List<XContDto> versionXCont = versionService.getVersionXCont(xcontDto);
                VersionInfoDto versionInfo = versionService.getVersionInfo(xcontDto);

                // versionXcont의 wimg 값이 존재하는지 체크
                boolean hasWimg = !versionXCont.isEmpty() && versionXCont.stream()
                        .map(XContDto::getXcont)
                        .filter(Objects::nonNull)
                        .anyMatch(cont -> cont.contains("wimg"));

                if (versionXCont.isEmpty() || hasWimg) { // versionXcont 내용이 비어있거나 WC wimg 형식일 때
                    String title = "a".equalsIgnoreCase(verStatus) ? TITLE.VER_APPEND : TITLE.VER_UPDATE;
                    responseMap.put("returnData", title);

                } else { // 그 외에 versionXcont가 정상일 때
                    responseMap.put("returnData", versionXCont);
                }

                responseMap.put("versionData", versionInfo);
            }

        } catch (Exception ex) {
            log.info("{} . contOpener Exception:{}", this.getClass(), ex.toString());

        }
        return ResponseEntity.ok(responseMap);
    }

    /**
     * CL 교범 체크박스
     * toKey, tocoId, 체크박스 유무를 통해, 세션에 담아 저장
     *
     * @param toKey 교범명
     * @param tocoId 교범내용ID
     * @param chkVal 체크박스 유무
     * @return 뷰페이지를 위한 CmntViewDTO
     */
    @PostMapping("/checkCL.do")
    public ResponseEntity<?> checkCL(HttpSession session,
                                     @RequestParam String toKey, @RequestParam String tocoId, @RequestParam boolean chkVal){
        List<String> clList = new ArrayList<>();
        String clId = toKey + "_" + tocoId;

        if(session.getAttribute("CL_KEY") != null){
            clList = (ArrayList<String>) session.getAttribute("CL_KEY");
        }

        if(chkVal){
            clList.add(clId);
        }else{
            clList.remove(clId);
        }

        session.setAttribute("CL_KEY", clList);
        return ResponseEntity.ok(clList);
    }

    @PostMapping("/checkIndexByTocoId.do")
    public ResponseEntity<?> checkIndexByTocoId(@PathVariable String bizCode,
                                                @RequestParam(value = "tocoId", defaultValue = "") String tocoId
    ) {
        DataSourceContextHolder.setDataSource(bizCode);
        Map<String,Object> responseMap = new HashMap<>();

        try {
            ExternalFileEx externalFileEx = new ExternalFileEx();
            String checkStr = externalFileEx.getCHECK_INDEX();
            String[] ids = checkStr.replace("\"", "").split(",");

            boolean checked = Arrays.asList(ids).contains(tocoId);

            responseMap.put("checked", checked);

        } catch (Exception ex) {
            log.info("findVersionName Exception : {}", String.valueOf(ex));
            responseMap.put("checked", false);
        }

        return ResponseEntity.ok(responseMap);
    }
}
