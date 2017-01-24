package com.sam.jcc.cloud.vcs.git.impl;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import lombok.Setter;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.lang.Integer.valueOf;
import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
public class GitRemoteStorage extends GitAbstractStorage {

    private String host = getProperty("git.remote.server.host");

    @Setter
    @VisibleForTesting
    private Integer port = valueOf(getProperty("git.remote.server.port"));

    private String protocol = getProperty("protocols.git");

    @Override
    public String getRepositoryURI(VCSRepository repo) {
        return format("{0}{1}:{2}/{3}", protocol, host, Integer.toString(port), repo.getName());
    }

    @Override
    public void setProtocol(String newProtocol) {
        if (!newProtocol.startsWith(protocol)) {
            throw new VCSUnknownProtocolException(newProtocol);
        }
    }
}
