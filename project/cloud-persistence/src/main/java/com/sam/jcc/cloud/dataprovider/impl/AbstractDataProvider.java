package com.sam.jcc.cloud.dataprovider.impl;

import static com.sam.jcc.cloud.dataprovider.AppDataStatus.UPDATED;
import static java.util.Objects.isNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.dataprovider.AppDataStatus;
import com.sam.jcc.cloud.dataprovider.ProjectSourcesNotFound;
import com.sam.jcc.cloud.exception.InternalCloudException;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.data.IDataMetadata;
import com.sam.jcc.cloud.persistence.data.ProjectData;
import com.sam.jcc.cloud.persistence.data.ProjectDataNotFoundException;
import com.sam.jcc.cloud.persistence.data.ProjectDataRepository;
import com.sam.jcc.cloud.provider.AbstractProvider;
import com.sam.jcc.cloud.provider.UnsupportedCallException;

/**
 * @author Alexey Zhytnik
 * @since 23-Jan-17
 */
abstract class AbstractDataProvider extends AbstractProvider<IDataMetadata> {

	@Autowired
	protected ProjectDataRepository repository;

	AbstractDataProvider(List<IEventManager<IDataMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	public boolean supports(IDataMetadata d) {
		return d instanceof AppData;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public IDataMetadata update(IDataMetadata d) {
		super.create(d);
		updateStatus(d, UPDATED);
		return d;
	}

	@Override
	public IDataMetadata preprocess(IDataMetadata d) {
		final AppData app = asAppData(d);

		final ProjectData data = getOrThrow(app);

		if (data.getDataSupport())
			throw new InternalCloudException();
		if (isNull(data.getSources()))
			throw new ProjectSourcesNotFound(app);

		app.setSources(data.getSources());
		return app;
	}

	@Override
	public IDataMetadata postprocess(IDataMetadata d) {
		final AppData app = asAppData(d);

		final ProjectData data = getOrThrow(app);
		data.setDb(getType());
		data.setDataSupport(true);
		data.setSources(app.getSources());

		repository.save(data);
		return app;
	}

	@Override
	public IDataMetadata read(IDataMetadata d) {
		throw new UnsupportedCallException();
	}

	@Override
	public IDataMetadata create(IDataMetadata d) {
		throw new UnsupportedCallException();
	}

	@Override
	public void delete(IDataMetadata d) {
		throw new UnsupportedCallException();
	}

	@Override
	public List<? super IDataMetadata> findAll() {
		throw new UnsupportedCallException();
	}

	protected ProjectData getOrThrow(AppData app) {
		final String name = app.getAppName();
		return repository.findByName(name).orElseThrow(() -> new ProjectDataNotFoundException(name));
	}

	protected void updateStatus(IDataMetadata data, AppDataStatus status) {
		asAppData(data).setStatus(status);
		notify(data);
	}

	protected AppData asAppData(IDataMetadata data) {
		return (AppData) data;
	}

	public abstract String getType();
}
