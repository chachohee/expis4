package com.expis.ietm.controller;

import com.expis.common.IConstants;
import com.expis.common.variable.VariableAspect;
import com.expis.ietm.dto.TocoInfoDto;
import com.expis.ietm.dto.VersionInfoDto;
import com.expis.ietm.dto.XContDto;
import com.expis.ietm.service.VersionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [IETM-TM]버전 정보 Controller Class
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/ietm")
public class VersionController {

    private final VersionService versionService;

//    @Value("${app.expis.webMobileType}")
//    private String webMobileType;

    /**
     * 버전 목록
     */
    @RequestMapping("/versionMain.do")
    public ResponseEntity<Map<String, Object>> versionMain(@ModelAttribute("VersionInfoDto") VersionInfoDto dto,
                                                           HttpServletRequest request) throws Exception {

        VariableAspect.setting(request);
        Map<String, Object> map = new HashMap<>();
        String toKey = request.getParameter("toKey");
        String allBook = IConstants.ALL_BOOK;
        String orgBook = IConstants.ORG_BOOK;

        dto.setToKey(toKey);

        /* 가져올 데이터 */
        List<VersionInfoDto> verList = versionService.getVersionList(dto);

        log.info("toKey : " + toKey);
        log.info("verList : " + verList);

        map.put("versionList", verList);
        map.put("toKey", toKey);
        map.put("allBook", allBook);
        map.put("orgBook", orgBook);

        return ResponseEntity.ok(map);
    }


    /**
     * 버전별 목차 목록
     */
    @RequestMapping("/versionDetailList.do")
    public ResponseEntity<Map<String, Object>> versionTocoList(@ModelAttribute("TocoInfoDto") TocoInfoDto dto,
                                                               HttpServletRequest request) throws Exception {

        Map<String, Object> map = new HashMap<>();
        try {
            VariableAspect.setting(request);
            String toKey = request.getParameter("toKey");
            String verId = request.getParameter("verId");
            int totCnt = 0;

            /*
             * TocoInfoDto 담아서 보낼 것
             */
            dto.setToKey(toKey);
            dto.setTocoVerId(verId);
            String book = request.getParameter("book");
            log.info("verID : " + verId);
            log.info("book : " + book);

            if(book.equals("all")) {
                dto.setTocoVerId(null);
            } else if(book.equals("org")) {
                dto.setTocoVerId("org");
            } else if(book.equals("total")) {
                dto.setTocoVerId(null);
                dto.setParam(book);
            }

            List<TocoInfoDto> tocoList = versionService.getVersionTocoList(dto);

            totCnt = versionService.getVersionTocoCount(dto);
            if(tocoList != null) {
                if(tocoList.size() == 0) {
                    map.put("ntocoName", tocoList.size());
                }
                log.info("tocoList : " + tocoList.toString());
            }else {
                map.put("ntocoName", 0);
            }


            map.put("totCnt", totCnt);
            map.put("detailList", tocoList);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("VersionController.versionTocoList Exception:" + ex.toString());
        }
        return ResponseEntity.ok(map);
    }

    /**
     * 교범 버전 링크
     */
/*
    @RequestMapping("/versionInfo.do")
    public ResponseEntity<Map<String, Object>> versionInfo(@RequestParam(value="verStatus") String verStatus
            ,@RequestParam(value="toKey") String toKey
            ,@RequestParam(value="tocoId") String tocoId
            ,@RequestParam(value="contId") String contId
            ,@RequestParam(value="verId") String verId
            ,@RequestParam(value="changebasis") String changebasis
            ,@RequestParam(value="lang", defaultValue="ko") String lang
            ,@RequestParam(value="bizCode", required = false) String bizCode) throws Exception {

        log.info("Version CAll : "+changebasis);
        log.info("Version CAll - param bizCode : "+bizCode);

        Map<String, Object> map = new HashMap<String, Object>();

        String biz				= IConstants.BIZ_DF;
        String fiCont = "";
        String lastVersionNo = "";

        XContDto contDto = new XContDto();
        contDto.setBiz(biz);
        contDto.setToKey(toKey);
        contDto.setTocoId(tocoId);
        contDto.setContId(contId);
        contDto.setVerId(verId);
        contDto.setVerStatus(verStatus);
        contDto.setChangebasis(changebasis);
//        contDto.setWebMobileKind(webMobileType);
        contDto.setLanguageType(lang);
        contDto.setBizCode(bizCode);

        // KTA, toKey 공백이 있을 경우 치환하도록 보완
        if ( toKey.contains("%20") ) {
            contDto.setToKey(toKey.replace("%20", " "));
        }

        VersionInfoDto verDto = versionService.getVersionInfo(contDto);
        StringBuffer contSB = versionService.getVersionXCont(contDto);
        lastVersionNo = versionService.getLastVersionInfo(contDto);
        log.info("contSB : "+contSB.toString());
        log.info("toKey : "+toKey);
        // 모든교범 변경 사유 처리하기위해 처리
        // if(toKey.indexOf("FR") != -1 || toKey.indexOf("FI") != -1) {
        log.info("In Check versionDto ");
        VersionInfoDto versionDto = new VersionInfoDto();
        versionDto.setToKey(toKey);
        // toco_id 있을 경우 추가 - jingi.kim
        if ( "".equalsIgnoreCase(tocoId) == false ) {
            versionDto.setTocoId(tocoId);
        }
        versionDto.setVerId(verId);
        versionDto.setContId(contId);

        List<String> verList = versionService.getVersionInfoList(versionDto);

        log.info("verList : "+verList.size());

        for(int i=0; i < verList.size(); i++) {
            fiCont += verList.get(i);
        }

        fiCont = fiCont.replaceAll("&amp;#254;&amp;#158;&amp;#0;&amp;#255;&amp;#254;&amp;#160;&amp;#-20;&amp;#255;","");
        fiCont = fiCont.replaceAll("&amp;","");
        fiCont = fiCont.replaceAll("amp;","");
        fiCont = fiCont.replaceAll("#254;","");
        fiCont = fiCont.replaceAll("#200;","");
        fiCont = fiCont.replaceAll("#1;","");
        fiCont = fiCont.replaceAll("#255;","");
        fiCont = fiCont.replaceAll("#0;","");
        fiCont = fiCont.replaceAll("#0","");
        fiCont = fiCont.replaceAll("&#32;"," ");
        fiCont = fiCont.replaceAll("#32;"," ");
        fiCont = fiCont.replaceAll("#-256", "");

        map.put("fiCont", fiCont);
        if(verDto != null) {
            map.put("verChgNo", verDto.getChgNo());
            map.put("verChgDate", verDto.getChgDate());
        }

        // 표시내용에 특수문자 제거
        log.info("contSB.toString() : "+contSB.toString());
        String verCont = contSB.toString();
        verCont = verCont.replaceAll("&#254;&#200;&#1;&#255;","<B>");
        verCont = verCont.replaceAll("&#254;&#200;&#0;&#255;","</B>");

        log.info("verCont : "+verCont);
        map.put("verHtml", verCont);
        map.put("lastVerNo", lastVersionNo);

        return ResponseEntity.ok(map);
    }
*/

    @RequestMapping("/versionOpenWin.do")
    public String printMain(HttpServletRequest request, Model model) throws Exception {
        log.info("versionOpenWin : " + request.getParameter("verHtml"));
        VariableAspect.setting(request);

//        ModelAndView mv = new ModelAndView("/ietm/ietm_popup/version_open");
//        try {
//            //2022 10 25 Park.J.S.  ADD : IPB 일경우 화면 변경
//            if(request.getParameter("verHtml") != null && request.getParameter("verHtml").contains("in_table_ipb") && request.getParameter("verHtml").contains("part_tr")) {
//                mv = new ModelAndView("/ietm/ietm_popup/version_open_ipb");
//            }
//        }catch (Exception e) {
//        }

        try {
            // IPB 일경우 화면 변경
            if (request.getParameter("verHtml") != null && request.getParameter("verHtml").contains("in_table_ipb") && request.getParameter("verHtml").contains("part_tr")) {
                model.addAttribute("view", "/ietm/ietm_popup/version_open_ipb");
            } else {
                model.addAttribute("view", "/ietm/ietm_popup/version_open");
            }
        } catch (Exception e) {
            log.error("Error processing view selection", e);
        }

        model.addAttribute("verChgNo", request.getParameter("verChgNo"));
        model.addAttribute("verChgDate", request.getParameter("verChgDate"));
        model.addAttribute("title", request.getParameter("title"));
        model.addAttribute("verHtml", request.getParameter("verHtml"));
        model.addAttribute("changebasis", request.getParameter("changebasis"));

        log.info("versionOpenWin in verHtml : "+request.getParameter("verHtml"));
        //2022 07 19 Park.J.S. 사천 요청으로 grphprim 들어가는 경우 도해로 판단해서 표시되는 내용 변경
        log.info("versionOpenWin in verHtml : " + request.getParameter("verHtml"));
        if (request.getParameter("verHtml") != null && (request.getParameter("verHtml").indexOf("pngDivArea") > 0 || request.getParameter("verHtml").indexOf("class=\"ac-noimg\"") > 0)) {
            log.info("title : " + request.getParameter("title"));
            log.info("title : " + request.getSession().getAttribute("lang"));
            if (request.getParameter("title") != null && "a".equalsIgnoreCase(request.getParameter("title"))) {
                if (request.getSession().getAttribute("lang") != null && "en".equalsIgnoreCase(request.getSession().getAttribute("lang").toString())) {
                    model.addAttribute("verHtml", "Add");
                } else {
                    model.addAttribute("verHtml", "추가된 항목임");
                }
            } else {
                if (request.getSession().getAttribute("lang") != null && "en".equalsIgnoreCase(request.getSession().getAttribute("lang").toString())) {
                    model.addAttribute("verHtml", "Update");
                } else {
                    model.addAttribute("verHtml", "변경된 항목임");
                }
            }
        } else {
            if (request.getParameter("verHtml") != null && !"".equals(request.getParameter("verHtml"))) {
                log.info("verHtml");
            } else {
                log.info("title : " + request.getParameter("title"));
                log.info("title : " + request.getSession().getAttribute("lang"));
                if (request.getParameter("title") != null && "a".equalsIgnoreCase(request.getParameter("title"))) {
                    if (request.getSession().getAttribute("lang") != null && "en".equalsIgnoreCase(request.getSession().getAttribute("lang").toString())) {
                        model.addAttribute("verHtml", "Add");
                    } else {
                        model.addAttribute("verHtml", "추가된 항목임");
                    }
                } else {
                    if (request.getSession().getAttribute("lang") != null && "en".equalsIgnoreCase(request.getSession().getAttribute("lang").toString())) {
                        model.addAttribute("verHtml", "Update");
                    } else {
                        model.addAttribute("verHtml", "변경된 항목임");
                    }
                }
            }
        }

        return "ietm/fragments/fi/fi_popup";
    }

    @RequestMapping("/versionName.do")
    public ResponseEntity<Map<String, Object>> findVersionName(@RequestParam(value = "toKey") String toKey,
                                                               @RequestParam(value = "verId") String verId) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            XContDto contDto = new XContDto();
            contDto.setToKey(toKey);
            contDto.setVerId(verId);

            VersionInfoDto versionDto = new VersionInfoDto();
            versionDto.setToKey(toKey);
            versionDto.setVerId(verId);

            // Version name을 가져오는 서비스 호출
            String verName = versionService.getVersionName(versionDto);

            // verName이 null이면 빈 문자열로 처리
            if (verName == null) verName = "";
            map.put("verName", verName);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("findVersionName Exception:" + ex.toString());
        }

        return ResponseEntity.ok(map);
    }
}
