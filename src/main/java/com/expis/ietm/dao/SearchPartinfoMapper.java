package com.expis.ietm.dao;

import com.expis.ietm.dto.SearchPartinfoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * [IETM]Search Partinfo Contents DAO Class
 */
@Mapper
public interface SearchPartinfoMapper {
	
	ArrayList<SearchPartinfoDto> selectListDao(SearchPartinfoDto dto);
	
	SearchPartinfoDto selectDetailDao(SearchPartinfoDto dto);
	
	int selectCountDao(SearchPartinfoDto dto);
	
	int insertDao(SearchPartinfoDto dto);
	
	int insertAllDao(ArrayList<SearchPartinfoDto> list);
	
	int insertAllDaoMDB(SearchPartinfoDto dto);
	
	int updateDao(SearchPartinfoDto dto);
	
	int deleteDao(SearchPartinfoDto dto);
	
	int deleteAllDao();
	
}
