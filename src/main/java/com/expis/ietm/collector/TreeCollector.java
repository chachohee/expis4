package com.expis.ietm.collector;

import com.expis.domparser.XmlDomParser;
import com.expis.manage.dao.SystemInfoMapper;
import com.expis.manage.dao.TreeXContMapper;
import com.expis.manage.dto.SystemInfoDto;
import com.expis.manage.dto.TreeXContDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * [IETM-TM]교범 목록 Collector Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TreeCollector {

    private final TreeXContMapper treeMapper;
    private final SystemInfoMapper sysMapper;

    /**
     * 계통/TO 목록 데이터를 DB에서 추출하는 DAO 처리하여 String(문자열)로 반환
     * @MethodName	: getToTreeStr
     * @AuthorDate		: LIM Y.M. / 2014. 6. 6.
     * @ModificationHistory	:
     * @return
     */
    public StringBuffer getToTreeStr(TreeXContDto treeDto) {
        StringBuffer rtSB = new StringBuffer();

        try {

            //1) DTO 객체에 값 셋팅
            //2) DAO에서 DB Data 추출
            ArrayList<TreeXContDto> treeList = treeMapper.selectListDao(treeDto);

            //3) Contents 추출
            if (treeList != null && treeList.size() > 0) {
                for (TreeXContDto rsDto : treeList) {
                    rtSB.append(rsDto.getTreeXcont());
                }
            } else {

            }

        }  catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeCollector.getToTreeStr Exception:"+ex.toString());
        }

        return rtSB;
    }

    /**
     * 교범목록 추출 : 트리 DOM 만들어 메모리 띄우기위해, 교범 목록 필요
     * @MethodName	: getSystemToList
     * @param sysDto
     * @return
     */
    public ArrayList<SystemInfoDto> getSystemToList(SystemInfoDto sysDto) {

        ArrayList<SystemInfoDto> rtList = null;

        try {

            rtList = sysMapper.selectListSystemToDao();

        }  catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeCollector.getSystemToList Exception:"+ex.toString());
        } finally {
        }

        return rtList;
    }

    /**
     * 계통/TO 목록 XML 데이터 받아서 DOM으로 반환
     * @MethodName	: getToIndexDom
     * @return
     */
    public Object getToTreeDom(TreeXContDto treeDto) {

        Document doc = null;
        Element rtElem = null;

        try {

            //2. 스트링으로 된 XML 데이터를 W3C DOM으로 생성하는 모듈 호출
            StringBuffer treeSB = this.getToTreeStr(treeDto);

            if (treeSB.length() > 0) {
                doc = XmlDomParser.createDomTree(treeSB.toString() ,1);
                rtElem = doc.getDocumentElement();
            }

        }  catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeCollector.getToIndexDom Exception:"+ex.toString());
        }

        return rtElem;
    }

    /**
     * 관련교범목록
     * @MethodName	: getRelatedToList
     * @param sysDto
     * @return
     */
    public ArrayList<SystemInfoDto> getRelatedToList(SystemInfoDto sysDto) {
        ArrayList<SystemInfoDto> rtList = null;

        try {

            rtList = sysMapper.selectListRelatedToDao();

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeCollector.getRelatedToList Exception:"+ex.toString());
        }

        return rtList;
    }
}
