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
	
	boolean hasCQ();

	byte[] getProjectSources();
	
	String getCi();
	
	String getDb();
	
	String getVcs();
	
	String getCQ();
}
