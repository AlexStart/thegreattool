/**
 *
 */
package com.sam.jcc.cloud.provider;

import com.sam.jcc.cloud.crud.AbstractCRUD;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.ILoggable;
import com.sam.jcc.cloud.i.IProvider;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PACKAGE;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

/**
 * @author Alec Kotovich
 */
public abstract class AbstractProvider<T> extends AbstractCRUD<T> implements IProvider<T>, ILoggable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final List<IEventManager<T>> eventManagers;

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
        if (nonNull(t)) {
            if (supports(t)) {
                T preprocessed = preprocess(t);
                notify(preprocessed);

                T processed = process(preprocessed);
                notify(processed);

                T result = postprocess(processed);
                notify(result);

                return result;
            } else {
                notify(t);
                throw new UnsupportedTypeException(t);
            }
        }
        notify(null);
        return null;
    }

    protected void notify(T item) {
        eventManagers.forEach(manager -> manager.fireEvent(item, this));
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private String getLanguage() {
        return getLocale().getLanguage();
    }
}
