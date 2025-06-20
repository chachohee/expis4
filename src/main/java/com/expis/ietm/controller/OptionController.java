package com.expis.ietm.controller;

import com.expis.common.CommonConstants;
import com.expis.ietm.dto.OptionDto;
import com.expis.ietm.service.OptionService;
import com.expis.login.interceptor.Login;
import com.expis.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/ietm")
public class OptionController {

    private final OptionService optionService;

    @GetMapping("/optionMain.do")
    public ResponseEntity<?> optionMain(HttpServletRequest request, @Login UserDto userDto) {

        Map<String, String> response = new HashMap<>();

        HttpSession session = request.getSession();
        String userId = userDto.getUserId();

        OptionDto optionDto;
        if ( session.getAttribute(CommonConstants.USER_SETTINGS) != null ) {
            optionDto = (OptionDto) session.getAttribute(CommonConstants.USER_SETTINGS);
        } else {
            optionDto = optionService.selectOption(userId);
            session.setAttribute(CommonConstants.USER_SETTINGS, optionDto);
        }
        log.info("{} 옵션: {}", userId, optionDto);

        String exploreMode = optionDto.getExploreMode();
        String outputMode = optionDto.getOutputMode();
        String fiMode = optionDto.getFiMode();
        String viewMode = optionDto.getViewMode();
        String fontSize = optionDto.getFontSize();
        String fontFamily = optionDto.getFontFamily();

        response.put("exploreMode", exploreMode);
        response.put("outputMode", outputMode);
        response.put("fiMode", fiMode);
        response.put("viewMode", viewMode);
        response.put("fontSize", fontSize);
        response.put("fontFamily", fontFamily);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/optionUpdate.do")
    public ResponseEntity<?> optionUpdate(HttpServletRequest request, @Login UserDto userDto, @RequestBody OptionDto optionDto) {

        Map<String, String> response = new HashMap<>();

        String exploreMode = optionDto.getExploreMode();
        String outputMode = optionDto.getOutputMode();
        String fiMode = optionDto.getFiMode();
        String viewMode = optionDto.getViewMode();
        String fontSize = optionDto.getFontSize();
        String fontFamily = optionDto.getFontFamily();

        log.info("TO 탐색 모드: {}", exploreMode);
        log.info("TO 탐색 범위: {}", outputMode);
        log.info("F.I 보기 유형: {}", fiMode);
        log.info("내용 보기 유형: {}", viewMode);
        log.info("글꼴 크기: {}", fontSize);
        log.info("글꼴 조정: {}", fontFamily);

        HttpSession session = request.getSession();
        String userId = userDto.getUserId();
        log.info("userId {}", userId);
        optionDto.setUserId(userId);

        optionService.insertOption(optionDto);
        session.setAttribute(CommonConstants.USER_SETTINGS, optionDto);

        //TODO: language
        response.put("message", "설정이 적용되었습니다.");
        response.put("exploreMode", exploreMode);
        response.put("outputMode", outputMode);
        response.put("fiMode", fiMode);
        response.put("viewMode", viewMode);
        response.put("fontSize", fontSize);
        response.put("fontFamily", fontFamily);

        return ResponseEntity.ok(response);
    }
}
