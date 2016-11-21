package com.sam.jcc.cloud.project;

import com.sam.jcc.cloud.i.InternalCloudException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.cleanDirectory;

/**
 * @author Alexey Zhytnik
 * @since 21.11.2016
 */
@Component
class Cleaner {

    public void remove(File dir) {
        clean(dir);

        if (!dir.delete()) {
            throw new InternalCloudException("Can't delete " + dir);
        }
    }

    public void clean(File dir) {
        try {
            cleanDirectory(dir);
        } catch (IOException e) {
            throw new InternalCloudException(e);
        }
    }
}
