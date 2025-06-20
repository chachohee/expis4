package com.expis;

import com.expis.common.CommonConstants;
import com.expis.user.dto.UserDto;
import com.expis.login.interceptor.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
//    private final LoginMemberRepository memberRepository;

    @Value("${app.expis.bizCode}")
    private String bizCode;
    @Value("${app.expis.introPage}")
    private String introPage;

    @GetMapping("/")
    public String homeLogin(@Login UserDto userDto, Model model) {
        //세션 회원 데이터가 없으면 home :
        if (userDto == null) {
            model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
            return "redirect:/EXPIS/"+bizCode+"/login";
        }

        //Define
        String cmntIntro = "cmnt/cmntIntro.do";
        String adminIntro = "manage/approvalManage.do";
        String ietmIntro = "ietm/toMain.do";

        // T50/FA50, 관리자 계정(슈퍼 관리자)로그인 시 커뮤니티 사이트로 이동
        if( (bizCode.equalsIgnoreCase("T50") || bizCode.equalsIgnoreCase("FA50") || bizCode.equalsIgnoreCase("KT1"))
                && userDto.getUserRoleCode().equalsIgnoreCase("S")) {
            return "redirect:/EXPIS/"+bizCode+"/"+cmntIntro;
        }

        // 통합관리자(S), 운용관리자(A) 일 경우 관리자 페이지로 이동
        if(userDto.getUserRoleCode().equalsIgnoreCase("S") || userDto.getUserRoleCode().equalsIgnoreCase("A")) {
            return "redirect:/EXPIS/"+bizCode+"/"+adminIntro;
        }

        // CBT사용자(D) 경우 IETM 화면으로 이동
        if(userDto.getUserRoleCode().equalsIgnoreCase("D")) {
            return "redirect:/EXPIS/"+bizCode+"/"+ietmIntro;
        }

//        log.info("bizCode={}", bizCode);
        // intro 페이지 옵션 적용
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        if ("CMNT".equalsIgnoreCase(introPage)) {
            return "redirect:/EXPIS/"+bizCode+"/"+cmntIntro;
        }
        return "redirect:/EXPIS/"+bizCode+"/"+ietmIntro;
    }

    /**
     * Redirect eXPIS-III
     */
    //FA-50
    @GetMapping("/ExpisWeb/intro.do")
    public String redictFA50(@Login UserDto loginUser, Model model) {
        String rdBizCode = "FA50";
        if (loginUser == null) {
            model.addAttribute(CommonConstants.BIZ_CODE, rdBizCode);
            return "redirect:/EXPIS/"+rdBizCode+"/login";
        }
        return "redirect:/EXPIS/"+rdBizCode+"/ietm";
    }

    //T-50
    @GetMapping("/ExpisWebT50/intro.do")
    public String redictT50(@Login UserDto loginUser, Model model) {
        String rdBizCode = "T50";
        if (loginUser == null) {
            model.addAttribute(CommonConstants.BIZ_CODE, rdBizCode);
            return "redirect:/EXPIS/"+rdBizCode+"/login";
        }
        return "redirect:/EXPIS/"+rdBizCode+"/ietm";
    }

    //KA1
    @GetMapping("/ExpisWebKA1/intro.do")
    public String redictKA50(@Login UserDto loginUser, Model model) {
        String rdBizCode = "KA1";
        if (loginUser == null) {
            model.addAttribute(CommonConstants.BIZ_CODE, rdBizCode);
            return "redirect:/EXPIS/"+rdBizCode+"/login";
        }
        return "redirect:/EXPIS/"+rdBizCode+"/ietm";
    }

    //KT1
    @GetMapping("/ExpisWebKT1/intro.do")
    public String redictKT1(@Login UserDto loginUser, Model model) {
        String rdBizCode = "KT1";
        if (loginUser == null) {
            model.addAttribute(CommonConstants.BIZ_CODE, rdBizCode);
            return "redirect:/EXPIS/"+rdBizCode+"/login";
        }
        return "redirect:/EXPIS/"+rdBizCode+"/ietm";
    }

    //KT100
    @GetMapping("/ExpisWebKT100/intro.do")
    public String redictKT100(@Login UserDto loginUser, Model model) {
        String rdBizCode = "KT100";
        if (loginUser == null) {
            model.addAttribute(CommonConstants.BIZ_CODE, rdBizCode);
            return "redirect:/EXPIS/"+rdBizCode+"/login";
        }
        return "redirect:/EXPIS/"+rdBizCode+"/ietm";
    }

}
