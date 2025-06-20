package com.expis.manage.controller;

import com.expis.common.CommonConstants;
import com.expis.login.interceptor.Login;
import com.expis.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 2024.12.09 - 관리자메뉴 - 이력관리 서비스 - jingi.kim
 * 변경이력 :
 *
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/manage")
public class HistoryManagerController {

    //메뉴: 이력관리 - 목차정보
    @GetMapping("/historyList.do")
    public String historyList(@PathVariable String bizCode,
                              @Login UserDto userDto,
                              Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaHistory");
        model.addAttribute("secondId", "list");
        model.addAttribute("userDto", userDto);

        return "manage/manageHome";
    }

    //메뉴: 이력관리 - 버전별정보
    @GetMapping("/historyVer.do")
    public String historyVersion(@PathVariable String bizCode,
                                 @Login UserDto userDto,
                                 Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaHistory");
        model.addAttribute("secondId", "version");
        model.addAttribute("userDto", userDto);

        return "manage/manageHome";
    }

}
