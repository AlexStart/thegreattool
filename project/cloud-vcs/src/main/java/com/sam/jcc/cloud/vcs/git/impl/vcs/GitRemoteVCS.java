package com.sam.jcc.cloud.vcs.git.impl.vcs;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.vcs.VCSRepository;
import com.sam.jcc.cloud.vcs.exception.VCSUnknownProtocolException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.lang.Integer.valueOf;
import static java.text.MessageFormat.format;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
@Component
public class GitRemoteVCS extends AbstractGitVCS {

    @Getter
    private String host = getProperty("git.remote.server.host");

    @Setter
    @Getter
    @VisibleForTesting
    private Integer port = valueOf(getProperty("git.remote.server.port"));

    private String protocol = getProperty("protocols.git");

    @Override
    public String getRepositoryURI(VCSRepository repo) {
        return format("{0}{1}:{2}/{3}", protocol, host, Integer.toString(port), repo.getName());
    }

    @Override
    public void setProtocol(String newProtocol) {
        if (!newProtocol.startsWith(protocol)) {
            throw new VCSUnknownProtocolException(newProtocol);
        }
    }

    public String getRepositoryURL() throws UnknownHostException {
        String hostName = host.equals("localhost") ? InetAddress.getLocalHost().getHostName() : host;
        return format("{0}{1}:{2}", protocol, hostName, Integer.toString(port));
    }

}
