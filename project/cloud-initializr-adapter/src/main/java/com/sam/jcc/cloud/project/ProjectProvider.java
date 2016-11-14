/**
 * 
 */
package com.sam.jcc.cloud.project;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.AbstractProvider;
import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.i.project.IProjectProvider;

/**
 * @author Alec Kotovich
 *
 */
@Component
public class ProjectProvider extends AbstractProvider<IProjectMetadata> implements IProjectProvider {

	@Autowired
	public ProjectProvider(List<IEventManager<IProjectMetadata>> eventManagers) {
		super(eventManagers);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sam.jcc.cloud.i.IProvider#getName()
	 */
	@Override
	public String getI18NName() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sam.jcc.cloud.i.IProvider#getDescription()
	 */
	@Override
	public String getI18NDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sam.jcc.cloud.i.IProvider#supports(java.lang.Object)
	 */
	@Override
	public boolean supports(IProjectMetadata t) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sam.jcc.cloud.i.IProvider#preprocess(java.lang.Object)
	 */
	@Override
	public IProjectMetadata preprocess(IProjectMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sam.jcc.cloud.i.IProvider#process(java.lang.Object)
	 */
	@Override
	public IProjectMetadata process(IProjectMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sam.jcc.cloud.i.IProvider#postprocess(java.lang.Object)
	 */
	@Override
	public IProjectMetadata postprocess(IProjectMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sam.jcc.cloud.i.IProvider#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IProjectMetadata read(IProjectMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProjectMetadata update(IProjectMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(IProjectMetadata t) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IProjectMetadata> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
