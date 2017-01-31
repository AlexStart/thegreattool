package com.sam.jcc.cloud.dataprovider.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.sam.jcc.cloud.dataprovider.AppData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static com.sam.jcc.cloud.dataprovider.impl.MongoDatabaseManager.COLLECTION_EXAMPLE;
import static org.assertj.core.api.Assertions.assertThat;

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
        mongoManager = new MongoDatabaseManager();
        mongoClient = mongoManager.getMongoClient(app);
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