package com.sam.jcc.cloud.vcs.utils;

import java.io.File;
import java.net.InetSocketAddress;

import org.eclipse.jgit.transport.Daemon;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Alexey Zhytnik
 * @since 05.12.2016
 */
@Slf4j
public class GitDaemonRunner {

	private Daemon daemon;
	private final String host;
	private final int port;

	public GitDaemonRunner(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void start(File dir) {
		log.info("Run Git-daemon in {}", dir);

		try {
			daemon = new Daemon(new InetSocketAddress(host, port));
			daemon.start();
			log.info("Git-daemon started");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		if (daemon != null) {
			daemon.stop();
		}
	}

	public String getHost() {
		if (daemon != null) {
			return daemon.getAddress().getHostName();
		}
		return null;
	}

	public int getPort() {
		if (daemon != null) {
			return daemon.getAddress().getPort();
		}
		return -1;
	}
}