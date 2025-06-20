package com.expis.ietm.controller;

import com.expis.common.CommonConstants;
import com.expis.common.variable.VariableAspect;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/ietm")
public class AirplaneController {

    @GetMapping("/airplane.do")
    public ResponseEntity<?> airplaneMain(@PathVariable String bizCode, HttpServletRequest request) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        log.info("toKey: {}", request.getParameter("toKey"));
        log.info("tocoId: {}", request.getParameter("tocoId"));

        request.setAttribute("toKey", request.getParameter("toKey"));
        request.setAttribute("tocoId", request.getParameter("tocoId"));
        VariableAspect.setting(request);

        resultMap.put(CommonConstants.BIZ_CODE, bizCode);
        return ResponseEntity.ok(resultMap);
    }

    @GetMapping("/airplaneTypeCheck.do")
    public String airplaneTypeCheck(@PathVariable String bizCode,
                               Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);

        return "ietm/ietmHome";
    }

}
