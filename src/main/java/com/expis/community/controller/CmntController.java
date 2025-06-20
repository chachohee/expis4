package com.expis.community.controller;

import com.expis.common.CommonConstants;
import com.expis.common.config.datasource.DataSourceContextHolder;
import com.expis.community.dto.CmntViewDTO;
import com.expis.community.facade.BoardFacade;
import com.expis.community.facade.RelatedSiteFacade;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
@RequestMapping("/EXPIS/{bizCode}/cmnt")
@RequiredArgsConstructor
public class CmntController {
    private final BoardFacade boardFacade;
    private final RelatedSiteFacade relatedSiteFacade;

    /**
     * Home
     */
    @GetMapping("/cmntIntro.do")
    public String cmntIntro(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                            HttpSession session, Model model) {

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = boardFacade.getBoardMainPage(cmntViewDTO.getBoardMSeq(), session);

        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "cmntIntro");
        model.addAttribute("cmnt", cmntViewData);

        return "community/cmntHome";
    }

    /**
     * Home
     */
    @GetMapping("/introList.do")
    public ResponseEntity<Map<String, Object>> introList(@PathVariable String bizCode) {

        DataSourceContextHolder.setDataSource(bizCode);
        Map<String, Object> introList = boardFacade.getIntroList();

        return ResponseEntity.ok(introList);
    }

    /**
     * 게시글 목록
     */
    @GetMapping("/boardList.do")
    public String boardList(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                            Model model, HttpSession session) {

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = boardFacade.getBoardList(cmntViewDTO, session);

        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);;
        model.addAttribute("firstId", "cmntBoard");
        model.addAttribute("secondId", "board");
        model.addAttribute("thirdId", "boardMain");
        model.addAttribute("cmnt", cmntViewData);

        return "community/cmntHome";
    }

    /**
     * 게시글 상세
     */
    @GetMapping("/boardDetail.do")
    public String boardDetail(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                              HttpSession session, Model model) {

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = boardFacade.getBoardDetail(cmntViewDTO, session);

        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "cmntBoard");
        model.addAttribute("secondId", "board");
        model.addAttribute("thirdId", "boardDetail");
        model.addAttribute("cmnt", cmntViewData);

        return "community/cmntHome";
    }

    /**
     * 게시글 등록(GET)
     */
    @GetMapping("/boardWrite.do")
    public String boardWrite(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                             HttpSession session, Model model) {

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = boardFacade.getBoardWrite(cmntViewDTO, session);

        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);;
        model.addAttribute("firstId", "cmntBoard");
        model.addAttribute("secondId", "board");
        model.addAttribute("thirdId", "boardWrite");
        model.addAttribute("cmnt", cmntViewData);

        return "community/cmntHome";
    }

    /**
     * 게시글 등록(POST)
     */
    @PostMapping("/boardInsert.do")
    public String boardInsert(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                              RedirectAttributes redirectAttributes, HttpSession session, MultipartHttpServletRequest mult) {

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = boardFacade.getBoardInsert(cmntViewDTO,session,mult);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        redirectAttributes.addAttribute("nPage", cmntViewData.getNPage());
        redirectAttributes.addAttribute("boardMSeq", cmntViewData.getBoardMSeq());
        redirectAttributes.addAttribute("boardTopYn", cmntViewData.getBoardTopYn());

        return "redirect:/EXPIS/{bizCode}/cmnt/boardList.do";
    }

    /**
     * 게시글 수정(GET)
     */
    @GetMapping("/boardModify.do")
    public String boardModify(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                              Model model, HttpSession session) {

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = boardFacade.getBoardModify(cmntViewDTO, session);

        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "cmntBoard");
        model.addAttribute("secondId", "board");
        model.addAttribute("thirdId", "boardEdit");
        model.addAttribute("cmnt", cmntViewData);

        return "community/cmntHome";
    }

    /**
     * 게시글 수정(POST)
     */
    @PostMapping("/boardUpdate.do")
    public String boardUpdate(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                              HttpSession session, RedirectAttributes redirectAttributes, MultipartHttpServletRequest mult) {

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = boardFacade.getBoardUpdate(cmntViewDTO, session, mult);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE,  bizCode);
        redirectAttributes.addAttribute("boardDSeq", cmntViewData.getBoardDSeq());
        redirectAttributes.addAttribute("boardMSeq", cmntViewData.getBoardMSeq());
        redirectAttributes.addAttribute("nPage", cmntViewData.getNPage());

        return "redirect:/EXPIS/{bizCode}/cmnt/boardDetail.do";
    }

    /**
     * 게시글 삭제
     */
    @GetMapping("/boardDelete.do")
    public String boardDelete(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO, HttpSession session){

        DataSourceContextHolder.setDataSource(bizCode);
        boardFacade.getBoardDelete(cmntViewDTO,session);

        return "redirect:/EXPIS/{bizCode}/cmnt/boardList.do";
    }

    /**
     * 게시글 댓글 등록
     */
    @PostMapping("/boardCommentInsert.do")
    public String boardCommentInsert(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                                     HttpSession session, RedirectAttributes redirectAttributes) {

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = boardFacade.getBoardCommentInsert(cmntViewDTO,session);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE,  bizCode);
        redirectAttributes.addAttribute("boardDSeq", cmntViewData.getBoardDSeq());
        redirectAttributes.addAttribute("nPage", cmntViewData.getNPage());
        redirectAttributes.addAttribute("boardMSeq", cmntViewData.getBoardMSeq());

        return "redirect:/EXPIS/{bizCode}/cmnt/boardDetail.do";
    }

    /**
     * 게시글 댓글 수정(POST)
     */
    @PostMapping("/boardCommentUpdate.do")
    public String boardCommentUpdate(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                                     HttpSession session, RedirectAttributes redirectAttributes){

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = boardFacade.getBoardCommentUpdate(cmntViewDTO, session);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE,  bizCode);
        redirectAttributes.addAttribute("boardDSeq", cmntViewData.getBoardDSeq());
        redirectAttributes.addAttribute("boardMSeq", cmntViewData.getBoardMSeq());
        redirectAttributes.addAttribute("nPage", cmntViewData.getNPage());

        return "redirect:/EXPIS/{bizCode}/cmnt/boardDetail.do";
    }

    /**
     * 게시글 댓글 삭제
     */
    @PostMapping("/boardCommentDelete.do")
    public String boardCommentDelete(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                                     HttpSession session, RedirectAttributes redirectAttributes) {

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = boardFacade.getBoardCommentDelete(cmntViewDTO, session);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE,  bizCode);
        redirectAttributes.addAttribute("boardDSeq", cmntViewData.getBoardDSeq());
        redirectAttributes.addAttribute("boardMSeq", cmntViewData.getBoardMSeq());
        redirectAttributes.addAttribute("nPage", cmntViewData.getNPage());

        return "redirect:/EXPIS/{bizCode}/cmnt/boardDetail.do";
    }

    /**
     * 관련 사이트
     */
    @GetMapping("/relatedList.do")
    public String relatedList(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                              HttpSession session, Model model) {

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = relatedSiteFacade.getRelatedList(cmntViewDTO, session);

        model.addAttribute(CommonConstants.BIZ_CODE,  bizCode);
        model.addAttribute("firstId", "cmntRelatedSites");
        model.addAttribute("secondId", "relatedSites");
        model.addAttribute("cmnt", cmntViewData);

        return "community/cmntHome";
    }

    /**
     * 관련 사이트 insert
     */
    @PostMapping("/relatedInsert.do")
    public String relatedInsert(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                                HttpSession session, RedirectAttributes redirectAttributes, MultipartHttpServletRequest mult) {

        DataSourceContextHolder.setDataSource(bizCode);
        relatedSiteFacade.getRelatedInsert(cmntViewDTO, session, mult);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE,  bizCode);

        return "redirect:/EXPIS/{bizCode}/cmnt/relatedList.do";
    }

    /**
     * 관련 사이트 update
     */
    @PostMapping("/relatedUpdate.do")
    public String relatedUpdate(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO,
                                HttpSession session, RedirectAttributes redirectAttributes, MultipartHttpServletRequest mult) {

        DataSourceContextHolder.setDataSource(bizCode);
        relatedSiteFacade.getRelatedUpdate(cmntViewDTO, session, mult);

        redirectAttributes.addAttribute(CommonConstants.BIZ_CODE,  bizCode);
        redirectAttributes.addAttribute("nPage", cmntViewDTO.getNPage());

        return "redirect:/EXPIS/{bizCode}/cmnt/relatedList.do";
    }

    /**
     * 관련사이트 상세
     */
    @GetMapping("/relatedDetail.do")
    @ResponseBody
    public ResponseEntity<?> relatedDetail(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO) {

        DataSourceContextHolder.setDataSource(bizCode);
        CmntViewDTO cmntViewData = relatedSiteFacade.getRelatedDetail(cmntViewDTO);

        return ResponseEntity.ok(cmntViewData);
    }

    /**
     * 관련사이트 삭제
     */
    @GetMapping("/relatedDelete.do")
    @ResponseBody
    public ResponseEntity<?> relatedDelete(@PathVariable String bizCode, @ModelAttribute CmntViewDTO cmntViewDTO, HttpSession session) {

        DataSourceContextHolder.setDataSource(bizCode);
        try {
            int result = relatedSiteFacade.getRelatedDelete(cmntViewDTO, session);

            if (result == 1) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "게시글이 정상적으로 삭제 되었습니다.",
                        "nPage", cmntViewDTO.getNPage()
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

    /**
     * 파일 다운로드
     */
    @GetMapping("/doDownload.do")
    public void doDownload(@PathVariable String bizCode, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "fileSeq", defaultValue = "0") int fileSeq) {

        DataSourceContextHolder.setDataSource(bizCode);
        boardFacade.getDownload(request, response, fileSeq);
    }
}
