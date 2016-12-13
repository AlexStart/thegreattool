package com.sam.jcc.cloud.vcs;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

import java.io.File;
import java.util.Arrays;
import java.util.Map.Entry;

import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.TempFile;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.utils.parsers.ProjectParser;
import com.sam.jcc.cloud.vcs.git.GitFileStorage;
import com.sam.jcc.cloud.vcs.git.GitRemoteStorage;
import com.sam.jcc.cloud.vcs.git.GitVCS;

/**
 * @author Alexey Zhytnik
 * @since 29.11.2016
 */
@Deprecated
public class VCSHelper {

    private static ZipArchiveManager zipManager = new ZipArchiveManager();

    private final FileManager files;
    private final ProjectParser parser;
    private final VCS<VCSCredentials> vcs;

    public VCSHelper(String protocol) {
        files = new FileManager();
        parser = new ProjectParser();
        vcs = new GitVCS();

        setUpStorageByProtocol(protocol);
    }

    VCSHelper(VCSStorage<VCSCredentials> storage) {
        files = new FileManager();
        parser = new ProjectParser();

        vcs = new GitVCS();
        vcs.setStorage(storage);
    }

    private void setUpStorageByProtocol(String protocol) {
        if (protocol.equals("git")) {
            final GitRemoteStorage storage = new GitRemoteStorage();
            storage.installBaseRepository();
            vcs.setStorage(storage);
        } else {
            final GitFileStorage storage = new GitFileStorage();
            storage.installBaseRepository();
            vcs.setStorage(storage);
        }
    }

    public static void main(String[] args) {
        failOnIllegalFormat(args);

        final String vcs = args[0];
        final String protocol = args[1];
        final String operation = args[2];
        final File project = new File(args[3]);

        failOnNotExistence(vcs, "git");
        failOnNotExistence(protocol, "git", "file");
        failOnNotExistence(operation, "create", "read", "update", "delete");
        failOnSimpleFile(project);

        new VCSHelper(protocol).execute(vcs, protocol, operation, project);
    }

    private static void failOnIllegalFormat(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Arguments must be in next format: " +
                    "[vcs, protocol, operation, project file]!");
        }
    }

    private static void failOnNotExistence(String arg, String... supported) {
        stream(supported)
                .filter(s -> s.equals(arg))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(format(
                                "Argument {0} is not supported, supports: {1}", arg, Arrays.toString(supported)))
                );
    }

    private static void failOnSimpleFile(File project) {
        if (!zipManager.isZip(project)) {
            throw new IllegalArgumentException("Works only with zip!");
        }
    }

    void execute(String vcs, String protocol, String operation, File project) {
        switch (operation) {
            case "create": {
                createRepo(vcs, protocol, project);
                return;
            }
            case "read": {
                System.out.print(readSourcesFromRepo(vcs, protocol, project));
                return;
            }
            case "update": {
                commitUpdatedSourcesToRepo(vcs, protocol, project);
                return;
            }
            case "delete": {
                deleteRepo(vcs, protocol, project);
            }
        }
    }

    private void createRepo(String vcs, String protocol, File project) {
        final VCSRepository repo = parse(project);

        create(repo);
        commit(repo);
    }

    private File readSourcesFromRepo(String vcs, String protocol, File project) {
        final VCSRepository repo = read(parse(project));
        return repo.getSources();
    }

    private void commitUpdatedSourcesToRepo(String vcs, String protocol, File project) {
        commit(parse(project));
    }

    private void deleteRepo(String vcs, String protocol, File project) {
        delete(parse(project));
    }

    private void create(VCSRepository repo) {
        vcs.create(repo);
    }

    private void delete(VCSRepository repo) {
        vcs.delete(repo);
    }

    private void commit(VCSRepository repo) {
        try (TempFile temp = files.createTempDir()) {
            zipManager.unzip(repo.getSources(), temp);

            repo.setSources(temp);
            vcs.commit(repo);
        }
    }

    private VCSRepository read(VCSRepository repo) {
        try (TempFile temp = files.createTempDir()) {
            repo.setSources(temp);
            vcs.read(repo);

            //TODO: direct zip creation
            final byte[] content = zipManager.zip(temp);
            final File sources = files.createTempFile(repo.getName(), ".zip");
            files.write(content, sources);
            repo.setSources(sources);
        }
        return repo;
    }

    private VCSRepository parse(File project) {
        final Entry<String, String> metadata = parser.parse(project);

        final VCSRepository repo = new VCSRepository();
        repo.setGroupId(metadata.getKey());
        repo.setArtifactId(metadata.getValue());
        repo.setSources(project);
        return repo;
    }
}
