package com.sam.jcc.cloud.ci.jenkins.config.vcs;

import com.sam.jcc.cloud.PropertyResolver;
import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.vcs.git.impl.GitFileProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Functionality for configuring Jenkins config for VSC with type {@link GitFileProvider#TYPE}
 */
@Component
@Slf4j
public class GitFileVCSConfigurator extends GitConfigurator {
    @Override
    public String getType() {
        return GitFileProvider.TYPE;
    }

    //TODO[rfisenko 6/16/17]: make test
    @Override
    protected URI resolveGitURL(CIProject project) {
        try {
            return new URI(PropertyResolver.getProperty("repository.base.folder") + File.separator + project.getName());
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
