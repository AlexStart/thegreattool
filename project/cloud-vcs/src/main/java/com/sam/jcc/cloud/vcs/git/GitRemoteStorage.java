package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import com.sam.jcc.cloud.vcs.VCSRepository;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
public class GitRemoteStorage extends GitAbstractStorage {

    private String host = getProperty("git.server.host");
    private String protocol = getProperty("protocols.git");

    @Override
    public String getRepositoryURI(VCSRepository repo) {
        return format("{0}{1}/{2}", protocol, host, repo.getName());
    }

    @Override
    public void setProtocol(String newProtocol) {
        if (!newProtocol.startsWith(protocol)) {
            throw new VCSUnknownProtocolException(newProtocol);
        }
    }
}
