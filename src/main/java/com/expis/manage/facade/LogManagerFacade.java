package com.expis.manage.facade;

import com.expis.common.paging.CommPageing;
import com.expis.common.paging.PageDTO;
import com.expis.manage.dto.AdminLogDto;
import com.expis.manage.service.LogManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogManagerFacade {

    private final LogManagerService logManagerService;

    public Map<String, Object> getLogData(AdminLogDto adminLogDto, String type) {
        Map<String, Object> map = new HashMap<>();

        int totalCount;
        List<AdminLogDto> aList;

        // 1. totalCount 분기 처리
        if (type.equals("info")) {
            totalCount = logManagerService.logTotCount(adminLogDto);
        } else if (type.equals("code")) {
            totalCount = logManagerService.logCodeTotCount(adminLogDto);
        } else {
            throw new IllegalArgumentException("잘못된 로그 타입입니다: " + type);
        }

        // 2. Paging
        PageDTO pageDto = new PageDTO();
        pageDto.setNowPage(adminLogDto.getNowPage());
        pageDto.setTotalCount(totalCount);

        CommPageing commPageing = new CommPageing(pageDto, 10, 10, "logPageEvent");
        adminLogDto.setStartRow(commPageing.getStartRow());
        adminLogDto.setEndRow(commPageing.getEndRow());

        // 3. 실제 목록 분기처리
        if (type.equals("info")) {
            aList = logManagerService.logSelect(adminLogDto);
        } else {
            aList = logManagerService.logCodeSelect(adminLogDto);
        }

        // 4. 날짜 조건 포함 시만 추가
        if (adminLogDto.getStartDate() != null && !adminLogDto.getStartDate().equals("none")) {
            map.put("startDate", adminLogDto.getStartDate());
            map.put("endDate", adminLogDto.getEndDate());
        }

        // 5. 결과 구성
        map.put("aList", aList);
        map.put("nowPage", pageDto.getNowPage());
        map.put("totCnt", totalCount);
        map.put("recordCnt", 10);
        map.put("tPage", commPageing.getTotalPage());
        map.put("pageTag", commPageing.resultString());

        return map;
    }
}
