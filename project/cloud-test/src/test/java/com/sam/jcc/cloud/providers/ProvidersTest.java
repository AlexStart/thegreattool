/**
 * 
 */
package com.sam.jcc.cloud.providers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sam.jcc.cloud.i.IProvider;
import com.sam.jcc.cloud.i.project.IProjectProvider;

/**
 * @author olegk
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProvidersTest {

	@Autowired
	private List<IProvider<?>> providers;

	@Autowired
	private List<IProjectProvider> projectProviders;

	@Test
	public void listProvidersTest() {
		assertNotNull(providers);
		assertEquals(2, providers.size());
	}

	@Test
	public void listPProjectProvidersTest() {
		assertNotNull(projectProviders);
		assertEquals(2, projectProviders.size());
	}

}
