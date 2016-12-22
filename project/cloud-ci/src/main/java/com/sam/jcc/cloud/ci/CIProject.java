package com.sam.jcc.cloud.ci;

import com.sam.jcc.cloud.i.IStatusable;
import lombok.Data;

import java.io.File;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.ci.CIProjectStatus.CONFIGURED;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
@Data
public class CIProject implements IStatusable {

    private final static String REPOSITORY_PREFIX = getProperty("ci.prefix");

    private File sources;

    private String groupId;
    private String artifactId;

    private CIProjectStatus status = CONFIGURED;

    public String getName() {
        return REPOSITORY_PREFIX + artifactId;
    }
}
