package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.utils.files.ItemStorage.ItemNotFoundException;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryNotFoundException;
import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
@Component
@Scope("prototype")
public class GitFileVCS extends AbstractGitVCS {

    private String protocol = getProperty("protocols.file");

    @Override
    public String getRepositoryURI(VCSRepository repo) {
        try {
            final String uri = storage.get(repo).getAbsolutePath();
            return protocol + uri;
        } catch (ItemNotFoundException e) {
            throw new VCSRepositoryNotFoundException(repo);
        }
    }

    @Override
    public void setProtocol(String newProtocol) {
        if (!newProtocol.startsWith(protocol)) {
            throw new VCSUnknownProtocolException(newProtocol);
        }
    }
}
