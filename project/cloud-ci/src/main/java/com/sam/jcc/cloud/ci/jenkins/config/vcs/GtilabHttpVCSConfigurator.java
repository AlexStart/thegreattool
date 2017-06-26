package com.sam.jcc.cloud.ci.jenkins.config.vcs;

import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.vcs.git.impl.provider.GitlabProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Functionality for configuring Jenkins config for VSC with type {@link GitlabProvider#TYPE}
 */
@Component
@Slf4j
public class GtilabHttpVCSConfigurator extends GitConfigurator {

    private static final String CREDENTIALS_GITLAB_ID = getProperty("ci.jenkins.gitlab.credentialsId");

    private final InitOnceAdminBean initAdmin;
    private final JenkinsCredentialsApi credentialsApi;

    public GtilabHttpVCSConfigurator(InitOnceAdminBean initAdmin, JenkinsCredentialsApi credentialsApi) {
        this.initAdmin = requireNonNull(initAdmin);
        this.credentialsApi = requireNonNull(credentialsApi);
    }

    @PostConstruct
    public void setUp() {
        String password = initAdmin.getRawPassword();
        if (isNotBlank(password)) {
            credentialsApi.createJenkinsCredentials(
                    CREDENTIALS_GITLAB_ID, getProperty("gitlab.remote.server.user"), password, CREDENTIALS_GITLAB_ID);
        }
    }

    @Override
    public String getType() {
        return GitlabProvider.TYPE;
    }

    @Override
    protected URI resolveGitURL(CIProject project) {
        try {
            String host = getProperty("gitlab.remote.server.host");
            Integer port = Integer.valueOf(getProperty("gitlab.remote.server.port"));
            String path = getProperty("gitlab.remote.server.path");
            String user = getProperty("gitlab.remote.server.user");
            return new URL("http", host, port, format("/{0}/{1}/{2}.git", path, user, project.getName())).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public void setUp(VCSConfigurationData data) {
        super.setUp(data);
        data.getConfig().getScm().getUserRemoteConfigs().getHudsonPluginsGitUserRemoteConfig()
                .setCredentialsId(CREDENTIALS_GITLAB_ID);
    }
}
