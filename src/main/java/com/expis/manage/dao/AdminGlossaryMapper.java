package com.expis.manage.dao;

import com.expis.manage.dto.AdminGlossaryDto;
import com.expis.manage.dto.AdminLogDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface AdminGlossaryMapper {
    List<AdminGlossaryDto> selectGlossaryList(AdminGlossaryDto glossaryDto);
    int selectGlossaryTotalCount(AdminGlossaryDto glossaryDto);
    AdminGlossaryDto selectDetailGlossary(int glsSeq);

    //GS
    List<AdminGlossaryDto> selectLsaList(AdminGlossaryDto glossaryDto);
    int selectLsaTotalCount(AdminGlossaryDto glossaryDto);
    int insertLsa(AdminGlossaryDto glossaryDto);
    int deleteLsa(AdminGlossaryDto glossaryDto);
    int deleteAllLsa(AdminGlossaryDto glossaryDto);
    ///////////////////////////////

    int allInsertGlossary(ArrayList<AdminGlossaryDto> glossaryDto);
    int insertGlossary(AdminGlossaryDto glossaryDto);
    int modifyGlossary(AdminGlossaryDto glossaryDto);
    int deleteGlossary(AdminGlossaryDto glossaryDto);
    int deleteAllGlossary(AdminGlossaryDto glossaryDto);

    List<AdminLogDto> logSelect(AdminLogDto logDto);

    int logTotCount(AdminLogDto logDto);
    void insertLog(AdminLogDto logDto);

    int logCodeTotCount(AdminLogDto logDto);
    List<AdminLogDto> logCodeSelect(AdminLogDto logDto);
    int deleteLog(int logSeq);

    //mdb
    //용어
    int insertTempGlossary(AdminGlossaryDto dto);
    int deleteTempGlossary();
    int alterTempGlossaryAuto();
    int selectTempCount(AdminGlossaryDto dto);
    List<AdminGlossaryDto> selectTempGlossary(AdminGlossaryDto dto);

    //로그
    int deleteTempLog();
    int alterTempLogAuto();
    int selectTempLogCount(AdminLogDto dto);

    int insertTempLogInfo(AdminLogDto dto);
    List<AdminLogDto> selectTempLogInfo(AdminLogDto dto);

    int insertTempLogCode(AdminLogDto dto);
    List<AdminLogDto> selectTempLogCode(AdminLogDto dto);
}
