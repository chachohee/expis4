package com.expis.manage.service;

import com.expis.manage.dao.DBInsertErrorMapper;
import com.expis.manage.dto.DBInsertErrorDto;
import com.expis.manage.dto.RegisterDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DBInsertErrorService {

    private final DBInsertErrorMapper dbInsertErrorMapper;

    public List<DBInsertErrorDto> selectDBInsertError(RegisterDto rcDto) {
        DBInsertErrorDto dto = new DBInsertErrorDto();
        if (rcDto.getToKey() != null && !rcDto.getToKey().isEmpty()) {
            dto.setToKey(rcDto.getToKey());
        }
        log.info("DBInsertErrorDto dto.toKey={}, rcDto.toKey={}", dto.getToKey(), rcDto.getToKey());
        return dbInsertErrorMapper.selectListDao(dto);
    }

    public void insertDBInsertError(RegisterDto rcDto, String errStr) {
        if (rcDto.getToKey() == null || rcDto.getToKey().isEmpty()) {
            return;
        }
        if (rcDto.getTocoId() == null || rcDto.getTocoId().isEmpty()) {
            return;
        }
        DBInsertErrorDto dto = new DBInsertErrorDto();
        dto.setToKey(rcDto.getToKey());
        dto.setTocoId(rcDto.getTocoId());
        dto.setType("Thread");
        dto.setErrMsg(errStr);
        dbInsertErrorMapper.insertDao(dto);
    }

    public void deleteDBInserError(RegisterDto rcDto) {
        DBInsertErrorDto dto = new DBInsertErrorDto();
        if (rcDto.getToKey() != null && !rcDto.getToKey().isEmpty()) {
            dto.setToKey(rcDto.getToKey());
        }
        log.info("DBInsertErrorDto dto.toKey={}, rcDto.toKey={}", dto.getToKey(), rcDto.getToKey());
        dbInsertErrorMapper.deleteSearch(dto);
    }

    public int deletePrintInfo(DBInsertErrorDto dto) {
        return dbInsertErrorMapper.deletePrintInfo(dto);
    }

}
