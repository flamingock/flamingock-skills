package com.example.migrations;

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-order-archive-collection", author = "TODO", transactional = false)
public class _ORDER__CreateOrderArchiveCollection {

    private static final String COLLECTION = "orderArchive";

    @Apply
    public void apply(MongoTemplate mongoTemplate) {
        if (!mongoTemplate.collectionExists(COLLECTION)) {
            mongoTemplate.createCollection(COLLECTION);
        }
        mongoTemplate.indexOps(COLLECTION).ensureIndex(new Index().on("createdAt", Sort.Direction.DESC));
    }

    @Rollback
    public void rollback(MongoTemplate mongoTemplate) {
        if (mongoTemplate.collectionExists(COLLECTION)) {
            mongoTemplate.dropCollection(COLLECTION);
        }
    }
}
