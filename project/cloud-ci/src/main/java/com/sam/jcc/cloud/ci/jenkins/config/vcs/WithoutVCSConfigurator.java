package com.sam.jcc.cloud.ci.jenkins.config.vcs;

import com.sam.jcc.cloud.ci.jenkins.config.JenkinsProjectConfiguration;
import org.springframework.stereotype.Component;

/**
 * Configurator for projects without VCS and with using copy-to-workspace plugin
 */
@Component
public class WithoutVCSConfigurator implements VCSConfigurator {
    //TODO[rfisenko 6/19/17]: create plugin version properties
    private static final String PLUGIN_VERSION = "1.0";
    private static final String SCM_CLASS = "hudson.scm.NullSCM";
    private static final String PLUGIN_NAME = "copy-data-to-workspace-plugin";

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setUp(VCSConfigurationData data) {
        JenkinsProjectConfiguration config = data.getConfig();

        config.setScm(new JenkinsProjectConfiguration.Scm());
        config.getScm().setClazz(SCM_CLASS);

        config.getBuildWrappers().setHpiCopyDataToWorkspacePlugin(
                new JenkinsProjectConfiguration.BuildWrappers.HpiCopyDataToWorkspacePlugin());
        config.getBuildWrappers()
                .getHpiCopyDataToWorkspacePlugin()
                .setPlugin(PLUGIN_NAME + "@" + PLUGIN_VERSION);
        config.getBuildWrappers()
                .getHpiCopyDataToWorkspacePlugin()
                .setFolderPath(data.getWorkspace().get(data.getProject()).getAbsolutePath());
        config.getBuildWrappers()
                .getHpiCopyDataToWorkspacePlugin()
                .setMakeFilesExecutable("true");
        config.getBuildWrappers()
                .getHpiCopyDataToWorkspacePlugin()
                .setDeleteFilesAfterBuild("false");
    }
}
