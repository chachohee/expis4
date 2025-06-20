package com.expis.common.config;

import com.expis.login.interceptor.LoginCheckInterceptor;
import com.expis.login.interceptor.LoginMemberArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.expis.fileUploadPath}")
    private String fileUploadPath;

    @Value("${app.expis.bizCode}")
    private String bizCode;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/EXPIS/**/login", "/EXPIS/**/logout",
                        "/component/**", "/EXPIS/rsa/**", "/uploads/**", "/EXPIS/i18n",
                        "/js/**", "/css/**", "/ietmdata/**",
                        "/*.ico", "/*.png", "/*.svg", "/*.jpg",
                        "/xslt/**", "/*.xml", "/img/**",
                        "/popup", "/signup/**",
                        "/error");
//
//        registry.addInterceptor(new RoutingDataInterceptor())
//                .order(2);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //WebMvcConfigurer.super.addArgumentResolvers(resolvers);
        resolvers.add(new LoginMemberArgumentResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        //CMNT
        registry.addResourceHandler("/EXPIS/" + bizCode + "/ietmdata/cmnt/**")
                .addResourceLocations("file:" + fileUploadPath);

        //T50
        registry.addResourceHandler("/EXPIS/T50/ietmdata/**")
                .addResourceLocations("file:///C:/EXPIS3/ietmdata/T50/");
//                .addResourceLocations("file:/EXPIS3/ietmdata/T50/");
        //FA50
        registry.addResourceHandler("/EXPIS/FA50/ietmdata/**")
                .addResourceLocations("file:/EXPIS3/ietmdata/FA50/");

        registry.addResourceHandler("/EXPIS/KT1/ietmdata/**")
                .addResourceLocations("file:/EXPIS3/ietmdata/KT1/");
    }

}
