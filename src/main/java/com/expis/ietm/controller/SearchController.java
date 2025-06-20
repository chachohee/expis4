package com.expis.ietm.controller;

import com.expis.common.config.datasource.DataSourceContextHolder;
import com.expis.common.variable.VariableAspect;
import com.expis.ietm.dto.IPBSearchDto;
import com.expis.ietm.dto.SearchDto;
import com.expis.ietm.service.GlossaryService;
import com.expis.ietm.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/ietm")
public class SearchController {

    private final SearchService searchService;
    private final GlossaryService glossaryService;

    @PostMapping("/searchList.do")
    public ResponseEntity<Map<String, Object>> searchList(@ModelAttribute("SearchDto") SearchDto searchDto,
                                                          HttpServletRequest request) throws Exception {

        VariableAspect.setting(request);
        String userId = (String)request.getSession().getAttribute("SS_USER_ID");

        //리턴되는 모델
        Map<String, Object> map = new HashMap<String, Object>();

        /*
         * 검색 조건	/ 서브 검색 조건
         * 01 : 현재교범	1 : 목차,  2 : 내용,  3 : 그래픽,  4 : 표(테이블),  5 : 경고
         * 02 : 현재계통
         * 03 : 부품정보	1 : 부품명,  2 : 부품번호,  3 : 재고번호(NSN),  4 : RDN,  5 : 카이규격(KAI)
         * 04 : 결함코드
         * 05 : 작업카드
         * 06 : 작업단위부호
         * 07 : 용어검색
         */
        // 용어검색일 경우 다른 서비스에 태워야 된다
//        if(searchDto.getSearchCond().equals("07")) {
//            GlossaryDto glDto = new GlossaryDto();
//
//            glDto.setSearchArray(searchDto.getSearchArray());
//            glDto.setChkVal(searchDto.getChkVal());
//            glDto.setCreateUserId(userId);
//
//            List<GlossaryDto> searchList = glossaryService.getGloSelectList(glDto);
//            model.put("searchList", searchList);

//        } else {
        searchDto.setCreateUserId(userId);
        int searchSize = searchDto.getSearchArray().length;
        log.info("searchSize=" + searchSize);

        List<SearchDto> searchList = searchService.getSearchList(searchDto);
        map.put("searchList", searchList);
//        }

        return ResponseEntity.ok(map);
    }

    /**
     * 2025.06.09 - osm
     * 검색 조건 : 1 = 부품번호, 2 = RDN, 3 = 국가 재고 번호
     */
    @PostMapping("/searchIPBList.do")
    public ResponseEntity<?> searchIPBList(@PathVariable String bizCode,
                                           @ModelAttribute("SearchDto") SearchDto searchDto
    ) {
        DataSourceContextHolder.setDataSource(bizCode);
        Map<String, Object> responseMap = new HashMap<>();

        IPBSearchDto ipbSearchDto = new IPBSearchDto();
        ipbSearchDto.setSearchType(searchDto.getSearchCond());
        ipbSearchDto.setSearchKeyWork(searchDto.getSearchWord());
        if ( !"".equalsIgnoreCase(searchDto.getToKey()) ) ipbSearchDto.setSearchToKey(searchDto.getToKey());

        List<IPBSearchDto> searchList = glossaryService.getIPBSearchList(ipbSearchDto);

        responseMap.put("searchList", searchList);

        return ResponseEntity.ok(responseMap);
    }
}
