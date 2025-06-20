package com.expis.manage.dao;

import com.expis.manage.dto.TreeXContDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface TreeXContMapper {

    int selectCountDao(TreeXContDto dto);

    ArrayList<TreeXContDto> selectListDao(TreeXContDto dto);

    TreeXContDto selectDetailDao(TreeXContDto dto);

    int insertDao(TreeXContDto dto);

    int insertAllDao(ArrayList<TreeXContDto> list);

    int insertAllDaoMDB(TreeXContDto dto);

    int updateDao(TreeXContDto dto);

    int deleteDao(TreeXContDto dto);

    int deleteAllDao();

    ArrayList<TreeXContDto> selectSiblingList(String toKey);

    ArrayList<String> selectInsertTmList();

}
