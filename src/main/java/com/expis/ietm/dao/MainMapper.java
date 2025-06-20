package com.expis.ietm.dao;

import com.expis.ietm.dto.MainDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MainMapper {

    List<MainDto> selectToList(MainDto Dto);

    List<MainDto> selectToKeyDao(MainDto dto);

    int selectUserToDao(MainDto dto);

    void insertToDao(MainDto dto);

    void updateToDao(MainDto dto);

    void deleteToTestDao(MainDto dto);

    List<MainDto> selectTocoList(MainDto dto);
}
