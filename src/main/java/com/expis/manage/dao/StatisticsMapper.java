package com.expis.manage.dao;

import com.expis.manage.dto.StatisticsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatisticsMapper {

    /** --- 사용자 접속 통계 --- **/
    // 막대 차트 - 전체 데이터
    List<StatisticsDto> selectUserStatusList();

    // 막대 차트 - 연도별 데이터
    List<StatisticsDto> selectYearUserStatusList(StatisticsDto statisticsDto);

    // 막대 차트 - 월별 데이터
    List<StatisticsDto> selectMonthUserStatusList(StatisticsDto statisticsDto);

    // 막대 차트 - 일별 데이터
    List<StatisticsDto> selectUserDayContactStatus(StatisticsDto statisticsDto);

    // 파이 차트 데이터
    List<StatisticsDto> selectUserList(StatisticsDto statisticsDto);

    // 내역 탭 데이터 (검색, 페이징)
    List<StatisticsDto> selectUserDetailList(StatisticsDto statisticsDto);

    // 내역 탭 데이터 카운트 (검색, 페이징)
    int selectUserIdDetailCount(StatisticsDto statisticsDto);

    int selectTempConnUserCount(StatisticsDto statisticsDto);

    /**
     * 현재 사용되지 않음
     * -> 우선 동작하도록 생성만 해둔상태
     */
    int insertTempConnUser(StatisticsDto statisticsDto);

    int deleteTempConnUser();

    int alterTempConnUserAuto();
    // -------------------------
    /** --- 사용자 접속 통계 end --- **/

    /** --- 교범 접속 통계 --- **/
    // 파이 차트 - 교범별 TOP5
    List<StatisticsDto> selectToConnPie(StatisticsDto statisticsDto);

    // 파이 차트 - 목차별 TOP5
    List<StatisticsDto> selectTocoConnPie(StatisticsDto statisticsDto);

    // 내역 탭 데이터 (검색, 페이징)
    List<StatisticsDto> selectDetailToConnList(StatisticsDto statisticsDto);

    List<StatisticsDto> selectTempConnTo(StatisticsDto statisticsDto);

    // 내역 탭 데이터 카운트 (검색, 페이징)
    int selectToConnTotalCount(StatisticsDto statisticsDto);

    int selectTempConnToCount(StatisticsDto statisticsDto);

    // 연도 생성
    List<StatisticsDto> toCreateDate();

    /**
     * 현재 사용되지 않음
     * -> 우선 동작하도록 생성만 해둔상태
     */
    int insertTempConnTo(StatisticsDto statisticsDto);

    int deleteTempConnTo();

    int alterTempConnToAuto();
    // -------------------------
    /** --- 교범 접속 통계 end --- **/
}
