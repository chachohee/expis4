package com.expis.manage.controller;

import com.expis.common.CommonConstants;
import com.expis.login.interceptor.Login;
import com.expis.manage.dto.AdminLogDto;
import com.expis.manage.facade.LogManagerFacade;
import com.expis.manage.service.LogManagerService;
import com.expis.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 2024.12.09 - 관리자메뉴 - 로그관리 서비스 - jingi.kim
 * 변경이력 :
 *
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/manage")
public class LogManagerController {

    private final LogManagerService logManagerService;
    private final LogManagerFacade logManagerFacade;

    //메뉴: 로그관리 - 로그보기
    @GetMapping("/logInfo.do")
    public String logView(@PathVariable String bizCode,
                          @Login UserDto userDto,
                          Model model
    ) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaLog");
        model.addAttribute("secondId", "view");
        model.addAttribute("userDto", userDto);

        return "manage/manageHome";
    }

    @GetMapping("/tools/logInfo.do")
    public ResponseEntity<?> logInfo(@PathVariable String bizCode,
                                     @RequestParam(value = "nowPage", defaultValue = "1") int nowPage,
                                     @RequestParam(value = "startDate", defaultValue = "none") String startDate,
                                     @RequestParam(value = "endDate", defaultValue = "none") String endDate
    ) {
        AdminLogDto adminLogDto = new AdminLogDto();
        adminLogDto.setStartDate(startDate);
        adminLogDto.setEndDate(endDate);
//        adminLogDto.setDbType(dbType);
        adminLogDto.setNowPage(nowPage);

        Map<String, Object> responseMap = logManagerFacade.getLogData(adminLogDto, "info");

        return ResponseEntity.ok(responseMap);
    }

    //메뉴: 로그관리 - 로그코드
    @GetMapping("/logCode.do")
    public String logCode(@PathVariable String bizCode,
                          @Login UserDto userDto,
                          Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaLog");
        model.addAttribute("secondId", "code");
        model.addAttribute("userDto", userDto);

        return "manage/manageHome";
    }

    @GetMapping("/tools/logCode.do")
    public ResponseEntity<?> logCode(@PathVariable String bizCode,
                                     @RequestParam(value = "nowPage", defaultValue = "1") int nowPage,
                                     @RequestParam(value = "startDate", defaultValue = "none") String startDate,
                                     @RequestParam(value = "endDate", defaultValue = "none") String endDate
    ) {
        AdminLogDto adminLogDto = new AdminLogDto();
        adminLogDto.setStartDate(startDate);
        adminLogDto.setEndDate(endDate);
//        adminLogDto.setDbType(dbType);
        adminLogDto.setNowPage(nowPage);

        Map<String, Object> responseMap = logManagerFacade.getLogData(adminLogDto, "code");

        return ResponseEntity.ok(responseMap);
    }

}
