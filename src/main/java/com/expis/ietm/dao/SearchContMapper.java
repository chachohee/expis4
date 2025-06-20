package com.expis.ietm.dao;

import com.expis.ietm.dto.SearchDto;
import com.expis.ietm.dto.SearchPartinfoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * [IETM]Search Text Contents DAO Class
 */
@Mapper
public interface SearchContMapper {
	
	ArrayList<SearchDto> selectListDao(SearchDto dto);
	
	ArrayList<SearchDto> selectListIPBDao(SearchDto dto);
	
	SearchDto selectDetailDao(SearchDto dto);
	
	int selectCountDao(SearchDto dto);
	
	int insertDao(SearchDto dto);
	
	int insertAllDao(ArrayList<SearchDto> list);
	
	int insertAllDaoMDB(SearchDto dto);
	
	int updateDao(SearchDto dto);
	
	int deleteDao(SearchDto dto);
	
	int deleteAllDao();
	
	ArrayList<SearchDto> selectWcDao(SearchDto dto);

	int updateIPBSearchForKTA(SearchPartinfoDto dto);
	
}
