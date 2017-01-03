package com.sam.jcc.cloud.ci.impl;

import hudson.model.DownloadService;
import hudson.model.UpdateCenter;
import hudson.model.UpdateSite;
import hudson.util.FormValidation;
import hudson.util.PersistedList;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

import static hudson.util.FormValidation.Kind.ERROR;

/**
 * @author Alexey Zhytnik
 * @since 23-Dec-16
 */
@Slf4j
class JenkinsUpdateCenterManager {

    private static final String JENKINS_CENTRAL = "JenkinsCentral";
    private static final String JENKINS_CENTRAL_URL = "https://updates.jenkins-ci.org/current/update-center.json";

    private jenkins.model.Jenkins jenkins;

    public JenkinsUpdateCenterManager(jenkins.model.Jenkins jenkins) {
        this.jenkins = jenkins;
    }

    public void addJenkinsCentral() {
        addSite(new UpdateSite(JENKINS_CENTRAL, JENKINS_CENTRAL_URL));
    }

    public void addSite(UpdateSite site) {
        log.info("Add {} plugin repository", site.getId());

        final UpdateCenter updateCenter = jenkins.getUpdateCenter();
        final PersistedList<UpdateSite> sites = updateCenter.getSites();
        sites.add(site);
        forceUpdate(site);
    }

    private void forceUpdate(UpdateSite site) {
        try {
            final FormValidation result = site
                    .updateDirectly(DownloadService.signatureCheck)
                    .get();
            if (result.kind == ERROR) throw new RuntimeException();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        log.info("All {} plugins are loaded", site.getId());
    }
}