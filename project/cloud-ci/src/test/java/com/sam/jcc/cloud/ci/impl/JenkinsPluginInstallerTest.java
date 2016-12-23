package com.sam.jcc.cloud.ci.impl;

import com.offbytwo.jenkins.JenkinsServer;
import org.junit.Test;

import java.util.Map.Entry;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author Alexey Zhytnik
 * @since 20.12.2016
 */
public class JenkinsPluginInstallerTest extends JenkinsBaseTest {

    JenkinsServer server;
    JenkinsPluginInstaller installer;

    public void setUp() {
        server = spy(jenkins.getServer());
        installer = new JenkinsPluginInstaller(server);
    }

    @Test
    public void installsPlugins() {
        installer.install(plugin());
    }

    @Test
    public void usesPluginManager() throws Exception {
        installer.install(plugin());
        verify(server).getPluginManager();
    }

    Set<Entry<String, String>> plugin() {
        return singleton(entry("copyartifact", "1.38.1"));
    }
}