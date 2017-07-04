/**
 *
 */
package com.sam.jcc.cloud.rules.service.impl.provider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.jcc.cloud.i.cd.ICDMetadata;
import com.sam.jcc.cloud.i.ci.ICIProvider;
import com.sam.jcc.cloud.provider.UnsupportedCallException;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *         <p>
 *         TODO
 */
@Service
public class CDProviderService implements IService<ICDMetadata> {

	// CD based on CI - so it's OK
	@Autowired
	private List<ICIProvider> ciProviders;

	
	@Override
	public ICDMetadata create(ICDMetadata t) {
		throw new UnsupportedCallException();
	}

	@Override
	public ICDMetadata read(ICDMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICDMetadata update(ICDMetadata t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(ICDMetadata t) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<? super ICDMetadata> findAll() {
		List<? super ICDMetadata> cds = new ArrayList<>();
		ciProviders.forEach(ciProvider -> cds.add(new ICDMetadata() {

			@Override
			public String getName() {
				return ciProvider.getI18NName();
			}

			@Override
			public String getDescription() {
				return ciProvider.getI18NDescription();
			}

			@Override
			public String getType() {
				// TODO Auto-generated method stub
				return null;
			}

		}));
		return cds;
	}

	@Override
	public ICDMetadata create(Map<?, ?> props) {
		throw new UnsupportedCallException();
	}

	@Override
	public void delete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public ICDMetadata update(Map<?, ?> props) {
		throw new UnsupportedCallException();
	}

	@Override
	public void findAndDelete(Map<String, String> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<Long, String> getNames() {
		ciProviders.sort((p1, p2) -> {
			if (p1 != null && p2 != null && p1.getId() != null && p2.getId() != null) {
				return p1.getId().compareTo(p2.getId());
			}
			return 0;
		});

		Map<Long, String> names = new LinkedHashMap<>();
		ciProviders.forEach(p -> names.put(p.getId(), p.getI18NName()));

		return names;
	}

}
