package com.expis.manage.service;

import com.expis.manage.dao.AdminGlossaryMapper;
import com.expis.manage.dto.AdminLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogManagerService {

    private final AdminGlossaryMapper adminGlossaryMapper;

    public int logTotCount(AdminLogDto adminLogDto) {
/*
        int result = 0;
        if(adminLogDto.getDbType().equalsIgnoreCase("mdb") && !adminLogDto.getStartDate().equals("none")) {
            if(adminLogDto.getNowPage() == 1) {
                adminGlossaryMapper.deleteTempLog();
                adminGlossaryMapper.alterTempLogAuto();
                adminGlossaryMapper.insertTempLogInfo(adminLogDto);
            }

            result = adminGlossaryMapper.selectTempLogCount(adminLogDto);
        } else {
        }
            result = adminGlossaryMapper.logTotCount(adminLogDto);
*/
        return adminGlossaryMapper.logTotCount(adminLogDto);
    }

    public List<AdminLogDto> logSelect(AdminLogDto adminLogDto) {
/*
        List<AdminLogDto> rtList = null;
        if(adminLogDto.getDbType().equalsIgnoreCase("mdb") && !adminLogDto.getStartDate().equals("none")) {
            rtList = adminGlossaryMapper.selectTempLogInfo(adminLogDto);
        } else {
            rtList = adminGlossaryMapper.logSelect(adminLogDto);
        }
*/
        return adminGlossaryMapper.logSelect(adminLogDto);
    }

    public int logCodeTotCount(AdminLogDto adminLogDto) {
/*
        int result = 0;
        if(adminLogDto.getDbType().equalsIgnoreCase("mdb") && !adminLogDto.getStartDate().equals("none")) {
            if(adminLogDto.getNowPage() == 1) {
                adminGlossaryMapper.deleteTempLog();
                adminGlossaryMapper.alterTempLogAuto();
                adminGlossaryMapper.insertTempLogCode(adminLogDto);
            }

            result = adminGlossaryMapper.selectTempLogCount(adminLogDto);
        } else {
            result = adminGlossaryMapper.logCodeTotCount(adminLogDto);
        }
*/
        return adminGlossaryMapper.logCodeTotCount(adminLogDto);
    }

    public List<AdminLogDto> logCodeSelect(AdminLogDto adminLogDto) {
/*
        List<AdminLogDto> rtList = null;
        if(adminLogDto.getDbType().equalsIgnoreCase("mdb") && !adminLogDto.getStartDate().equals("none")) {
            rtList = adminGlossaryMapper.selectTempLogCode(adminLogDto);
        } else {
            rtList = adminGlossaryMapper.logCodeSelect(adminLogDto);
        }
*/
        return adminGlossaryMapper.logCodeSelect(adminLogDto);
    }
}
