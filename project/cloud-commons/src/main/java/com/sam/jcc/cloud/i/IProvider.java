/**
 * 
 */
package com.sam.jcc.cloud.i;

/**
 * @author Alec Kotovich
 * 
 */
public interface IProvider<T> {
	
	Long getId();

	String getI18NName();

	String getI18NDescription();

	boolean supports(T t);

	T preprocess(T t);

	T process(T t);

	T postprocess(T t);

	boolean isEnabled();

}
