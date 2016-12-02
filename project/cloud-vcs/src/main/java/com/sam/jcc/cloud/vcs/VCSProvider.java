package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.tool.FileManager;
import com.sam.jcc.cloud.tool.TempFile;
import com.sam.jcc.cloud.tool.ZipArchiveManager;
import com.sam.jcc.cloud.vcs.git.GitLocalStorage;
import com.sam.jcc.cloud.vcs.git.GitVCS;
import com.sam.jcc.cloud.vcs.parser.ProjectParser;

import java.io.File;
import java.util.Arrays;
import java.util.Map.Entry;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

/**
 * @author Alexey Zhytnik
 * @since 29.11.2016
 */
public class VCSProvider {

    private static ZipArchiveManager zipManager = new ZipArchiveManager();

    private FileManager files;
    private ProjectParser parser;
    private VCS<VCSCredentialsProvider> vcs;

    public VCSProvider(){
        files = new FileManager();
        parser = new ProjectParser();

        final GitLocalStorage localGit = new GitLocalStorage();
        localGit.installBaseRepository();

        vcs = new GitVCS();
        vcs.setStorage(localGit);
    }

    public static void main(String[] args) {
        failOnIllegalFormat(args);

        final String vcs = args[0];
        final String protocol = args[1];
        final String operation = args[2];
        final File project = new File(args[3]);

        failOnNotExistence(vcs, "git");
        failOnNotExistence(protocol, "file");
        failOnNotExistence(operation, "create", "read", "update", "delete");
        failOnSimpleFile(project);

        new VCSProvider().execute(vcs, protocol, operation, project);
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

    private void execute(String vcs, String protocol, String operation, File project) {
        switch (operation) {
            case "create": {
                createRepo(vcs, protocol, project);
                return;
            }
            case "read": {
                System.out.println(readSourcesFromRepo(vcs, protocol, project));
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

            final byte[] content = zipManager.zip(temp);
            final File sources = files.createTempFile(repo.getName(), "zip");
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
