package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.google.common.io.Files;
import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.GzipTarArchiveManager;
import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.VCSCredentials;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import com.sam.jcc.cloud.vcs.exception.VCSRepositoryNotFoundException;
import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import com.sam.jcc.cloud.vcs.git.impl.vcs.gitlab.CreateCommitCommand;
import com.sam.jcc.cloud.vcs.git.impl.vcs.gitlab.GetVersionCommand;
import com.sam.jcc.cloud.vcs.git.impl.vcs.gitlab.GitlabVersion;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabCommit;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;
import org.gitlab.api.models.GitlabUser;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * @author Alexey Zhytnik
 * @since 28.11.2016
 */
@Component
@Slf4j
public class GitlabServerVCS extends AbstractGitServerVCS implements VCS<VCSCredentials> {

    @Getter
    private String host = getProperty("gitlab.remote.server.host");
    @Getter
    private String port = getProperty("gitlab.remote.server.port");
    @Getter
    private String url = getProperty("gitlab.remote.server.path");
    @Getter
    private String user = getProperty("gitlab.remote.server.user");
    @Getter
    private String password = getProperty("gitlab.remote.server.password");

    private final InitOnceAdminBean initAdmin;
    private final CreateCommitCommand commitCommand;
    private final GetVersionCommand versionCommand;

    public GitlabServerVCS(InitOnceAdminBean initAdmin, CreateCommitCommand commitCommand, GetVersionCommand versionCommand) {
        this.commitCommand = requireNonNull(commitCommand);
        this.versionCommand = requireNonNull(versionCommand);
        this.initAdmin = requireNonNull(initAdmin);
    }

    @PostConstruct
    public void setUp() {
        if (isNotBlank(initAdmin.getRawPassword())) {
            if (isEnabled()) {
                updateCurrentUserPassword(initAdmin.getRawPassword());
                log.debug("Gitlab password was successfully updated");
            } else {
                log.warn("GITLAB REQUIRES MANUAL PASSWORD UPDATE! Gitlab is not enabled. " +
                        "Gitlab password update failed. Default gitlab password can be used.");
            }
        }
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
        final GitlabProject project = search(connect(), repo);
        commitCommand.call(repo, getRepositoryURI(), project.getId(), getToken());
    }

    @Override
    public void read(VCSRepository repo) {
        final GitlabAPI api = connect();
        final GitlabProject project = search(api, repo);
        downloadSources(api, project, repo.getSources());
    }

    //Get project gzip-archive, ungzip, untar project, put to repository sources
    private void downloadSources(final GitlabAPI api, GitlabProject project, File sources) {
        try {
            byte[] archiveBytes = api.getFileArchive(project);
            File targz = new FileManager().createTempFile();
            writeByteArrayToFile(targz, archiveBytes);
            GzipTarArchiveManager archiveManager = new GzipTarArchiveManager();
            File tar = archiveManager.unGzip(targz);
            File sourcesTemp = archiveManager.unTar(tar, Files.createTempDir());
            copyDirectory(sourcesTemp, sources);
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

    public boolean isEnabled() {
        try {
            GitlabAPI.connect(getRepositoryURI(), user, password);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void updateCurrentUserPassword(String newPassword) {
        GitlabAPI api = connect();
        try {
            GitlabUser user = api.getUser();
            api.updateUser(user.getId(), user.getEmail(), newPassword, null,
                    null, null, null, null, null, null,
                    null, null, null, null, null);
            this.password = newPassword;
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }

    public GitlabVersion getVersion() {
        return versionCommand.call(getRepositoryURI(), getToken());
    }

    public URL getApiUrl() {
        try {
            return connect().getAPIUrl(EMPTY);
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
        return GitlabAPI.connect(getRepositoryURI(), getToken());
    }

    private String getToken() {
        return getToken(user, password);
    }

    String getToken(String user, String password) {
        try {
            GitlabSession session = GitlabAPI.connect(getRepositoryURI(), user, password);
            return session.getPrivateToken();
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }
}