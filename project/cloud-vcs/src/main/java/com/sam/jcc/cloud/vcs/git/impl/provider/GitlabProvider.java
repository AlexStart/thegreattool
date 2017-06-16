/**
 *
 */
package com.sam.jcc.cloud.vcs.git.impl.provider;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.vcs.git.impl.vcs.GitlabServerVCS;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

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
        return checkHealth().isAlive();
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
                return "http://" + getHost() + ":" + getPort() + "/" + vcs.getUrl();
            }

            @Override
            public boolean isAlive() {
                try {
                    HttpGet request = new HttpGet(getUrl());
                    HttpResponse response = httpClient.execute(request);
                    return response.getStatusLine().getStatusCode() == 200;
                } catch (Exception e) {
                    return false;
                }
            }

            //TODO remove isAlive.isEnabled
//            @Override
//            public boolean isAlive() {
//                return storage.isEnabled();
//            }

        };

    }

}
