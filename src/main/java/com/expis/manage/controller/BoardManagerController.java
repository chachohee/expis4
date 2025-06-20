package com.expis.manage.controller;

import com.expis.common.CommonConstants;
import com.expis.common.config.datasource.DataSourceContextHolder;
import com.expis.manage.dto.BoardManagerViewDto;
import com.expis.manage.facade.BoardManagerFacade;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/manage")
public class BoardManagerController {

    private final BoardManagerFacade boardManagerFacade;

    /**
     * 관리자 게시판 목록
     */
    @GetMapping("/allBoard.do")
    public String allBoard(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto, Model model) {

        DataSourceContextHolder.setDataSource(bizCode);
        BoardManagerViewDto boardManagerData = boardManagerFacade.getBoardAllBoard(boardManagerViewDto);

        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaBoard");
        model.addAttribute("secondId", "all");
        model.addAttribute("boardManagerData", boardManagerData);
        return "manage/manageHome";
    }

    /**
     * 관리자 게시판 수정
     */
    @PostMapping("/boardMUpdate.do")
    public String boardMUpdate(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto,
                               HttpSession session, RedirectAttributes redirectAttributes) {

        DataSourceContextHolder.setDataSource(bizCode);
        boardManagerFacade.getBoardMUpdate(boardManagerViewDto, session);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        return "redirect:/EXPIS/{bizCode}/manage/allBoard.do";
    }

    /**
     * 관리자 게시판 등록
     */
    @PostMapping("/boardMInsert.do")
    public String boardMInsert(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto,
                               HttpSession session, RedirectAttributes redirectAttributes) {

        DataSourceContextHolder.setDataSource(bizCode);
        boardManagerFacade.getBoardMInsert(boardManagerViewDto, session);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        return "redirect:/EXPIS/{bizCode}/manage/allBoard.do";

    }

    /**
     * 관리자 게시판 삭제
     */
    @GetMapping("/boardDelete.do")
    public String boardMDelete(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto,
                               HttpSession session, RedirectAttributes redirectAttributes) {

        DataSourceContextHolder.setDataSource(bizCode);
        boardManagerFacade.getBoardMDelete(boardManagerViewDto, session);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        return "redirect:/EXPIS/{bizCode}/manage/allBoard.do";
    }

    /**
     * 활성화 게시판 목록
     */
    @GetMapping("/board.do")
    public String board(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto, Model model) {

        DataSourceContextHolder.setDataSource(bizCode);
        BoardManagerViewDto boardManagerData = boardManagerFacade.getBoard(boardManagerViewDto);

        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);;
        model.addAttribute("firstId", "areaBoard");
        model.addAttribute("secondId", "active");
        model.addAttribute("boardManagerData", boardManagerData);

        return "manage/manageHome";
    }

    /**
     * 활성화 게시판 순서 이동
     */
    @GetMapping("/indexUpdate.do")
    public String indexUpdate(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto,
                              HttpSession session, RedirectAttributes redirectAttributes) {

        DataSourceContextHolder.setDataSource(bizCode);
        boardManagerFacade.getIndexUpdate(boardManagerViewDto, session);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        return "redirect:/EXPIS/{bizCode}/manage/board.do";
    }

    /**
     * 게시판 상세 목록
     */
    @GetMapping("/activateBoard.do")
    public String activateList(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto,
                               HttpSession session, Model model) {

        DataSourceContextHolder.setDataSource(bizCode);
        BoardManagerViewDto boardManagerData = boardManagerFacade.getActivateBoard(boardManagerViewDto, session);

        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);;
        model.addAttribute("firstId", "areaBoard");
        model.addAttribute("secondId", "activeBoard");
        model.addAttribute("boardManagerData", boardManagerData);

        return "manage/manageHome";
    }

    /**
     * 게시판 상세 게시글 목록
     */
    @GetMapping("/activateBoardDetail.do")
    public String activateBoardDetail(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto,
                                      HttpSession session, Model model) {

        DataSourceContextHolder.setDataSource(bizCode);
        BoardManagerViewDto boardManagerData = boardManagerFacade.getActivateBoardDetail(boardManagerViewDto, session);

        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaBoard");
        model.addAttribute("secondId", "activeBoardDetail");
        model.addAttribute("boardManagerData", boardManagerData);

        return "manage/manageHome";
    }

    /**
     * 게시판 상세 삭제
     */
    @GetMapping("/activateBoardDetailDelete.do")
    public String boardDetailMDelete(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto,
                                     RedirectAttributes redirectAttributes, HttpSession session) {

        DataSourceContextHolder.setDataSource(bizCode);
        BoardManagerViewDto boardManagerData = boardManagerFacade.getBoardDetailMDelete(boardManagerViewDto, session);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        redirectAttributes.addAttribute("boardMSeq", boardManagerData.getBoardDSeq());

        return "redirect:/EXPIS/{bizCode}/manage/activateBoard.do";
    }

    /**
     * 관리자 관련 사이트 목록
     */
    @GetMapping("/relatedSites.do")
    public String relatedMSites(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto, Model model) {

        DataSourceContextHolder.setDataSource(bizCode);
        BoardManagerViewDto boardManagerData = boardManagerFacade.getRelatedMSites(boardManagerViewDto);

        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaBoard");
        model.addAttribute("secondId", "related");
        model.addAttribute("boardManagerData", boardManagerData);

        return "manage/manageHome";
    }

    /**
     * 관리자 관련 사이트 등록
     */
    @PostMapping("/relatedInsert.do")
    public String relatedMInsert(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto, RedirectAttributes redirectAttributes,
                                 HttpSession session, MultipartHttpServletRequest mult) {

        DataSourceContextHolder.setDataSource(bizCode);
        boardManagerFacade.getRelatedMInsert(boardManagerViewDto, session, mult);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        return "redirect:/EXPIS/{bizCode}/manage/relatedSites.do";
    }

    /**
     * 관리자 관련 사이트 수정
     */
    @PostMapping("/relatedUpdate.do")
    public String relatedMUpdate(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto,
                                 RedirectAttributes redirectAttributes, HttpSession session,
                                 MultipartHttpServletRequest mult) {

        DataSourceContextHolder.setDataSource(bizCode);
        boardManagerFacade.getRelatedMUpdate(boardManagerViewDto, session, mult);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        return "redirect:/EXPIS/{bizCode}/manage/relatedSites.do";
    }

    /**
     * 관리자 관련 사이트 삭제
     */
    @GetMapping("/relatedDelete.do")
    @ResponseBody
    public ResponseEntity<?> relatedMDelete(@PathVariable String bizCode, @ModelAttribute BoardManagerViewDto boardManagerViewDto, HttpSession session) {

        DataSourceContextHolder.setDataSource(bizCode);
        try {
            int boardManagerData = boardManagerFacade.getRelatedMDelete(boardManagerViewDto, session);

            if (boardManagerData == 1) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "게시글이 정상적으로 삭제 되었습니다."
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "status", "error"
                ));
            }

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "삭제 중 오류 발생: " + e.getMessage()
            ));
        }
    }
}
