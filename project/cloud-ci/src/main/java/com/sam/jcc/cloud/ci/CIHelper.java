package com.sam.jcc.cloud.ci;

import com.sam.jcc.cloud.ci.jenkins.Jenkins;
import com.sam.jcc.cloud.utils.files.FileManager;
import com.sam.jcc.cloud.utils.files.ZipArchiveManager;
import com.sam.jcc.cloud.utils.parsers.ProjectParser;

import java.io.File;
import java.util.Arrays;
import java.util.Map.Entry;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

/**
 * @author Alexey Zhytnik
 * @since 15-Dec-16
 */
public class CIHelper {

    private static ZipArchiveManager zipManager = new ZipArchiveManager();

    private CIServer jenkins;

    private ProjectParser parser = new ProjectParser();
    private FileManager files = new FileManager();

    public CIHelper() {
        jenkins = new Jenkins();
    }

    CIHelper(CIServer jenkins) {
        this.jenkins = jenkins;
    }

    public static void main(String[] args) {
        failOnIllegalFormat(args);

        final String ci = args[0];
        final String operation = args[1];
        final File project = new File(args[2]);

        failOnNotExistence(ci, "jenkins");
        failOnNotExistence(operation, "create", "getBuild", "getStatus", "update", "delete");
        failOnSimpleFile(project);

        new CIHelper().execute(ci, operation, project);
    }

    private static void failOnIllegalFormat(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Arguments must be in next format: " +
                    "[ci, operation, project file]!");
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

    void execute(String ci, String operation, File project) {
        switch (operation) {
            case "create": {
                createNewProject(ci, project);
                return;
            }
            case "getBuild": {
                System.out.print(getBuild(ci, project));
                return;
            }
            case "getStatus": {
                System.out.print(getStatus(ci, project));
                return;
            }
            case "update": {
                updateSources(ci, project);
                return;
            }
            case "delete": {
                deleteProject(ci, project);
            }
        }
    }

    private void createNewProject(String ci, File project) {
        jenkins.create(parse(project));
    }

    private File getBuild(String ci, File sources) {
        final CIProject project = parse(sources);
        final File zip = files.createTempFile(project.getName(), ".zip");

        final byte[] build = jenkins.getLastSuccessfulBuild(project);
        files.write(build, zip);
        return zip;
    }

    private CIBuildStatus getStatus(String ci, File project) {
        return jenkins.getLastBuildStatus(parse(project));
    }

    private void updateSources(String ci, File project) {
        jenkins.build(parse(project));
    }

    private void deleteProject(String ci, File project) {
        jenkins.delete(parse(project));
    }

    private CIProject parse(File zip) {
        final Entry<String, String> metadata = parser.parse(zip);

        final CIProject p = new CIProject();
        p.setArtifactId(metadata.getValue());
        p.setSources(extractSources(zip));
        return p;
    }

    private File extractSources(File zip) {
        final File temp = new FileManager().createTempDir();
        temp.deleteOnExit();

        zipManager.unzip(zip, temp);
        return temp;
    }
}