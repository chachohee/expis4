package com.expis.ietm.dao;

import com.expis.ietm.dto.MemoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface MemoMapperMI {
	
	public ArrayList<MemoDto> selectMigrationListDao(MemoDto dto);

}
