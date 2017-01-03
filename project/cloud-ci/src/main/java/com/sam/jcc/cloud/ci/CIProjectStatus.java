package com.sam.jcc.cloud.ci;

import com.sam.jcc.cloud.i.IStatus;

/**
 * @author Alexey Zhytnik
 * @since 22-Dec-16
 */
public enum CIProjectStatus implements IStatus {

    CONFIGURED,
    CREATED,
    UPDATED,
    HAS_BUILD,
    HAS_NO_BUILD,
    DELETED
}
