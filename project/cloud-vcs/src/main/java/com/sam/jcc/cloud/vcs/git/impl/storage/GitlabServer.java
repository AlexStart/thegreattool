package com.sam.jcc.cloud.vcs.git.impl.storage;

import com.sam.jcc.cloud.i.Experimental;
import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSStorage;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryNotFoundException;
import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import com.sam.jcc.cloud.vcs.git.impl.GitCredentials;
import com.sam.jcc.cloud.vcs.git.impl.storage.gitlab.GitlabCreateCommitCommand;
import lombok.Getter;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabCommit;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.text.MessageFormat.format;
import static java.util.Optional.of;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
@Component
@Experimental("Integration with a GitLab storage") //TODO - probably, this can be removed
public class GitlabServer extends AbstractGitServerStorage implements VCSStorage<VCSCredentials> {

    @Getter
    private String host = getProperty("gitlab.remote.server.host");
    @Getter
    private String port = getProperty("gitlab.remote.server.port");
    @Getter
    private String url = getProperty("gitlab.remote.server.path");
    private String user = getProperty("gitlab.remote.server.user");
    private String password = getProperty("gitlab.remote.server.password");
    //TODO find compromise timeout
    private int requestTimeout = Integer.parseInt(getProperty("gitlab.remote.server.timeout"));

    @PostConstruct
    public void setUp() {
        setUser(user);
        setPassword(password);
    }

    @Override
    public void create(VCSRepository repo) {
        try {
            connect().createProject(repo.getName());
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }

    @Override
    public void delete(VCSRepository repo) {
        final GitlabAPI api = connect();
        final GitlabProject project = search(api, repo);
        try {
            api.deleteProject(project.getId());
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }

    @Override
    public boolean isExist(VCSRepository repo) {
        return trySearch(connect(), repo).isPresent();
    }

    @Override
    public String getRepositoryURI(VCSRepository repo) {
        return getRepositoryURI() + format("/{0}/{1}.git", user, repo.getName());
    }

    @Override
    public Optional<VCSCredentials> getCredentialsProvider() {
        return of(new GitCredentials(user, password));
    }

    @Override
    public List<VCSRepository> getAllRepositories() {
        final GitlabAPI api = connect();
        return trySearchAll(api).stream().map(p -> {
            VCSRepository repo = new VCSRepository();
            repo.setArtifactId(p.getName());
            return repo;
        }).collect(Collectors.toList());
    }

    @Override
    public void setProtocol(String protocol) {
        if (!protocol.equals("http")) {
            throw new VCSUnknownProtocolException(protocol);
        }
    }

    @Override
    public void commit(VCSRepository repo) {
        String token = getToken();
        final GitlabProject project = search(connect(), repo);
        final GitlabCreateCommitCommand commitCommand = new GitlabCreateCommitCommand();

        commitCommand.call(repo, getRepositoryURI(), project.getId(), token);
    }

    @Override
    public void read(VCSRepository repo) {
        final GitlabAPI api = connect();
        final GitlabProject project = search(api, repo);
        repo.setSources(archiveProject(api, project));
    }

    //TODO solve byte array to archive
    private File archiveProject(final GitlabAPI api, GitlabProject project) {
        try {
            File archive = new File("file");
            writeByteArrayToFile(archive, api.getFileArchive(project));
            return archive;
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }

    //Used for testing
    public List<GitlabCommit> getAllCommits(VCSRepository repo) {
        final GitlabAPI api = connect();
        final GitlabProject project = search(api, repo);
        try {
            return api.getAllCommits(project.getId());
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }

    private String getRepositoryURI() {
        return format("http://{0}:{1}/{2}", host, port, url);
    }

    private GitlabProject search(GitlabAPI api, VCSRepository repo) {
        final Optional<GitlabProject> result = trySearch(api, repo);

        if (!result.isPresent()) {
            throw new VCSRepositoryNotFoundException(repo);
        }
        return result.get();
    }

    private Optional<GitlabProject> trySearch(GitlabAPI api, VCSRepository repo) {
        return trySearchAll(api).stream()
                .filter(byNameFilter(repo.getName()))
                .findFirst();
    }

    private List<GitlabProject> trySearchAll(GitlabAPI api) {
        try {
            return api.getProjects();
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }

    private Predicate<GitlabProject> byNameFilter(final String name) {
        return project -> project.getName().equals(name);
    }

    private GitlabAPI connect() {
        GitlabAPI api = GitlabAPI.connect(getRepositoryURI(), getToken());
        api.setRequestTimeout(requestTimeout);
        return api;
    }

    private String getToken() {
        try {
            GitlabSession session = GitlabAPI.connect(getRepositoryURI(), user, password);
            return session.getPrivateToken();
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }
}
