package com.expis.ietm.dao;

import com.expis.ietm.dto.SearchPartinfoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * [IETM]Search IPB Contents DAO Class
 */
@Mapper
public interface SearchIPBMapper {
	
	public ArrayList<SearchPartinfoDto> selectListDao(SearchPartinfoDto dto);
	
	public SearchPartinfoDto selectDetailDao(SearchPartinfoDto dto);
	
	public List<SearchPartinfoDto> selectIpb(SearchPartinfoDto dto);
	
	public int selectCountDao(SearchPartinfoDto dto);
	
	public int insertDao(SearchPartinfoDto dto);
	
	public int insertAllDao(ArrayList<SearchPartinfoDto> list);
	
	public int updateDao(SearchPartinfoDto dto);
	
	public int deleteDao(SearchPartinfoDto dto);
	
	public int deleteAllDao();
	
}
