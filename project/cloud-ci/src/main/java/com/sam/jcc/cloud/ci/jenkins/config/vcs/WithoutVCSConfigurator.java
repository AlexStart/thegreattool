package com.sam.jcc.cloud.ci.jenkins.config.vcs;

import org.springframework.stereotype.Component;

/**
 * Configurator for projects without VCS and with using copy-to-workspace plugin
 */
@Component
public class WithoutVCSConfigurator implements VCSConfigurator {
    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setUp(VCSConfigurationData data) {
        data.getConfig().getScm().setClazz("hudson.scm.NullSCM");
        final String dir = data.getWorkspace().get(data.getProject()).getAbsolutePath();
        data.getConfig().getBuildWrappers()
                .getHpiCopyDataToWorkspacePlugin()
                .setFolderPath(dir);
    }
}
