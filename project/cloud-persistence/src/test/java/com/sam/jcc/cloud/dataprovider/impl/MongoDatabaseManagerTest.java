package com.sam.jcc.cloud.dataprovider.impl;

import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static com.sam.jcc.cloud.dataprovider.impl.MongoDatabaseManager.COLLECTION_EXAMPLE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.sam.jcc.cloud.dataprovider.AppData;

/**
 * @author Alexey Zhytnik
 * @since 25.01.2017
 */
public class MongoDatabaseManagerTest {

	MongoClient mongoClient;
	MongoDatabaseManager mongoManager;

	AppData app;

	@Before
	public void setUp() {
		app = app();

		// TODO remove later. For embedded mongo integration test only!
		String bckp = getProperty("db.mongo.port");
		System.setProperty("db.mongo.port", "37017");

		mongoManager = new MongoDatabaseManager();
		mongoClient = mongoManager.getMongoClient(app);

		// TODO remove later.
		System.setProperty("db.mongo.port", bckp);
	}

	@Test
	public void creates() {
		mongoManager.create(app);

		final MongoDatabase db = mongoClient.getDatabase(app.getAppName());
		assertThat(db.listCollectionNames()).isNotEmpty().contains(COLLECTION_EXAMPLE);
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