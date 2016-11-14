/**
 * 
 */
package com.sam.jcc.cloud.event;

import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.IEvent;
import com.sam.jcc.cloud.i.IEventManager;

/**
 * @author Alec Kotovich
 *
 */
@Component
public class DefaultLoggingEventManager<T> extends IEventManager<T> {

	@Override
	public IEvent fireEvent(T src, Object parent) {
		ILoggable loggable = (ILoggable) parent;
		loggable.getLogger().info(src.toString());
		return new DefaultLoggingEvent();
	}

	public final class DefaultLoggingEvent implements IEvent {

	}

}
