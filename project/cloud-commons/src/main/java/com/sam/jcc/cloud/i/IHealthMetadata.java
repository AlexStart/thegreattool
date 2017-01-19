/**
 * 
 */
package com.sam.jcc.cloud.i;

/**
 * @author olegk
 *
 */
public interface IHealthMetadata {

	String getHost();
	
	String getPort();
	
	String getUrl();
	
	boolean isAlive();	
	
}
