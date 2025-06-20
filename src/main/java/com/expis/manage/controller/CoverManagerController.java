package com.expis.manage.controller;

import com.expis.common.CommonConstants;
import com.expis.login.interceptor.Login;
import com.expis.manage.dto.CoverManageDto;
import com.expis.manage.service.SystemOptionService;
import com.expis.manage.service.SystemService;
import com.expis.user.dto.UserDto;
import com.expis.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * 2024.12.09 - 관리자메뉴 - 게시판관리 서비스 - jingi.kim
 * 변경이력 :
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/manage")
public class CoverManagerController {

    private final SystemOptionService systemOptionService;

    //메뉴: 표지관리 - 표지관리
    @GetMapping("/coverManage.do")
    public String coverManage(@PathVariable String bizCode,
                              @Login UserDto userDto,
                              Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaCover");
        model.addAttribute("secondId", "cover");
        model.addAttribute("userDto", userDto);

        return "manage/manageHome";
    }

    /**
     * cover 정보 조회
     * 2025.05.09 - osm
     * - 조회 값 : toKey, 최초 발간일, 최종 수정일, 수정 넘버
     */
    @PostMapping("/coverInfo.do")
    public ResponseEntity<?> toCoverAjax(@PathVariable String bizCode,
                                         @Login UserDto userDto,
                                         HttpServletRequest request
    ) {
        Map<String, Object> responseMap = new HashMap<>();
        String toKey = StringUtil.checkNull(request.getParameter("toKey"));

        try {
            CoverManageDto coverManageDto = systemOptionService.getCoverCont(toKey);

            // cover 정보 없을 시 저장할 수 있도록 수정 - 2025.05.27 osm
            if (coverManageDto == null) {
                coverManageDto = new CoverManageDto();
                coverManageDto.setToKey(toKey);
                coverManageDto.setCreateUserId(userDto.getUserId());

                systemOptionService.insertCoverCont(coverManageDto);
                coverManageDto = systemOptionService.getCoverCont(toKey);
            }

            String coverDate = systemOptionService.getCoverDate(toKey);
            String coverVerDate = systemOptionService.getCoverVerDate(toKey);
            String coverChgNo = systemOptionService.getCoverChgNo(toKey);

            if (coverDate != null) coverManageDto.setCoverDate(coverDate);
            if (coverVerDate != null) coverManageDto.setCoverVerDate(coverVerDate);
            if (coverChgNo != null) coverManageDto.setCoverChgNo(coverChgNo);

            responseMap.put("coverManageDto", coverManageDto);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return ResponseEntity.ok(responseMap);
    }
}
