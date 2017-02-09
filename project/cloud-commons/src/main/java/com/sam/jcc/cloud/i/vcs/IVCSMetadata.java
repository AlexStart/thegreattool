/**
 * 
 */
package com.sam.jcc.cloud.i.vcs;

/**
 * @author Alec Kotovich
 *
 */
public interface IVCSMetadata {

	void setName(String name);
	
	String getName();
	
	void setDescription(String description);
	
	String getDescription();
	
	void setCommitMessage(String message);
	
	String getCommitMessage();
	
	String getVcsType();
}
