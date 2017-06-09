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
@ContextConfiguration(locations = { "/WEB-INF/spring/spring-application-context.xml",
		"/WEB-INF/spring/spring-mvc-context.xml" })
@WebAppConfiguration
public class HealthServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private HealthService healthService;

	@Test
	public void testCheckHealth() {
		List<? super HealthDTO> findAll = healthService.findAll();
		assertNotNull(findAll);
		assertEquals(8, findAll.size());
		//
		HealthDTO st = (HealthDTO) findAll.get(0);
		assertEquals(1L, st.getId().longValue());
		assertNotNull(st.getUrl());
		//
		HealthDTO nd = (HealthDTO) findAll.get(1);
		assertEquals(2L, nd.getId().longValue());
		assertNotNull(nd.getUrl());
		//
		HealthDTO rd = (HealthDTO) findAll.get(2);
		assertEquals(3L, rd.getId().longValue());
		assertNotNull(rd.getUrl());
		//
		HealthDTO fouth = (HealthDTO) findAll.get(3);
		assertEquals(4L, fouth.getId().longValue());		
		assertNotNull(fouth.getUrl());
		//
		HealthDTO fifth = (HealthDTO) findAll.get(4);
		assertEquals(5L, fifth.getId().longValue());
		assertNotNull(fifth.getUrl());
		//
		HealthDTO sixth = (HealthDTO) findAll.get(5);
		assertEquals(6L, sixth.getId().longValue());
		assertNotNull(sixth.getUrl());
		//
		HealthDTO seventh = (HealthDTO) findAll.get(6);
		assertEquals(7L, seventh.getId().longValue());
		assertNotNull(seventh.getUrl());		
		//
		HealthDTO achte = (HealthDTO) findAll.get(7);
		assertEquals(8L, achte.getId().longValue());
		assertNotNull(achte.getUrl());		
		
	}
}
