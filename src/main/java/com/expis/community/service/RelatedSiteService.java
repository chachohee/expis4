package com.expis.community.service;

import com.expis.common.paging.PageDTO;
import com.expis.common.paging.PagingDTO;
import com.expis.community.dao.RelatedSiteMapper;
import com.expis.community.dto.RelatedDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelatedSiteService {
    private final RelatedSiteMapper relatedSiteMapper;

    @Value("${app.expis.pagingCnt}")
    private int pagingCnt;

    public PagingDTO createPaging(RelatedDTO relatedDTO) {
        int nPage = relatedDTO.getNPage() > 0 ? relatedDTO.getNPage() : 1;

        int totCnt = relatedSiteMapper.relatedCount();
        totCnt = (totCnt == 0 ? 1 : totCnt);

        PageDTO pageDTO = PageDTO.builder()
                .nowPage(nPage)
                .totalCount(totCnt)
                .build();

        PagingDTO pagingDTO = new PagingDTO(pageDTO, 12, pagingCnt);

        relatedDTO.setStartRow(pagingDTO.getStartRow());
        relatedDTO.setEndRow(pagingDTO.getEndRow());

        return pagingDTO;
    }

    public int relatedCount() {
        return relatedSiteMapper.relatedCount();
    }

    public List<RelatedDTO> relatedCmnt(RelatedDTO dto) {
        return relatedSiteMapper.relatedCmnt(dto);
    }

    public int relatedInsert(RelatedDTO dto) {
        return relatedSiteMapper.relatedInsert(dto);
    }

    public int updateRelated(RelatedDTO dto) {
        return relatedSiteMapper.updateRelated(dto);
    }

    public RelatedDTO relatedDetail(int relSeq) {
        return relatedSiteMapper.relatedDetail(relSeq);
    }

    public int deleteRelated(RelatedDTO dto) {
        return relatedSiteMapper.deleteRelated(dto);
    }

    public List<RelatedDTO> relatedSelect(RelatedDTO dto) {
        return relatedSiteMapper.relatedSelect(dto);
    }
}

