package com.expis.ietm.dao;

import com.expis.ietm.dto.VersionInfoDto;
import com.expis.ietm.dto.XContDto;
import com.expis.ietm.dto.XContVersionDto;
import com.expis.ietm.parser.ParserDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

/**
 * [IETM]Version XML Contents DAO Class
 */
@Mapper
public interface XContVersionMapper {
	
	public ArrayList<XContDto> selectListDao(XContDto dto);
	
	public XContDto selectDetailDao(XContDto dto);
	
	public int selectCountDao(XContDto dto);
	
	public int insertDao(XContDto dto);
	
	public int insertAllDao(ArrayList<XContDto> list);
	
	public int insertAllDaoMDB(XContDto dto);
	
	public int updateDao(XContDto dto);
	
	public int deleteDao(XContDto dto);
	
	public int deleteAllDao();
	
	public XContVersionDto versionUpdateInfo(XContVersionDto dto);
	
	public XContVersionDto versionUpdate(XContVersionDto dto);
	
	public VersionInfoDto versionAdd(XContVersionDto dto);

	public String getLastVersionId(ParserDto pDto);
	
}
