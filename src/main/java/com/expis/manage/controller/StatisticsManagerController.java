package com.expis.manage.controller;

import com.expis.common.CommonConstants;
import com.expis.login.interceptor.Login;
import com.expis.manage.dto.StatisticsDto;
import com.expis.manage.facade.StatisticsFacade;
import com.expis.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 2024.12.09 - 관리자메뉴 - 통계관리 서비스 - jingi.kim
 * 변경이력 :
 * 2025.05.21 - Facade 패턴 적용 - osm
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/EXPIS/{bizCode}/manage")
public class StatisticsManagerController {

    private final StatisticsFacade statisticsFacade;

    /**
     * 메뉴: 통계관리 - 사용자 접속 통계
     */
    @GetMapping("/userStatistics.do")
    public String userStatistics(@PathVariable String bizCode,
                                 @Login UserDto userDto,
                                 Model model
    ) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaStatistics");
        model.addAttribute("secondId", "user");
        model.addAttribute("userDto", userDto);

        return "manage/manageHome";
    }

    /**
     * 2025.05.19 - osm
     * 사용자 접속 통계 - 막대 차트
     * - 사용자 접속 건수 데이터
     */
    @PostMapping("/userContactStatus.do")
    public ResponseEntity<?> userContactStatus(@PathVariable String bizCode,
                                               @RequestBody StatisticsDto statisticsDto
    ) {
        Map<String, Object> responseMap = statisticsFacade.getUserContactStatus(statisticsDto);

        return ResponseEntity.ok(responseMap);
    }

    /**
     * 2025.05.19 - osm
     * 사용자 아이디(ID) 별 리스트 - 파이 차트
     */
    @PostMapping("/userIdStatusList.do")
    public ResponseEntity<?> userIdStatusList(@PathVariable String bizCode,
                                              @RequestBody StatisticsDto statisticsDto
    ) {
        Map<String, Object> responseMap = statisticsFacade.getSelectUserList(statisticsDto);

        return ResponseEntity.ok(responseMap);
    }

    /**
     * 2025.05.19 - osm
     * 사용자 접속통계 상세내역
     * - 내역 테이블
     */
    @PostMapping("/userIdStatusDetailList.do")
    public ResponseEntity<?> userIdStatusDetailList(@PathVariable String bizCode,
                                                    @RequestBody StatisticsDto statisticsDto
    ) {
        Map<String, Object> responseMap = statisticsFacade.getUserIdStatusDetail(statisticsDto);

        return ResponseEntity.ok(responseMap);
    }

    /**
     * 메뉴: 통계관리 - 교범 접속 통계
     */
    @GetMapping("/toStatistics.do")
    public String toStatistics(@PathVariable String bizCode,
                               @Login UserDto userDto,
                               Model model
    ) {
        model.addAttribute(CommonConstants.BIZ_CODE, bizCode);
        model.addAttribute("firstId", "areaStatistics");
        model.addAttribute("secondId", "ietm");
        model.addAttribute("userDto", userDto);

        return "manage/manageHome";
    }

    /**
     * 2025.05.20 - osm
     * 교범 접속 통계 - 교범별 접속 TOP5
     */
    @PostMapping("/toPieChart.do")
    public ResponseEntity<?> toPieChart(@PathVariable String bizCode,
                                        @RequestBody StatisticsDto statisticsDto
    ) {
        Map<String, Object> responseMap = statisticsFacade.getSelectToConnPie(statisticsDto);

        return ResponseEntity.ok(responseMap);
    }

    /**
     * 2025.05.20 - osm
     * 교범 접속 통계 - 목차별 접속 TOP5
     */
    @PostMapping("/tocoPieChart.do")
    public ResponseEntity<?> tocoPieChart(@PathVariable String bizCode,
                                          @RequestBody StatisticsDto statisticsDto
    ) {
        Map<String, Object> responseMap = statisticsFacade.getSelectTocoConnPie(statisticsDto);

        return ResponseEntity.ok(responseMap);
    }

    /**
     * 2025.05.20 - osm
     * 교범 접속 통계 - 내역 탭
     */
    @PostMapping("/toDetailList.do")
    public ResponseEntity<?> toDetailList(@PathVariable String bizCode,
                                          @RequestBody StatisticsDto statisticsDto
    ) {
        Map<String, Object> responseMap = statisticsFacade.getSelectTocoPie(statisticsDto);

        return ResponseEntity.ok(responseMap);
    }

    /**
     * 2025.05.20 - osm
     * 교범 접속 통계 - 연도 생성
     */
    @RequestMapping("/toCreateDate.do")
    public ResponseEntity<?> toCreateDate(@PathVariable String bizCode) {
        Map<String, Object> responseMap = statisticsFacade.toCreateDate();

        return ResponseEntity.ok(responseMap);
    }

}
