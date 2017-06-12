/**
 *
 */
package com.sam.jcc.cloud.vcs.git.impl;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.vcs.VCSRepository;
import lombok.Getter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.COMMITED;
import static com.sam.jcc.cloud.vcs.VCSRepositoryStatus.PUSHED;

/**
 * @author olegk
 */
@Component
public class GitlabProvider extends VCSProvider implements IHealth {

    private static final long GITLAB_PROVIDER_ID = 8L;
    public static final String TYPE = "gitlab";

    @Getter
    @VisibleForTesting
    private GitlabServer gitlab;

    public GitlabProvider(List<IEventManager<IVCSMetadata>> eventManagers) {
        super(eventManagers);
    }

    @PostConstruct
    public void setUp() {
        gitlab = new GitlabServer();
        git.setStorage(gitlab);
    }

    @Override
    public Long getId() {
        return GITLAB_PROVIDER_ID;
    }

    @Override
    public boolean supports(IVCSMetadata metadata) {
        return metadata instanceof VCSRepository;
    }

    @Override
    public boolean isEnabled() {
        return gitlab.isEnabled();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    //TODO refactor, resolve copy-paste
    @Override
    public IVCSMetadata update(IVCSMetadata m) {
        final VCSRepository repo = asVCSRepository(m);

        gitlab.commit(repo);

        updateStatus(repo, COMMITED);
        updateStatus(repo, PUSHED);
        return repo;
    }

    @Override
    protected GitAbstractStorage getStorage() {
        return gitlab;
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
                return getProperty("gitlab.remote.server.host");
            }

            @Override
            public String getPort() {
                return getProperty("gitlab.remote.server.port");
            }

            @Override
            public String getUrl() {
                return "http" + getHost() + ":" + getPort() + "/" + getUrl() + "/" + getProperty("gitlab.remote.path");
            }

            @Override
            public boolean isAlive() {
                try {
                    HttpGet request = new HttpGet(getUrl());
                    HttpResponse response = httpClient.execute(request);
                    return (response.getStatusLine().getStatusCode() == 200);
                } catch (Exception e) {
                    return false;
                }
            }

        };

    }

}
