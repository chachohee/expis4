package com.expis.manage.service;

import com.expis.manage.dao.SystemInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemInfoService {

    private final SystemInfoMapper systemInfoMapper;

    public int getSysInfoCount() {
        return systemInfoMapper.getSysInfoCount();
    }
}
