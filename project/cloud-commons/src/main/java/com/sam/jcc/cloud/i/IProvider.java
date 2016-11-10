/**
 * 
 */
package com.sam.jcc.cloud.i;

/**
 * @author Alec Kotovich
 * 
 */
public interface IProvider<T> extends ICRUD<T> {

	String getName();

	String getDescription();
	
	boolean supports(T t);

	T preprocess(T t);

	T process(T t);

	T postprocess(T t);
	
	boolean isEnabled();
	
}
