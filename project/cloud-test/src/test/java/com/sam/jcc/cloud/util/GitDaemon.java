package com.sam.jcc.cloud.util;

import com.sam.jcc.cloud.i.Experimental;
import com.sam.jcc.cloud.vcs.VCSRepository;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.transport.Daemon;
import org.eclipse.jgit.transport.resolver.FileResolver;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.lang.Integer.valueOf;
import static lombok.AccessLevel.NONE;

/**
 * @author Alexey Zhytnik
 * @since 05.12.2016
 */
@Data
@Slf4j
@SuppressWarnings("Duplicates")
public class GitDaemon {

    private String host = getProperty("git.remote.server.host");
    private Integer port = valueOf(getProperty("git.remote.server.port"));

    @Setter(NONE)
    private File storage;

    private Daemon daemon;
    private int currentPort;

    public void startUp(File dir) {
        this.storage = dir;
        this.daemon = configureDaemon(dir);

        run(daemon);

        log.info("Git-daemon started");
    }

    public void shutDown() {
        daemon.stop();
        log.info("Git-daemon stopped");
    }

    /**
     * Applies fix for the Daemon bug, when Daemon doesn't close repositories.
     * Besides Daemon locks repository's files by OS.
     */
    public void disableExport(VCSRepository repo) throws IOException {
        final File location = new File(storage, repo.getName());
        final RepositoryCache.FileKey key = RepositoryCache.FileKey.exact(location, FS.DETECTED);
        final Repository jGitRepo = RepositoryCache.open(key, true);

        RepositoryCache.close(jGitRepo);
    }

    private void run(Daemon daemon) {
        try {
            daemon.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Daemon configureDaemon(File home) {
        final int availablePort = new AvailablePortFinder().find(host, port);

        if (availablePort != port) {
            log.info("Port {} isn't available, next available is {}", port, availablePort);
        }
        log.info("Configure Git-daemon[{}:{}] for {}", host, availablePort, home);

        currentPort = availablePort;

        final Daemon daemon = new Daemon(new InetSocketAddress(host, availablePort));
        daemon.setRepositoryResolver(new FileResolver<>(home, true));
        daemon.getService("receive-pack").setEnabled(true);
        return daemon;
    }

    @Experimental("Searches available port")
    private static class AvailablePortFinder {

        private static final int MAX_ATTEMPT_COUNT = 100;

        public int find(String host, int startPort) {
            int port = startPort, attempt = MAX_ATTEMPT_COUNT;

            while (attempt > 0) {
                if (isAvailable(host, port)) {
                    return port;
                }

                port++;
                attempt--;
            }
            throw new RuntimeException("Available port not found!");
        }

        private boolean isAvailable(String host, int port) {
            ServerSocket ss = null;
            DatagramSocket ds = null;
            try {
                InetAddress address = InetAddress.getByName(host);
                ss = new ServerSocket(port, 50, address);
                ss.setReuseAddress(true);
                ds = new DatagramSocket(port, address);
                ds.setReuseAddress(true);
                return true;
            } catch (IOException ignored) {
            } finally {
                IOUtils.closeQuietly(ds);
                IOUtils.closeQuietly(ss);
            }
            return false;
        }
    }
}