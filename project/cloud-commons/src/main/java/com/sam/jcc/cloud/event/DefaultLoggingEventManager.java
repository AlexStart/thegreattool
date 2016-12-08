/**
 *
 */
package com.sam.jcc.cloud.event;

import com.sam.jcc.cloud.i.IEvent;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.project.IStatus;
import com.sam.jcc.cloud.i.project.IStatusable;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

/**
 * @author Alec Kotovich
 */
@Component
public class DefaultLoggingEventManager<T> extends IEventManager<T> {

    @Override
    public IEvent fireEvent(T src, Object parent) {
        if (nonNull(src) && hasLogger(parent)) {
            log(src, parent);
        }
        return new DefaultLoggingEvent();
    }

    private void log(T src, Object parent) {
        if (!hasStatus(src)) {
            getLogger(parent).info("{} executed", src);
        } else {
            getLogger(parent).info("{} has {} status", src, getStatus(src));
        }
    }

    private boolean hasLogger(Object obj) {
        return obj instanceof ILoggable;
    }

    private boolean hasStatus(T src) {
        return src instanceof IStatusable;
    }

    private Logger getLogger(Object obj) {
        final ILoggable loggable = (ILoggable) obj;
        return loggable.getLogger();
    }

    private IStatus getStatus(Object obj) {
        final IStatusable s = (IStatusable) obj;
        return s.getStatus();
    }

    public final class DefaultLoggingEvent implements IEvent {
    }
}
