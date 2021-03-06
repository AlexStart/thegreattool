package com.sam.jcc.cloud.dataprovider.impl;

import static com.mongodb.MongoCredential.createCredential;
import static com.sam.jcc.cloud.PropertyResolver.getProperty;
import static java.util.Collections.singletonList;

import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.sam.jcc.cloud.dataprovider.AppData;
import com.sam.jcc.cloud.i.data.IDBManager;

/**
 * @author Alexey Zhytnik
 * @since 23-Jan-17
 */
@Component
class MongoDatabaseManager implements IDBManager<AppData> {

    protected static final String COLLECTION_EXAMPLE = "example";

    private String user = getProperty("db.mongo.user");
    private String host = getProperty("db.mongo.host");
    private Integer port = Integer.valueOf(getProperty("db.mongo.port"));

    public void create(AppData app) {
        try (MongoClient mongo = getMongoClient(app)) {
            final MongoDatabase db = mongo.getDatabase(app.getAppName());
            create(db);
        }
    }

    public void drop(AppData app) {
        try (MongoClient mongo = getMongoClient(app)) {
            mongo.dropDatabase(app.getAppName());
        }
    }

    private void create(MongoDatabase database) {
        database.createCollection(COLLECTION_EXAMPLE);
    }

    @VisibleForTesting
    MongoClient getMongoClient(AppData app) {
        if (user.isEmpty()) return new MongoClient(host, port);

        final MongoCredential credential = createCredential(
                user,
                app.getAppName(),
                getProperty("db.mongo.password").toCharArray()
        );
        return new MongoClient(new ServerAddress(host, port), singletonList(credential));
    }
}
