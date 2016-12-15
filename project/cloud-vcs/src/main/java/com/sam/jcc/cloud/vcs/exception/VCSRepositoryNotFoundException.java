package com.sam.jcc.cloud.vcs.exception;

import com.sam.jcc.cloud.vcs.VCSRepository;

/**
 * @author Alexey Zhytnik
 * @since 15-Dec-16
 */
public class VCSRepositoryNotFoundException extends VCSException {

    public VCSRepositoryNotFoundException(VCSRepository repository) {
        super("vcs.repository.notFound", repository);
    }
}
