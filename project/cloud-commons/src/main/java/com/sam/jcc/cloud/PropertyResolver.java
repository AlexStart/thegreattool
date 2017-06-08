package com.sam.jcc.cloud;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.exception.InternalCloudException;

/**
 * @author Alexey Zhytnik
 * @since 07.12.2016
 */
@Component
public class PropertyResolver {

	private static PropertyResolver INSTANCE = new PropertyResolver();

	private PropertiesConfiguration configuration;
	private PropertiesConfiguration configurationOverride;

	private PropertyResolver() {
		tryLoadProperties();

		configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
		if (configurationOverride != null) {
			configurationOverride.setReloadingStrategy(new FileChangedReloadingStrategy());
		}
	}

	private void tryLoadProperties() {
		try {
			configuration = new PropertiesConfiguration("cloud.properties");
		} catch (ConfigurationException e) {
			// Mandatory
			throw new InternalCloudException(e);
		}

		try {
			configurationOverride = new PropertiesConfiguration("cloud-override.properties");
		} catch (ConfigurationException e) {
			// Optional file. Nothing to do if it is missing.
		}
	}

	@VisibleForTesting
	PropertyResolver(PropertiesConfiguration configuration) {
		this.configuration = configuration;
	}

	public static String getProperty(String key) {
		return INSTANCE.getValue(key);
	}

	@VisibleForTesting
	String getValue(String key) {
		final String systemValue = System.getProperty(key);
		if (nonNull(systemValue))
			return systemValue;

		String value = configuration.getString(key);
		value = overrideValueIfExists(key, value);

		if (isNull(value)) {
			if (isNull(value)) {
				throw new PropertyNotFoundException(key);
			}
		}
		return tryResolveInjections(value);
	}

	private String overrideValueIfExists(String key, String value) {
		if (configurationOverride != null) {
			String valueOverridden = configurationOverride.getString(key);
			if (valueOverridden != null) {
				value = valueOverridden;
				// fix: expose to the rest of the world )
				System.setProperty(key, valueOverridden);
			}
		}
		return value;
	}

	// TODO: use PropertyPlaceholderConfigurer
	private String tryResolveInjections(String value) {
		if (value.contains("${user.home}")) {
			String home = System.getProperty("user.home");
			return value.replace("${user.home}", home);
		}
		return value;
	}

	public static class PropertyNotFoundException extends InternalCloudException {
		public PropertyNotFoundException(String property) {
			super("property.notFound", property);
		}
	}
}
