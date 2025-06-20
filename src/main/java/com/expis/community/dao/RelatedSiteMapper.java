package com.expis.community.dao;

import com.expis.community.dto.RelatedDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface RelatedSiteMapper {
    // 관련사이트
    int relatedCount();
    List<RelatedDTO> relatedSelect(RelatedDTO dto);
    int relatedInsert(RelatedDTO dto);
    int updateRelated(RelatedDTO dto);
    int deleteRelated(RelatedDTO rDto);
    List<RelatedDTO> relatedCmnt(RelatedDTO dto);
    RelatedDTO relatedDetail(int relSeq);
}
