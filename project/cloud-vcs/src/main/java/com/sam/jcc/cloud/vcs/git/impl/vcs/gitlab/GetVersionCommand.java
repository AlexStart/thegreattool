package com.sam.jcc.cloud.vcs.git.impl.vcs.gitlab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.apache.commons.lang3.Validate.isTrue;

@Component
public class GetVersionCommand extends AbstractCommand {

    private ObjectMapper objectMapper = new ObjectMapper();

    public GitlabVersion call(String repositoryUri, String token) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(getGetVersionUri(repositoryUri));
        setRequestHeaders(request, token);
        try {
            HttpResponse response = httpClient.execute(request);
            isTrue(response.getStatusLine().getStatusCode() == 200,
                    "Get project version failed due to" + response.getStatusLine());
            return objectMapper.readValue(response.getEntity().getContent(), GitlabVersion.class);
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }

    private String getGetVersionUri(String repositoryUri) {
        return getBaseUri(repositoryUri) + "/version";
    }
}

