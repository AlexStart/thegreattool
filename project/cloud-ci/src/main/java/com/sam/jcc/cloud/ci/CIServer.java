package com.sam.jcc.cloud.ci;

import java.io.InputStream;

/**
 * @author Alexey Zhytnik
 * @since 16-Dec-16
 */
public interface CIServer {

    void create(CIProject project);

    void build(CIProject project);

    void delete(CIProject project);

    CIProjectStatus getStatus(CIProject project);

    InputStream getBuild(CIProject project);
}
