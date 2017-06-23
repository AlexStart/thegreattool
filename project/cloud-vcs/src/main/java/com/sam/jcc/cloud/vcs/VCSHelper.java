package com.sam.jcc.cloud.vcs;

import com.sam.jcc.cloud.auth.InitOnceAdminBean;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.TempFile;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import com.sam.jcc.cloud.vcs.git.impl.VCSRepositoryBuilder;
import com.sam.jcc.cloud.vcs.git.impl.vcs.GitFileVCS;
import com.sam.jcc.cloud.vcs.git.impl.vcs.GitRemoteVCS;
import com.sam.jcc.cloud.vcs.git.impl.vcs.GitlabServerVCS;
import com.sam.jcc.cloud.vcs.git.impl.vcs.gitlab.CreateCommitCommand;
import com.sam.jcc.cloud.vcs.git.impl.vcs.gitlab.GetVersionCommand;

import java.io.File;
import java.util.Arrays;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

/**
 * @author Alexey Zhytnik
 * @since 29.11.2016
 */
@Deprecated
public class VCSHelper {

    private static ZipArchiveManager zipManager = new ZipArchiveManager();

    private final FileManager files;
    private final VCS<VCSCredentials> vcs;

    public VCSHelper(String protocol) {
        files = new FileManager();
        vcs = setUpVcsByProtocol(protocol);
    }

    VCSHelper(VCS<VCSCredentials> vcs) {
        files = new FileManager();
        this.vcs = vcs;
    }

    private VCS<VCSCredentials> setUpVcsByProtocol(String protocol) {
        VCS<VCSCredentials> vcs;
        if (protocol.equals("git")) {
            vcs = new GitRemoteVCS();
            ((GitRemoteVCS) vcs).installBaseRepository();
        } else if (protocol.equals("file")) {
            vcs = new GitFileVCS();
            ((GitFileVCS) vcs).installBaseRepository();
        } else if (protocol.equals("gitlab")) {
            vcs = new GitlabServerVCS(
                    new InitOnceAdminBean(), new CreateCommitCommand(), new GetVersionCommand());
        } else {
            throw new VCSUnknownProtocolException(protocol);
        }
        return vcs;
    }

    public static void main(String[] args) {
        failOnIllegalFormat(args);

        final String vcs = args[0];
        final String protocol = args[1];
        final String operation = args[2];
        final File project = new File(args[3]);

        failOnNotExistence(vcs, "git");
        failOnNotExistence(protocol, "git", "file", "gitlab");
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

            final byte[] content = zipManager.zip(temp);
            final File sources = files.createTempFile(repo.getName(), ".zip");
            files.write(content, sources);
            repo.setSources(sources);
        }
        return repo;
    }

    private VCSRepository parse(File project) {
        return new VCSRepositoryBuilder().apply(project);
    }
}
