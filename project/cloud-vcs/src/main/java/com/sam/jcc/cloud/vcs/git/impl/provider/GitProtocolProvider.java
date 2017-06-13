package com.sam.jcc.cloud.vcs.git.impl.provider;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.vcs.VCS;
import com.sam.jcc.cloud.vcs.git.impl.storage.GitRemoteStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author olegk
 */
@Component
public class GitProtocolProvider extends VCSProvider implements IHealth {

    private static final long GIT_PROTOCOL_PROVIDER_ID = 4L;
    public static final String TYPE = "git-protocol";

    @Autowired
    private GitRemoteStorage storage;

    @Autowired
    @Qualifier("gitVCS")
    protected VCS vcs;

    public GitProtocolProvider(List<IEventManager<IVCSMetadata>> eventManagers) {
        super(eventManagers);
    }

    @PostConstruct
    public void setUp() {
        setVcs(requireNonNull(vcs));
        vcs.setStorage(requireNonNull(storage));
        storage.installBaseRepository();
    }

    // TODO: need check server
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Long getId() {
        return GIT_PROTOCOL_PROVIDER_ID;
    }

    @Override
    public IHealthMetadata checkHealth() {
        return new IHealthMetadata() {

            @Override
            public String getHost() {
                return storage.getHost();
            }

            @Override
            public String getPort() {
                return String.valueOf(storage.getPort());
            }

            @Override
            public String getUrl() {
                try {
                    StringBuilder sb = new StringBuilder();
                    String line = storage.getRepositoryURL();
                    sb.append(line);
                    return sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public boolean isAlive() {
                try {
                    InetSocketAddress sa = new InetSocketAddress(getHost(), Integer.valueOf(getPort()));
                    Socket ss = new Socket();
                    ss.connect(sa, 500);
                    ss.close();
                    return true;
                } catch (Exception e) {
                    return false;
                }

            }

            @Override
            public String getName() {
                return getI18NDescription();
            }

            @Override
            public Long getId() {
                return GIT_PROTOCOL_PROVIDER_ID;
            }

        };
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
