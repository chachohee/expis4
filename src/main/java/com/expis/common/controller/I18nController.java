package com.expis.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/EXPIS/i18n")
public class I18nController {
    private final ReloadableResourceBundleMessageSource messageSource;
    private final ResourceLoader resourceLoader;

    @GetMapping
    public Map<String, String> allMessages(@RequestParam(defaultValue = "ko") String lang) throws IOException {
        Locale locale = Locale.forLanguageTag(lang);

        Properties properties = new Properties();

        for (String basename : messageSource.getBasenameSet()) {
            String location = basename + ".properties";
            Resource resource = resourceLoader.getResource(location);
            if (!resource.exists()) continue;

            try (InputStream in = resource.getInputStream();
                 Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                Properties p = new Properties();
                p.load(reader);
                properties.putAll(p);
            }
        }

        return properties.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> e.getValue().toString()
                ));
    }
}
