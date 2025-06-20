package com.expis.ietm.service;

import com.expis.ietm.dao.GlossaryMapper;
import com.expis.ietm.dto.IPBSearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GlossaryService {

    private final GlossaryMapper glossaryMapper;

    public List<IPBSearchDto> getIPBSearchList(IPBSearchDto ipbSearchDto) {
        return glossaryMapper.selectIPBListDao(ipbSearchDto);
    }
}
