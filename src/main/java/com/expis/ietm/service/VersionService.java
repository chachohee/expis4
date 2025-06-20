package com.expis.ietm.service;

import com.expis.ietm.component.VersionComponent;
import com.expis.ietm.dao.XContVersionMapper;
import com.expis.ietm.dto.TocoInfoDto;
import com.expis.ietm.dto.VersionInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.expis.ietm.dto.XContDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VersionService {

    private final VersionComponent versionComponent;
    private final XContVersionMapper xContVersionMapper;

    public ArrayList<VersionInfoDto> getVersionList(VersionInfoDto dto) throws Exception {
        return versionComponent.getVersionList(dto);
    }

    public int getVersionCount(VersionInfoDto dto) throws Exception {
        return versionComponent.getVersionCount(dto);
    }

    public ArrayList<TocoInfoDto> getVersionTocoList(TocoInfoDto dto) throws Exception {
        return versionComponent.getVersionTocoList(dto);
    }

    public int getVersionTocoCount(TocoInfoDto dto) throws Exception {
        return versionComponent.getVersionTocoCount(dto);
    }

    public VersionInfoDto getVersionInfo(XContDto dto) throws Exception {
        return versionComponent.getVersionInfo(dto);
    }

    public ArrayList<XContDto> getVersionXCont(XContDto dto) {
        return xContVersionMapper.selectListDao(dto);
    }

/*
    public StringBuffer getVersionXCont(XContDto dto) throws Exception {
        return versionComponent.getVersionXCont(dto);
    }
*/

    public String getLastVersion(XContDto contDto) throws Exception {
        return versionComponent.getLastVersion(contDto);
    }

    public String getLastVersionInfo(XContDto contDto) throws Exception {
        return versionComponent.getLastVersionInfo(contDto);
    }

    public List<String> getVersionInfoList(VersionInfoDto versionDto) {
        return versionComponent.getVersionInfoList(versionDto);
    }

    public String getVersionName(VersionInfoDto versionDto) {
        return versionComponent.getVersionName(versionDto);
    }
}
