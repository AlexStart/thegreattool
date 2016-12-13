package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.i.IStatus;

/**
 * @author Alexey Zhytnik
 * @since 08.12.2016
 */
public enum VCSRepositoryStatus implements IStatus {

    INITIALIZED,
    CREATED,
    COMMITED,
    PUSHED,
    CLONED,
    DELETED
}
