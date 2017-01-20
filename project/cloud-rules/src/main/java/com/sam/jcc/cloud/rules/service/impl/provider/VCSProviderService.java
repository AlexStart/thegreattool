/**
 * 
 */
package com.sam.jcc.cloud.rules.service.impl.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.vcs.IVCSMetadata;
import com.sam.jcc.cloud.provider.UnsupportedCallException;
import com.sam.jcc.cloud.rules.service.IService;
import com.sam.jcc.cloud.vcs.VCSProvider;
import com.sam.jcc.cloud.vcs.VCSRepositoryMetadata;

/**
 * @author olegk
 * 
 *         TODO
 *
 */
@Service
public class VCSProviderService implements IService<IVCSMetadata> {

	@Autowired
	private List<VCSProvider> vcsProviders;

	@Override
	public IVCSMetadata create(IVCSMetadata t) {
		throw new UnsupportedCallException();
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
		List<? super IVCSMetadata> vcss = new ArrayList<>();
		vcsProviders.forEach(vcsProvider -> vcss
				.add(new VCSRepositoryMetadata(vcsProvider.getI18NName(), vcsProvider.getI18NDescription())));
		return vcss;
	}

	@Override
	public IVCSMetadata create(Map<String, String> props) {
		throw new UnsupportedCallException();
	}

	@Override
	public void delete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public IVCSMetadata update(Map<String, String> props) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void findAndDelete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<Long, String> getNames() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
