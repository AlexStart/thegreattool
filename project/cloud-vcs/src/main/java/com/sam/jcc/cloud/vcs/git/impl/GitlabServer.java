package com.sam.jcc.cloud.vcs.git.impl;

import com.sam.jcc.cloud.i.Experimental;
import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSStorage;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryNotFoundException;
import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import lombok.Setter;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabCommit;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.text.MessageFormat.format;
import static java.util.Optional.of;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
@Setter
@Experimental("Integration with a GitLab storage")
public class GitlabServer extends GitAbstractStorage implements VCSStorage<VCSCredentials> {

    private String host = getProperty("gitlab.remote.server.host");
    private String port = getProperty("gitlab.remote.server.port");
    private String url = getProperty("gitlab.remote.server.path");
    private String user = getProperty("gitlab.remote.server.user");
    private String password = getProperty("gitlab.remote.server.password");
    //TODO find compromise timeout
    private int requestTimeout = Integer.parseInt(getProperty("gitlab.remote.server.timeout"));

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

    public void commit(VCSRepository repo) {
        String token = getToken();
        final GitlabProject project = search(connect(), repo);
        final GitlabCreateCommitCommand commitCommand = new GitlabCreateCommitCommand();

        commitCommand.call(repo, getRepositoryURI(), project.getId(), token);
    }

    List<GitlabCommit> getAllCommits(VCSRepository repo) {
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
