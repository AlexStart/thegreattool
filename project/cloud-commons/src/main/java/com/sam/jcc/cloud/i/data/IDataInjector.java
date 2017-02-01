/**
 * 
 */
package com.sam.jcc.cloud.i.data;

/**
 * @author olegk
 *
 */
public interface IDataInjector<T> {

	void inject(T data);
}
