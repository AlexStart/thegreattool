/**
 *
 */
package com.sam.jcc.cloud.vcs.git.impl.provider;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.vcs.git.impl.vcs.GitlabServerVCS;
import com.sam.jcc.cloud.vcs.git.impl.vcs.gitlab.GitlabVersion;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author olegk
 */
@Component
public class GitlabProvider extends VCSProvider implements IHealth {

    private static final long GITLAB_PROVIDER_ID = 8L;
    public static final String TYPE = "gitlab";

    private GitlabServerVCS vcs;

    public GitlabProvider(List<IEventManager<IVCSMetadata>> eventManagers, GitlabServerVCS vcs) {
        super(eventManagers, vcs);
        this.vcs = vcs;
    }

    @Override
    public Long getId() {
        return GITLAB_PROVIDER_ID;
    }

    @Override
    public boolean isEnabled() {
        return vcs.isEnabled();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public IHealthMetadata checkHealth() {
        return new IHealthMetadata() {

            private final CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            @Override
            public Long getId() {
                return GITLAB_PROVIDER_ID;
            }

            @Override
            public String getName() {
                return getI18NDescription();
            }

            @Override
            public String getHost() {
                return vcs.getHost();
            }

            @Override
            public String getPort() {
                return vcs.getPort();
            }

            @Override
            public String getUrl() {
                try {
                    GitlabVersion gitlabVersion = vcs.getVersion();
                    URL apiURL = vcs.getApiUrl();

                    StringBuilder sb = new StringBuilder();
                    sb.append(getGitlabUrl()).append("\n");
                    sb.append("Gitlab: ").append(gitlabVersion.getVersion());
                    sb.append(" (revision: ").append(gitlabVersion.getRevision()).append(")").append("\n");
                    sb.append("API url: ").append(apiURL.getPath());
                    return sb.toString();

                } catch (Exception e) {
                    try {
                        return getGitlabUrl();
                    } catch (Exception e2) {
                        return null;
                    }
                }
            }

            @Override
            public boolean isAlive() {
                try {
                    HttpGet request = new HttpGet(getGitlabUrl());
                    HttpResponse response = httpClient.execute(request);
                    return response.getStatusLine().getStatusCode() == 200;
                } catch (Exception e) {
                    return false;
                }
            }

            private String getGitlabUrl() throws MalformedURLException, UnknownHostException {
                Integer port = Integer.valueOf(getPort());
                String hostName = getHost().equals("localhost") ? InetAddress.getLocalHost().getHostName() : getHost();
                return new URL("http", hostName, port, "/" + vcs.getUrl()).toString();
            }
        };
    }
}