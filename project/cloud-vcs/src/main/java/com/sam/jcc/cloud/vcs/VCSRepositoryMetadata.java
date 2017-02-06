package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.i.vcs.IVCSMetadata;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author olegk
 * @since Jan, 11, 2016
 */
@Data
@Deprecated
@NoArgsConstructor
public class VCSRepositoryMetadata implements IVCSMetadata {

    private String name;
    private String description;
    private String commitMessage;

    public VCSRepositoryMetadata(String name, String description, String commitMessage) {
        this.name = name;
        this.description = description;
        this.commitMessage = commitMessage;
    }
}
