package com.sam.jcc.cloud.ci.impl;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "actions",
        "description",
        "keepDependencies",
        "properties",
        "scm",
        "canRoam",
        "disabled",
        "blockBuildWhenDownstreamBuilding",
        "blockBuildWhenUpstreamBuilding",
        "triggers",
        "concurrentBuild",
        "builders",
        "publishers",
        "buildWrappers"
})
@XmlRootElement(name = "project")
public class JenkinsProjectConfiguration {

    @XmlElement(required = true)
    protected String actions;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected String keepDependencies;
    @XmlElement(required = true)
    protected String properties;
    @XmlElement(required = true)
    protected JenkinsProjectConfiguration.Scm scm;
    @XmlElement(required = true)
    protected String canRoam;
    @XmlElement(required = true)
    protected String disabled;
    @XmlElement(required = true)
    protected String blockBuildWhenDownstreamBuilding;
    @XmlElement(required = true)
    protected String blockBuildWhenUpstreamBuilding;
    @XmlElement(required = true)
    protected String triggers;
    @XmlElement(required = true)
    protected String concurrentBuild;
    @XmlElement(required = true)
    protected JenkinsProjectConfiguration.Builders builders;
    @XmlElement(required = true)
    protected JenkinsProjectConfiguration.Publishers publishers;
    @XmlElement(required = true)
    protected JenkinsProjectConfiguration.BuildWrappers buildWrappers;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "hudsonPluginsWsCleanupPreBuildCleanup",
            "hudsonPluginsBuildTimeoutBuildTimeoutWrapper",
            "hpiCopyDataToWorkspacePlugin"
    })
    public static class BuildWrappers {

        @XmlElement(name = "hudson.plugins.ws__cleanup.PreBuildCleanup", required = true)
        protected JenkinsProjectConfiguration.BuildWrappers.HudsonPluginsWsCleanupPreBuildCleanup hudsonPluginsWsCleanupPreBuildCleanup;
        @XmlElement(name = "hudson.plugins.build__timeout.BuildTimeoutWrapper", required = true)
        protected JenkinsProjectConfiguration.BuildWrappers.HudsonPluginsBuildTimeoutBuildTimeoutWrapper hudsonPluginsBuildTimeoutBuildTimeoutWrapper;
        @XmlElement(name = "hpi.CopyDataToWorkspacePlugin", required = true)
        protected JenkinsProjectConfiguration.BuildWrappers.HpiCopyDataToWorkspacePlugin hpiCopyDataToWorkspacePlugin;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "folderPath",
                "makeFilesExecutable",
                "deleteFilesAfterBuild"
        })
        public static class HpiCopyDataToWorkspacePlugin {

            @XmlElement(required = true)
            protected String folderPath;
            @XmlElement(required = true)
            protected String makeFilesExecutable;
            @XmlElement(required = true)
            protected String deleteFilesAfterBuild;
            @XmlAttribute(name = "plugin")
            protected String plugin;
        }

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "strategy",
                "operationList"
        })
        public static class HudsonPluginsBuildTimeoutBuildTimeoutWrapper {

            @XmlElement(required = true)
            protected JenkinsProjectConfiguration.BuildWrappers.HudsonPluginsBuildTimeoutBuildTimeoutWrapper.Strategy strategy;
            @XmlElement(required = true)
            protected JenkinsProjectConfiguration.BuildWrappers.HudsonPluginsBuildTimeoutBuildTimeoutWrapper.OperationList operationList;
            @XmlAttribute(name = "plugin")
            protected String plugin;

            @Data
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "hudsonPluginsBuildTimeoutOperationsFailOperation"
            })
            public static class OperationList {

                @XmlElement(name = "hudson.plugins.build__timeout.operations.FailOperation", required = true)
                protected String hudsonPluginsBuildTimeoutOperationsFailOperation;
            }

            @Data
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                    "timeoutMinutes"
            })
            public static class Strategy {

                protected byte timeoutMinutes;
                @XmlAttribute(name = "class")
                protected String clazz;
            }
        }

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "deleteDirs",
                "cleanupParameter",
                "externalDelete"
        })
        public static class HudsonPluginsWsCleanupPreBuildCleanup {

            @XmlElement(required = true)
            protected String deleteDirs;
            @XmlElement(required = true)
            protected String cleanupParameter;
            @XmlElement(required = true)
            protected String externalDelete;
            @XmlAttribute(name = "plugin")
            protected String plugin;
        }
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "hudsonTasksBatchFile",
            "hudsonTasksShell"
    })
    public static class Builders {

        @XmlElement(name = "hudson.tasks.BatchFile")
        protected JenkinsProjectConfiguration.Builders.HudsonTasksBatchFile hudsonTasksBatchFile;
        @XmlElement(name = "hudson.tasks.Shell")
        protected JenkinsProjectConfiguration.Builders.HudsonTasksShell hudsonTasksShell;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "command"
        })
        public static class HudsonTasksBatchFile {

            @XmlElement(required = true)
            protected String command;
        }

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "command"
        })
        public static class HudsonTasksShell {

            @XmlElement(required = true)
            protected String command;
        }
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "hudsonTasksArtifactArchiver"
    })
    public static class Publishers {

        @XmlElement(name = "hudson.tasks.ArtifactArchiver", required = true)
        protected JenkinsProjectConfiguration.Publishers.HudsonTasksArtifactArchiver hudsonTasksArtifactArchiver;

        @Data
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "artifacts",
                "allowEmptyArchive",
                "onlyIfSuccessful",
                "fingerprint",
                "defaultExcludes",
                "caseSensitive"
        })
        public static class HudsonTasksArtifactArchiver {

            @XmlElement(required = true)
            protected String artifacts;
            @XmlElement(required = true)
            protected String allowEmptyArchive;
            @XmlElement(required = true)
            protected String onlyIfSuccessful;
            @XmlElement(required = true)
            protected String fingerprint;
            @XmlElement(required = true)
            protected String defaultExcludes;
            @XmlElement(required = true)
            protected String caseSensitive;
        }
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "value"
    })
    public static class Scm {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "class")
        protected String clazz;
    }
}
