package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.i.PropertyResolver;
import com.sam.jcc.cloud.vcs.VCSException;
import com.sam.jcc.cloud.vcs.VCSRepository;
import org.springframework.stereotype.Component;

import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
@Component
public class GitFileStorage extends GitAbstractStorage {

    private String protocol = PropertyResolver.getProperty("protocols.file");

    @Override
    public String getRepositoryURI(VCSRepository repo) {
        final String uri = get(repo).toURI().getSchemeSpecificPart().substring(1);
        return protocol + uri;
    }

    @Override
    public void setProtocol(String newProtocol) {
        if (!newProtocol.startsWith(protocol)) {
            throw new VCSException(format("Unknown protocol \"{0}\"", newProtocol));
        }
    }
}
