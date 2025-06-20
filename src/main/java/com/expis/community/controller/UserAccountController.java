package com.expis.community.controller;

import com.expis.common.CommonConstants;
import com.expis.common.config.datasource.DataSourceContextHolder;
import com.expis.community.dto.CmntViewDTO;
//import com.expis.community.dto.ValidationDTO;
//import com.expis.community.dto.ValidationRequestDTO;
import com.expis.community.facade.UserAccountFacade;
//import com.expis.community.service.UserAccountService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/EXPIS/{bizCode}/cmnt")
@RequiredArgsConstructor
public class UserAccountController {
    private final UserAccountFacade userAccountFacade;
//    private final UserAccountService userAccountService;

    /**
     * 개인정보수정 화면
     */
    @GetMapping("/userAccount.do")
    public String userAccount(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO
            , HttpSession session, Model model)  {

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = userAccountFacade.getUserAccount(cmntViewDTO, session);

        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "cmntAccount");
        model.addAttribute("secondId", "userAccount");
        model.addAttribute("cmnt", cmntViewData);

        return "community/cmntHome";
    }

//    /**
//     * 개인정보수정 validation 검증
//     */
//    @PostMapping("/validate.do")
//    public ResponseEntity<ValidationDTO> validateField(@RequestBody ValidationRequestDTO validationRequestDTO){
//        switch (validationRequestDTO.getName()){
//            case "email":
//                return ResponseEntity.ok(userAccountService.validateName(validationRequestDTO.getName()));
//            case "address":
//                return ResponseEntity.ok(validateAddress(validationRequestDTO.getAddress()));
//        }
//    }



}
