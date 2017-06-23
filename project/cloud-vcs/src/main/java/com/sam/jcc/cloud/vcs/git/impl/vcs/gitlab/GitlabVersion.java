package com.sam.jcc.cloud.vcs.git.impl.vcs.gitlab;

import lombok.Data;

@Data
public class GitlabVersion {
    private String version;
    private String revision;
}
