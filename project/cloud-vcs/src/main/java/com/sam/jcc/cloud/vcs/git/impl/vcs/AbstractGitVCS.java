package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ItemStorage;
import com.sam.jcc.cloud.utils.files.TempFile;
import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryAlreadyExistsException;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;

/**
 * @author Alexey Zhytnik
 * @since 25.11.2016
 */
@Slf4j
public abstract class AbstractGitVCS extends AbstractVCS implements VCS<VCSCredentials> {

    private FileManager files = new FileManager();

    @Override
    public void create(VCSRepository repo) {
        try {
            initBare(storage.create(repo));
        } catch (ItemStorage.ItemAlreadyExistsException e) {
            throw new VCSRepositoryAlreadyExistsException(repo);
        }
    }

    @Override
    public boolean isExist(VCSRepository repo) {
        return storage.isExist(repo);
    }

    @Override
    public void delete(VCSRepository repo) {
        try {
            storage.delete(repo);
        } catch (ItemStorage.ItemNotFoundException e) {
            throw new VCSRepositoryNotFoundException(repo);
        }
    }

    @Override
    public List<VCSRepository> getAllRepositories() {
        return storage.getItems();
    }

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

    private void initBare(File dir) {
        log.debug("Init --bare in {}", dir);
        try {
            Git.init().setDirectory(dir).setBare(true).call().close();
        } catch (GitAPIException e) {
            throw new VCSException(e);
        }
    }

    /**
     * You should call for install the vcs storage in production mode!
     */
    //TODO(a bad part of the app): should be changed by @PostConstruct
    public void installBaseRepository() {
        final File base = new File(getProperty("repository.base.folder"));
        setBaseRepository(base);
    }

    public void setBaseRepository(File dir) {
        storage.setRoot(dir);
    }

    public File getBaseRepository() {
        return storage.getRoot();
    }

    private void clone(VCSRepository repo) throws GitAPIException {
        log.info("Clone of {}", repo);

        final CloneCommand clone = Git.cloneRepository().setDirectory(repo.getSources())
                .setURI(getRepositoryURI(repo));

        setCredentials(clone);
        clone.call().close();
    }

    private Git init(VCSRepository repo, File dir) throws GitAPIException, URISyntaxException {
        log.info("Init {} of {}", dir, repo);

        final Git git = Git.init().setDirectory(dir).call();

        final RemoteAddCommand remote = git.remoteAdd();
        remote.setName("origin");
        remote.setUri(new URIish(getRepositoryURI(repo)));
        remote.call();

        return git;
    }

    private boolean isEmptyRemote(VCSRepository repo) throws GitAPIException, IOException {
        String repoURI = getRepositoryURI(repo);
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
        if (getCredentialsProvider().isPresent()) {
            CredentialsProvider cp = (CredentialsProvider) getCredentialsProvider().get();
            command.setCredentialsProvider(cp);
        }
    }
}
