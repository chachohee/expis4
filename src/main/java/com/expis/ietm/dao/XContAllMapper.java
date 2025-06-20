package com.expis.ietm.dao;

import com.expis.ietm.dto.XContDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;


/**
 * [IETM]ALL XML Contents DAO Class
 */
@Mapper
public interface XContAllMapper {
	
	public ArrayList<XContDto> selectListDao(XContDto dto);
	
	public ArrayList<XContDto> selectMultiListDao(XContDto dto);
	
	public ArrayList<XContDto> selectWCMultiListDao(XContDto dto);
	
	public XContDto selectDetailDao(XContDto dto);
	
	public int selectCountDao(XContDto dto);
	
	public int insertDao(XContDto dto);
	
	public int insertAllDao(ArrayList<XContDto> list);
	public int insertAllDaoMDB(XContDto dto);
	
//	public int insertAllDao(XContDto list);
	public int updateDao(XContDto dto);
	
	public int deleteDao(XContDto dto);
	
	public int deleteAllDao();
	public int selectIPBCount(XContDto dto);

	public List<XContDto> linkWDXcontList(XContDto contDto);

	public List<XContDto> findTocoId(XContDto dto);

	public List<XContDto> findTocoIdIPB(XContDto dto);

	public List<XContDto> getlinkWDImgIndexInfo(XContDto contDto);

}
