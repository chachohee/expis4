package com.expis.ietm.dao;

import com.expis.ietm.dto.XContDto;

import java.util.ArrayList;

/**
 * [IETM]Table XML Contents DAO Class
 */
public interface XContTableMapper {
	
	public ArrayList<XContDto> selectListDao(XContDto dto);
	
	public XContDto selectDetailDao(XContDto dto);
	
	public int selectCountDao(XContDto dto);
	
	public int insertDao(XContDto dto);
	
	public int insertAllDao(ArrayList<XContDto> list);
	
	public int insertAllDaoMDB(XContDto dto);
	
	public int updateDao(XContDto dto);
	
	public int deleteDao(XContDto dto);
	
	public int deleteAllDao();
	
}
