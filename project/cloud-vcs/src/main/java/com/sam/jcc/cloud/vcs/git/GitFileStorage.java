package com.sam.jcc.cloud.vcs.git;

import static com.sam.jcc.cloud.PropertyResolver.*;
import static java.text.MessageFormat.format;

import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.vcs.VCSException;
import com.sam.jcc.cloud.vcs.VCSRepository;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
@Component
public class GitFileStorage extends GitAbstractStorage {

    private String protocol = getProperty("protocols.file");

    @Override
    public String getRepositoryURI(VCSRepository repo) {
        final String uri = get(repo).toURI().getSchemeSpecificPart().substring(1);
        return protocol + uri;
    }

    @Override
    public void setProtocol(String newProtocol) {
        if (!newProtocol.startsWith(protocol)) {
            throw new VCSException(format("Unknown protocol \"{0}\"", newProtocol));
        }
    }
}
