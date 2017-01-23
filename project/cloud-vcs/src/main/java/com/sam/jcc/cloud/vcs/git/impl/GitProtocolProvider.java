package com.sam.jcc.cloud.vcs.git.impl;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author olegk
 */
@Component
public class GitProtocolProvider extends VCSProvider {

    private static final long GIT_PROTOCOL_PROVIDER_ID = 4L;

	public GitProtocolProvider(List<IEventManager<IVCSMetadata>> eventManagers) {
        super(eventManagers);
    }

    @Override
    protected GitAbstractStorage getStorage() {
        return new GitRemoteStorage();
    }

    //TODO: need check server
    @Override
    public boolean isEnabled() {
        return true;
    }

	@Override
	public Long getId() {
		return GIT_PROTOCOL_PROVIDER_ID;
	}
}
