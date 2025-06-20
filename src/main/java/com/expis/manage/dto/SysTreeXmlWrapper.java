package com.expis.manage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SysTreeXmlWrapper {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "system")
    private List<SystemXmlWrapper> systemList;

}
