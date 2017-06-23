package com.sam.jcc.cloud.vcs.git.impl.vcs.gitlab;

import org.apache.http.client.methods.HttpRequestBase;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;

public class AbstractCommand {

    private final static String API_VERSION = "/api/" + getProperty("gitlab.remote.server.api.version");

    protected String getBaseUri(String repositoryUri) {
        return repositoryUri + API_VERSION;
    }

    protected void setRequestHeaders(HttpRequestBase request, String token) {
        request.setHeader("PRIVATE-TOKEN", token);
        request.setHeader("Content-Type", "application/json;");
    }
}
