/**
 *
 */
package com.sam.jcc.cloud.vcs.git.impl.provider;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.git.impl.storage.GitlabServer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author olegk
 */
@Component
public class GitlabProvider extends VCSProvider implements IHealth {

    private static final long GITLAB_PROVIDER_ID = 8L;
    public static final String TYPE = "gitlab";

    @Autowired
    private GitlabServer storage;

    @Autowired
    @Qualifier("gitServerVCS")
    protected VCS vcs;

    public GitlabProvider(List<IEventManager<IVCSMetadata>> eventManagers) {
        super(eventManagers);
    }

    @PostConstruct
    public void setUp() {
        setVcs(requireNonNull(vcs));
        vcs.setStorage(requireNonNull(storage));
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
                return storage.getHost();
            }

            @Override
            public String getPort() {
                return storage.getPort();
            }

            @Override
            public String getUrl() {
                return "http://" + getHost() + ":" + getPort() + "/" + storage.getUrl();
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
