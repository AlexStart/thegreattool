package com.sam.jcc.cloud.vcs.git.impl.provider;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.vcs.git.impl.vcs.GitRemoteVCS;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * @author olegk
 */
@Component
public class GitProtocolProvider extends VCSProvider implements IHealth {

    private static final long GIT_PROTOCOL_PROVIDER_ID = 4L;
    public static final String TYPE = "git-protocol";

    protected GitRemoteVCS vcs;

    public GitProtocolProvider(List<IEventManager<IVCSMetadata>> eventManagers, GitRemoteVCS vcs) {
        super(eventManagers, vcs);
        this.vcs = vcs;
        vcs.installBaseRepository();
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
                return vcs.getHost();
            }

            @Override
            public String getPort() {
                return String.valueOf(vcs.getPort());
            }

            @Override
            public String getUrl() {
                try {
                    StringBuilder sb = new StringBuilder();
                    String line = vcs.getRepositoryURL();
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
