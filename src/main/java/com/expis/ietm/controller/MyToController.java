package com.expis.ietm.controller;

import com.expis.common.CommonConstants;
import com.expis.common.config.datasource.DataSourceContextHolder;
import com.expis.common.variable.VariableAspect;
import com.expis.ietm.dto.MyToDto;
import com.expis.ietm.service.MyToService;
import com.expis.login.interceptor.Login;
import com.expis.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/ietm")
public class MyToController {

    private final MyToService myToService;

    /**
     * MYTO 팝업창
     */
    @GetMapping("/myToPopup.do")
    public String myToPopup(HttpServletRequest request, @PathVariable String bizCode, MyToDto myToDto, Model model) throws Exception {

        DataSourceContextHolder.setDataSource(bizCode);
        HttpSession session = request.getSession();
        session.setAttribute("bizCode", bizCode);

        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);

        return "ietm/fragments/popup/myto";
    }

    /**
     * 폴더 목록 조회
     */
    @GetMapping("/selectMyFolder.do")
    public ResponseEntity<Map<String, Object>> selectMyFolder(@PathVariable String bizCode, @Login UserDto userDto, HttpServletRequest request) {

        DataSourceContextHolder.setDataSource(bizCode);
        Map<String, Object> resultMap = new HashMap<String, Object>();

//        String userId = request.getSession().getAttribute("SS_USER_ID").toString();
        HttpSession session = request.getSession();
        session.setAttribute("bizCode", bizCode);

        //UserDto userDto = (UserDto) session.getAttribute(CommonConstants.LOGIN_MEMBER);
        String userId = userDto.getUserId();

        MyToDto myDto = new MyToDto();
        myDto.setUserId(userId);
        List<MyToDto> folderList = myToService.selectMyFolder(myDto);

        resultMap.put("folderList", folderList);
        resultMap.put("success", true);
        return ResponseEntity.ok(resultMap);
    }


    /**
     * 폴더 생성
     */
    @PostMapping("/insertMyFolder.do")
    public ResponseEntity<Map<String, Object>> myFolderInsert(@PathVariable String bizCode,
                                                              @Login UserDto userDto,
                                                              @RequestBody MyToDto myToDto,
                                                              HttpServletRequest request) throws Exception {

        DataSourceContextHolder.setDataSource(bizCode);
        HttpSession session = request.getSession();
        session.setAttribute("bizCode", bizCode);

        log.info("브라우저에서 받아 온 myToDto: {}", myToDto);
        log.info("myToDto mytoPSeq: {}", myToDto.getMytoPSeq());
        Map<String, Object> resultMap = new HashMap<String, Object>();

        VariableAspect.setting(request);
        MyToDto myDto = new MyToDto();
        boolean insertYn;

//        String userId = request.getSession().getAttribute("SS_USER_ID").toString();
        String userId = userDto.getUserId();
        String mytoName = myToDto.getMytoName();
        String mytoKind = myToDto.getMytoKind();
        long mytoPSeq = myToDto.getMytoPSeq();


        myDto.setUserId(userId);
        myDto.setMytoName(mytoName); // 폴더 이름 설정
        myDto.setMytoKind(mytoKind);
        myDto.setCreateUserId(userId);
        myDto.setMytoPSeq(mytoPSeq);

        log.info("myDto mytoPSeq : {}", myDto.getMytoPSeq());

        insertYn = myToService.insertMyFolder(myDto);

        // 결과 맵에 success 키 추가
        resultMap.put("success", insertYn);
        resultMap.put("message", insertYn ? "폴더가 성공적으로 생성되었습니다." : "폴더 생성에 실패했습니다.");
        resultMap.put("mytoSeq", myToDto.getMytoSeq()); // 생성된 mytoSeq 반환

        return ResponseEntity.ok(resultMap);
    }


    /**
     * 폴더 수정
     */
    @PostMapping("/updateMyFolder.do")
    public ResponseEntity<Map<String, Object>> updateMyFolder(
            @PathVariable String bizCode,
            @RequestBody MyToDto myToDto,
            @Login UserDto userDto,
            HttpServletRequest request) {

        DataSourceContextHolder.setDataSource(bizCode);
        HttpSession session = request.getSession();
        session.setAttribute("bizCode", bizCode);

        Map<String, Object> resultMap = new HashMap<>();

//        String userId = (String) request.getSession().getAttribute("SS_USER_ID");
        String userId = userDto.getUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "로그인이 필요합니다."));
        }

        // 필수 데이터 확인
        if (myToDto.getMytoSeq() == 0 || myToDto.getMytoName() == null || myToDto.getMytoName().trim().isEmpty()) {
            resultMap.put("success", false);
            resultMap.put("message", "폴더 ID 또는 이름이 올바르지 않습니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        // 업데이트할 사용자 ID 설정
        myToDto.setModifyUserId(userId);

        // 업데이트 실행
        boolean updateSuccess = myToService.updateMyFolder(myToDto);
        log.info("updateSuccess: " + updateSuccess);

        resultMap.put("success", updateSuccess);
        resultMap.put("message", updateSuccess ? "폴더 이름이 성공적으로 변경되었습니다." : "폴더 이름 변경에 실패했습니다.");

        return ResponseEntity.ok(resultMap);
    }


    /**
     * 폴더 삭제
     */
    @PostMapping("/deleteMyFolder.do")
    public ResponseEntity<Map<String, Object>> deleteMyFolder(@PathVariable String bizCode,
                                                              @Login UserDto userDto,
                                                              @RequestBody Map<String, Object> requestData,
                                                              HttpServletRequest request) {

        DataSourceContextHolder.setDataSource(bizCode);
        Map<String, Object> resultMap = new HashMap<>();

        try {
            HttpSession session = request.getSession();
            session.setAttribute("bizCode", bizCode);

//            String userId = (String) request.getSession().getAttribute("SS_USER_ID");
            String userId = userDto.getUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "로그인이 필요합니다."));
            }

            Map<String, Object> params = (Map<String, Object>) requestData.get("requestData");
            log.info("mytoSeq 타입: {}", params.get("mytoSeq").getClass().getName());
            long mytoSeq = (Integer) params.get("mytoSeq");
            Boolean hasChildren = (Boolean) params.get("hasChildren");
            Boolean deleteChildren = (Boolean) params.get("deleteChildren");

            log.info("mytoSeq: {} hasChildren: {} deleteChildren {}", mytoSeq, hasChildren, deleteChildren);

            // 필수 데이터 확인
            if (mytoSeq == 0) {
                resultMap.put("success", false);
                resultMap.put("message", "올바른 폴더 ID가 필요합니다.");
                return ResponseEntity.badRequest().body(resultMap);
            }

            // 삭제 실행
            boolean deleteSuccess = myToService.deleteMyFolder(mytoSeq, hasChildren, deleteChildren);
            log.info("deleteSuccess: " + deleteSuccess);

            resultMap.put("success", deleteSuccess);
            resultMap.put("message", deleteSuccess ? "폴더가 성공적으로 삭제되었습니다." : "폴더 삭제에 실패했습니다.");

            return ResponseEntity.ok(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * for 중첩 폴더 만들 때 폴더 안에 목차가 등록되어 있는지 확인
     */
    @GetMapping("/selectMyTocoList.do")
    public ResponseEntity<Map<String, Object>> selectMyTocoList(@PathVariable String bizCode,
                                                                @Login UserDto userDto,
                                                                @RequestParam(required = false) long mytoSeq,
                                                                HttpServletRequest request) {

        DataSourceContextHolder.setDataSource(bizCode);
        HttpSession session = request.getSession();
        session.setAttribute("bizCode", bizCode);

        Map<String, Object> resultMap = new HashMap<String, Object>();

//        String userId = request.getSession().getAttribute("SS_USER_ID").toString();
        String userId = userDto.getUserId();
        MyToDto myDto = new MyToDto();
        myDto.setUserId(userId);
        myDto.setMytoSeq(mytoSeq);
        int myTocoListCount = myToService.selectMyTocoList(myDto);
        log.info("myTocoListCount: {}", myTocoListCount);

        resultMap.put("success", (myTocoListCount > 0) ? "true" : "false");
        log.info("myTocoListCount result: {}", (myTocoListCount > 0) ? "true" : "false");
        return ResponseEntity.ok(resultMap);
    }


    /**
     * MYTO 목차 추가
     */
    @PostMapping("/insertMyToco.do")
    public ResponseEntity<Map<String, Object>> insertMyToco(@PathVariable String bizCode,
                                                            @Login UserDto userDto,
                                                            @RequestParam(value="paramTocoId[]") List<String> paramTocoId,
                                                            @RequestParam(value="paramTocoName[]") List<String> paramTocoName,
                                                            @RequestParam(value="paramPTocoId[]") List<String> paramPTocoId,
                                                            @RequestParam(value="paramType[]") List<String> paramType,
                                                            @RequestParam(value="paramOrd[]") List<Long> paramOrd,
                                                            @RequestParam(value="toKey") String toKey,
                                                            @RequestParam(value="mytoSeq") long mytoSeq, HttpServletRequest request) throws Exception {

        DataSourceContextHolder.setDataSource(bizCode);
        HttpSession session = request.getSession();
        session.setAttribute("bizCode", bizCode);

        VariableAspect.setting(request);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        MyToDto myDto = new MyToDto();
        boolean insertYn;
//        String userId = request.getSession().getAttribute("SS_USER_ID").toString();
        String userId = userDto.getUserId();
        log.info("mytoSeq :" + mytoSeq);
        if(mytoSeq == 0) {
            mytoSeq = myToService.selectMytoSeq();
        }

        log.info("toKey" + toKey);
        log.info("paramTocoId" + paramTocoId);
        log.info("paramTocoName" + paramTocoName);
        log.info("paramPTocoId : " + paramPTocoId);
        log.info("paramOrd : " + paramOrd);

        myDto.setUserId(userId);
        myDto.setMytoKind("02");
        myDto.setToKey(toKey);
        myDto.setParamTocoId(paramTocoId);
        myDto.setParamTocoName(paramTocoName);
        myDto.setParamParentId(paramPTocoId);
        myDto.setParamType(paramType);
        myDto.setParamOrd(paramOrd);
        myDto.setMytoSeq(mytoSeq);
//        myDto.setDbType(dbType);
        myDto.setDbType("oracle");
        insertYn = myToService.insertMyCoService(myDto);

        resultMap.put("message", insertYn);

        return ResponseEntity.ok(resultMap);
    }


    /**
     * MYTO 목차 삭제
     */
    @PostMapping("/deleteMyToco.do")
    public ResponseEntity<Map<String, Object>> deleteMyToco(@PathVariable String bizCode,
                                                            @Login UserDto userDto,
                                                            @RequestBody MyToDto myToDto,
                                                            HttpServletRequest request) {
        log.info("TO 삭제 myToDto : {}", myToDto.toString());

        DataSourceContextHolder.setDataSource(bizCode);
        HttpSession session = request.getSession();
        session.setAttribute("bizCode", bizCode);

        Map<String, Object> resultMap = new HashMap<>();

//        String userId = (String) request.getSession().getAttribute("SS_USER_ID");
        String userId = userDto.getUserId();
        if (userId == null) {
            resultMap.put("success", false);
            resultMap.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultMap);
        }

        myToDto.setUserId(userId);

        try {

            boolean deleteSuccess = myToService.delMytoco(myToDto);

            log.info("TO 삭제 결과 : {}", deleteSuccess);

            // 결과 맵 구성
            resultMap.put("success", deleteSuccess);
            resultMap.put("message", deleteSuccess ? "목차가 성공적으로 삭제되었습니다." : "목차 삭제에 실패했습니다.");

            return ResponseEntity.ok(resultMap);
        } catch (Exception e) {
            // 예외 발생 시 오류 메시지 반환
            resultMap.put("success", false);
            resultMap.put("message", "목차 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultMap);
        }
    }


    /**
     * MY T.O List XML 생성
     */
    @GetMapping("/myToListXml.do")
    public ResponseEntity<Map<String, Object>> myToListXml(@PathVariable String bizCode, @Login UserDto userDto, HttpServletRequest request) throws Exception {

        DataSourceContextHolder.setDataSource(bizCode);
        HttpSession session = request.getSession();
        session.setAttribute("bizCode", bizCode);

        Map<String, Object> resultMap = new HashMap<String, Object>();

        MyToDto myToDto = new MyToDto();
        VariableAspect.setting(request);

//        String userId = request.getSession().getAttribute("SS_USER_ID").toString();
        String userId = userDto.getUserId();
        String toKey = request.getParameter("toKey");

        myToDto.setUserId(userId);
        myToDto.setToKey(toKey);

        StringBuffer sbToIndex = myToService.getMytoTree(myToDto);
        log.info("myToListXml.do sbToIndex : ", String.valueOf(sbToIndex));

        resultMap.put("resultData", sbToIndex);

        return ResponseEntity.ok(resultMap);
    }


    @PostMapping("/mytocoTreeAjax.do")
    public ResponseEntity<Map<String, Object>> toTreeAjax (@PathVariable String bizCode, @Login UserDto userDto, HttpServletRequest request) {

        DataSourceContextHolder.setDataSource(bizCode);
        Map<String, Object> resultMap = new HashMap<String, Object>();

        StringBuffer sbToTree = new StringBuffer();

        try {
            VariableAspect.setting(request);

            HttpSession session = request.getSession();
            session.setAttribute("bizCode", bizCode);

            String userId = userDto.getUserId();

            String treeKind = (String) request.getParameter("to_kind");
            long mytoSeq = Long.parseLong(request.getParameter("mytoSeq"));
            log.info("mytoSeq : " +mytoSeq + " treeKind : " + treeKind);

            MyToDto myToDto = new MyToDto();
            myToDto.setTreeKind(treeKind);
            myToDto.setMytoSeq(mytoSeq);
            myToDto.setUserId(userId);

            Map<String, Object> treeDataMap = myToService.getMyTocoXmlList(myToDto);

            resultMap.put("returnData", treeDataMap.get("treeData"));
            resultMap.put("toKey", treeDataMap.get("toKey"));

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeController.toTree Exception:"+ex.toString());
        }

        return ResponseEntity.ok(resultMap);
    }


    /**
     * MY T.O  dupChkTokey
     */
    @PostMapping("/dupChkTokey.do")
    public ResponseEntity<Map<String, Object>> dupChkTokey(@PathVariable String bizCode, @Login UserDto userDto, HttpServletRequest request) throws Exception {

        DataSourceContextHolder.setDataSource(bizCode);
        HttpSession session = request.getSession();
        session.setAttribute("bizCode", bizCode);

        VariableAspect.setting(request);
        Map<String, Object> resultMap = new HashMap<String, Object>();
//        String userId = request.getSession().getAttribute("SS_USER_ID").toString();
        String userId = userDto.getUserId();
        String toKey = request.getParameter("toKey");
        MyToDto myToDto = new MyToDto();
        int result = 0;

        myToDto.setUserId(userId);
        myToDto.setToKey(toKey);

        result = myToService.dupChkTokey(myToDto);

        resultMap.put("result", result);

        return ResponseEntity.ok(resultMap);
    }

}
