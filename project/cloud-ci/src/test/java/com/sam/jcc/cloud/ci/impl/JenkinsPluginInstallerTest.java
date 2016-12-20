package com.sam.jcc.cloud.ci.impl;

import com.offbytwo.jenkins.JenkinsServer;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.singleton;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author Alexey Zhytnik
 * @since 20.12.2016
 */
public class JenkinsPluginInstallerTest {

    JenkinsServer server;
    JenkinsPluginInstaller installer;

    @Before
    public void setUp() {
        server = spy(new Jenkins().getServer());
        installer = new JenkinsPluginInstaller(server);
    }

    @Test
    public void usesPluginManager() throws Exception {
        installer.install(singleton(entry("copyartifact", "1.38.1")));
        verify(server).getPluginManager();
    }
}