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
 * 2024.12.09 - 관리자메뉴 - 계정관리 서비스 - jingi.kim
 * 변경이력 :
 *
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/manage")
public class AccountManagerController {

    //메뉴: 계정관리 - 승인관리
    @GetMapping("/approvalManage.do")
    public String account(@PathVariable String bizCode,
                          @Login UserDto userDto,
                          Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaAccount");
        model.addAttribute("secondId", "account");
        model.addAttribute("userDto", userDto);

        model.addAttribute("userCount", "1");

        return "manage/manageHome";
    }

    //메뉴: 계정관리 - 사용자관리
    @GetMapping("/user.do")
    public String userManage(@PathVariable String bizCode,
                             @Login UserDto userDto,
                             Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaAccount");
        model.addAttribute("secondId", "user");
        model.addAttribute("userDto", userDto);

        model.addAttribute("userCount", "1");

        return "manage/manageHome";
    }

    //메뉴: 계정관리 - 미접속자관리
    @GetMapping("/notConnManage.do")
    public String nonUser(@PathVariable String bizCode,
                          @Login UserDto userDto,
                          Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaAccount");
        model.addAttribute("secondId", "nonuser");
        model.addAttribute("userDto", userDto);

        model.addAttribute("userCount", "1");

        return "manage/manageHome";
    }

    //메뉴: 계정관리 - 비활성화사용자관리
    @GetMapping("/lockedManage.do")
    public String locked(@PathVariable String bizCode,
                         @Login UserDto userDto,
                         Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaAccount");
        model.addAttribute("secondId", "locked");
        model.addAttribute("userDto", userDto);

        model.addAttribute("userCount", "1");

        return "manage/manageHome";
    }

    //메뉴: 계정관리 - 사용자이력관리
    @GetMapping("/approvalHistory.do")
    public String history(@PathVariable String bizCode,
                          @Login UserDto userDto,
                          Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaAccount");
        model.addAttribute("secondId", "history");
        model.addAttribute("userDto", userDto);

        model.addAttribute("userCount", "1");

        return "manage/manageHome";
    }

    //메뉴: 계정관리 - 비밀번호관리
    @GetMapping("/passwordManage.do")
    public String password(@PathVariable String bizCode,
                           @Login UserDto userDto,
                           Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaAccount");
        model.addAttribute("secondId", "password");
        model.addAttribute("userDto", userDto);

        return "manage/manageHome";
    }

}
