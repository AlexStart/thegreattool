package com.sam.jcc.cloud.vcs;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
public class VCSRepositoryDataHelper {

    private VCSRepositoryDataHelper() {
    }

    public static VCSRepository repository() {
        final VCSRepository r = new VCSRepository();
        r.setArtifactId("temp");
        return r;
    }
}
