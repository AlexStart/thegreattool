package com.sam.jcc.cloud.ci.jenkins.config.vcs;

import com.sam.jcc.cloud.ci.CIProject;

import java.net.URI;

public abstract class GitConfigurator implements VCSConfigurator {

    @Override
    public void setUp(VCSConfigurationData data) {
        //TODO[rfisenko 6/7/17]: prepare all config values for deleting dependency to basic-config.xml
        data.getConfig().getScm().getUserRemoteConfigs().getHudsonPluginsGitUserRemoteConfig()
                .setUrl(resolveGitURL(data.getProject()).toString());
    }

    protected abstract URI resolveGitURL(CIProject project);
}
