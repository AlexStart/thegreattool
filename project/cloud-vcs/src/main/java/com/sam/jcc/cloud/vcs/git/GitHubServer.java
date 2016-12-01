package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.i.Experimental;
import com.sam.jcc.cloud.vcs.VCSException;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSServer;
import lombok.Setter;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

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
public class GitHubServer implements VCSServer<CredentialsProvider> {

    private String user;
    private String token;

    //TODO: use char[]
    private String password;

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
        return format("https://github.com/{0}/{1}.git", user, repo.getName());
    }

    @Override
    public CredentialsProvider getCredentialProvider() {
        return new UsernamePasswordCredentialsProvider(user, password);
    }

    @Override
    public void setProtocol(String protocol) {
        if (!protocol.equals("https")) {
            throw new UnsupportedOperationException("Supported only https protocol");
        }
    }

    private GHRepository search(VCSRepository repo) {
        final Optional<GHRepository> result = trySearch(repo);

        if (!result.isPresent()) {
            throw new VCSException(format("VCSRepository {0} not found!", repo));
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
