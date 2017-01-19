package com.sam.jcc.cloud.ci.impl;

import org.junit.Before;
import org.junit.Test;

import java.util.Map.Entry;
import java.util.Set;

import static java.util.Collections.singleton;
import static org.assertj.core.data.MapEntry.entry;
import static org.junit.Assert.fail;

/**
 * @author Alexey Zhytnik
 * @since 20.12.2016
 */
public class JenkinsPluginInstallerTest extends JenkinsBaseTest {

    JenkinsPluginInstaller installer;

    @Before
    public void setUp() {
        installer = new JenkinsPluginInstaller(jenkins.getServer());
    }

    @Test
    public void installsPlugins() {
        installer.install(plugin());

        if (!installer.isInstalled("copyartifact")) fail();
    }

    Set<Entry<String, String>> plugin() {
        return singleton(entry("copyartifact", "1.38.1"));
    }
}