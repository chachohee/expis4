package com.expis.ietm.dao;

import com.expis.ietm.dto.XContDto;
import com.expis.ietm.dto.XContWCDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * [IETM]WC XML Contents DAO Class
 */
@Mapper
public interface XContWCMapper {
	
	public ArrayList<XContDto> selectListDao(XContDto dto);
	
	public ArrayList<XContDto> selectMultiListDao(XContDto dto);
	
	public ArrayList<XContDto> selectAllMultiListDao(XContDto dto);
	
	public XContDto selectDetailDao(XContDto dto);
	
	public int selectCountDao(XContDto dto);
	
	public int insertDao(XContDto dto);
	
	public int insertAllDao(ArrayList<XContDto> list);
	
	public int insertAllDaoMDB(XContDto dto);
	
	public int updateDao(XContDto dto);
	
	public int deleteDao(XContDto dto);
	
	public int deleteAllDao();

	public int insertAllDaoKTA(XContWCDto dto);

	public List<XContDto> viewWCLinkDao(XContDto contDto);

	public void deleteDaoKTA(XContDto contDto);
	
}
