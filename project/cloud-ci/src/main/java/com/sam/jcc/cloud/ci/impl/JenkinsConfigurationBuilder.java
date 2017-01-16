package com.sam.jcc.cloud.ci.impl;

import com.sam.jcc.cloud.ci.CIProject;
import com.sam.jcc.cloud.ci.exception.CIException;
import com.sam.jcc.cloud.ci.impl.JenkinsProjectConfiguration.Builders.HudsonTasksBatchFile;
import com.sam.jcc.cloud.ci.impl.JenkinsProjectConfiguration.Builders.HudsonTasksShell;
import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.i.Experimental;
import com.sam.jcc.cloud.i.OSDependent;
import com.sam.jcc.cloud.utils.files.ItemStorage;
import com.sam.jcc.cloud.utils.parsers.ProjectParser;
import org.springframework.core.io.ClassPathResource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static com.sam.jcc.cloud.utils.SystemUtils.isWindowsOS;

/**
 * @author Alexey Zhytnik
 * @since 18-Dec-16
 */
class JenkinsConfigurationBuilder {

    public static final String MAVEN_ARTIFACTS = "target/*.jar";
    public static final String GRADLE_ARTIFACTS = "build/libs/*.jar";

    private JaxbConfigSupport jaxbSupport;
    private ItemStorage<CIProject> workspace;

    private ProjectParser parser = new ProjectParser();

    public JenkinsConfigurationBuilder(ItemStorage<CIProject> workspace) {
        this.workspace = workspace;
        this.jaxbSupport = new JaxbConfigSupport();
    }

    public String build(CIProject project) {
        try {
            return buildUnsecured(project);
        } catch (JAXBException e) {
            throw new CIException(e);
        }
    }

    private String buildUnsecured(CIProject project) throws JAXBException {
        final JenkinsProjectConfiguration config = jaxbSupport.marshal("/basic-config.xml");

        setUpProjectDir(config, project);

        boolean maven = parser.isMaven(workspace.get(project));
        setUpBuilder(config, maven);
        setUpArtifacts(config, maven);

        return jaxbSupport.unmarshal(config);
    }

    private void setUpProjectDir(JenkinsProjectConfiguration config, CIProject project) {
        final String dir = workspace.get(project).getAbsolutePath();

        config
                .getBuildWrappers()
                .getHpiCopyDataToWorkspacePlugin()
                .setFolderPath(dir);
    }

    private void setUpBuilder(JenkinsProjectConfiguration config, boolean isMaven) {
        final String command = getBuildCommand(isMaven);

        if (isWindowsOS()) {
            HudsonTasksBatchFile cmdCommand = new HudsonTasksBatchFile();
            cmdCommand.setCommand(command);
            config.getBuilders().setHudsonTasksBatchFile(cmdCommand);
        } else {
            HudsonTasksShell shellCommand = new HudsonTasksShell();
            shellCommand.setCommand(command);
            config.getBuilders().setHudsonTasksShell(shellCommand);
        }
    }

    private void setUpArtifacts(JenkinsProjectConfiguration config, boolean maven) {
        final String artifacts = maven ? MAVEN_ARTIFACTS : GRADLE_ARTIFACTS;

        config
                .getPublishers()
                .getHudsonTasksArtifactArchiver()
                .setArtifacts(artifacts);
    }

    @OSDependent("Supported for Windows & Unix")
    private String getBuildCommand(boolean maven) {
        if (isWindowsOS()) {
            if (maven) {
                return "mvnw.cmd install";
            } else {
                return "gradlew.bat build";
            }
        }
        if (maven) {
            return "./mvnw install";
        }
        return "./gradlew build";
    }

    @Experimental("JAXB configuration loader")
    private static class JaxbConfigSupport {

        private JAXBContext context;

        public JaxbConfigSupport() {
            try {
                context = JAXBContext.newInstance(JenkinsProjectConfiguration.class);
            } catch (JAXBException e) {
                throw new CIException(e);
            }
        }

        public JenkinsProjectConfiguration marshal(String xml) throws JAXBException {
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final InputStream config = load(xml);

            return (JenkinsProjectConfiguration) unmarshaller.unmarshal(config);
        }

        public String unmarshal(JenkinsProjectConfiguration config) throws JAXBException {
            final Marshaller marshaller = context.createMarshaller();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(config, out);
            return convertToString(out);
        }

        private InputStream load(String xml) {
            try {
                return new ClassPathResource(xml).getInputStream();
            } catch (IOException e) {
                throw new InternalCloudException(e);
            }
        }

        private String convertToString(ByteArrayOutputStream stream) {
            try {
                return stream.toString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new InternalCloudException(e);
            }
        }
    }
}
