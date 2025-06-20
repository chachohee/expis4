package com.expis.ietm.dao;

import com.expis.ietm.dto.MyToDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface MyToMapper {

    void insertMyFolder(MyToDto myDto);

    long selectCntMyCo(String userId);

    List<MyToDto> selectMyFolder(MyToDto myDto);

    int updateMyFolder(MyToDto myToDto);

    int deleteMyFolder(long mytoSeq);

    List<MyToDto> selectTreeDao(MyToDto myToDto);

    ArrayList<MyToDto> getMyTocoXmlList(MyToDto myToDto);

    Map<String, Object> selectMaxValue(MyToDto myDto);

    int dupChkToKey(MyToDto myToDto);

    long selectMytoSeq();

    long selectMytocoSeq();

    void insertMyTocoAllInfoMDB(MyToDto rsDto);

    void insertMyTocoAllInfo(ArrayList<MyToDto> dataList);

    ArrayList<MyToDto> selectTocoList(MyToDto myDto);

    void deleteMytoXmlInfo(MyToDto myDto);

    void insertMytoXmlInfo(MyToDto myDto);

    void delMytoco(MyToDto myDto);

    int deleteMyTocoInfo(long mytoSeq);

    int deleteMyToXcont(long mytoSeq);

    int selectMyTocoList(MyToDto myDto);

    List<Long> findChildrenMytoSeq(long mytoSeq);
}
