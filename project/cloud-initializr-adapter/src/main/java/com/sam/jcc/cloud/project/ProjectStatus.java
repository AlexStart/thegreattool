package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.i.project.IStatus;

/**
 * @author Alexey Zhytnik
 * @since 16.11.2016
 */
enum ProjectStatus implements IStatus {
    UNPROCESSED,
    PRE_PROCESSED,
    PROCESSED,
    POST_PROCESSED
}
