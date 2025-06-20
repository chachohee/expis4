package com.expis.common.ext;

import lombok.Data;
import lombok.Value;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@Data
public class ExternalFileEx implements InitializingBean, DisposableBean {

    private String PROJECT;
    private String M_PROJECT;
    private String DBTYPE;

    private String DF_BIZ;
    private String DF_CSS;
    private String DF_IMG;
    private String DF_JS;
    private String DF_JSP;
    private String DF_IETMDATA;
    private String DF_COVER;
    private String DF_SYSPATH;
    private String DF_WEBDATA_SYSPATH;
    private String DF_EN_CSS;
    private String DF_EN_IMG;
    private String DF_M_BIZ;
    private String DF_M_CSS;
    private String DF_M_IMG;
    private String DF_M_JS;
    private String DF_M_JSP;
    private String DF_M_IETMDATA;
    private String DF_M_SYSPATH;
    private String EN_M_CSS;
    private String EN_M_IMG;

    private String modelImg;
    private String bizCode;
    private String type;			//2021 12 21 Add Park Js

    private String commonBizCode;   //2022 11 21 Add jysi

    private String MIG_SERVER_URL;		//20221201 add ejkim
    private String MIG_BIZ;				//20221201 add ejkim
    private String MIG_DOWNFILE_PAGE;	//20221201 add ejkim
    private String MIG_ADMIN_ID;		//20221201 add ejkim
    private String MIG_ADMIN_PW;		//20221201 add ejkim
    private String MIG_TYPE;			//20221201 add ejkim

    private String SHAPE_TYPE;
    private String CHECK_INDEX = "";

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
