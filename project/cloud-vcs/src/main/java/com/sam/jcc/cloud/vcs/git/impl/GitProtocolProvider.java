package com.sam.jcc.cloud.vcs.git.impl;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.vcs.VCSProvider;
import com.sam.jcc.cloud.vcs.git.GitAbstractStorage;
import com.sam.jcc.cloud.vcs.git.GitRemoteStorage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author olegk
 */
@Component
public class GitProtocolProvider extends VCSProvider {

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
}
