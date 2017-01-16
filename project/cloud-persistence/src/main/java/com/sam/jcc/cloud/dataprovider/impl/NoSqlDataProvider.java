package com.sam.jcc.cloud.dataprovider.impl;

import java.util.List;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.i.data.INoSqlDataProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;

/**
 * @author Alec Kotovich
 */
public abstract class NoSqlDataProvider extends AbstractProvider<IDataMetadata>implements INoSqlDataProvider {

	public NoSqlDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	public boolean supports(IDataMetadata t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IDataMetadata preprocess(IDataMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataMetadata process(IDataMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataMetadata postprocess(IDataMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IDataMetadata read(IDataMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataMetadata update(IDataMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(IDataMetadata t) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<? super IDataMetadata> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
