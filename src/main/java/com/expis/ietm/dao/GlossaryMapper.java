package com.expis.ietm.dao;

import com.expis.ietm.dto.IPBSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GlossaryMapper {

    // IPB 색인 검색
    List<IPBSearchDto> selectIPBListDao(IPBSearchDto ipbSearchDto);
}
