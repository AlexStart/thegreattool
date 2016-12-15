package com.sam.jcc.cloud.utils.parsers;

import com.sam.jcc.cloud.exception.BusinessCloudException;

import java.io.File;

/**
 * @author Alexey Zhytnik
 * @since 15-Dec-16
 */
public class MetadataNotFoundException extends BusinessCloudException {

    public MetadataNotFoundException(String item, String context) {
        super("parser.metadata.notFound", item, context);
    }

    public MetadataNotFoundException(File project) {
        super("parser.metadata.unknown", project);
    }
}
