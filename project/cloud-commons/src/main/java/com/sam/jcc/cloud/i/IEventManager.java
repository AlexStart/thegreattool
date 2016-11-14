/**
 * 
 */
package com.sam.jcc.cloud.i;

/**
 * @author Alec Kotovich
 *
 */
public abstract class IEventManager<T> {

	public abstract IEvent fireEvent(T src, Object parent);
}
