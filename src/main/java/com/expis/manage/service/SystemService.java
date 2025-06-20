package com.expis.manage.service;

import com.expis.manage.dao.TreeXContMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemService {




    /**
     * System Type 이 문자열로 된것을 코드로 변환
     */
//    public String convertSysType(String typeName) {
//
//        String rtStr = "";
//
//        if (typeName == null || typeName.equals("")) {
//            return rtStr;
//        }
//
//        try {
//            typeName = typeName.toUpperCase();
//
//            if (typeName.equals(VAL.SYS_TYPE_SYS)) {
//                rtStr = IConstants.SYS_TYPE_SYS;
//            } else if (typeName.equals(VAL.SYS_TYPE_SUBSYS)) {
//                rtStr = IConstants.SYS_TYPE_SUBSYS;
//            } else if (typeName.equals(VAL.SYS_TYPE_ASS)) {
//                rtStr = IConstants.SYS_TYPE_ASS;
//            } else if (typeName.equals(VAL.SYS_TYPE_SUBASS)) {
//                rtStr = IConstants.SYS_TYPE_SUBASS;
//            } else if (typeName.equals(VAL.SYS_TYPE_COMP)) {
//                rtStr = IConstants.SYS_TYPE_COMP;
//            } else if (typeName.equals(VAL.SYS_TYPE_TO)) {
//                rtStr = IConstants.SYS_TYPE_TO;
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            logger.error(ex.getMessage(),ex);
//            //logger.debug("SystemRegister.convertSysType Exception:"+ex.toString());
//        }
//
//        return rtStr;
//    }

}
