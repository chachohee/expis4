package com.expis.ietm.service;

import com.expis.common.IConstants;
import com.expis.ietm.dao.OptionMapper;
import com.expis.ietm.dto.OptionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionMapper optionMapper;

    public OptionDto selectUserFontSize(OptionDto dto) {
        OptionDto optionDto = null;
        int count = 0;

        try {
            count = optionMapper.selectUserIdCount(dto);
            if (count == 0) {
                /*
                 * userId로 옵션 설정한 적이 없다면 기본값으로 세팅
                 * */
                optionDto = new OptionDto();
                optionDto.setFontSize("01");
                optionDto.setPrintWordMode("MS");
                optionDto.setFontFamily("03");
                optionDto.setExploreMode("01");
                optionDto.setOutputMode("01");
                optionDto.setViewMode("01");
                optionDto.setFiMode("01");
                optionDto.setCoverType("01");
            } else {
                optionDto = optionMapper.selectUserFontSize(dto);
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        return optionDto;

    }


    public void insertOption(OptionDto dto) {

        int count = 0;
        try {
            /*
             * 한번도 옵션을 설정한 적이 없다면 insert
             * 한번이라도 userId로 옵션 설정을 했다면 업데이트
             * */
            count = optionMapper.selectUserIdCount(dto);
            log.info("userId count: {}", count);

            if (count == 0) {
                dto.setFontFamily(IConstants.OPT_FONT_FAMILY_1);
                dto.setCoverType(IConstants.OPT_COVER_1);

                optionMapper.insertOption(dto);
            } else {
//                AdminLogDto logDto =  new AdminLogDto();
//                logDto.setCreateUserId(dto.getUserId());
//                logDto.setCodeType("4501");
//                glossaryMapper.insertLog(logDto);

                log.info("option update 진입");
                log.info("OptionDto: {}", dto.toString());

                int updatedRows = optionMapper.updateOption(dto);
                log.info("updatedRows: {}", updatedRows);
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public OptionDto selectOption(String userId) {

        OptionDto optionDto = optionMapper.selectOption(userId);

        if (optionDto == null) {
            log.error("OptionDto is null for userId: {}", userId);
        } else {
            log.info("OptionDto: {}", optionDto);
        }

        return optionDto;

    }

}
