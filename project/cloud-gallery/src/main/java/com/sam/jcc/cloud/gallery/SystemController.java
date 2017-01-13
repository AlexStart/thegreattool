package com.sam.jcc.cloud.gallery;

import com.sam.jcc.cloud.TranslationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Map.Entry;

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
    public Map<String, String> getTranslations(@RequestParam Map<String, String> keys) {
        return keys.entrySet().stream().collect(toMap(Entry::getKey, this::translate));
    }

    private String translate(Entry<String, String> entry) {
        return translations.getLocalizedMetadata(entry.getValue());
    }
}
