package com.sam.jcc.cloud.ci.impl;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.model.Plugin;
import com.sam.jcc.cloud.ci.exception.CIException;
import com.sam.jcc.cloud.exception.NotImplementedCloudException;
import com.sam.jcc.cloud.i.Experimental;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.makeAccessible;

/**
 * @author Alexey Zhytnik
 * @since 18-Dec-16
 */
@Slf4j
@Experimental(
        "Checks installed plugins, installs new plugins, " +
        "but after installation needs in Jenkins restart"
)
class JenkinsPluginInstaller {

    private static final String INSTALLATION_PATH = "/pluginManager/installNecessaryPlugins";

    private long maxInstallTimeOut;

    private JenkinsServer server;
    private JenkinsHttpClient client;

    public JenkinsPluginInstaller(JenkinsServer server) {
        this.server = server;
        this.client = extractHttpClient(server);
        this.maxInstallTimeOut = Long.valueOf(getProperty("jenkins.install.timeout"));
    }

    public void install(Set<Entry<String, String>> plugins) {

        final Set<Entry<String, String>> pluginsForInstall = plugins.stream()
                .filter(p -> !isInstalled(p.getKey()))
                .collect(Collectors.toSet());

        pluginsForInstall.forEach(this::installAndWait);

        if(!pluginsForInstall.isEmpty()) refreshPlugins();
    }

    private boolean isInstalled(String name) {
        final List<Plugin> plugins = getPluginManager().getPlugins();

        final Optional<Plugin> plugin = plugins.stream()
                .filter(p -> name.equals(p.getShortName()))
                .findAny();

        return plugin.isPresent();
    }

    public void installAndWait(Entry<String, String> plugin) {
        install(plugin);
        waitInstallation(plugin);
    }

    public void install(Entry<String, String> plugin) {
        log.info("{0}-{1} will be installed", plugin.getKey(), plugin.getValue());

        try {
            client.post_xml(
                    INSTALLATION_PATH,
                    installPluginQuery(plugin.getKey(), plugin.getValue())
            );
        } catch (IOException e) {
            throw new CIException(e);
        }
    }

    private String installPluginQuery(String name, String version) {
        return format("<jenkins><install plugin=\"{0}@{1}\"/></jenkins>", name, version);
    }

    private void waitInstallation(Entry<String, String> plugin) {
        long remaining = maxInstallTimeOut;

        while (!isInstalled(plugin.getKey()) && remaining > 0L) {
            log.info("Installation waiting of {0}-{1}", plugin.getKey(), plugin.getValue());

            sleepUninterruptibly(1_000L, MILLISECONDS);
            remaining -= 1_000L;
        }
    }

    //TODO: missed something important installation step, needs Jenkins restart
    private void refreshPlugins() {
        throw new NotImplementedCloudException();
    }

    private JenkinsHttpClient extractHttpClient(JenkinsServer server) {
        final Field client = findField(JenkinsServer.class, "client");
        makeAccessible(client);
        return (JenkinsHttpClient) getField(client, server);
    }

    private com.offbytwo.jenkins.model.PluginManager getPluginManager() {
        try {
            return server.getPluginManager();
        } catch (IOException e) {
            throw new CIException(e);
        }
    }
}
