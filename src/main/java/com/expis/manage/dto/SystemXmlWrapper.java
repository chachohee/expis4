package com.expis.manage.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SystemXmlWrapper {
    @JacksonXmlProperty(isAttribute = true)
    private String cdm;

    @JacksonXmlProperty(isAttribute = true)
    private String changeno;

    @JacksonXmlProperty(isAttribute = true)
    private String chgdate;

    @JacksonXmlProperty(isAttribute = true)
    private String dummy;

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlProperty(isAttribute = true)
    private String itemid;

    @JacksonXmlProperty(isAttribute = true)
    private String listname;

    @JacksonXmlProperty(isAttribute = true)
    private String listtype;

    @JacksonXmlProperty(isAttribute = true)
    private String listuuid;

    @JacksonXmlProperty(isAttribute = true)
    private String name;

    @JacksonXmlProperty(isAttribute = true)
    private String part;

    @JacksonXmlProperty(isAttribute = true)
    private String psysid;

    @JacksonXmlProperty(isAttribute = true)
    private String ref;

    @JacksonXmlProperty(isAttribute = true)
    private String status;

    @JacksonXmlProperty(isAttribute = true)
    private String saveyn;

    @JacksonXmlProperty(isAttribute = true)
    private String security;

    @JacksonXmlProperty(isAttribute = true)
    private String sssn;

    @JacksonXmlProperty(isAttribute = true)
    private String subname;

    @JacksonXmlProperty(isAttribute = true)
    private String supplement;

    @JacksonXmlProperty(isAttribute = true)
    private String totype;

    @JacksonXmlProperty(isAttribute = true)
    private String type;

    @JacksonXmlProperty(isAttribute = true)
    private String vehicletype;

    @JacksonXmlProperty(isAttribute = true)
    private String version;

    @JacksonXmlProperty(isAttribute = true)
    private String versiondate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "system")
    private List<SystemXmlWrapper> systemList;

    // 상위 노드 찾기
    public SystemXmlWrapper findParentOf(SystemXmlWrapper child) {
        if (systemList != null) {
            for (SystemXmlWrapper s : systemList) {
                if ( s == child ) {
                    return this;
                }
                //하위 노드도 찾음
                SystemXmlWrapper found = s.findParentOf(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }


//    @JsonInclude(JsonInclude.Include.NON_EMPTY)
//    @JacksonXmlElementWrapper(useWrapping = false)
//    @JacksonXmlProperty(localName = "descinfo")
//    private List<DescInfoXmlWrapper> descinfoList;
//
//    @JsonInclude(JsonInclude.Include.NON_EMPTY)
//    @JacksonXmlElementWrapper(useWrapping = false)
//    @JacksonXmlProperty(localName = "task")
//    private List<TaskXmlWrapper> taskList;

}
