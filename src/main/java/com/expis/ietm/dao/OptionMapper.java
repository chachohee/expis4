package com.expis.ietm.dao;

import com.expis.ietm.dto.OptionDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OptionMapper {

    int selectUserIdCount(OptionDto dto);

    OptionDto selectUserFontSize(OptionDto dto);

    void insertOption(OptionDto dto);

    int updateOption(OptionDto dto);

    OptionDto selectOption(String userId);
}
