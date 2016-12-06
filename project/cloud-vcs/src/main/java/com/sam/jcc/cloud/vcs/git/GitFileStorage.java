package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.vcs.VCSException;
import com.sam.jcc.cloud.vcs.VCSRepository;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
public class GitFileStorage extends GitAbstractStorage {

    @Override
    public String getRepositoryURI(VCSRepository repo) {
        final String uri = get(repo).toURI().getSchemeSpecificPart();
        return "file:/" + uri;
    }

    @Override
    public void setProtocol(String protocol) {
        if (!protocol.equals("file")) {
            throw new VCSException("Supported only file protocol!");
        }
    }
}
