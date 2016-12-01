package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.i.Experimental;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
@Experimental("Integration with Git-server")
public interface VCSServer<CP> {

    void create(VCSRepository repo);

    void delete(VCSRepository repo);

    boolean isExist(VCSRepository repo);

    /**
     * Returns a URI for access to the server.
     */
    String getRepositoryURI(VCSRepository repo);

    CP getCredentialProvider();

    void setProtocol(String protocol);
}
