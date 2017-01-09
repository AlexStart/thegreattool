/**
 * 
 */
package com.sam.jcc.cloud.providers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sam.jcc.cloud.crud.ICRUD;
import com.sam.jcc.cloud.i.IProvider;
import com.sam.jcc.cloud.i.data.IDataProvider;
import com.sam.jcc.cloud.i.data.INoSqlDataProvider;
import com.sam.jcc.cloud.i.data.ISqlDataProvider;
import com.sam.jcc.cloud.i.project.IProjectProvider;
import com.sam.jcc.cloud.i.vcs.IVCSProvider;
import com.sam.jcc.cloud.rules.model.Operation;
import com.sam.jcc.cloud.rules.model.Rule;
import com.thoughtworks.xstream.XStream;

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

	@Autowired
	private List<IDataProvider> dataProviders;

	@Autowired
	private List<ISqlDataProvider> sqlDataProviders;

	@Autowired
	private List<INoSqlDataProvider> noSqlDataProviders;

	@Test
	public void listProvidersTest() {
		assertNotNull(providers);
		assertEquals(7, providers.size());
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

	@Test
	public void listDataProvidersTest() {
		assertNotNull(dataProviders);
		assertEquals(2, dataProviders.size());
		dataProviders.stream().map(p -> p.getI18NName()).forEach(System.out::println);
		// sql
		assertNotNull(sqlDataProviders);
		assertEquals(1, sqlDataProviders.size());
		// nosql
		assertNotNull(noSqlDataProviders);
		assertEquals(1, noSqlDataProviders.size());
	}

	@Test
	public void walkThroughProviders() throws Exception {
		Rule rule = new Rule();
		for (int i = 0; i < providers.size(); i++) {

			ICRUD<?> crud = (ICRUD<?>) providers.get(i);

			Rule subRuleCr = new Rule();
			Operation operationCr = new Operation("create", crud.getClass());
			subRuleCr.setOperation(operationCr);
			rule.getRules().add(subRuleCr);

			for (int j = 0; j < providers.size(); j++) {
				ICRUD<?> crud2 = (ICRUD<?>) providers.get(j);
				helperMethod(crud2, subRuleCr.getRules());
			}

			Rule subRuleRe = new Rule();
			Operation operationRe = new Operation("read", crud.getClass());
			subRuleRe.setOperation(operationRe);
			rule.getRules().add(subRuleRe);

			for (int j = 0; j < providers.size(); j++) {
				ICRUD<?> crud2 = (ICRUD<?>) providers.get(j);
				helperMethod(crud2, subRuleRe.getRules());
			}

			Rule subRuleUp = new Rule();
			Operation operationUp = new Operation("update", crud.getClass());
			subRuleUp.setOperation(operationUp);
			rule.getRules().add(subRuleUp);

			for (int j = 0; j < providers.size(); j++) {
				ICRUD<?> crud2 = (ICRUD<?>) providers.get(j);
				helperMethod(crud2, subRuleUp.getRules());
			}

			Rule subRuleDe = new Rule();
			Operation operationDe = new Operation("delete", crud.getClass());
			subRuleDe.setOperation(operationDe);
			rule.getRules().add(subRuleDe);

			for (int j = 0; j < providers.size(); j++) {
				ICRUD<?> crud2 = (ICRUD<?>) providers.get(j);
				helperMethod(crud2, subRuleDe.getRules());
			}
		}
		File tmpFile = File.createTempFile("rules", ".xml");
		(new XStream()).toXML(rule, new FileOutputStream(tmpFile));
		//
		Rule fromXML = (Rule) (new XStream()).fromXML(tmpFile);
		assertNotNull(fromXML);
		assertNull(fromXML.getOperation());
		assertNotNull(fromXML.getRules());
		assertEquals(4 * providers.size(), fromXML.getRules().size());
		for (Rule fromXMLRule : fromXML.getRules()) {
			assertNotNull(fromXMLRule.getRules());
			assertEquals(4 * providers.size(), fromXMLRule.getRules().size());
		}

	}

	private void helperMethod(ICRUD<?> crud, List<Rule> rules) {

		Rule subRuleCr = new Rule();
		Operation operationCr = new Operation("create", crud.getClass());
		subRuleCr.setOperation(operationCr);
		rules.add(subRuleCr);

		Rule subRuleRe = new Rule();
		Operation operationRe = new Operation("read", crud.getClass());
		subRuleRe.setOperation(operationRe);
		rules.add(subRuleRe);

		Rule subRuleUp = new Rule();
		Operation operationUp = new Operation("update", crud.getClass());
		subRuleUp.setOperation(operationUp);
		rules.add(subRuleUp);

		Rule subRuleDe = new Rule();
		Operation operationDe = new Operation("delete", crud.getClass());
		subRuleDe.setOperation(operationDe);
		rules.add(subRuleDe);
	}
}
