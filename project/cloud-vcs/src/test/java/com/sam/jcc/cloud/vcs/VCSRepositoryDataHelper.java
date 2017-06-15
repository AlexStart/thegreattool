package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.utils.files.FileManager;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.junit.Assert.fail;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
public class VCSRepositoryDataHelper {

    private VCSRepositoryDataHelper() {
    }

    //Used to create unique test artifactId
    //In case of Gitlab few seconds are required to wait while the existed repository will be removed
    private static int counter = 1;

    public static VCSRepository repository() {
        final VCSRepository r = new VCSRepository();
        r.setArtifactId("temp" + counter++);
        return r;
    }

    public static VCSRepository notEmptyRepository() {
        final File tempSource = new FileManager().createTempDir();
        final File tempFile = new File(tempSource, "test-file.txt");
        final VCSRepository r = repository();
        try {
            writeStringToFile(tempFile, r.getArtifactId() + r.getCommitMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }
        r.setSources(tempSource);
        return r;
    }
}
