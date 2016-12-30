package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.exception.NotImplementedCloudException;
import com.sam.jcc.cloud.i.Experimental;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryNotFoundException;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSStorage;
import lombok.Setter;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.text.MessageFormat.format;
import static java.util.Optional.of;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
@Setter
@Experimental("Integration with a GitLab storage")
public class GitLabServer implements VCSStorage<GitCredentials> {

    private String host;
    private String user;

    //TODO: use char[]
    private String password;

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
        return format("{0}/{1}/{3}.git", host, user, repo.getName());
    }

    @Override
    public Optional<GitCredentials> getCredentialsProvider() {
        return of(new GitCredentials(user, password));
    }

    @Override
    public List<VCSRepository> getAllRepositories() {
        throw new NotImplementedCloudException();
    }

    @Override
    public void setProtocol(String protocol) {
    }

    private GitlabProject search(GitlabAPI api, VCSRepository repo) {
        final Optional<GitlabProject> result = trySearch(api, repo);

        if (!result.isPresent()) {
            throw new VCSRepositoryNotFoundException(repo);
        }
        return result.get();
    }

    private Optional<GitlabProject> trySearch(GitlabAPI api, VCSRepository repo) {
        try {
            return api.getProjects().stream()
                    .filter(byNameFilter(repo.getName()))
                    .findFirst();
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }

    private Predicate<GitlabProject> byNameFilter(final String name) {
        return project -> project.getName().equals(name);
    }

    private GitlabAPI connect() {
        return GitlabAPI.connect(host, getToken());
    }

    private String getToken() {
        try {
            GitlabSession session = GitlabAPI.connect(host, user, password);
            return session.getPrivateToken();
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }
}
