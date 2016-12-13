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
public class GitFileProvider extends VCSProvider {

	public GitFileProvider(List<IEventManager<IVCSMetadata>> eventManagers) {
		super(eventManagers);
	}

}
