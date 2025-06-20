package com.expis.ietm.dao;

import com.expis.ietm.dto.TocoInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * [IETM]TO Contents Info DAO Class
 */
@Mapper
public interface TocoInfoMapper {
	
	public int selectCountVersionDao(TocoInfoDto tocoDto);
	
	public ArrayList<TocoInfoDto> selectListVersionDao(TocoInfoDto tocoDto);
	
	public ArrayList<TocoInfoDto> selectListTocoTypeDao(TocoInfoDto tocoDto);
	
	public TocoInfoDto selectDetailDao(@Param("toKey") String toKey, @Param("tocoId") String tocoId);
	
	public String selectTocoNameDao(@Param("toKey") String toKey, @Param("tocoId") String tocoId);
	
	public TocoInfoDto selectParentTocoDao(@Param("toKey") String toKey, @Param("tocoId") String tocoId);
	
	//public String selectSsssnNoDao(@Param("toKey") String toKey, @Param("tocoId") String tocoId);
	
	public String selectOneDao(@Param("toKey") String toKey, @Param("tocoId") String tocoId, @Param("colName") String colName);
	
	public String selectTocoIdFromIpbcodeDao(@Param("toKey") String toKey, @Param("ipbCode") String ipbCode);
	
	
	public int insertDao(TocoInfoDto tocoDto);
	
	public int insertAllDao(ArrayList<TocoInfoDto> list);
	
	public int insertAllDaoMDB(TocoInfoDto dto);
	
	public String selectTocoId(String toKey);
	
	public int updateDao(TocoInfoDto tocoDto);

	public int updateVersionDao(TocoInfoDto tocoDto);
	
	public int deleteDao(TocoInfoDto tocoDto);

	public int deleteAllDao();
	
	public List<TocoInfoDto> selectTocoList(TocoInfoDto infoDto);
	
	public List<TocoInfoDto> selectUpdateToList(TocoInfoDto dto);

	public void updateVehicleType(TocoInfoDto toInfoDto);
	
	public ArrayList<TocoInfoDto> selectTocoDto(TocoInfoDto dto);

}
