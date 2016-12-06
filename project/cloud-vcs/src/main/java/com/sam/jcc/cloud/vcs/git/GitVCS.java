package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.TempFile;
import com.sam.jcc.cloud.vcs.*;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
public class GitVCS implements VCS<VCSCredentialsProvider> {

    @Setter
    @Getter
    private VCSStorage<VCSCredentialsProvider> storage;

    private FileManager files = new FileManager();

    @Override
    public boolean isExist(VCSRepository repo) {
        return storage.isExist(repo);
    }

    @Override
    public void delete(VCSRepository repo) {
        storage.delete(repo);
    }

    @Override
    public void read(VCSRepository repo) {
        try {
            clone(repo);

            //TODO: transfer to top level
            final File metadata = new File(repo.getSources(), ".git");
            files.delete(metadata);

        } catch (GitAPIException e) {
            throw new VCSException(e);
        }
    }

    @Override
    public void create(VCSRepository repo) {
        storage.create(repo);
    }

    @Override
    public void commit(VCSRepository repo) {
        try (TempFile temp = files.createTempDir()) {
            try (Git git = init(repo, temp)) {

                if (!isEmptyRemote(repo)) {
                    pull(git);
                    clear(temp);
                }
                files.copyDir(repo.getSources(), temp);

                add(git);
                commit(git);
                push(git);

                git.getRepository().close();
            } catch (GitAPIException | URISyntaxException | IOException e) {
                throw new VCSException(e);
            }
        }
    }

    private void clone(VCSRepository repo) throws GitAPIException {
        final CloneCommand clone = Git.cloneRepository()
                .setDirectory(repo.getSources())
                .setURI(storage.getRepositoryURI(repo));

        setCredentials(clone);
        clone.call().close();
    }

    private Git init(VCSRepository repo, File dir) throws GitAPIException, URISyntaxException {
        final Git git = Git.init()
                .setDirectory(dir)
                .call();

        final RemoteAddCommand remote = git.remoteAdd();
        remote.setName("origin");
        remote.setUri(new URIish(storage.getRepositoryURI(repo)));
        remote.call();

        return git;
    }

    private boolean isEmptyRemote(VCSRepository repo) throws GitAPIException, IOException {
        final LsRemoteCommand lsRemote = Git.lsRemoteRepository()
                .setRemote(storage.getRepositoryURI(repo))
                .setHeads(true);
        setCredentials(lsRemote);
        return lsRemote.call().isEmpty();
    }

    private void pull(Git git) throws GitAPIException {
        final PullCommand pull = git.pull()
                .setRemote("origin")
                .setRemoteBranchName("master");

        setCredentials(pull);
        pull.call();
    }

    private void clear(File dir) throws GitAPIException {
        final File[] files = dir.listFiles();

        if (files != null) {
            Arrays.stream(files)
                    .filter(f -> !f.getName().equals(".git"))
                    .forEach(this.files::delete);
        }
    }

    private void add(Git git) throws GitAPIException {
        git.add()
                .addFilepattern(".")
                .call();
    }

    private void commit(Git git) throws GitAPIException {
        git.commit()
                .setAll(true)
                .setMessage("Update")
                .call();
    }

    private void push(Git git) throws GitAPIException {
        final PushCommand push = git.push()
                .setRemote("origin")
                .setPushAll()
                .setPushTags()
                .setForce(true);

        setCredentials(push);
        push.call();
    }

    private void setCredentials(TransportCommand<?, ?> command) {
        if (storage.getCredentialsProvider().isPresent()) {
            CredentialsProvider cp = (CredentialsProvider) storage.getCredentialsProvider().get();
            command.setCredentialsProvider(cp);
        }
    }
}
