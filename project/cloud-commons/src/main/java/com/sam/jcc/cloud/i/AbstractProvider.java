/**
 *
 */
package com.sam.jcc.cloud.i;

import com.sam.jcc.cloud.event.ILoggable;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PACKAGE;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

/**
 * @author Alec Kotovich
 */
public abstract class AbstractProvider<T> extends AbstractCRUD<T> implements IProvider<T>, ILoggable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<IEventManager<T>> eventManagers;

    @Setter(value = PACKAGE)
    private Map<String, String> names;

    @Setter(value = PACKAGE)
    private Map<String, String> descriptions;

    public AbstractProvider(List<IEventManager<T>> eventManagers) {
        super();
        this.eventManagers = eventManagers;
    }

    @Override
    public String getI18NDescription() {
        return descriptions.get(getLanguage());
    }

    @Override
    public String getI18NName() {
        return names.get(getLanguage());
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
                eventManagers.forEach(manager -> manager.fireEvent(result, this));

                return result;
            } else {
                eventManagers.forEach(manager -> manager.fireEvent(t, this));
                throw new BusinessCloudException("Unsupported type " + t);
            }
        }
        eventManagers.forEach(manager -> manager.fireEvent(null, this));
        return null;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private String getLanguage() {
        return getLocale().getLanguage();
    }
}
