/**
 * 
 */
package com.sam.jcc.cloud.i;

/**
 * @author olegk
 *
 */
public interface IHealthMetadata {
	
	Long getId();
	
	String getName();

	String getHost();
	
	String getPort();
	
	String getUrl();
	
	boolean isAlive();	
	
}
