package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.utils.files.FileManager;

import java.io.File;

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
        final VCSRepository r = repository();

        final File temp = new FileManager().createTempDir();
        temp.deleteOnExit();
        r.setArtifactId("notEmptyProject");
        r.setSources(new File(temp.getPath()));
        return r;
    }
}
