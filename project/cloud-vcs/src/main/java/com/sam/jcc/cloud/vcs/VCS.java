package com.sam.jcc.cloud.vcs;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
public interface VCS<CP extends VCSCredentials> {

    void create(VCSRepository repo);

    void commit(VCSRepository repo);

    void read(VCSRepository repo);

    boolean isExist(VCSRepository repo);

    void delete(VCSRepository repo);

    void setStorage(VCSStorage<CP> storage);

    VCSStorage<CP> getStorage();
}
