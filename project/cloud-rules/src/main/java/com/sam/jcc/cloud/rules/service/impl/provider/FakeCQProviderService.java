/**
 * 
 */
package com.sam.jcc.cloud.rules.service.impl.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sam.jcc.cloud.i.cq.ICQMetadata;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 *         TODO remove after REAL provider service is added
 */
@Component
public class FakeCQProviderService implements IService<ICQMetadata> {

	@Override
	public ICQMetadata create(ICQMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICQMetadata read(ICQMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICQMetadata update(ICQMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(ICQMetadata t) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<? super ICQMetadata> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void findAndDelete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public ICQMetadata create(Map<?, ?> props) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public ICQMetadata update(Map<?, ?> props) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, String> getNames() {
		// TODO Auto-generated method stub
		return new HashMap<Long, String>();
	}

}
