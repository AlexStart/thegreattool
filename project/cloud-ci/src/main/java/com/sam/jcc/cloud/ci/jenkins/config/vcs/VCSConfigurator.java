package com.sam.jcc.cloud.ci.jenkins.config.vcs;

/**
 * Contains functionality for changing Jenkins config and configure VCS
 */
public interface VCSConfigurator {

    //TODO[rfisenko 6/16/17]: may be use enum
    String getType();

    void setUp(VCSConfigurationData data);

}
