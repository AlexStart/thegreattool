package com.sam.jcc.cloud.gallery;

import com.sam.jcc.cloud.TranslationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Alexey Zhytnik
 * @since 12-Jan-17
 */
@RestController
public class SystemController {

    @Autowired
    TranslationResolver translations;

    @RequestMapping(value = "translations", method = GET)
    public Map<String, String> getTranslations(@RequestParam List<String> keys) {
        return keys.stream().collect(toMap(Object::toString, translations::getLocalizedMetadata));
    }
}
