package com.expis.ietm.dao;

import com.expis.ietm.dto.XContDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;


/**
 * [IETM]Alert XML Contents DAO Class
 */
@Mapper
public interface XContAlertMapper {
	
	public ArrayList<XContDto> selectListDao(XContDto dto);
	
	public XContDto selectDetailDao(XContDto dto);
	
	public int selectCountDao(XContDto dto);
	
	public int insertDao(XContDto dto);
	
	public int insertAllDao(ArrayList<XContDto> list);
	
	public int insertAllDaoMDB(XContDto	dto);
	
	public int updateDao(XContDto dto);
	
	public int deleteDao(XContDto dto);
	
	public int deleteAllDao();
	
}
