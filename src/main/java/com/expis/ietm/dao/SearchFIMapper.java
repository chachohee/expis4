package com.expis.ietm.dao;

import com.expis.ietm.dto.SearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * [IETM]Search FI Contents DAO Class
 */
@Mapper
public interface SearchFIMapper {
	
	ArrayList<SearchDto> selectListDao(SearchDto dto);
	
	SearchDto selectDetailDao(SearchDto dto);
	
	int selectCountDao(SearchDto dto);
	
	int insertDao(SearchDto dto);
	
	int insertAllDao(ArrayList<SearchDto> list);
	
	int updateDao(SearchDto dto);
	
	int deleteDao(SearchDto dto);
	
	int deleteAllDao();
	
}
