package com.expis.ietm.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "replace")
public class ReplacePartDto {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "partbase")
    private List<PartbaseAttr> partbase;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PartbaseAttr {

        @JacksonXmlProperty(isAttribute = true)
        private String partnum;

        @JacksonXmlProperty(isAttribute = true)
        private String nsn;

        @JacksonXmlProperty(isAttribute = true)
        private String cage;

        @JacksonXmlProperty(isAttribute = true)
        private String kai_std;

    }

}
