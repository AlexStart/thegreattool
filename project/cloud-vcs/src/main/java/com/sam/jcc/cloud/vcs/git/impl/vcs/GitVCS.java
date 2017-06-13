package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.TempFile;
import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.URIish;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Slf4j
@Component
@Scope("prototype")
public class GitVCS extends AbstractVCS implements VCS<VCSCredentials> {

    private FileManager files = new FileManager();

    @Override
    public void read(VCSRepository repo) {
        try {
            clone(repo);
            files.delete(new File(repo.getSources(), ".git"));
        } catch (GitAPIException e) {
            throw new VCSException(e);
        }
    }

    @Override
    public void commit(VCSRepository repo) {
        try (TempFile temp = files.createTempDir()) {
            try (Git git = init(repo, temp)) {

                if (!isEmptyRemote(repo)) {
                    log.debug("Pull {} to {}", repo, temp);
                    pull(git);
                    clear(temp);
                }
                files.copyDir(repo.getSources(), temp);

                add(git);
                commit(git, repo.getCommitMessage());
                push(git);

                git.getRepository().close();
            } catch (GitAPIException | URISyntaxException | IOException e) {
                log.error(e.getMessage());
                throw new VCSException(e);
            }
        }
    }

    private void clone(VCSRepository repo) throws GitAPIException {
        log.info("Clone of {}", repo);

        final CloneCommand clone = Git.cloneRepository().setDirectory(repo.getSources())
                .setURI(storage.getRepositoryURI(repo));

        setCredentials(clone);
        clone.call().close();
    }

    private Git init(VCSRepository repo, File dir) throws GitAPIException, URISyntaxException {
        log.info("Init {} of {}", dir, repo);

        final Git git = Git.init().setDirectory(dir).call();

        final RemoteAddCommand remote = git.remoteAdd();
        remote.setName("origin");
        remote.setUri(new URIish(storage.getRepositoryURI(repo)));
        remote.call();

        return git;
    }

    private boolean isEmptyRemote(VCSRepository repo) throws GitAPIException, IOException {
        String repoURI = storage.getRepositoryURI(repo);
        log.info("Check origin master branch of {}, URI: {}", repo, repoURI);

        final LsRemoteCommand lsRemote = Git.lsRemoteRepository().setRemote(repoURI)
                .setHeads(true);
        setCredentials(lsRemote);
        return lsRemote.call().isEmpty();
    }

    private void pull(Git git) throws GitAPIException {
        final PullCommand pull = git.pull().setRemote("origin").setRemoteBranchName("master");

        setCredentials(pull);
        pull.call();
    }

    private void clear(File dir) throws GitAPIException {
        files.getDirectoryFiles(dir).stream().filter(f -> !f.getName().equals(".git")).forEach(this.files::delete);
    }

    private void add(Git git) throws GitAPIException {
        log.info("Before add()");
        DirCache call = git.add().addFilepattern(".").call();
        log.info("Added to " + call);
    }

    private void commit(Git git, String commitMessage) throws GitAPIException {
        log.info("Before commit()");
        RevCommit call = git.commit().setAll(true).setMessage(commitMessage).call();
        log.info("Committed.  Result is " + call);
    }

    private void push(Git git) throws GitAPIException {
        log.info("Before push()");
        final PushCommand push = git.push().setRemote("origin").setPushAll().setPushTags().setForce(true);
        log.info("Push command is " + push);
        setCredentials(push);
        Iterable<PushResult> pushResults = push.call();
        log.info("Push results are " + pushResults);
    }

    private void setCredentials(TransportCommand<?, ?> command) {
        if (storage.getCredentialsProvider().isPresent()) {
            CredentialsProvider cp = (CredentialsProvider) storage.getCredentialsProvider().get();
            command.setCredentialsProvider(cp);
        }
    }
}
