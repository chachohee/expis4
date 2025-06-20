package com.expis.manage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ExpisXmlWrapper {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "version")
    private List<VersionXmlWrapper> versionList;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "system")
    private List<SystemXmlWrapper> systemList;

}
