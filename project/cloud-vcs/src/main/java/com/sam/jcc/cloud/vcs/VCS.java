package com.sam.jcc.cloud.vcs;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
public interface VCS<CP> {

    void create(VCSRepository repo);

    void commit(VCSRepository repo);

    VCSRepository read(VCSRepository repo);

    boolean isExist(VCSRepository repo);

    void delete(VCSRepository repo);

    void setServer(VCSServer<CP> server);
}
