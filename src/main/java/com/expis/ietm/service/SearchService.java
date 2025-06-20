package com.expis.ietm.service;

import com.expis.ietm.component.SearchComponent;
import com.expis.ietm.dto.SearchDto;
import com.expis.ietm.dto.SearchPartinfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchComponent searchComponent;

    public List<SearchDto> getSearchList(SearchDto scDto) throws Exception {
        return searchComponent.getSearchList(scDto);
    }

    public List<SearchPartinfoDto> selectIpb(SearchPartinfoDto dto) throws Exception {
        return searchComponent.selectIpb(dto);
    }
}
