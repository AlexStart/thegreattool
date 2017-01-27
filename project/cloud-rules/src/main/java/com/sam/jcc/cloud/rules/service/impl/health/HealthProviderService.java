/**
 * 
 */
package com.sam.jcc.cloud.rules.service.impl.health;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.IHealth;
import com.sam.jcc.cloud.i.IHealthMetadata;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@Service
public class HealthProviderService implements IService<IHealthMetadata> {

	@Autowired
	private List<IHealth> providersWithHealth;

	@Override
	public IHealthMetadata create(IHealthMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHealthMetadata read(IHealthMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHealthMetadata update(IHealthMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(IHealthMetadata t) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<? super IHealthMetadata> findAll() {
		List<? super IHealthMetadata> healths = new ArrayList<>();
		for (IHealth health : providersWithHealth) {
			healths.add(health.checkHealth());
		}
		return healths;
	}

	@Override
	public void findAndDelete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public IHealthMetadata create(Map<?, ?> props) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public IHealthMetadata update(Map<?, ?> props) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, String> getNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
