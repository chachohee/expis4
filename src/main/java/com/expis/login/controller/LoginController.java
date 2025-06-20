package com.expis.login.controller;

import com.expis.common.CommonConstants;
import com.expis.common.config.datasource.DataSourceContextHolder;
import com.expis.common.controller.RSAController;
import com.expis.community.dto.FileMasterDTO;
import com.expis.community.service.FileMasterService;
import com.expis.ietm.dto.OptionDto;
import com.expis.ietm.service.OptionService;
import com.expis.login.facade.LoginFacade;
import com.expis.manage.dao.AdminGlossaryMapper;
import com.expis.manage.dto.AdminLogDto;
import com.expis.manage.dto.SystemOptionDto;
import com.expis.manage.service.SystemOptionService;
import com.expis.user.dto.UserDto;
import com.expis.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.crypto.BadPaddingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}")
public class LoginController {
    private final RSAController rsaController;
    private final UserService userService;
    private final MessageSource messageSource;
    private final OptionService optionService;
    private final SystemOptionService systemOptionService;
    private final FileMasterService fileMasterService;
    private final AdminGlossaryMapper glossaryMapper;
    private final LoginFacade loginFacade;

    @Value("${app.expis.language}")
    private String lang;

    @GetMapping("/login")
    public String loginForm(@PathVariable String bizCode,
                            Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        String publicKey = rsaController.getPublicKey();
        model.addAttribute("publicKey", publicKey);
        return "login/login" + bizCode;
    }

    /**
     * 로그인 이후 리다이렉트 처리
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @PathVariable String bizCode,
            HttpServletRequest request,
            Locale locale,
            @RequestBody String postData ) {

//        log.info("login ------- start.");
//        log.info("postData={}", postData);

        Map<String, String> responseMap = new HashMap<>();

        // 데이터 소스 선택
        DataSourceContextHolder.setDataSource(bizCode);

        UserDto findUserDto = loginFacade.authenticate(postData, request, locale);

        // 세션 고정 공격 방지를 위한 세션 재생성
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        session = request.getSession(true);
        session.setAttribute(CommonConstants.LOGIN_MEMBER, findUserDto); // 세션에 로그인한 멤버 저장
        session.setAttribute(CommonConstants.BIZ_CODE, bizCode);
        session.setAttribute(CommonConstants.MAIN_DOOR, "Y");

        if(request.getParameterValues("airType") != null) {
            String[] airTypeArrayStr = request.getParameterValues("airType");
            for(int i=0;i<airTypeArrayStr.length;i++) {
                log.info("airTypeArrayStr[{}] : {}", i, airTypeArrayStr[i]);
                session.setAttribute(CommonConstants.VEHICLE_TYPE, airTypeArrayStr[i]);
            }
        }

        // 비밀번호 변경일자 계산
        long dateDiff = loginFacade.calcPasswordExpired(findUserDto.getPwChangeDate());
        if(dateDiff > 30) {
            session.setAttribute(CommonConstants.PASSWORD_FLAG, true);
        } else {
            session.setAttribute(CommonConstants.PASSWORD_FLAG, false);
        }
        if (dateDiff < 0) {
            session.setAttribute(CommonConstants.DATE_DIFF, 100);
        } else {
            session.setAttribute(CommonConstants.DATE_DIFF, dateDiff);
        }

        // 시스템 옵션 정보
        SystemOptionDto optionDto = systemOptionService.selectOptionAll();
        session.setAttribute(CommonConstants.SYSTEM_OPTIONS, optionDto);

        // 사용자 옵션 정보
        OptionDto optDto = new OptionDto();
        optDto.setUserId(findUserDto.getUserId());
        OptionDto userOptionDto = optionService.selectUserFontSize(optDto);
        session.setAttribute(CommonConstants.USER_SETTINGS, userOptionDto);

        // 환경설정의 언어 값을 적용
        setLanguageToSession(session);

        // 로그인 성공 히스토리 추가
        userService.saveLoginConnHistory(findUserDto, request);

        // 로그인 성공인 경우 접속 실패 데이터 리셋
        userService.updateFailConnHistoryStatusByUserId(findUserDto, request);

        //insert log
        insertLog(findUserDto.getUserId());

        //TODO: TO Tree Dom
        //20190704 add 최초 교범 목차 트리를 Dom 형태로 전역변수 생성
        //20190917 edit LYM 최초 로그인 시에만 적용되도록 수정
        //20191017 edit LYM Dom 생성했는지 구분 변수(isMemoryDom)로 검사
        //if (treeController.arrToTreeDom.length > 0 && treeController.arrToTreeDom[0] != null) {
//                if (treeController.isMemoryDom == true) {
//                    //logger.info("Create To Tree Dom : login : (A) Already have file..!!");
//                    System.out.println("Create To Tree Dom : login : (A) Already have file..!!");
//                } else {
//                    boolean rtBl = treeController.setToTreeDom();
//                    if (rtBl == true) {
//                        //logger.info("Create To Tree Dom : login : (S) Success..!!");
//                        System.out.println("Create To Tree Dom : login : (S) Success..!!");
//                    } else {
//                        //logger.info("Create To Tree Dom : login : (F) Fail..!!");
//                        System.out.println("Create To Tree Dom : login : (F) Fail..!!");
//                    }
//                }


        // 로그인 성공 시 JSON 응답
        responseMap.put("status", "success");
        responseMap.put(CommonConstants.BIZ_CODE, bizCode);
        responseMap.put("userId", findUserDto.getUserId());
        responseMap.put("userName", findUserDto.getUserName());
        responseMap.put("role", findUserDto.getUserRoleCode());

        return ResponseEntity.ok(responseMap);
    }

    // BadPaddingException
    @ExceptionHandler(BadPaddingException.class)
    public ResponseEntity<?> handleBadPaddingException(Exception ex) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("status", "fail");
        responseMap.put("message", messageSource.getMessage("login.error.global", null, Locale.KOREA));
        return ResponseEntity.ok(responseMap);
    }

    private void insertLog(String decryptedLoginId) {
        AdminLogDto logDto =  new AdminLogDto();
        logDto.setCreateUserId(decryptedLoginId);
        logDto.setCodeType("1101");
        glossaryMapper.insertLog(logDto);
    }

    // 환경설정의 언어 값을 적용
    private void setLanguageToSession(HttpSession session) {
        // 최초 로그인언어 설정 처리
        String cfgLocale = lang;
        log.info("User Login locale : {}", cfgLocale);
        if(cfgLocale == null || cfgLocale.isEmpty()) {
            cfgLocale = "ko";
        }
        Locale locales = null;
        if (cfgLocale.matches("ko")) {
            locales = Locale.KOREAN;
        } else {
            locales = Locale.ENGLISH;

        }
        session.setAttribute("lang", cfgLocale);

        // 세션에 존재하는 Locale을 새로운 언어로 변경해준다.
        session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locales);
        session.setAttribute("lang", cfgLocale);
    }

    private void setSessionInformation(HttpServletRequest request, HttpSession session, UserDto userDto, SystemOptionDto optionDto) {
        session.setAttribute("SS_USER_ID", userDto.getUserId());
        session.setAttribute("SS_USER_PW", userDto.getUserPw());
        session.setAttribute("SS_USER_NAME", userDto.getUserName());
        session.setAttribute("SS_LEFTLHS", "none");
        session.setAttribute("SS_MAIN_DOOR", "Y");
        session.setAttribute("SS_PRINT_ROLE_CODE", userDto.getPrintRoleCode());
        session.setAttribute("userDetails", userDto);
        session.setAttribute("userRoleCode", userDto.getAuthority());

        if(userDto.getPwChangeDate() != null) {
            try {
                SimpleDateFormat sdf	= new SimpleDateFormat("yyyy-MM-dd");
                Date pwChangeDat		= sdf.parse(userDto.getPwChangeDate());
                Date nowDate			= new Date();
                long dateDiff 			= ((nowDate.getTime() - pwChangeDat.getTime()) /60000)/(24*60);
                log.info("userDetails pwChangeDat:{}, nowDate:{}, dateDiff:{}",pwChangeDat, nowDate, dateDiff);
                session.setAttribute("dateDiff", dateDiff);
                if(dateDiff > 30) {
                    session.setAttribute("passwdFlag", true);
                }else {
                    session.setAttribute("passwdFlag", false);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            session.setAttribute("dateDiff", 100);
            session.setAttribute("passwdFlag", true);
        }

        if(request.getParameterValues("airType") != null) {
            String[] airTypeArrayStr = request.getParameterValues("airType");
            for(int i=0;i<airTypeArrayStr.length;i++) {
                log.info("airTypeArrayStr[{}] : {}", i, airTypeArrayStr[i]);
                session.setAttribute("SS_VEHICLE_TYPE", airTypeArrayStr[i]);
            }
        }

        FileMasterDTO fileMasterDto = new FileMasterDTO();
        FileMasterDTO fileMaster = new FileMasterDTO();

        try {
            //TODO: FileMaster
//            fileMasterDto = fileMasterService.getMainImg(systemOptionDTO.getOptIetmImg());
//            fileMaster = fileMasterService.getMainImg(systemOptionDTO.getOptCmntImg());
        }catch (Exception e) {
            log.info("fileMaster Check Error  : {}", e.getMessage());
        }
        log.info("systemOptionDTO : {}", optionDto);
        log.info("systemOptionDTO.getOptAirplane() : {}", optionDto.getOptAirplane());
        log.info("systemOptionDTO.getOptCtrlKind() : {}", optionDto.getOptCtrlKind());
        //??session.setAttribute("dbType", dbType);
        session.setAttribute("optAlert", optionDto.getOptAlert());
        session.setAttribute("VERSION", optionDto.getOptVersion());
        session.setAttribute("ipbType", optionDto.getOptIpb());
        session.setAttribute("ctrlKind", optionDto.getOptCtrlKind());
        session.setAttribute("toOutput", optionDto.getOptToOutput());
        session.setAttribute("optFontResize", optionDto.getOptFontResize());
        session.setAttribute("SS_OPT_COVER_TYPE", optionDto.getOptCover());
        session.setAttribute("SS_OPT_REMO", optionDto.getOptRemo());
        session.setAttribute("SS_OPT_AIRPLANE", optionDto.getOptAirplane());
        session.setAttribute("SS_OPT_UNIT", optionDto.getOptUnit());
        session.setAttribute("SS_OPT_PRINT", optionDto.getOptPrint());
        session.setAttribute("SS_OPT_CMNT_TITLE", optionDto.getOptCmntTitle());
        session.setAttribute("SS_OPT_CMNT_SUBTITLE", optionDto.getOptCmntSubTitle());

        if(fileMasterDto != null) {
            session.setAttribute("SS_FILE_STRNM", fileMasterDto.getFileStrNm());
            session.setAttribute("SS_FILE_TYPE", fileMasterDto.getFileType());
        }
        if(fileMaster != null) {
            session.setAttribute("SS_FILE_CMNT_STRNM", fileMaster.getFileStrNm());
            session.setAttribute("SS_FILE_CMNT_TYPE", fileMaster.getFileType());
        }

    }


    /**
     * 로그아웃 기능
     */
    @GetMapping("/logout")
    public String logout(@PathVariable String bizCode, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            //loginService.updateDisconnDate(session.getId()); // dissConnDate 업뎃
            session.invalidate(); // 세션 무효화
            log.info("User logged out successfully.");
        }
        return "redirect:/login/" + bizCode; // 로그아웃 후 로그인 페이지로 리다이렉트
    }
}
