package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.vcs.VCSCredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * @author Alexey Zhytnik
 * @since 02-Dec-16
 */
class GitCredentialsProvider extends UsernamePasswordCredentialsProvider implements VCSCredentialsProvider {

    public GitCredentialsProvider(String username, String password) {
        super(username, password);
    }
}
