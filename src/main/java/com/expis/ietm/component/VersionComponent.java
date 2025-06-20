package com.expis.ietm.component;

import com.expis.common.IConstants;
import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.domparser.DTD;
import com.expis.domparser.TITLE;
import com.expis.domparser.VAL;
import com.expis.ietm.collector.VersionCollector;
import com.expis.ietm.dao.TocoInfoMapper;
import com.expis.ietm.dao.VersionInfoMapper;
import com.expis.ietm.dao.XContVersionMapper;
import com.expis.ietm.dto.TocoInfoDto;
import com.expis.ietm.dto.VersionInfoDto;
import com.expis.ietm.dto.XContDto;
import com.expis.ietm.parser.ContParser;
import com.expis.ietm.parser.ParserDto;
import com.expis.ietm.parser.VersionParser;
import com.expis.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * [IETM-TM]교범 버전 정보 Component Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VersionComponent {

    private final VersionCollector versionCollector;

    // ObjectProvider를 사용하여 지연 로딩
    private final ObjectProvider<VersionParser> versionParserProvider;
    private final ObjectProvider<ContParser> contParserProvider;
    private final VersionInfoMapper verInfoMapper;
    private final TocoInfoMapper tocoMapper;
    private final XContVersionMapper verXcontMapper;

    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
    ExternalFileEx ext = ctx.getBean("extConf", ExternalFileEx.class);

    /**
     * 버전 목록 카운트
     */
    public int getVersionCount(VersionInfoDto dto) {

        int nResult = 0;

        try {

            nResult = verInfoMapper.selectCountDao(dto);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("VersionComponent.getVersionCount Exception:" + ex.toString());
        }

        return nResult;
    }

    /**
     * 버전 목록
     */
    public ArrayList<VersionInfoDto> getVersionList(VersionInfoDto dto) {

        ArrayList<VersionInfoDto> rtList = null;

        try {

            rtList = verInfoMapper.selectListDao(dto);

            log.info("getVersionList rtList=" + rtList.size());

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("VersionComponent.getVersionList Exception:" + ex.toString());
        }

        return rtList;
    }

    /**
     * 버전별 목차 목록 카운트
     */
    public int getVersionTocoCount(TocoInfoDto dto) {

        int nResult = 0;

        try {

            nResult = tocoMapper.selectCountVersionDao(dto);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("VersionComponent.getVersionTocoCount Exception:" + ex.toString());
        }

        return nResult;
    }

    /**
     * 버전별 목차 목록
     */
    public ArrayList<TocoInfoDto> getVersionTocoList(TocoInfoDto dto) {

        ArrayList<TocoInfoDto> rtList = null;

        try {

            rtList = tocoMapper.selectListVersionDao(dto);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("VersionComponent.getVersionTocoList Exception:" + ex.toString());
        }

        return rtList;
    }

    /**
     * 버전 상세정보
     */
    public VersionInfoDto getVersionDetail(VersionInfoDto dto) {

        VersionInfoDto rtDto = null;

        try {
            rtDto = verInfoMapper.selectDetailDao(dto);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("VersionComponent.getVersionDetail Exception:" + ex.toString());
        }

        return rtDto;
    }

    /**
     * 버전 등록
     */
    public boolean insertVersion(VersionInfoDto dto) {

        boolean bresult = false;

        try {

            int irtn = verInfoMapper.insertDao(dto);

            if (irtn > 0) {
                bresult = true;
            } else {
                bresult = false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("VersionComponent.updateVersion Exception:" + ex.toString());
        }

        return bresult;
    }

    /**
     * 버전 수정
     */
    public boolean updateVersion(VersionInfoDto dto) {

        boolean bresult = false;

        try {

            int irtn = verInfoMapper.updateDao(dto);

            if (irtn > 0) {
                bresult = true;
            } else {
                bresult = false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("VersionComponent.updateVersion Exception:" + ex.toString());
        }

        return bresult;
    }

    /**
     * 버전 삭제
     */
    public boolean deleteVersion(VersionInfoDto dto) {

        boolean bresult = false;

        try {

            int irtn = verInfoMapper.deleteDao(dto);

            if (irtn > 0) {
                bresult = true;
            } else {
                bresult = false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("VersionComponent.deleteVersion Exception:" + ex.toString());
        }

        return bresult;
    }

    /**
     * 변경바 클릭시 팝업에서 변경정보 시현 - 상단의 변경번호, 변경일자
     */
    public VersionInfoDto getVersionInfo(XContDto contDto) {

        VersionInfoDto rtDto = new VersionInfoDto();

        try {
            VersionInfoDto verDto = new VersionInfoDto();
            verDto.setToKey(contDto.getToKey());
            verDto.setVerId(contDto.getVerId());

            rtDto = verInfoMapper.selectDetailDao(verDto);
            //2022 03 30 Park.J.S : TP 처리위해 버전 정보가 없을경우 like %TP-% 되게 수정
            if(rtDto == null){
                verDto = new VersionInfoDto();
                verDto.setToKey("%TP-%");
                verDto.setVerId(contDto.getVerId());
                rtDto = verInfoMapper.selectDetailDao(verDto);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("VersionComponent.getVersionInfo Exception:" + ex.toString());
        }

        return rtDto;
    }

    /**
     * 변경바 클릭시 팝업에서 변경정보 시현 - 본문의 변경내용
     */
    public StringBuffer getVersionXCont(XContDto contDto) {

        StringBuffer rtSB = new StringBuffer();

        try {
            // 2023.07.19 - language 처리 추가 - jingi.kim
            String msg_ver_append = TITLE.VER_APPEND;
            String msg_ver_update = TITLE.VER_UPDATE;
            String msg_ver_update_2 = TITLE.VER_UPDATE_2;
            if ( contDto.getLanguageType() != null && "en".equalsIgnoreCase(contDto.getLanguageType().toString()) ) {
                msg_ver_append = TITLE.VER_APPEND_EN;
                msg_ver_update = TITLE.VER_UPDATE_EN;
                msg_ver_update_2 = TITLE.VER_UPDATE_2_EN;
            }

            if (contDto.getVerStatus().equals(VAL.CONT_STATUS_APPEND)) {
                rtSB.append(msg_ver_append);

            } else if (contDto.getVerStatus().equals(VAL.CONT_STATUS_UPDATE)) {
                String bizName	= StringUtil.checkNull(contDto.getBiz());
                ParserDto psDto = new ParserDto();
                {
                    String ietmdata = ext.getDF_IETMDATA();
                    String syspath = ext.getDF_SYSPATH();

                    psDto.setBiz(bizName);
//                    psDto.setBizIetmdata( StringUtil.checkNull( StringUtil.getRemoveSlash(ietmdata) ) );
//                    psDto.setBizSyspath( StringUtil.checkNull( StringUtil.getRemoveSlash(syspath) ) );
                    psDto.setSearchWord( StringUtil.checkNull( contDto.getSearchWord() ) );
                    psDto.setToKey( StringUtil.checkNull( contDto.getToKey() ) );
                    psDto.setTocoId( StringUtil.checkNull( contDto.getTocoId() ) );
                    psDto.setUserId( StringUtil.checkNull( contDto.getUserId() ) );
                    psDto.setTocoType(IConstants.TOCO_TYPE_NONE);
                    psDto.setVehicleType( StringUtil.checkNull( contDto.getVehicleType() ) );
                    psDto.setLastVersionId( StringUtil.checkNull( contDto.getLastVersionId() ) );
                    psDto.setWebMobileKind(StringUtil.checkNull( contDto.getWebMobileKind()));
                    psDto.setVersionStatus("ver");

                    String lang = contDto.getLanguageType();
                    if(lang != null) psDto.setLanguageType(lang);
                }

                // versionParser는 ObjectProvider를 통해 지연 로딩
                VersionParser versionParser = versionParserProvider.getIfAvailable();
                Element contElem = versionCollector.getVersionContDom(contDto);
                log.info("contDto.getTocoId() : "+contDto.getTocoId());
                if(psDto.getTocoId() == null || "".equals(psDto.getTocoId())) {
                    psDto.setTocoId(contDto.getTocoId());
                }
                rtSB = versionParser.getPageHtml(psDto, contElem, 0, -1, contDto.getChangebasis(), contDto);
                log.info("rtSB : "+rtSB);
                if (rtSB.length() <= 0) {
                    rtSB.append(msg_ver_update);
                }

                // 최신 변경 도해에는 변경 이전 정보가 공란으로 표시되기 때문에 "변경된 항목임" 문구 추가
                if(contElem.getChildNodes().getLength() == 1 && contElem.getFirstChild().getNodeName() == DTD.WC_WIMG) {
                    rtSB.delete(0, 0);
                    rtSB.append(msg_ver_update_2);
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("VersionComponent.getVersionXCont Exception:" + ex.toString());
        }

        return rtSB;
    }

    /**
     * 최신 변경정보 ver_id select
     */
    public String getLastVersion(XContDto contDto) {

        return verInfoMapper.selectLastVersion(contDto);
    }


    public String getLastVersionInfo(XContDto contDto) {
        return verInfoMapper.getLastVersionInfo(contDto);
    }


    public List<String> getVersionInfoList(VersionInfoDto versionDto) {
        return verInfoMapper.getVersionInfoList(versionDto);
    }

    public String getVersionName(VersionInfoDto versionDto) {
        return verInfoMapper.getVersionName(versionDto);
    }
}
