package com.expis.common.ext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExtConfig {

    // webpath.properties에서 읽어온 값들
    @Value("${app.expis.project}")
    private String PROJECT;
//    @Value("${app.expis.m_project}")
//    private String M_PROJECT;
//    @Value("${app.expis.dbtype}")
//    private String DBTYPE;
//    @Value("${app.expis.df_biz}")
//    private String DF_BIZ;
//    @Value("${app.expis.df_css}")
//    private String DF_CSS;
//    @Value("${app.expis.df_img}")
//    private String DF_IMG;
//    @Value("${app.expis.df_js}")
//    private String DF_JS;
//    @Value("${app.expis.df_jsp}")
//    private String DF_JSP;
//    @Value("${app.expis.df_ietmdata}")
//    private String DF_IETMDATA;
//    @Value("${app.expis.df_cover}")
//    private String DF_COVER;
//    @Value("${app.expis.df_syspath}")
//    private String DF_SYSPATH;
//    @Value("${app.expis.df_webdata_syspath}")
//    private String DF_WEBDATA_SYSPATH;
//    @Value("${app.expis.en_css}")
//    private String DF_EN_CSS;
//    @Value("${app.expis.en_img}")
//    private String DF_EN_IMG;

    // Mobile Path
//    @Value("${app.expis.df_m_biz}")
//    private String DF_M_BIZ;
//    @Value("${app.expis.df_m_css}")
//    private String DF_M_CSS;
//    @Value("${app.expis.df_m_img}")
//    private String DF_M_IMG;
//    @Value("${app.expis.df_m_js}")
//    private String DF_M_JS;
//    @Value("${app.expis.df_m_jsp}")
//    private String DF_M_JSP;
//    @Value("${app.expis.df_m_ietmdata}")
//    private String DF_M_IETMDATA;
//    @Value("${app.expis.df_m_syspath}")
//    private String DF_M_SYSPATH;
//    @Value("${app.expis.en_m_css}")
//    private String EN_M_CSS;
//    @Value("${app.expis.en_m_img}")
//    private String EN_M_IMG;

    // 기타 설정값들
//    @Value("${app.expis.model_img}")
//    private String modelImg;  // 20201118 add LYM
//    @Value("${app.expis.biz_code}")
//    private String bizCode;   // 20201123 add LYM
//    @Value("${app.expis.type}")
//    private String type;      // 20211221 add Park Js

    // commonValidate.properties에서 읽어온 값들
//    @Value("${app.expis.commonBizCode}")
//    private String commonBizCode;

//    @Value("${app.expis.mig_server_url}")
//    private String MIG_SERVER_URL;  // 20221201 add ejkim
//    @Value("${app.expis.mig_biz}")
//    private String MIG_BIZ;         // 20221201 add ejkim
//    @Value("${app.expis.mig_downfile_page}")
//    private String MIG_DOWNFILE_PAGE; // 20221201 add ejkim
//    @Value("${app.expis.mig_admin_id}")
//    private String MIG_ADMIN_ID;     // 20221201 add ejkim
//    @Value("${app.expis.mig_admin_pw}")
//    private String MIG_ADMIN_PW;     // 20221201 add ejkim
//    @Value("${app.expis.mig_type}")
//    private String MIG_TYPE;         // 20221201 add ejkim

    // 추가 설정
    @Value("${app.expis.shape_type: ''}")
    private String SHAPE_TYPE;
    @Value("${app.expis.check_index: ''}")
    private String CHECK_INDEX;

    @Bean
    public ExternalFileEx extConf() {

        ExternalFileEx ext = new ExternalFileEx();

        ext.setPROJECT(PROJECT);
//        ext.setM_PROJECT(M_PROJECT);
//        ext.setDBTYPE(DBTYPE);
//        ext.setDF_BIZ(DF_BIZ);
//        ext.setDF_CSS(DF_CSS);
//        ext.setDF_IMG(DF_IMG);
//        ext.setDF_JS(DF_JS);
//        ext.setDF_JSP(DF_JSP);
//        ext.setDF_IETMDATA(DF_IETMDATA);
//        ext.setDF_COVER(DF_COVER);
//        ext.setDF_SYSPATH(DF_SYSPATH);
//        ext.setDF_WEBDATA_SYSPATH(DF_WEBDATA_SYSPATH);
//        ext.setDF_EN_CSS(DF_EN_CSS);
//        ext.setDF_EN_IMG(DF_EN_IMG);
//        ext.setDF_M_BIZ(DF_M_BIZ);
//        ext.setDF_M_CSS(DF_M_CSS);
//        ext.setDF_M_JS(DF_M_JS);
//        ext.setDF_M_JSP(DF_M_JSP);
//        ext.setDF_M_IETMDATA(DF_M_IETMDATA);
//        ext.setDF_M_SYSPATH(DF_M_SYSPATH);
//        ext.setEN_M_CSS(EN_M_CSS);
//        ext.setEN_M_IMG(EN_M_IMG);

//        ext.setModelImg(modelImg);
//        ext.setBizCode(bizCode);
//        ext.setType(type);

//        ext.setCommonBizCode(commonBizCode);

//        ext.setMIG_SERVER_URL(MIG_SERVER_URL);
//        ext.setMIG_BIZ(MIG_BIZ);
//        ext.setMIG_DOWNFILE_PAGE(MIG_DOWNFILE_PAGE);
//        ext.setMIG_ADMIN_ID(MIG_ADMIN_ID);
//        ext.setMIG_ADMIN_PW(MIG_ADMIN_PW);
//        ext.setMIG_TYPE(MIG_TYPE);

        ext.setSHAPE_TYPE(SHAPE_TYPE);
        ext.setCHECK_INDEX(CHECK_INDEX);

        return ext;
    }
}
