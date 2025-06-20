package com.expis.manage.dao;

import com.expis.manage.dto.DBInsertErrorDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface DBInsertErrorMapper {
    ArrayList<DBInsertErrorDto> selectListDao(DBInsertErrorDto dto);
    int insertDao(DBInsertErrorDto dto);
    int deleteSearch(DBInsertErrorDto dto);
    int deletePrintInfo(DBInsertErrorDto dto);
}
