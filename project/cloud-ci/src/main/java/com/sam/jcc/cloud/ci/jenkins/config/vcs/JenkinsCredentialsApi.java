package com.sam.jcc.cloud.ci.jenkins.config.vcs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sam.jcc.cloud.vcs.exception.VCSException;
import lombok.Builder;
import lombok.Getter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static org.apache.commons.lang3.Validate.isTrue;

@Component
public class JenkinsCredentialsApi {

    private static final String CREATE_CREDENTIALS_URL = "/credentials/store/system/domain/_/createCredentials";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    private static final String EMPTY_VALUE = "0";
    private static final String CREDENTIALS_SCOPE = "GLOBAL";
    private static final String CLASS = "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl";

    private ObjectMapper mapper = new ObjectMapper();

    //For authenticated usage additional header is required 'Jenkins-Crumb'
    //Use GET request '/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)'
    public void createJenkinsCredentials(String id, String user, String password, String description) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(getProperty("ci.jenkins.url") + CREATE_CREDENTIALS_URL);
        request.setHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
        request.setEntity(generateRequestJson(id, user, password, description));

        try {
            HttpResponse response = httpClient.execute(request);
            isTrue(response.getStatusLine().getStatusCode() == 302,
                    "Jenkins credentials for gitlab access was not created: " + response.getStatusLine());
        } catch (IOException e) {
            throw new VCSException(e);
        }
    }

    private StringEntity generateRequestJson(String id, String user, String password, String description) {
        PayloadJson json = PayloadJson.builder().empty(EMPTY_VALUE).credentials(
                Credentials.builder().scope(CREDENTIALS_SCOPE)
                        .id(id)
                        .username(user)
                        .password(password)
                        .description(description)
                        .clazz(CLASS)
                        .build()
        ).build();

        try {
            return new StringEntity("json=" + mapper.writeValueAsString(json));
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            throw new VCSException(e);
        }
    }

    @Getter
    @Builder
    static class PayloadJson {
        @JsonProperty
        private String empty;
        private Credentials credentials;
    }

    @Getter
    @Builder
    static class Credentials {
        private String scope;
        private String id;
        private String username;
        private String password;
        private String description;
        @JsonProperty("$class")
        private String clazz;
    }
}


