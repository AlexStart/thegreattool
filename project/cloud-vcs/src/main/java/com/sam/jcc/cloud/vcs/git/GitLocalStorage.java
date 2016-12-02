package com.sam.jcc.cloud.vcs.git;

import com.sam.jcc.cloud.tool.FileManager;
import com.sam.jcc.cloud.vcs.VCSCredentialsProvider;
import com.sam.jcc.cloud.vcs.VCSException;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.VCSStorage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static java.text.MessageFormat.format;
import static java.util.Optional.empty;

/**
 * @author Alexey Zhytnik
 * @since 01.12.2016
 */
public class GitLocalStorage implements VCSStorage<VCSCredentialsProvider> {

    @Getter
    @Setter
    private File baseRepository;

    private FileManager files = new FileManager();

    @Override
    public void create(VCSRepository repo) {
        failOnExist(repo);

        final File dir = repositoryByName(repo);
        files.createHiddenDir(dir);
    }

    private void failOnExist(VCSRepository repo) {
        if (isExist(repo)) {
            throw new VCSException(format("Repository {0} exists!", repo.getName()));
        }
    }

    @Override
    public boolean isExist(VCSRepository repo) {
        return repositoryByName(repo).exists();
    }

    @Override
    public String getRepositoryURI(VCSRepository repo) {
        final String uri = get(repo).toURI().getSchemeSpecificPart();
        return "file:/" +  uri;
    }

    @Override
    public void delete(VCSRepository repo) {
        files.delete(get(repo));
    }

    private File get(VCSRepository repo) {
        final File dir = repositoryByName(repo);
        failOnNotExist(repo);
        return dir;
    }

    private File repositoryByName(VCSRepository repo) {
        return new File(baseRepository, repo.getName());
    }

    private void failOnNotExist(VCSRepository repo) {
        if (!isExist(repo)) {
            throw new VCSException(format("Repository {0} not exist!", repo.getName()));
        }
    }

    @Override
    public void setProtocol(String protocol) {
        if (!protocol.equals("file")) {
            throw new UnsupportedOperationException("Supported only file protocol!");
        }
    }

    @Override
    public Optional<VCSCredentialsProvider> getCredentialsProvider() {
        return empty();
    }

    public void installBaseRepository() {
        final File home = new File(System.getProperty("user.home"));
        final File base = new File(home, loadBaseRepositoryDirName());

        if (!base.exists()) {
            files.createHiddenDir(base);
        }
        baseRepository = base;
    }

    private String loadBaseRepositoryDirName() {
        final Properties props = new Properties();
        try {
            ClassPathResource resource = new ClassPathResource("vcs.properties");
            props.load(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return props.getProperty("base.repository.folder");
    }
}
