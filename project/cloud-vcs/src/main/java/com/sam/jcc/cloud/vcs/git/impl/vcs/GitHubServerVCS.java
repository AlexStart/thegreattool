package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.sam.jcc.cloud.PropertyResolver;
import com.sam.jcc.cloud.exception.NotImplementedCloudException;
import com.sam.jcc.cloud.i.Experimental;
import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryNotFoundException;
import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import lombok.Setter;
import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
@Setter
@Experimental("Integration with GitHub")
public class GitHubServerVCS extends AbstractGitServerVCS implements VCS<VCSCredentials> {

    private String user;
    private String token;
    private String password;

    private String github = PropertyResolver.getProperty("servers.github");

    @PostConstruct
    public void setUp() {
        setUser(user);
        setPassword(password);
    }

    @Override
    public void create(VCSRepository repo) {
        final String repoName = repo.getName();
        final GHCreateRepositoryBuilder builder = connect().createRepository(repoName);
        build(builder);
    }

    @Override
    public void delete(VCSRepository repo) {
        try {
            search(repo).delete();
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }

    @Override
    public boolean isExist(VCSRepository repo) {
        return trySearch(repo).isPresent();
    }

    @Override
    public String getRepositoryURI(VCSRepository repo) {
        return format("{0}/{1}/{2}.git", github, user, repo.getName());
    }

    @Override
    public void commit(VCSRepository repo) {
        throw new NotImplementedCloudException();
    }

    @Override
    public void read(VCSRepository repo) {
        throw new NotImplementedCloudException();
    }

    @Override
    public List<VCSRepository> getAllRepositories() {
        throw new NotImplementedCloudException();
    }

    @Override
    public void setProtocol(String protocol) {
        if (!protocol.equals("https")) {
            throw new VCSUnknownProtocolException(protocol);
        }
    }

    private GHRepository search(VCSRepository repo) {
        final Optional<GHRepository> result = trySearch(repo);

        if (!result.isPresent()) {
            throw new VCSRepositoryNotFoundException(repo);
        }
        return result.get();
    }

    private Optional<GHRepository> trySearch(VCSRepository repo) {
        final String repoName = repo.getName();
        final List<GHRepository> repositories = connect()
                .searchRepositories()
                .user(user)
                .repo(repoName)
                .list().asList();

        return repositories.stream()
                .filter(byNameFilter(repoName))
                .findFirst();
    }

    private Predicate<GHRepository> byNameFilter(String name) {
        return r -> r.getName().equals(name);
    }

    private GitHub connect() {
        try {
            return GitHub.connect(user, token);
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }

    private GHRepository build(GHCreateRepositoryBuilder builder) {
        try {
            return builder.create();
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }
}
