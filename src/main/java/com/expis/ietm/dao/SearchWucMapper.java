package com.expis.ietm.dao;

import com.expis.ietm.dto.SearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * [IETM]Search WUC Contents DAO Class
 */
@Mapper
public interface SearchWucMapper {
	
	public ArrayList<SearchDto> selectListDao(SearchDto dto);
	
	public SearchDto selectDetailDao(SearchDto dto);
	
	public int selectCountDao(SearchDto dto);
	
	public int insertDao(SearchDto dto);
	
	public int insertAllDao(ArrayList<SearchDto> list);
	
	public int insertAllDaoMDB(SearchDto dto);
	
	public int updateDao(SearchDto dto);
	
	public int deleteDao(SearchDto dto);
	
	public int deleteAllDao();
	
	public ArrayList<SearchDto> selectWucDao(SearchDto dto); 
}
