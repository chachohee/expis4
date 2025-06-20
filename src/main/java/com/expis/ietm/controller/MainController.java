package com.expis.ietm.controller;

import com.expis.common.CommonConstants;
import com.expis.common.file.FileMaster;
import com.expis.common.variable.VariableAspect;
import com.expis.community.dto.FileMasterDTO;
import com.expis.ietm.service.OptionService;
import com.expis.ietm.service.VersionService;
import com.expis.login.interceptor.Login;
import com.expis.manage.dto.SystemOptionDto;
import com.expis.ietm.dto.OptionDto;
import com.expis.ietm.service.MainService;
import com.expis.ietm.dto.MainDto;
import com.expis.user.dto.UserDetailInfoDto;
import com.expis.user.dto.UserDto;
import com.expis.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/ietm")
public class MainController {

    private final MainService mainService;
    private final OptionService optionService;
    private final VersionService versionService;
    private final UserService userService;

    @Value("${app.expis.ietmType}")
    private String ietmType;

    @GetMapping
    public String ietmHome(@PathVariable String bizCode, HttpServletRequest request) {
        log.info("Accessing bizCode: {}", bizCode);
        return "ietm/ietmHome";
    }

    @GetMapping("/toMain.do")
    public String ietmMain(@PathVariable String bizCode, @Login UserDto userDto, HttpServletRequest request, Model model) {

        List<MainDto> mainToList;
        List<MainDto> mainToCoList;
        List<MainDto> mainToKeyList;

        MainDto dto = new MainDto();
        OptionDto optionDto;

        try {
            HttpSession session = request.getSession();

            String mainDoor = (String) session.getAttribute(CommonConstants.MAIN_DOOR);
            session.setAttribute(CommonConstants.MAIN_DOOR, "N");

            String sessionId = request.getSession().getId();
            String userId = userDto.getUserId();

            dto.setSessionId(sessionId);
            dto.setUserId(userId);

            optionDto = (OptionDto) session.getAttribute(CommonConstants.USER_SETTINGS);
            model.addAttribute(CommonConstants.USER_SETTINGS, optionDto);

//            last verId
//            String verId = versionService.getLastVersion();
//            session.setAttribute("SS_LAST_VER_ID", verId);

            //vehicleType
            if(session.getAttribute(CommonConstants.VEHICLE_TYPE) == null) {
                session.setAttribute(CommonConstants.VEHICLE_TYPE, "A");
            }

            request.setAttribute("subContent", "main/ietm_main");


            // 최근 to,toco 가져왔음, getToKey(); , getTocoName();
            mainToList = mainService.getFinalToAjax(dto);
            mainToCoList = mainService.getFinalTocoAjax(dto);

            // toKey 중복체크
            mainToKeyList = mainService.getToKeyList(dto);

            // 최근 로그인시간, 로그아웃시간
            dto = userService.getDisConnDate(dto);
            String connDate = dto.getConnDate();
            String disConnDate = dto.getDisconnDate();

            if (connDate.equals(disConnDate)) {
                // 최초 로그인시 같은 시간이면 최종 로그인시간은 없고, 방금 로그인한 시간만 나오게 하는곳
                disConnDate = "";
                model.addAttribute("connDate", connDate);
                model.addAttribute("disConnDate", disConnDate);
            } else {
                model.addAttribute("connDate", connDate);
                model.addAttribute("disConnDate", disConnDate);
            }
            model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
            model.addAttribute("mainToList", mainToList);
            model.addAttribute("mainToCoList", mainToCoList);
            model.addAttribute("mainToKeyList", mainToKeyList);
            model.addAttribute("mainDoor", mainDoor);

            model.addAttribute(CommonConstants.GV_TYPE, ietmType);
            model.addAttribute("USER_NAME", userDto.getUserName());
            model.addAttribute("USER_ID", userDto.getUserId());

        } catch (Exception e) {
            e.printStackTrace();
            log.info("MainController.ietmMain Exception");
        }

        return "ietm/ietmHome";
    }

    /**
     * 2025.04.16 - osm
     * 이미지 다운로드 API
     */
    @GetMapping("/imageDownload.do")
    public void doDownload(@RequestParam(value = "filePath", defaultValue = "none") String filePath,
                           @PathVariable String bizCode,
                           HttpServletResponse response,
                           HttpServletRequest request) throws Exception {
        FileMaster fileMaster = new FileMaster();
        VariableAspect.setting(request);

        String fullPath = mainService.getDownloadPath(filePath);

        fileMaster.fileDownload(response, request, null, fullPath);
    }
}
