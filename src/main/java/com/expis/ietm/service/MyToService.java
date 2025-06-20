package com.expis.ietm.service;

import com.expis.ietm.dao.MyToMapper;
import com.expis.ietm.dto.MyToDto;
import com.expis.manage.dao.AdminGlossaryMapper;
import com.expis.manage.dto.AdminLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
@RequiredArgsConstructor
public class MyToService {

    private final MyToMapper myToMapper;
    private final AdminGlossaryMapper adminGlossaryMapper;

    /**
     * 폴더
     */
    public boolean insertMyFolder(MyToDto myDto) {

        boolean result = true;
        AdminLogDto logDto =  new AdminLogDto();

        logDto.setCreateUserId(myDto.getUserId());
        logDto.setCodeType("4101");
        adminGlossaryMapper.insertLog(logDto);

        Map<String, Object> resultMap = myToMapper.selectMaxValue(myDto);

        BigDecimal mytoSeqBig = (BigDecimal) resultMap.get("MYTOSEQ");
        BigDecimal mytoOrdBig = (BigDecimal) resultMap.get("MYTOORD");
        log.info("mytoSeqBig {}", mytoSeqBig);
        log.info("mytoOrdBig {}", mytoOrdBig);

        long mytoSeq = (mytoSeqBig != null) ? mytoSeqBig.longValue() : 0L;
        long mytoOrd = (mytoOrdBig != null) ? mytoOrdBig.longValue() : 0L;

        myDto.setMytoSeq(mytoSeq);
        myDto.setMytoOrd(mytoOrd);

        myToMapper.insertMyFolder(myDto);

        return result;
    }

    public List<MyToDto> selectMyFolder(MyToDto myDto) {
        return myToMapper.selectMyFolder(myDto);
    }

    public boolean updateMyFolder(MyToDto myToDto) {
        int updatedRows = myToMapper.updateMyFolder(myToDto);
        return updatedRows > 0;
    }

    public boolean deleteMyFolder(long mytoSeq, boolean hasChildren, boolean deleteChildren) {

        int deletedRowsMytoInfo = 0;
        int deletedRowsMyTocoInfo = 0;
        int deletedRowsMyToXcont = 0;

        /*
         * hasChildren : 자식폴더 여부
         * deleteChildren : 자식폴더 삭제 여부
         *
         * 1. hasChildren, deleteChildren
         *   (true, false) --> 원래대로 진행
         *   (false, false)  --> 원래대로 진행
         *   (true, true) --> 자식폴더 번호까지 찾아서 진행
         * */
        if (!deleteChildren) {
            // tm_fn_myto_info 삭제
            deletedRowsMytoInfo = myToMapper.deleteMyFolder(mytoSeq);
            // tm_fn_mytoco_info 삭제
            deletedRowsMyTocoInfo = myToMapper.deleteMyTocoInfo(mytoSeq);
            // tm_fn_myto_xcont 삭제
            deletedRowsMyToXcont = myToMapper.deleteMyToXcont(mytoSeq);
        } else {
            if (hasChildren) {
                List<Long> mytoSeqList = new ArrayList<>();
                mytoSeqList.add(mytoSeq);

                List<Long> childrenList = myToMapper.findChildrenMytoSeq(mytoSeq);
                // 자식폴더가 있을 경우
                if (childrenList != null && !childrenList.isEmpty()) {
                    mytoSeqList.addAll(childrenList);

                    // 자식폴더가 부모일 경우 재귀적으로 자식 찾기
                    for (int i = 0; i < childrenList.size(); i++) {
                        long childSeq = childrenList.get(i);

                        List<Long> subChildrenList = myToMapper.findChildrenMytoSeq(childSeq);

                        while (subChildrenList != null && !subChildrenList.isEmpty()) {
                            mytoSeqList.addAll(subChildrenList);

                            // 또 자식 폴더가 있을 수 있으므로 계속해서 탐색
                            List<Long> tempSubChildrenList = new ArrayList<>();
                            for (Long subChildSeq : subChildrenList) {
                                tempSubChildrenList.addAll(myToMapper.findChildrenMytoSeq(subChildSeq));
                            }

                            subChildrenList = tempSubChildrenList;
                        }
                    }
                }

                log.info("삭제할 폴더 번호 목록: {}", mytoSeqList.toString());

                for (int i = 0; i < mytoSeqList.size(); i++) {
                    // tm_fn_myto_info 삭제
                    deletedRowsMytoInfo = myToMapper.deleteMyFolder(mytoSeqList.get(i));
                    // tm_fn_mytoco_info 삭제
                    deletedRowsMyTocoInfo = myToMapper.deleteMyTocoInfo(mytoSeqList.get(i));
                    // tm_fn_myto_xcont 삭제
                    deletedRowsMyToXcont = myToMapper.deleteMyToXcont(mytoSeqList.get(i));
                }
            }
        }

        // tm_fn_myto_info는 반드시 삭제되어야 함
        if (deletedRowsMytoInfo <= 0) {
            return false; // myto_info 테이블 삭제 실패
        }
        // 나머지 두 테이블은 폴더만 존재했으면 Updates : 0
        return true;
    }

    public int selectMyTocoList(MyToDto myDto) {
        return myToMapper.selectMyTocoList(myDto);
    }


    /**
     * 목차
     */
    public StringBuffer getMytoTree(MyToDto myToDto) {

        StringBuffer rtSB = new StringBuffer();

        try {
            List<MyToDto> treeList = myToMapper.selectTreeDao(myToDto);

            if (treeList != null && treeList.size() > 0) {
                for (MyToDto rsDto : treeList) {
                    rtSB.append(rsDto.getTreeXcont());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("TreeCollector.getToTreeStr Exception:" + e.toString());

        }
        return rtSB;
    }

    public Map<String, Object> getMyTocoXmlList(MyToDto myToDto) {

        Map<String, Object> resultMap = new HashMap<>();
        StringBuffer rtSB = new StringBuffer();
        ArrayList<MyToDto> rsData = myToMapper.getMyTocoXmlList(myToDto);

        if (rsData != null && rsData.size() > 0) {
            for (MyToDto rsDto : rsData) {
                rtSB.append(rsDto.getTreeXcont());
                // toKey 추가
                resultMap.put("toKey", rsDto.getToKey());
            }
        }

        // 특수문자처리
        String modifiedString = rtSB.toString().replace("&", "&amp;");

        // 정규식: name="" seq="" 구조일 때, name 속성값 안의 큰따옴표 처리
        Pattern pattern = Pattern.compile("name=\"(.*?)\"(?=\\sseq=)");
        Matcher matcher = pattern.matcher(modifiedString);

        StringBuffer fixedXml = new StringBuffer();

        // 매칭된 name 속성의 큰따옴표 escape 처리
        while (matcher.find()) {
            String nameValue = matcher.group(1);
            log.info("큰따옴표 escape 전 : {}", nameValue);
            // name 속성 값 안의 큰따옴표만 escape
            String escapedName = nameValue.replaceAll("\"", "&quot;");
            log.info("큰따옴표 escape 후 : {}", escapedName);
            // 수정된 문자열로 치환
            matcher.appendReplacement(fixedXml, Matcher.quoteReplacement("name=\"" + escapedName + "\""));
        }
        matcher.appendTail(fixedXml);
        log.info("fixedXml : {}", fixedXml);

        resultMap.put("treeData", fixedXml);

        return resultMap;
    }

    public int dupChkTokey(MyToDto myToDto) {
        return myToMapper.dupChkToKey(myToDto);
    }

    public long selectMytoSeq() {
        return myToMapper.selectMytoSeq();
    }

    public boolean insertMyCoService(MyToDto myDto) {
        long mytoOrd = 0;
        boolean result = true;
        int tocoSize = myDto.getParamTocoId().size();
        long mytocoSeq = myToMapper.selectMytocoSeq();

        //param
        ArrayList<MyToDto> dataList = new ArrayList<MyToDto>();

        //가장최근 mytoco_ord 번호 구하기 --> 안 해도 될것 같은데
        mytoOrd = myToMapper.selectCntMyCo(myDto.getUserId());
        myDto.setMytoOrd(mytoOrd);
        for(int i=0; i < tocoSize; i++) {
            MyToDto rsDto = new MyToDto();
            rsDto.setUserId(myDto.getUserId());
            rsDto.setMytocoSeq(mytocoSeq++);
            rsDto.setToKey(myDto.getToKey());
            rsDto.setTocoId(myDto.getParamTocoId().get(i));
            rsDto.setTocoName(myDto.getParamTocoName().get(i));
            rsDto.setPTocoId(myDto.getParamParentId().get(i));
            rsDto.setMyType(myDto.getParamType().get(i));
            rsDto.setMytocoOrd(myDto.getParamOrd().get(i));
            rsDto.setMytoSeq(myDto.getMytoSeq());

            if(myDto.getDbType().equals("mdb")) {
                myToMapper.insertMyTocoAllInfoMDB(rsDto);
            } else {
                dataList.add(rsDto);
            }
        }

        if(!myDto.getDbType().equals("mdb")) {
            myToMapper.insertMyTocoAllInfo(dataList);
        }

        createTocoXml(myDto);

        return result;
    }

    //tocoXml 만드는 함수
    public int createTocoXml(MyToDto myDto) {
        int mytocoSize = 0;
        long mytocoSeq = 0;
        int folderSize = 0;
        String mytocoName = "";
        String tocoId = "";
        String pTocoId = "";
        String type = "";
        ArrayList<String> folder = new ArrayList<String>();
        ArrayList<MyToDto> resultList = new ArrayList<MyToDto>();
        StringBuilder xmlData = new StringBuilder();

        resultList = myToMapper.selectTocoList(myDto);
        log.info("resultList : " + resultList.toString());

        mytocoSize = resultList.size();

        xmlData.append("<techinfo>");

        for(MyToDto rsDto : resultList) {
            mytocoSeq = rsDto.getMytocoSeq();
            mytocoName = rsDto.getMytocoName();
            tocoId = rsDto.getTocoId();
            pTocoId = rsDto.getPTocoId();
            type = rsDto.getMyType();

            if(folderSize == 0 || folder.get(folderSize-1) == pTocoId) {
                folderSize++;
                folder.add(tocoId);
            } else {
                for(int i=folderSize-1; i>=0; i--) {
                    if(folder.get(i).equals(pTocoId)) {
                        break;
                    }

                    folderSize--;
                    folder.remove(i);
                    for(int j=0; j<i; j++) {
                        xmlData.append("\t");
                    }
                    xmlData.append("</system>\n");
                }
                folderSize++;
                folder.add(tocoId);
            }
            for(int i=0; i<folderSize-1; i++) {
                xmlData.append("\t");
            }
            xmlData.append("<system id=\"" + tocoId + "\" name=\"" + mytocoName + "\" seq=\"" + mytocoSeq + "\" pTocoId=\"" + pTocoId + "\" type=\"" + type + "\" >\n");
        }

        for(int i=folderSize-1; i>=0; i--) {
            folderSize--;
            folder.remove(i);
            for(int j=0; j<i; j++) {
                xmlData.append("\t");
            }
            xmlData.append("</system>\n");
        }
        log.info("xmlData : " + xmlData);

        xmlCutAndInsert(xmlData, myDto, "02");

        return 0;
    }

    public int xmlCutAndInsert(StringBuilder xmlData, MyToDto myDto, String treeKind) {
        int XMLMAXLEN = 3000;
        int divData;
        int treeXth = 1;

        xmlData.append("</techinfo>");
        log.info("xmlCutAndInsert xmlData : {}", xmlData);
        myDto.setTreeKind(treeKind);
        myToMapper.deleteMytoXmlInfo(myDto);
        if(xmlData.length() > XMLMAXLEN) {
            // more then
            String tempXmlData;
            divData = (xmlData.length() / XMLMAXLEN);
            for(int i=0; i<divData; i++) {
                tempXmlData = xmlData.substring(i*XMLMAXLEN, i*XMLMAXLEN+XMLMAXLEN);
                log.info("tempXmlData : " + tempXmlData);
                myDto.setTreeXth(treeXth++);
                myDto.setTreeXcont(tempXmlData);

                myToMapper.insertMytoXmlInfo(myDto);
            }
            tempXmlData = xmlData.substring((divData)*XMLMAXLEN);
            myDto.setTreeXth(treeXth);
            myDto.setTreeXcont(tempXmlData);

            myToMapper.insertMytoXmlInfo(myDto);
        } else {
            // less or equals then
            myDto.setTreeXth(treeXth);
            myDto.setTreeXcont(xmlData.toString());

            myToMapper.insertMytoXmlInfo(myDto);
        }
        return treeXth;
    }

    public boolean delMytoco(MyToDto myDto) {
        boolean result = false;
        int tocoSize = myDto.getParamTocoId().size();
        AdminLogDto logDto =  new AdminLogDto();

        try {
            for(int i=0; i < tocoSize; i++) {
                MyToDto rsDto = new MyToDto();
                rsDto.setUserId(myDto.getUserId());
                rsDto.setMytoSeq(myDto.getMytoSeq());
                rsDto.setToKey(myDto.getParamToKey().get(i));
                rsDto.setTocoId(myDto.getParamTocoId().get(i));
                myToMapper.delMytoco(rsDto);
            }

            myDto.setTreeKind("02");
            myDto.setToKey(myDto.getParamToKey().get(0));
            createTocoXml(myDto);

            logDto.setCreateUserId(myDto.getUserId());
            logDto.setCodeType("4108");
            adminGlossaryMapper.insertLog(logDto);

            result = true;

        } catch (Exception e) {
            log.error("Error occurred while processing delMytoco and createTocoXml", e);
            result = false;
        }

        return result;
    }

}
