package com.expis.ietm.service;

import com.expis.ietm.dao.MemoMapper;
import com.expis.ietm.dto.MemoDto;
import com.expis.manage.dao.AdminGlossaryMapper;
import com.expis.manage.dto.AdminLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoMapper memoMapper;
    private final AdminGlossaryMapper glossaryMapper;

    public List<MemoDto> getMemoList(MemoDto memoDto) {

        List<MemoDto> memoList = new ArrayList<MemoDto>();

        try {

            memoList = memoMapper.selectListDao(memoDto);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("MemoComponent.getMemoList Exception:"+ex.toString());
        }

        return memoList;
    }

    public boolean insertMemo(MemoDto memoDto) {

        boolean insertResult = false;
        int conLength = memoDto.getCont().length();

        if(conLength < 100) {
            try {
                AdminLogDto logDto =  new AdminLogDto();
                logDto.setCreateUserId(memoDto.getCreateUserId());
                logDto.setCodeType("4401");
                glossaryMapper.insertLog(logDto);

                log.info("insertMemo : " + memoDto.toString());
                int insertRows = memoMapper.insertDao(memoDto);

                if (insertRows > 0) {
                    insertResult = true;
                } else {
                    insertResult = false;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                log.info("insertMemo Exception:"+ex.toString());
            }
        }

        return insertResult;
    }

    public boolean deleteMemo(MemoDto memoDto) {

        boolean deleteResult = false;

        try {
            AdminLogDto logDto =  new AdminLogDto();
            logDto.setCreateUserId(memoDto.getModifyUserId());
            logDto.setCodeType("4403");
            glossaryMapper.insertLog(logDto);
            int deleteRows = memoMapper.deleteDao(memoDto);

            if (deleteRows > 0) {
                deleteResult = true;
            } else {
                deleteResult = false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("deleteMemo Exception:"+ex.toString());
        }

        return deleteResult;
    }

    public boolean updateMemeo(MemoDto memoDto) {

        boolean updateResult = false;

        int contSize = memoDto.getCont().length();

        if(contSize < 100) {
            try {
                AdminLogDto logDto = new AdminLogDto();
                logDto.setCreateUserId(memoDto.getModifyUserId());
                logDto.setCodeType("4402");
                glossaryMapper.insertLog(logDto);

                int updateRows = memoMapper.updateDao(memoDto);
                if (updateRows > 0) {
                    updateResult = true;
                } else {
                    updateResult = false;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                log.info("updateMemo Exception:" + ex.toString());
            }
        }
        return updateResult;
    }

    public MemoDto getMemoDetail(MemoDto memoDto) {

        MemoDto rtMemoDto = new MemoDto();
        try {
            rtMemoDto = memoMapper.selectDetailDao(memoDto.getMemoSeq());
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("getMemoDetail Exception:"+ex.toString());
        }
        return rtMemoDto;
    }

    /**
     * 북마크 목록 (TO 내용으로부터 호출)
     */
    public ArrayList<MemoDto> selectSingleMemo(MemoDto memoDto) {

        ArrayList<MemoDto> list = null;

        try {

            list = memoMapper.selectSingleMemo(memoDto);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("MemoComponent.getMemoFromToList Exception:{}", ex.toString());
        }

        return list;
    }
}
