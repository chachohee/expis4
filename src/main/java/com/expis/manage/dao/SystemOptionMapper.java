package com.expis.manage.dao;

import com.expis.manage.dto.CoverManageDto;
import com.expis.manage.dto.SystemOptionDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SystemOptionMapper {
    int ipbTypeCheck();

    void optionUpdate(SystemOptionDto systemOptionDto);

    SystemOptionDto selectOptionAll();

    void cmntOptionUpdate(SystemOptionDto systemOptionDto);

    CoverManageDto getCoverCont(String toKey);

    void coverUpdate(CoverManageDto coverManageDto);

    String getCoverDate(String toKey);

    String getCoverVerDate(String toKey);

    String getCoverChgNo(String toKey);

    void insertCoverCont(CoverManageDto CoverDto);
}
