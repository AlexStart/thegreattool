package com.sam.jcc.cloud.vcs.git.impl;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.vcs.VCSProvider;
import com.sam.jcc.cloud.vcs.git.GitAbstractStorage;
import com.sam.jcc.cloud.vcs.git.GitFileStorage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author olegk
 */
@Component
public class GitFileProvider extends VCSProvider {

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
}
