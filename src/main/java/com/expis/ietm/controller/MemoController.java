package com.expis.ietm.controller;

import com.expis.common.CommonConstants;
import com.expis.ietm.dto.MemoDto;
import com.expis.ietm.service.MemoService;
import com.expis.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/ietm")
public class MemoController {

    private final MemoService memoService;

    @GetMapping("/memoList.do")
    public ResponseEntity<?> memoList(HttpServletRequest request,
                                      @RequestParam(required = false) String searchValue,
                                      @RequestParam(required = false) String sortCode) {

        Map<String, Object> resultMap = new HashMap<>();
        MemoDto memoDto = new MemoDto();

        HttpSession session = request.getSession();
        UserDto userDto = (UserDto) session.getAttribute(CommonConstants.LOGIN_MEMBER);
        String userId = userDto.getUserId();
        String auth = (String) request.getSession().getAttribute("userRoleCode");

        memoDto.setCreateUserId(userId);
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            memoDto.setSearchValue(searchValue);
        }

        // sortCode는 modifyDate 기준으로 정렬되어 있음
        // sortCode 값이 없으면 기본값 1 설정
        if (sortCode == null || sortCode.trim().isEmpty()) {
            sortCode = "1";  // 기본값 설정
        }
        memoDto.setSortCode(sortCode);

        List<MemoDto> memoList = memoService.getMemoList(memoDto);

        resultMap.put("memoList", memoList);
        return ResponseEntity.ok(resultMap);
    }


    @PostMapping("/memoInsert.do")
    public ResponseEntity<?> memoInsert(@RequestBody MemoDto memoDto, HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> resultMap = new HashMap<>();
        HttpSession session = request.getSession();
        UserDto userDto = (UserDto) session.getAttribute(CommonConstants.LOGIN_MEMBER);
        String userId = userDto.getUserId();

        memoDto.setCreateUserId(userId);
        memoDto.setModifyUserId(userId);

        boolean insertResult = memoService.insertMemo(memoDto);
        log.info("메모 작성 결과: {}", insertResult);

        resultMap.put("success", insertResult);
        return ResponseEntity.ok(resultMap);
    }


    @PostMapping("/memoUpdate.do")
    public ResponseEntity<?> memoUpdate(@RequestBody MemoDto memoDto, HttpServletRequest request, HttpServletResponse response) {

        log.info("수정하려는 메모의 MEMO_SEQ: {}", memoDto.getMemoSeq());

        Map<String, Object> resultMap = new HashMap<>();
        HttpSession session = request.getSession();
        UserDto userDto = (UserDto) session.getAttribute(CommonConstants.LOGIN_MEMBER);
        String userId = userDto.getUserId();

        memoDto.setModifyUserId(userId);
        boolean updateResult = memoService.updateMemeo(memoDto);
        log.info("메모 수정 결과: {}", updateResult);

        MemoDto updateMemoDto = new MemoDto();
        updateMemoDto = memoService.getMemoDetail(memoDto);

        resultMap.put("result", updateResult ? "success" : "fail");
        resultMap.put("memo", updateMemoDto);
        return ResponseEntity.ok(resultMap);
    }


    @DeleteMapping("/memoDelete.do")
    public ResponseEntity<?> memoDelete(@RequestBody MemoDto memoDto, HttpServletRequest request, HttpServletResponse response) {

        log.info("삭제하려면 메모의 MEMO_SEQ: {}", memoDto.getMemoSeq());

        Map<String, Object> resultMap = new HashMap<>();
        HttpSession session = request.getSession();
        UserDto userDto = (UserDto) session.getAttribute(CommonConstants.LOGIN_MEMBER);
        String userId = userDto.getUserId();

        memoDto.setModifyUserId(userId);
        memoDto.setStatusKind("40");
        boolean deleteResult = memoService.deleteMemo(memoDto);
        log.info("메모 삭제 결과: {}", deleteResult);

        resultMap.put("result", deleteResult ? "success" : "fail");
        return ResponseEntity.ok(resultMap);
    }

    @PostMapping("/memoDetail.do")
    public ResponseEntity<?> memoDetail(@RequestBody MemoDto memoDto, HttpServletRequest request) {

        Map<String, Object> resultMap = new HashMap<>();
        HttpSession session = request.getSession();
        UserDto userDto = (UserDto) session.getAttribute(CommonConstants.LOGIN_MEMBER);
        String userId = userDto.getUserId();

        memoDto.setModifyUserId(userId);
        MemoDto memo = memoService.getMemoDetail(memoDto);

        if (memo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Memo not found");
        }

        resultMap.put("memo", memo);
        return ResponseEntity.ok(resultMap);
    }

}
