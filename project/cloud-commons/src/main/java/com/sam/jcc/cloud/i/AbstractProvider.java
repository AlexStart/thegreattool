/**
 * 
 */
package com.sam.jcc.cloud.i;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sam.jcc.cloud.event.ILoggable;

/**
 * @author Alec Kotovich
 *
 */
public abstract class AbstractProvider<T> extends AbstractCRUD<T> implements IProvider<T>, ILoggable {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<IEventManager<T>> eventManagers;

	public AbstractProvider(List<IEventManager<T>> eventManagers) {
		super();
		this.eventManagers = eventManagers;
	}

	@Override
	public T create(T t) {
		if (t != null) {
			if (supports(t)) {
				T preprocessed = preprocess(t);
				eventManagers.forEach(manager -> manager.fireEvent(preprocessed, this));

				T processed = process(preprocessed);
				eventManagers.forEach(manager -> manager.fireEvent(processed, this));

				T result = postprocess(processed);
				eventManagers.forEach(manager -> manager.fireEvent(processed, this));

				return result;
			} else {
				eventManagers.forEach(manager -> manager.fireEvent(t, this));
				throw new UnsupportedOperationException(t.toString());
			}

		}
		eventManagers.forEach(manager -> manager.fireEvent(null, this));
		return null;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	
}
