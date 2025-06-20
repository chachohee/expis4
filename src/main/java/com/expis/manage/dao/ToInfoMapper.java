package com.expis.manage.dao;

import com.expis.manage.dto.ToInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface ToInfoMapper {

    ArrayList<ToInfoDto> selectListDao(ToInfoDto dto);

    ToInfoDto selectDetailDao(
            @Param("toKey") String toKey
    );

    int selectCountDao(ToInfoDto dto);

    int insertDao(ToInfoDto dto);

    int insertAllDao(ArrayList<ToInfoDto> list);
    int insertAllDaoMDB(ToInfoDto dto);

    int insertMergeDao();

    int mergeDaoChkMDB(ToInfoDto dto);
    int insertMergeDaoMDB(ToInfoDto dto);
    int updateMergeDaoMDB(ToInfoDto dto);

    int updateDao(ToInfoDto dto);

    int updatePropertyDao(ToInfoDto dto);

    int deleteDao(ToInfoDto dto);

    int deleteAllDao();

    int deleteMergeDao();

    ToInfoDto selectToVerDate(String toKey);

    int getSysInfoCount();
}
