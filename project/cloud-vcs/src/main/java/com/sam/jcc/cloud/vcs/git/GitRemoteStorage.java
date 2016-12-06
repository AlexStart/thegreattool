package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.vcs.VCSException;
import com.sam.jcc.cloud.vcs.VCSRepository;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
public class GitRemoteStorage extends GitAbstractStorage {

    @Override
    public String getRepositoryURI(VCSRepository repo) {
        return "git://localhost/" + repo.getName();
    }

    @Override
    public void setProtocol(String protocol) {
        if (!protocol.equals("git")) {
            throw new VCSException("Supported only git protocol!");
        }
    }
}
