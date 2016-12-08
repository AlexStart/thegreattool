package com.sam.jcc.cloud.vcs.git;

import static java.text.MessageFormat.format;

import com.sam.jcc.cloud.vcs.VCSException;
import com.sam.jcc.cloud.vcs.VCSRepository;

/**
 * @author Alexey Zhytnik
 * @since 06.12.2016
 */
public class GitRemoteStorage extends GitAbstractStorage {

	// TODO fix issue #3
	// private String host = PropertyResolver.getProperty("git.server.host");
	// private String protocol = PropertyResolver.getProperty("protocols.git");

	private final String host = "localhost";
	private final String protocol = "git://";

	@Override
	public String getRepositoryURI(VCSRepository repo) {
		return format("{0}{1}/{2}", protocol, host, repo.getName());
	}

	@Override
	public void setProtocol(String newProtocol) {
		if (!newProtocol.startsWith(protocol)) {
			throw new VCSException(format("Unknown protocol \"{0}\"", newProtocol));
		}
	}
}
