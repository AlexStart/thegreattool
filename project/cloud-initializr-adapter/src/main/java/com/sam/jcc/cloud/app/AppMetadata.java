package com.sam.jcc.cloud.app;

import com.sam.jcc.cloud.i.app.IAppMetadata;

import lombok.Data;

/**
 * @author Alexey Zhytnik
 * @since 09.01.2017
 */
@Data
public class AppMetadata implements IAppMetadata {

    private Long id;
    private String projectName;
    private String type;
    private String ci;
    private String jobName;
    private String db;
    private String vcs;
}
