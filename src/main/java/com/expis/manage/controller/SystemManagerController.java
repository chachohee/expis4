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
 * 2024.12.09 - 관리자메뉴 - 시스템옵션 서비스 - jingi.kim
 * 변경이력 :
 *
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/manage")
public class SystemManagerController {

    //메뉴: 시스템옵션 - IETM옵션
    @GetMapping("/systemOption.do")
    public String systemOption(@PathVariable String bizCode,
                               @Login UserDto userDto,
                               Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaSystem");
        model.addAttribute("secondId", "ietm");
        model.addAttribute("userDto", userDto);

        return "manage/manageHome";
    }

    //메뉴: 시스템옵션 - 게시판옵션
    @GetMapping("/cmntOption.do")
    public String cmntOption(@PathVariable String bizCode,
                             @Login UserDto userDto,
                             Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaSystem");
        model.addAttribute("secondId", "board");
        model.addAttribute("userDto", userDto);

        return "manage/manageHome";
    }

}
