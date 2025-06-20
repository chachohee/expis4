package com.expis.manage.service;

import com.expis.manage.dao.SystemInfoMapper;
import com.expis.manage.dao.SystemOptionMapper;
import com.expis.manage.dto.CoverManageDto;
import com.expis.manage.dto.SystemOptionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Element;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemOptionService {

    private final SystemOptionMapper systemOptionMapper;

    public int ipbTypeCheck() {
        return systemOptionMapper.ipbTypeCheck();
    }

    public void optionUpdate(SystemOptionDto systemOptionDto) {
        systemOptionMapper.optionUpdate(systemOptionDto);
    }

    public SystemOptionDto selectOptionAll() {
        return systemOptionMapper.selectOptionAll();
    }

    public void cmntOptionUpdate(SystemOptionDto systemOptionDto) {
        systemOptionMapper.cmntOptionUpdate(systemOptionDto);
    }

    public CoverManageDto getCoverCont(String toKey) {
        return systemOptionMapper.getCoverCont(toKey);
    }

    public void coverUpdate(CoverManageDto coverManageDto) {
        systemOptionMapper.coverUpdate(coverManageDto);
    }

    public String getCoverDate(String toKey) {
        return systemOptionMapper.getCoverDate(toKey);
    }

    public String getCoverVerDate(String toKey) {
        return systemOptionMapper.getCoverVerDate(toKey);
    }

    public String getCoverChgNo(String toKey) {
        return systemOptionMapper.getCoverChgNo(toKey);
    }

    public void insertCoverCont(CoverManageDto coverDto) {
        systemOptionMapper.insertCoverCont(coverDto);
    }
}
