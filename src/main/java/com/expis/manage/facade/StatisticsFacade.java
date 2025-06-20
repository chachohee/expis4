package com.expis.manage.facade;

import com.expis.common.paging.CommPageing;
import com.expis.common.paging.PageDTO;
import com.expis.manage.dto.StatisticsDto;
import com.expis.manage.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsFacade {

    private final StatisticsService statisticsService;

    /**
     * 2025.05.19 - osm
     * 사용자 접속 통계 - 막대 차트
     * - type 별 사용자 접속 건수 데이터 분기
     */
    public Map<String, Object> getUserContactStatus(StatisticsDto statisticsDto) {
        Map<String, Object> map = new HashMap<>();

        List<StatisticsDto> result = switch (statisticsDto.getType()) {
            case "total" -> statisticsService.selectUserStatusList();
            case "year" -> statisticsService.selectYearUserStatusList(statisticsDto);
            case "month" -> statisticsService.selectMonthUserStatusList(statisticsDto);
            case "day" -> statisticsService.selectUserDayContactStatus(statisticsDto);
            default -> Collections.emptyList();
        };

        map.put("userStatusList", result);

        return map;
    }

    /**
     * 2025.05.19 - osm
     * 사용자 아이디(ID) 별 리스트 - 파이 차트
     */
    public Map<String, Object> getSelectUserList(StatisticsDto statisticsDto) {
        HashMap<String, Object> map = new HashMap<>();

        List<StatisticsDto> userStatusList = statisticsService.selectUserList(statisticsDto);

        map.put("userStatusList", userStatusList);

        return map;
    }

    /**
     * 2025.05.19 - osm
     * 사용자 접속통계 상세내역
     * - 내역 테이블
     */
    public Map<String, Object> getUserIdStatusDetail(StatisticsDto statisticsDto) {
        Map<String, Object> map = new HashMap<>();

        statisticsDto.setRecordCnt(10); // Expis3 에서는 commonValidate.properties에서 값을 가져옴
//        statisticsDto.setDbType(dbType);
        int nowPage = statisticsDto.getNowPage();
        int totalCount = statisticsService.selectUserIdDetailCount(statisticsDto);

        PageDTO pageDto = new PageDTO();
        pageDto.setNowPage(nowPage);
        pageDto.setTotalCount(totalCount);

        CommPageing commPage = new CommPageing(pageDto, 10, 10, "statDetailPage");

        int startRow = commPage.getStartRow();
        int endRow = commPage.getEndRow();

        //검색값이 없을때
        if ("test".equalsIgnoreCase("mdb")) { // 기존 if(dbType.equalsIgnoreCase("mdb")) 임시 false 처리
            if (statisticsDto.getSelectId().equals("none") || statisticsDto.getSelectName().equals("none")) {
                if (startRow == 0) {
                    startRow = totalCount;
                } else {
                    startRow = (totalCount - startRow);
                }

                endRow = totalCount - endRow;
                statisticsDto.setStartRow(endRow);
                statisticsDto.setEndRow(startRow);
            } else {
                statisticsDto.setStartRow(startRow);
                statisticsDto.setEndRow(endRow);
            }
        } else {
            statisticsDto.setStartRow(startRow);
            statisticsDto.setEndRow(endRow);
        }

        List<StatisticsDto> userStatusList = statisticsService.selectUserDetailList(statisticsDto);

        map.put("userStatusList", userStatusList);
        map.put("nowPage", nowPage);
        map.put("recordCnt", 10);
        map.put("totalCount", totalCount);
        map.put("page", commPage.resultString());

        return map;
    }

    /**
     * 2025.05.21 - osm
     * 교범 접속 통계 - 교범별 접속 TOP5
     */
    public Map<String, Object> getSelectToConnPie(StatisticsDto statisticsDto) {
        Map<String, Object> map = new HashMap<>();

        List<StatisticsDto> toPie = statisticsService.selectToConnPie(statisticsDto);

        map.put("toPie", toPie);

        return map;
    }

    /**
     * 2025.05.21 - osm
     * 교범 접속 통계 - 목차별 접속 TOP5
     */
    public Map<String, Object> getSelectTocoConnPie(StatisticsDto statisticsDto) {
        Map<String, Object> map = new HashMap<>();

        List<StatisticsDto> tocoPie = statisticsService.selectTocoConnPie(statisticsDto);

        map.put("tocoPie", tocoPie);

        return map;
    }

    /**
     * 2025.05.21 - osm
     * 교범 접속 통계 - 내역 탭
     */
    public Map<String, Object> getSelectTocoPie(StatisticsDto statisticsDto) {
        Map<String, Object> map = new HashMap<>();

        statisticsDto.setRecordCnt(10);
//        statDto.setDbType(dbType);
        int totalCnt = statisticsService.selectToConnTotalCount(statisticsDto);

        PageDTO pageDTO = new PageDTO();
        pageDTO.setNowPage(statisticsDto.getNowPage());
        pageDTO.setTotalCount(totalCnt);

        CommPageing commPage = new CommPageing(pageDTO, 10, 10, "statDetailPage");

        int startRow = commPage.getStartRow();
        int endRow = commPage.getEndRow();

        //검색값이 없을때
        if ("test".equalsIgnoreCase("mdb")) { // (dbType.equalsIgnoreCase("mdb"))
            if (statisticsDto.getSearchType().equals("")) {
                if (startRow == 1) {
                    startRow = totalCnt;
                } else {
                    startRow = (totalCnt - startRow);
                }

                endRow = totalCnt - endRow;
                statisticsDto.setStartRow(endRow);
                statisticsDto.setEndRow(startRow);
            } else {
                statisticsDto.setStartRow(startRow);
                statisticsDto.setEndRow(endRow);
            }
        } else {
            statisticsDto.setStartRow(startRow);
            statisticsDto.setEndRow(endRow);
        }

        List<StatisticsDto> toConnDetailList = statisticsService.selectDetailToConnList(statisticsDto);

        map.put("page", commPage.resultString());
        map.put("result", toConnDetailList);
        map.put("totalCnt", totalCnt);
        map.put("recordCnt", 10);
        map.put("nowPage", statisticsDto.getNowPage());
        map.put("searchType", statisticsDto.getSearchType());

        return map;
    }

    /**
     * 2025.05.21 - osm
     * 교범 접속 통계 - 연도 생성
     */
    public Map<String, Object> toCreateDate() {
        Map<String, Object> map = new HashMap<>();

        List<StatisticsDto> createDate = statisticsService.toCreateDate();

        map.put("result", createDate);

        return map;
    }
}
