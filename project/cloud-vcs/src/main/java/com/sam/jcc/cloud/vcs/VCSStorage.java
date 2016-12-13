package com.sam.jcc.cloud.vcs;

import java.util.Optional;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
public interface VCSStorage<CP extends VCSCredentials> {

    void create(VCSRepository repo);

    void delete(VCSRepository repo);

    boolean isExist(VCSRepository repo);

    /**
     * Returns a URI for access to the storage.
     */
    String getRepositoryURI(VCSRepository repo);

    Optional<CP> getCredentialsProvider();

    void setProtocol(String protocol);
}
