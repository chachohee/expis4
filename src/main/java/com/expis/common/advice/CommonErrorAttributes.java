package com.expis.common.advice;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
public class CommonErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        options = options.excluding(
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.STACK_TRACE,
                ErrorAttributeOptions.Include.BINDING_ERRORS
        );

        Map<String, Object> attrs = super.getErrorAttributes(webRequest, options);

        //직접 제거
        attrs.remove("timestamp");
        attrs.remove("error");
        attrs.remove("path");
        attrs.remove("trace");

        return attrs;
    }

}
