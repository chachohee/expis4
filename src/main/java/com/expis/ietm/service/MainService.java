package com.expis.ietm.service;

import com.expis.ietm.dao.MainMapper;
import com.expis.ietm.dto.MainDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {

    @Value("${app.expis.ietmImgFile}")
    private String ietmImgFile;
    @Value("${app.expis.compileType}")
    private String compileType;

    private final MainMapper mainMapper;
    private final ResourceLoader resourceLoader;


    public List<MainDto> getFinalToAjax(MainDto dto) {

        List<MainDto> mainDto = null;
        int num = 0;

        try {
            if (dto.getTocoId() != null) {
//                mainMapper.insertToDao(dto);
            }
            mainDto = mainMapper.selectToList(dto);
            mainDto = mainMapper.selectToKeyDao(dto);

        } catch (Exception e) {
            e.printStackTrace();
            log.info("MainComponent.getCoverDetail Exception:"
                    + e.toString());
        }
        return mainDto;

    }


    public List<MainDto> getFinalTocoAjax(MainDto dto) {

        List<MainDto> tocoName = null;
        int num = 0;

        if (dto.getTocoId() != null) {
            // 가장 첫번째 DB에 넣을때
            num = mainMapper.selectUserToDao(dto);
            if (num < 1) {
                mainMapper.insertToDao(dto);
            }
            mainMapper.updateToDao(dto);
            mainMapper.deleteToTestDao(dto);
            mainMapper.insertToDao(dto);
        }
        tocoName = mainMapper.selectTocoList(dto);

        return tocoName;

    }


    public List<MainDto> getToKeyList(MainDto dto) {

        List<MainDto> mainDto = null;
        try {

            mainDto = mainMapper.selectToKeyDao(dto);

        } catch (Exception e) {
            // TODO: handle exception
        }

        return mainDto;

    }

    /**
     * 2025.04.16 - osm
     * compileType이 'ide'인 경우, 프로젝트 내부 경로에서 이미지 다운로드
     * 그렇지 않은 경우, 경로를 replace하여 다운로드 경로를 구성
     * -> 추후 진행상황에 따라 경로 처리 방식 수정 필요
     */
    public String getDownloadPath(String fileNamePath) throws IOException {
        if (fileNamePath.contains("..") || fileNamePath.contains(":/") || fileNamePath.contains("\\")) {
            throw new IllegalArgumentException("잘못된 파일 경로 입니다.");
        }

        String adjustedPath = compileType.equals("ide")
                ? fileNamePath.replace("web", "WebContent/web")
                : fileNamePath;

        Resource resource = resourceLoader.getResource(ietmImgFile + adjustedPath);
        File file = resource.getFile();
        return file.getAbsolutePath();
    }

}
