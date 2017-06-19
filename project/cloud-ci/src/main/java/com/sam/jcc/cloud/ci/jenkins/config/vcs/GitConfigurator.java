package com.sam.jcc.cloud.ci.jenkins.config.vcs;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.jenkins.config.JenkinsProjectConfiguration;

import java.net.URI;

public abstract class GitConfigurator implements VCSConfigurator {

    private static final String PLUGIN_NAME = "git";
    private static final String PLUGIN_VERSION = "3.3.0";
    private static final String SCM_CLASS = "hudson.plugins.git.GitSCM";
    private static final String DEFAULT_BRANCH = "*/master";
    private static final int CONFIG_VERSION = 2;

    @Override
    public void setUp(VCSConfigurationData data) {
        JenkinsProjectConfiguration config = data.getConfig();

        JenkinsProjectConfiguration.Scm scm = new JenkinsProjectConfiguration.Scm();
        config.setScm(scm);
        scm.setPlugin(PLUGIN_NAME + "@" + PLUGIN_VERSION);
        scm.setClazz(SCM_CLASS);
        scm.setConfigVersion(CONFIG_VERSION);

        scm.setUserRemoteConfigs(new JenkinsProjectConfiguration.Scm.UserRemoteConfigs());
        scm.getUserRemoteConfigs()
                .setHudsonPluginsGitUserRemoteConfig(new JenkinsProjectConfiguration.Scm.UserRemoteConfigs.HudsonPluginsGitUserRemoteConfig());
        scm.getUserRemoteConfigs().getHudsonPluginsGitUserRemoteConfig()
                .setUrl(resolveGitURL(data.getProject()).toString());

        scm.setBranches(new JenkinsProjectConfiguration.Scm.Branches());
        scm.getBranches().setHudsonPluginsGitBranchSpec(new JenkinsProjectConfiguration.Scm.Branches.HudsonPluginsGitBranchSpec());
        scm.getBranches().getHudsonPluginsGitBranchSpec().setName(DEFAULT_BRANCH);

        scm.setDoGenerateSubmoduleConfigurations(false);
        scm.setSubmoduleCfg(new JenkinsProjectConfiguration.Scm.SubmoduleCfg());
        scm.getSubmoduleCfg().setClazz("list");
    }

    protected abstract URI resolveGitURL(CIProject project);
}
