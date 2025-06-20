package com.expis.manage.dao;

import com.expis.manage.dto.SystemInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface SystemInfoMapper {

    int getSysInfoCount();

    int selectCountDao(SystemInfoDto dto);

    ArrayList<SystemInfoDto> selectListDao();

    ArrayList<SystemInfoDto> selectListSystemToDao();

    ArrayList<SystemInfoDto> selectListRelatedToDao();

    SystemInfoDto selectDetailDao(
            @Param("sysId") String sysId
    );

    int insertDao(SystemInfoDto dto);

    int insertAllDao(ArrayList<SystemInfoDto> list);
    int insertAllDaoMDB(SystemInfoDto dto);

    int updateDao(SystemInfoDto dto);

    int deleteDao(SystemInfoDto dto);

}
