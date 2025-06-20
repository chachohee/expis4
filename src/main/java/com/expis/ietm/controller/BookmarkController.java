package com.expis.ietm.controller;

import com.expis.common.CommonConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 2024.12.19 - IETM  - 북마크 서비스 - chohee.cha
 * 변경이력 :
 *
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/ietm")
public class BookmarkController {

    //메뉴: 북마크 - 북마크 리스트
    @GetMapping("/bookmarkList.do")
    public String bookmarkList(@PathVariable String bizCode,
                               Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
//        model.addAttribute("firstId", "areaHistory");
//        model.addAttribute("secondId", "list");

        return "ietm/ietmHome";
    }

    //메뉴: 북마크 - 북마크 작성
    @PostMapping("/bookmarkInsert.do")
    public String bookmarkInsert(@PathVariable String bizCode,
                             Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);

        return "ietm/ietmHome";
    }

    //메뉴: 북마크 - 북마크 수정
    @PatchMapping("/bookmarkUpdate.do")
    public String bookmarkUpdate(@PathVariable String bizCode,
                             Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);

        return "ietm/ietmHome";
    }

    //메뉴: 북마크 - 북마크 삭제
    @DeleteMapping("/bookmarkDelete.do")
    public String bookmarkDelete(@PathVariable String bizCode,
                             Model model) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);

        return "ietm/ietmHome";
    }

}
