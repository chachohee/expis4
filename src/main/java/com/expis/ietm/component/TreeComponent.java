package com.expis.ietm.component;

import com.expis.common.IConstants;
import com.expis.domparser.ATTR;
import com.expis.domparser.CSS;
import com.expis.domparser.XALAN;
import com.expis.domparser.XmlDomParser;
import com.expis.ietm.collector.TreeCollector;
import com.expis.manage.dto.SystemInfoDto;
import com.expis.manage.dto.TreeXContDto;
import com.expis.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class TreeComponent {

    private final TreeCollector treeCollector;

    /**
     * 계통/TO목록/TO목차
     */
    public StringBuffer getToTree(TreeXContDto treeDto) {
        StringBuffer rtSB = new StringBuffer();

        try {

            if (treeDto == null) { return rtSB; }

            if ( (StringUtil.checkNull(treeDto.getTreeKind()).equals(""))
                    || (treeDto.getTreeKind().equals(IConstants.TOINDEX_TO) && StringUtil.checkNull(treeDto.getRefToKey()).equals(""))
                    || (treeDto.getTreeKind().equals(IConstants.TOINDEX_MY) && StringUtil.checkNull(treeDto.getRefUserId()).equals("")) ) {
                return rtSB;
            }

            /**
             * 1. DB에서 데이터 추출하는 MyBatis의 DAO 처리 모듈 호출
             * 2. 스트링으로 된 XML 데이터를 W3C DOM으로 생성하는 모듈 호출
             * 3. XML 데이터를 W3C DOM으로 생성하는 모듈 호출
             * 4. DOM 데이터를 HTML로 파싱하는 모듈 호출
             */
            //Element toElem = toCollector.getToIndexDom(toIndexType, toKey, userId);
            //rtSB = ToParser.parseToIndex(toElem);
            //현재는 1,2만 수행. 3,4는 생략

            rtSB = treeCollector.getToTreeStr(treeDto);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeComponent.getToTree Exception:"+ex.toString());
        }

        return rtSB;
    }

    /**
     * 교범 목록
     */
    public ArrayList<SystemInfoDto> getSystemToList(SystemInfoDto sysDto) {

        ArrayList<SystemInfoDto> rtList = null;

        try {

            rtList = treeCollector.getSystemToList(sysDto);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeComponent.getSystemToList Exception:"+ex.toString());
        }

        return rtList;
    }

    /**
     * 교범 목차 Tree Dom 형태로 추출
     */
    public Node getToTreeDom(TreeXContDto treeDto) {

        Node rtNode = null;

        try {
            if (treeDto.getTreeKind() == null || treeDto.getRefToKey() == null) {
                return rtNode;
            }

            //해당 교범의 목차 트리를 Dom 형태로 추출하여 리턴
            rtNode = (Node) treeCollector.getToTreeDom(treeDto);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeComponent.getToTreeDom Exception:"+ex.toString());
        }

        return rtNode;
    }

    /**
     * 관련 교범 목록
     */
    public ArrayList<SystemInfoDto> getRelatedToList(SystemInfoDto sysDto) {
        ArrayList<SystemInfoDto> rtList = null;

        try {

            rtList = treeCollector.getRelatedToList(sysDto);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeComponent.getRelatedToList Exception:"+ex.toString());
        }

        return rtList;
    }

    /**
     * 그림/표목차 목록
     */
    public StringBuffer getSubTocoList(TreeXContDto treeDto) {
        StringBuffer rtSB = new StringBuffer();

        try {
            if (treeDto.getTocoListKind() == null || treeDto.getTocoListKind().equals("")) {
                return rtSB;
            }

            Node treeNode = (Node) treeCollector.getToTreeDom(treeDto);
            if (treeNode == null) {
                return rtSB;
            }

            String xpath = "";
            String vcontKind = "";
            if (treeDto.getTocoListKind().equals(IConstants.TOCOLIST_KIND_GRPH)) {
                xpath = XALAN.TOCOXALAN_GRPH;
                vcontKind = IConstants.VCONT_KIND_GRPH;
            } else if (treeDto.getTocoListKind().equals(IConstants.TOCOLIST_KIND_TABLE)) {
                xpath = XALAN.TOCOXALAN_TABLE;
                vcontKind = IConstants.VCONT_KIND_TABLE;
                //2023.05.10 jysi EDIT : 동영상목차 페이지 목록 만들기(vcKind=>05는 IPB여서 03(그림) 사용)
            } else if (treeDto.getTocoListKind().equals(IConstants.TOCOLIST_KIND_VIDEO)) {
                xpath = XALAN.TOCOXALAN_VIDEO;
                vcontKind = IConstants.VCONT_KIND_GRPH;
            }
            NodeList treeList = XmlDomParser.getNodeListFromXPathAPI(treeNode, xpath);

            if (treeList != null && treeList.getLength() > 0) {
                for (int i=0; i<treeList.getLength(); i++) {
                    NamedNodeMap treeAttr = treeList.item(i).getAttributes();
                    String toKey			= treeDto.getRefToKey();
                    String tocoId		= XmlDomParser.getAttributes(treeAttr, ATTR.SYS_TOCO_ID);
                    String tocoName	= XmlDomParser.getAttributes(treeAttr, ATTR.SYS_TOCO_NAME);
                    String grphId		= XmlDomParser.getAttributes(treeAttr, ATTR.SYS_ID);
                    String grphName	= XmlDomParser.getAttributes(treeAttr, ATTR.SYS_NAME);

                    rtSB.append( CSS.getSubTocoList(toKey, tocoId, tocoName, vcontKind, grphId, grphName, (i%2)) );
                }

                if (rtSB.length() > 0) {
                    rtSB.insert(0, CSS.TB_TABLE_SUBTOCO);
                    rtSB.append(CSS.TB_TABLEEND);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("TreeComponent.getSubTocoList Exception:"+ex.toString());
        }

        return rtSB;
    }
}
