/**
 * 
 */
package com.sam.jcc.cloud.vcs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sam.jcc.cloud.i.IEventManager;
import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.i.vcs.IVCSProvider;
import com.sam.jcc.cloud.provider.AbstractProvider;

/**
 * @author olegk
 *
 */
public abstract class VCSProvider extends AbstractProvider<IVCSMetadata> implements IVCSProvider {

	@Autowired
	public VCSProvider(List<IEventManager<IVCSMetadata>> eventManagers) {
		super(eventManagers);
	}

	@Override
	public IVCSMetadata read(IVCSMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVCSMetadata update(IVCSMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(IVCSMetadata t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<? super IVCSMetadata> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sam.jcc.cloud.i.IProvider#getI18NName()
	 */
	

	/* (non-Javadoc)
	 * @see com.sam.jcc.cloud.i.IProvider#supports(java.lang.Object)
	 */
	@Override
	public boolean supports(IVCSMetadata t) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.sam.jcc.cloud.i.IProvider#preprocess(java.lang.Object)
	 */
	@Override
	public IVCSMetadata preprocess(IVCSMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sam.jcc.cloud.i.IProvider#process(java.lang.Object)
	 */
	@Override
	public IVCSMetadata process(IVCSMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sam.jcc.cloud.i.IProvider#postprocess(java.lang.Object)
	 */
	@Override
	public IVCSMetadata postprocess(IVCSMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sam.jcc.cloud.i.IProvider#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

}
