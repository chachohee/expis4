package com.expis.ietm.dao;

import com.expis.ietm.dto.VersionInfoDto;
import com.expis.ietm.dto.XContDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface VersionInfoMapper {

    public ArrayList<VersionInfoDto> selectListDao(VersionInfoDto dto);

    public VersionInfoDto selectDetailDao(VersionInfoDto dto);

    public int selectCountDao(VersionInfoDto dto);

    public int insertDao(VersionInfoDto dto);

    public int insertAllDao(ArrayList<VersionInfoDto> list);

    public int insertAllDaoMDB(VersionInfoDto dto);

    public int updateDao(VersionInfoDto dto);

    public int deleteDao(VersionInfoDto dto);

    public int deleteAllDao();

    public String selectLastVersion(XContDto contDto);

    public String getLastVersionInfo(XContDto contDto);

    public List<String> getVersionInfoList(VersionInfoDto versionDto);

    public String getVersionName(VersionInfoDto versionDto);
}
