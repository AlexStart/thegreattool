package com.sam.jcc.cloud.ci.jenkins.config.vcs;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.jenkins.config.JenkinsProjectConfiguration;
import com.sam.jcc.cloud.utils.files.ItemStorage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VCSConfigurationData {
    private CIProject project;
    private JenkinsProjectConfiguration config;
    private ItemStorage<CIProject> workspace;

}
