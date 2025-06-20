package com.expis.manage.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VersionXmlWrapper {
    @JacksonXmlProperty(isAttribute = true)
    private String changeno;

    @JacksonXmlProperty(isAttribute = true)
    private String chgdate;

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlProperty(isAttribute = true)
    private String revdate;

    @JacksonXmlProperty(isAttribute = true)
    private String revision;

}
