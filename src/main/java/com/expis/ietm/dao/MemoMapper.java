package com.expis.ietm.dao;

import com.expis.ietm.dto.MemoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface MemoMapper {
	
	public int selectCountDao(MemoDto memoDto);
	
	public ArrayList<MemoDto> selectListDao(MemoDto memoDto);
	
	public MemoDto selectDetailDao(
			@Param("memoSeq") long memoSeq
			);
	
	public int insertDao(MemoDto memoDto);
	
	public int updateDao(MemoDto memoDto);
	
	public int deleteDao(MemoDto memoDto);
	
	public ArrayList<MemoDto> selectListFromToDao(MemoDto memoDto);
	public ArrayList<MemoDto> selectSingleMemo(MemoDto memoDto);
	
	public int insertMemoMI(MemoDto memoDto);

}
