package com.sam.jcc.cloud.ci.jenkins.config.vcs;

import com.sam.jcc.cloud.PropertyResolver;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.vcs.git.impl.GitProtocolProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Functionality for configuring Jenkins config for VSC with type {@link GitProtocolProvider#TYPE}
 */
@Component
@Slf4j
public class GitProtocolVCSConfigurator extends GitConfigurator {
    @Override
    public String getType() {
        return GitProtocolProvider.TYPE;
    }

    @Override
    protected URI resolveGitURL(CIProject project) {
        try {
            return new URI(property("protocols.git") + property("git.remote.server.host") + ":"
                    + property("git.remote.server.port") + "/" + project.getName());
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    protected String property(String key) {
        return PropertyResolver.getProperty(key);
    }
}
