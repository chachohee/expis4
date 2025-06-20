package com.expis.community.dao;

import com.expis.community.dto.FileMasterDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMasterMapper {
    public int insertFile(FileMasterDTO dto);
    public FileMasterDTO fileSelect(int fileSeq);
}
