package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.utils.parsers.ProjectParser;
import com.sam.jcc.cloud.vcs.VCSRepository;

import java.io.File;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * @author Alexey Zhytnik
 * @since 30.12.2016
 */
public class VCSRepositoryBuilder implements Function<File, VCSRepository> {

    private ProjectParser parser = new ProjectParser();

    @Override
    public VCSRepository apply(File sources) {
        final Entry<String, String> metadata = parser.parse(sources);

        final VCSRepository repo = new VCSRepository();
        repo.setGroupId(metadata.getKey());
        repo.setArtifactId(metadata.getValue());
        repo.setSources(sources);
        return repo;
    }
}
