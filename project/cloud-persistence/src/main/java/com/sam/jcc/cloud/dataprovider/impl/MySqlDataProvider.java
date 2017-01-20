/**
 * 
 */
package com.sam.jcc.cloud.dataprovider.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.data.IDataMetadata;

/**
 * @author olegk
 *
 */
@Component
public class MySqlDataProvider extends SqlDataProvider {

	private static final long MYSQL_PROVIDER_ID = 6L;

	public MySqlDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	public Long getId() {
		return MYSQL_PROVIDER_ID;
	}
}
