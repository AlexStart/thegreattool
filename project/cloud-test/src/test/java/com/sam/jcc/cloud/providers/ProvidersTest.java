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
import com.sam.jcc.cloud.i.vcs.IVCSProvider;

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

	@Autowired
	private List<IVCSProvider> vcsProviders;

	@Test
	public void listProvidersTest() {
		assertNotNull(providers);
		assertEquals(4, providers.size());
	}

	@Test
	public void listProjectProvidersTest() {
		assertNotNull(projectProviders);
		assertEquals(2, projectProviders.size());
		projectProviders.stream().map(p -> p.getI18NName()).forEach(System.out::println);
	}

	@Test
	public void listVcsProvidersTest() {
		assertNotNull(vcsProviders);
		assertEquals(2, vcsProviders.size());
		vcsProviders.stream().map(p -> p.getI18NName()).forEach(System.out::println);
	}

}
