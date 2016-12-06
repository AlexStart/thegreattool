package com.sam.jcc.cloud.utils.parsers;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Alexey Zhytnik
 * @since 03-Dec-16
 */
//TODO: maybe transfer to common utility-class
final class TestResourceReader {

    public static File read(String resource) {
        try {
            final URL url = TestResourceReader.class.getResource(resource);
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
