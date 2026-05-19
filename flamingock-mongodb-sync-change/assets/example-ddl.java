package com.example.migrations;

import com.mongodb.client.MongoDatabase;
import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;

import java.util.ArrayList;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-order-archive-collection", author = "TODO", transactional = false)
public class _ORDER__CreateOrderArchiveCollection {

    private static final String COLLECTION = "orderArchive";

    @Apply
    public void apply(MongoDatabase mongoDatabase) {
        if (!collectionExists(mongoDatabase, COLLECTION)) {
            mongoDatabase.createCollection(COLLECTION);
        }
    }

    @Rollback
    public void rollback(MongoDatabase mongoDatabase) {
        // Keep this pattern only for schema objects owned exclusively by this change.
        if (collectionExists(mongoDatabase, COLLECTION)) {
            mongoDatabase.getCollection(COLLECTION).drop();
        }
    }

    private boolean collectionExists(MongoDatabase mongoDatabase, String collectionName) {
        return mongoDatabase.listCollectionNames()
            .into(new ArrayList<>())
            .contains(collectionName);
    }
}
