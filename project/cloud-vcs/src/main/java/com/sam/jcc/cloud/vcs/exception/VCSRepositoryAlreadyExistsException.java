package com.sam.jcc.cloud.vcs.exception;

import com.sam.jcc.cloud.vcs.VCSRepository;

/**
 * @author Alexey Zhytnik
 * @since 15-Dec-16
 */
public class VCSRepositoryAlreadyExistsException extends VCSException {

    public VCSRepositoryAlreadyExistsException(VCSRepository repository) {
        super("vcs.repository.exist", repository);
    }
}
