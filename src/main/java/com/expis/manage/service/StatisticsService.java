package com.expis.manage.service;

import com.expis.manage.dao.StatisticsMapper;
import com.expis.manage.dto.StatisticsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsMapper statisticsMapper;

    // 막대 차트 - 전체 데이터
    public List<StatisticsDto> selectUserStatusList() {
        return statisticsMapper.selectUserStatusList();
    }

    // 막대 차트 - 연도별 데이터
    public List<StatisticsDto> selectYearUserStatusList(StatisticsDto statisticsDto) {
        return statisticsMapper.selectYearUserStatusList(statisticsDto);
    }

    // 막대 차트 - 월별 데이터
    public List<StatisticsDto> selectMonthUserStatusList(StatisticsDto statisticsDto) {
        return statisticsMapper.selectMonthUserStatusList(statisticsDto);
    }

    // 막대 차트 - 일별 데이터
    public List<StatisticsDto> selectUserDayContactStatus(StatisticsDto statisticsDto) {
        return statisticsMapper.selectUserDayContactStatus(statisticsDto);
    }

    // 파이 차트 데이터
    public List<StatisticsDto> selectUserList(StatisticsDto statisticsDto) {
        return statisticsMapper.selectUserList(statisticsDto);
    }

    // 내역 탭 데이터 (검색)
    public List<StatisticsDto> selectUserDetailList(StatisticsDto statisticsDto) {
        return statisticsMapper.selectUserDetailList(statisticsDto);
    }

    // 내역 탭 데이터 (페이징)
    public int selectUserIdDetailCount(StatisticsDto statisticsDto) {
        int result = 0;
        if ("test".equalsIgnoreCase("mdb") && (!statisticsDto.getSelectId().equals("none") || !statisticsDto.getSelectName().equals("none"))) {
            if (statisticsDto.getNowPage() == 1) {
                statisticsMapper.deleteTempConnUser();
                statisticsMapper.alterTempConnUserAuto();
                statisticsMapper.insertTempConnUser(statisticsDto);
            }

            result = statisticsMapper.selectTempConnUserCount(statisticsDto);
        } else {
            result = statisticsMapper.selectUserIdDetailCount(statisticsDto);
        }

        return result;
    }

    // 파이 차트 - 교범별 접속 TOP5
    public List<StatisticsDto> selectToConnPie(StatisticsDto statisticsDto) {
        return statisticsMapper.selectToConnPie(statisticsDto);
    }

    // 파이 차트 - 목차별 접속 TOP5
    public List<StatisticsDto> selectTocoConnPie(StatisticsDto statisticsDto) {
        return statisticsMapper.selectTocoConnPie(statisticsDto);
    }

    public int selectToConnTotalCount(StatisticsDto statisticsDto) {
        int result = 0;

        if ("test".equalsIgnoreCase("mdb")) { // (statDTO.getDbType().equalsIgnoreCase("mdb") && !statDTO.getSearchType().equals(""))
            if (statisticsDto.getNowPage() == 1) {
                statisticsMapper.deleteTempConnTo();
                statisticsMapper.alterTempConnToAuto();
                statisticsMapper.insertTempConnTo(statisticsDto);
            }

            result = statisticsMapper.selectTempConnToCount(statisticsDto);
        } else {
            result = statisticsMapper.selectToConnTotalCount(statisticsDto);
        }
        return result;
    }

    public List<StatisticsDto> selectDetailToConnList(StatisticsDto statisticsDto) {
        List<StatisticsDto> rtList;
/*        if (!statisticsDto.getSearchValue().equals("none")) {
            AdminLogDto logDto = new AdminLogDto();
            logDto.setCreateUserId(statisticsDto.getCreateUserId());
            logDto.setCodeType("3502");
            adminGlossaryMapper.insertLog(logDto);
        }*/

        if ("test".equalsIgnoreCase("mdb")) { // (statDTO.getDbType().equalsIgnoreCase("mdb") && !statDTO.getSearchType().equals(""))
            rtList = statisticsMapper.selectTempConnTo(statisticsDto);
        } else {
            rtList = statisticsMapper.selectDetailToConnList(statisticsDto);
        }
        return rtList;
    }

    public List<StatisticsDto> toCreateDate() {
        return statisticsMapper.toCreateDate();
    }
}
