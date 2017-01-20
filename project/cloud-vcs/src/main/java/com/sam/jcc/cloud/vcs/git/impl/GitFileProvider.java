package com.sam.jcc.cloud.vcs.git.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.vcs.VCSProvider;
import com.sam.jcc.cloud.vcs.git.GitAbstractStorage;
import com.sam.jcc.cloud.vcs.git.GitFileStorage;

/**
 * @author olegk
 */
@Component
public class GitFileProvider extends VCSProvider {

    private static final long GIT_FILE_PROVIDER_ID = 3L;

	public GitFileProvider(List<IEventManager<IVCSMetadata>> eventManagers) {
        super(eventManagers);
    }

    @Override
    protected GitAbstractStorage getStorage() {
        return new GitFileStorage();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

	@Override
	public Long getId() {
		return GIT_FILE_PROVIDER_ID;
	}
}
