package com.expis.domparser;

import com.expis.common.eXPIS3Constants;

public class eXPIS3Converter {

    public static String getTocoTypeWordFromCode(String tocoCode) {
        return switch (tocoCode) {
            case eXPIS3Constants.STOCO_TYPE_TO -> "TO";
            case eXPIS3Constants.STOCO_TYPE_INTRO -> "INTRO";
            case eXPIS3Constants.STOCO_TYPE_CHAPTER -> "CHAPTER";
            case eXPIS3Constants.STOCO_TYPE_SECTION -> "SECTION";
            case eXPIS3Constants.STOCO_TYPE_TOPIC -> "TOPIC";
            case eXPIS3Constants.STOCO_TYPE_SUBTOPIC -> "SUBTOPIC";
            case eXPIS3Constants.STOCO_TYPE_TASK -> "TASK";
            case eXPIS3Constants.STOCO_TYPE_WC_UNIT, eXPIS3Constants.STOCO_TYPE_WC_FIELD -> "WC";
            case eXPIS3Constants.STOCO_TYPE_IPB_UNIT, eXPIS3Constants.STOCO_TYPE_IPB_FIELD, eXPIS3Constants.STOCO_TYPE_IPB_ROOT -> "IPB";
            case eXPIS3Constants.STOCO_TYPE_FI_DI, eXPIS3Constants.STOCO_TYPE_FI_DIDESC, eXPIS3Constants.STOCO_TYPE_FI_FIA, eXPIS3Constants.STOCO_TYPE_FI_LR, eXPIS3Constants.STOCO_TYPE_FI_LRDESC,
                 eXPIS3Constants.STOCO_TYPE_FI_AP, eXPIS3Constants.STOCO_TYPE_FI_FIPIC, eXPIS3Constants.STOCO_TYPE_FI_DDS, eXPIS3Constants.STOCO_TYPE_FI_FIB -> "FI";
            default -> tocoCode;
        };
    }
}
