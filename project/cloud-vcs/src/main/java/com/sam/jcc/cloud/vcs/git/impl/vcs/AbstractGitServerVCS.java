package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.VCSCredentials;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Slf4j
public abstract class AbstractGitServerVCS extends AbstractVCS implements VCS<VCSCredentials> {
}
