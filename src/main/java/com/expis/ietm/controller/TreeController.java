package com.expis.ietm.controller;

import com.expis.common.IConstants;
import com.expis.common.config.datasource.DataSourceContextHolder;
import com.expis.common.variable.VariableAspect;
import com.expis.domparser.XmlDomParser;
import com.expis.ietm.service.TreeService;
import com.expis.manage.dto.SystemInfoDto;
import com.expis.manage.dto.TreeXContDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/ietm")
public class TreeController {

    private final TreeService treeService;

    public Node[] arrToTreeDom = {null, null, null, null, null, null, null, null, null, null};
    public String[] arrToKey	= {"", "", "", "", "", "", "", "", "", ""};
    public boolean isMemoryDom = false;

    /**
     * [IETM-TM] TO 목록
     */
    @RequestMapping("/toTree.do")
    public String toTree(HttpServletRequest request, Model model) {

        try {
            VariableAspect.setting(request);

            String treeKind		= IConstants.TOINDEX_SYS;
            String toKey			= (String) request.getAttribute("to_key");
            String userId		= (String) request.getAttribute("SS_USER_ID");

            TreeXContDto treeDto = new TreeXContDto();
            treeDto.setTreeKind(treeKind);
            treeDto.setRefToKey(toKey);
            treeDto.setRefUserId(userId);
            StringBuffer sbToIndex = treeService.getToTree(treeDto);

            model.addAttribute("message", "success");
            model.addAttribute("returnData", sbToIndex);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeController.toTree Exception:"+ex.toString());
        }

        return "ietm/ietmHome";
    }


    /**
     * [IETM-TM] TO 목록 AJax
     */
    @RequestMapping("/toTreeAjax.do")
    public ResponseEntity<Map<String, Object>> toTreeAjax(@PathVariable String bizCode,
            HttpServletRequest request) {

        Map<String, Object> model = null;
        StringBuffer sbToTree = new StringBuffer();

        try {

            VariableAspect.setting(request);

            String treeKind		= (String) request.getParameter("to_kind");
            String toKey			= (String) request.getParameter("to_key");
            String userId		= (String) request.getParameter("user_id");

            TreeXContDto treeDto = new TreeXContDto();
            treeDto.setTreeKind(treeKind);
            treeDto.setRefToKey(toKey);
            treeDto.setRefUserId(userId);

            log.info("set Data Source.");
            DataSourceContextHolder.setDataSource(bizCode);
            sbToTree = treeService.getToTree(treeDto);
            log.info("treeDto: {}", treeDto.toString());

            model = new HashMap<String, Object>();
            HttpSession session = (HttpSession) request.getSession();
            String fontSize = (String) session.getAttribute("SS_OPT_FONT_SIZE");
            model.put("fontSize", fontSize);

            model.put("message", "success");
            model.put("returnData", sbToTree.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeController.toTree Exception:"+ex.toString());
        }

        return ResponseEntity.ok(model);
    }


    /**
     * 관련교범목록
     */
    @RequestMapping("/relatedToList.do")
    public String relatedToList(HttpServletRequest request, Model model) {

        try {
            VariableAspect.setting(request);

            //Parameter
            String toKey			= (String) request.getAttribute("to_key");

            SystemInfoDto sysDto = new SystemInfoDto();
            sysDto.setToKey(toKey);

            List<SystemInfoDto> sysList = treeService.getRelatedToList(sysDto);

            model.addAttribute("message", "success");
            model.addAttribute("returnData", sysList);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeController.relatedToList Exception:"+ex.toString());
        }

        return "ietm/ietmHome";
    }


    /**
     * 관련교범목록
     */
    @RequestMapping("/selectSibling.do")
    public ResponseEntity<Map<String, Object>> selectSibling(@RequestParam("toKey") String toKey, HttpServletRequest request) throws Exception {
        //@SuppressWarnings("rawtypes")

        HashMap<String, Object> model = new HashMap<String, Object>();

//        String toKey = request.getParameter("to_key");
        ArrayList<TreeXContDto> siblingList = treeService.selectSiblingList(toKey);

        HttpSession session = (HttpSession) request.getSession();
        String fontSize = (String) session.getAttribute("SS_OPT_FONT_SIZE");
        model.put("fontSize", fontSize);
        model.put("siblingList", siblingList);

        return ResponseEntity.ok(model);
    }


    /**
     * 그림/표목차 목록
     */
    @RequestMapping("/toSubTocoList.do")
    public ResponseEntity<Map<String, Object>> toSubTocoListAjax(HttpServletRequest request) throws Exception {

        HashMap<String, Object> model = null;

        try {
            VariableAspect.setting(request);

            String toKey				= (String) request.getParameter("to_key");
            String tocoListKind	= (String) request.getParameter("toco_list_kind");

            TreeXContDto treeDto = new TreeXContDto();
            treeDto.setTreeKind(IConstants.TOINDEX_TO);
            treeDto.setRefToKey(toKey);
//            treeDto.setTocoListKind(tocoListKind); //그림/표/동영상목차 목록 구분

            StringBuffer sbTocoTree = treeService.getSubTocoList(treeDto);

            // 2023.07.19 - language 처리 추가 - jingi.kim
            String userLang = "ko";
            if(request.getSession().getAttribute("lang") != null && "en".equalsIgnoreCase(request.getSession().getAttribute("lang").toString())) {
                userLang = "en";
            }

            String subTocoCaption = "";
            String subTocoTitle = "";
            if (tocoListKind.equals(IConstants.TOCOLIST_KIND_GRPH)) {
                subTocoCaption = "그림목차";
                subTocoTitle = "도해 (그림제목)";
                if ( "en".equalsIgnoreCase(userLang) ) {
                    subTocoCaption = "List of Figures";
                    subTocoTitle = "Figure (Figure title)";
                }
            } else if (tocoListKind.equals(IConstants.TOCOLIST_KIND_TABLE)) {
                subTocoCaption = "표목차";
                subTocoTitle = "표 (표제목)";
                if ( "en".equalsIgnoreCase(userLang) ) {
                    subTocoCaption = "List of Table";
                    subTocoTitle = "Table (Table title)";
                }
                //2023.05.10 jysi EDIT : 동영상목차 페이지 추가
            } else if (tocoListKind.equals(IConstants.TOCOLIST_KIND_VIDEO)) {
                subTocoCaption = "동영상목차";
                subTocoTitle = "동영상 (동영상제목)";
                if ( "en".equalsIgnoreCase(userLang) ) {
                    subTocoCaption = "List of Video";
                    subTocoTitle = "Video (Video title)";
                }
            }

            model = new HashMap<String, Object>();

            model.put("message", "success");
            model.put("subTocoCaption", subTocoCaption);
            model.put("subTocoTitle", subTocoTitle);
            model.put("returnData", sbTocoTree.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeController.toSubTocoListAjax Exception:"+ex.toString());
        }

        return ResponseEntity.ok(model);
    }


    /**
     * 최초에(로그인) 교범 목차 트리 Dom으로 생성하여 전역변수 저장
     */
    public boolean setToTreeDom() throws Exception {

        boolean rtBl = false;

        try {
            //20191017 add LYM 교범 목록 추출
            SystemInfoDto sysDto = new SystemInfoDto();

            List<SystemInfoDto> sysList = treeService.getSystemToList(sysDto);
            if (sysList != null) {
                for (int i=0; i<sysList.size() && i< arrToKey.length ; i++) {
                    sysDto = (SystemInfoDto) sysList.get(i);
                    arrToKey[i] = sysDto.getSysName();
                }
            } else {
            }

            //교범별 목차(TO Contents Tree) 추출하여 DOM 생성
            for (int i=0; i<arrToKey.length; i++) {

                TreeXContDto treeDto = new TreeXContDto();
                treeDto.setTreeKind(IConstants.TOINDEX_TO);
                treeDto.setRefToKey(arrToKey[i]);

                Node toElem = treeService.getToTreeDom(treeDto);

                if (toElem != null) {
                    arrToTreeDom[i] = toElem;
                    log.info("~~~> setToTreeDom toKey="+arrToKey[i]+", success!!");
                } else {
                    log.info("~~~> setToTreeDom toKey="+arrToKey[i]+", fail(no data)!!");
                }
            }

            rtBl = true;
            isMemoryDom = true;

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeController.setToTreeDom Exception:"+ex.toString());
        }

        return rtBl;
    }



    /**
     * 전역변수로 저장된 교범 목차 트리 Dom 중에 해당 교범을 호출하여 사용
     */
    @RequestMapping("/childTocoList.do")
    public String childTocoListFromDom(HttpServletRequest request) throws Exception {

        HashMap<String, Object> model = null;

        try {
            VariableAspect.setting(request);

            String toKey			= (String) request.getParameter("to_key");
            String tocoId			= (String) request.getParameter("toco_id");

            Node toNode = null;
            String[] arrTocoList = null;

            log.info("toKey="+toKey+", tocoId="+tocoId);

            for (int i=0; i<arrToKey.length; i++) {
                log.info("===>BBB i="+i+", arrToKey[i]="+arrToKey[i]);
                if (arrToTreeDom[i] == null) {
                    log.info("===>bbb-2 arrToTreeDom[i]= is null");
                } else {
                    log.info("===>bbb-2 arrToTreeDom[i]="+arrToTreeDom[i]);
                }
            }

            for (int i=0; i<arrToKey.length; i++) {
                if (toKey.equals(arrToKey[i])) {
                    toNode = (Node) arrToTreeDom[i];

                    log.info("i="+i+", arrToTreeDom[i]="+arrToTreeDom[i]);
                    break;
                }
            }
            log.info("toNode : "+toNode);
            if (toNode != null) {
                String qryXalan = "//system[@id='" + tocoId + "']/descendant-or-self::*";
                NodeList tocoList = XmlDomParser.getNodeListFromXPathAPI(toNode, qryXalan);
                log.info("tocoList.getLength() : "+tocoList.getLength());
                if (tocoList.getLength() > 0) {
                    arrTocoList = new String[tocoList.getLength()];
                    for (int i=0; i<tocoList.getLength(); i++) {
                        Node tocoNode = tocoList.item(i);
                        NamedNodeMap tocoAttr = tocoNode.getAttributes();
                        String childTocoId = XmlDomParser.getAttributes(tocoAttr, "id");
                        arrTocoList[i] = childTocoId;
                    }
                }
            }

            model = new HashMap<String, Object>();
            model.put("message", "success");
            model.put("arrTocoList", arrTocoList);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeController.childTocoListFromDom Exception:"+ex.toString());
        }

        return "ietm/fragments/childTocoListView";
    }

}
