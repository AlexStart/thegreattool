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

/**
 * @author olegk
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProvidersTest {

	@Autowired
	private List<IProvider<?>> providers;

	@Test
	public void listProvidersTest() {
		assertNotNull(providers);
		assertEquals(1, providers.size());
	}
}
