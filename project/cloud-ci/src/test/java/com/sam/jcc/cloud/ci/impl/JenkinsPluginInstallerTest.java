package com.sam.jcc.cloud.ci.impl;

import com.offbytwo.jenkins.JenkinsServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Map.Entry;
import java.util.Set;

import static com.sam.jcc.cloud.ci.impl.JenkinsUtil.getJenkins;
import static java.util.Collections.singleton;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author Alexey Zhytnik
 * @since 20.12.2016
 */
public class JenkinsPluginInstallerTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    JenkinsServer server;
    JenkinsPluginInstaller installer;

    @Before
    public void setUp() throws Exception {
        server = spy(getJenkins(temp.newFolder()).getServer());
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