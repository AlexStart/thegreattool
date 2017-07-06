package com.sam.jcc.cloud.dataprovider.impl;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.dataprovider.impl.MongoDatabaseManager.COLLECTION_EXAMPLE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.sam.jcc.cloud.dataprovider.AppData;

/**
 * @author Alexey Zhytnik
 * @since 25.01.2017
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MongoDatabaseManager.class, TableNameValidator.class })
public class MongoDatabaseManagerTest {

	MongoClient mongoClient;
	@Autowired
	MongoDatabaseManager mongoManager;

	AppData app;
	private static String bckp;

	@BeforeClass
	public static void setUpClass() {
		bckp = getProperty("db.mongo.port");
		System.setProperty("db.mongo.port", "37017");
	}

	@AfterClass
	public static void tearDownClass() {
		System.setProperty("db.mongo.port", bckp);
	}

	@Before
	public void setUp() {
		app = app();
		mongoClient = mongoManager.getMongoClient(app);
	}

	@Test
	public void creates() {
		mongoManager.create(app);

		final MongoDatabase db = mongoClient.getDatabase(app.getAppName());
		assertThat(db.getName()).isNotEmpty().contains(COLLECTION_EXAMPLE);
	}

	@After
	public void reset() {
		mongoManager.drop(app);
		mongoClient.close();
	}

	AppData app() {
		final AppData app = new AppData();
		app.setAppName("test-example" + new Random().nextInt());
		return app;
	}
}