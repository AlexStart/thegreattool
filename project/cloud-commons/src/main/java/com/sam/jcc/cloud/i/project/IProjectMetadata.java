/**
 * 
 */
package com.sam.jcc.cloud.i.project;

/**
 * @author Alec Kotovich
 *
 */
public interface IProjectMetadata {

	Long getId();

	String getName();
	
	String getType();

	boolean hasSources();

	boolean hasVCS();

	boolean hasCI();

	boolean hasDb();

	byte[] getProjectSources();
}
