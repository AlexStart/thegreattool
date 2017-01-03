package com.sam.jcc.cloud.ci;

import com.sam.jcc.cloud.i.IStatusable;
import com.sam.jcc.cloud.i.ci.ICIMetadata;
import lombok.Data;

import java.io.File;

import static com.google.common.base.Objects.toStringHelper;
import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.ci.CIProjectStatus.CONFIGURED;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
@Data
public class CIProject implements ICIMetadata, IStatusable {

    public final static String CI_PREFIX = getProperty("ci.prefix");

    private String groupId;
    private String artifactId;

    private File sources;
    private byte[] build;

    private CIProjectStatus status = CONFIGURED;

    public String getName() {
        return CI_PREFIX + artifactId;
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("name", getName())
                .add("status", status)
                .toString();
    }
}
