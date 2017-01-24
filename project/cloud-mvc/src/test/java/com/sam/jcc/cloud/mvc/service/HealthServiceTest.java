/**
 * 
 */
package com.sam.jcc.cloud.mvc.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.sam.jcc.cloud.mvc.dto.HealthDTO;

/**
 * @author olegk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/WEB-INF/spring/spring-application-context.xml", "/WEB-INF/spring/spring-mvc-context.xml" })
@WebAppConfiguration 
public class HealthServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private HealthService healthService;

	@Test
	public void testCheckHealth() {
		List<? super HealthDTO> findAll = healthService.findAll();
		assertNotNull(findAll);
		assertEquals(2, findAll.size());
		HealthDTO st = (HealthDTO) findAll.get(0);
		assertEquals(1L, st.getId().longValue());
		assertNotNull(st.getUrl());
		HealthDTO nd = (HealthDTO) findAll.get(1);
		assertEquals(2L, nd.getId().longValue());
		assertNotNull(nd.getUrl());
	}
}