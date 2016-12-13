package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.vcs.VCSCredentials;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * @author Alexey Zhytnik
 * @since 02-Dec-16
 */
class GitCredentials extends UsernamePasswordCredentialsProvider implements VCSCredentials {

    public GitCredentials(String username, String password) {
        super(username, password);
    }
}
