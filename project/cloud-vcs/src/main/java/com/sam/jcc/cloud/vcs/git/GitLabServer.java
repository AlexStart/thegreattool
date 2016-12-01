package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.i.Experimental;
import com.sam.jcc.cloud.vcs.VCSException;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSServer;
import lombok.Setter;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;

import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
@Setter
@Experimental("Integration with a GitLab server")
public class GitLabServer implements VCSServer<CredentialsProvider> {

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
    public CredentialsProvider getCredentialProvider() {
        return new UsernamePasswordCredentialsProvider(user, password);
    }

    @Override
    public void setProtocol(String protocol) {
    }

    private GitlabProject search(GitlabAPI api, VCSRepository repo) {
        final Optional<GitlabProject> result = trySearch(api, repo);

        if (!result.isPresent()) {
            throw new VCSException(format("VCSRepository {0} not found!", repo));
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
