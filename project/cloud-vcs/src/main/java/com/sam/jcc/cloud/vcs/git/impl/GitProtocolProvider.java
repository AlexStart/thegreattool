/**
 * 
 */
package com.sam.jcc.cloud.vcs.git.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.vcs.VCSProvider;

/**
 * @author olegk
 *
 */
@Component
public class GitProtocolProvider extends VCSProvider {

	public GitProtocolProvider(List<IEventManager<IVCSMetadata>> eventManagers) {
		super(eventManagers);
	}

}
