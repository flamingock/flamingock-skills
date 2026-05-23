package com.example.migrations;

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
public class _ORDER__BackfillOrderStatus {

    private static final String COLLECTION = "orders";
    private static final String CHANGE_MARKER = "backfill-order-status";

    @Apply
    public void apply(MongoTemplate mongoTemplate) {
        mongoTemplate.updateMulti(
            Query.query(new Criteria().andOperator(
                Criteria.where("status").exists(false),
                Criteria.where("migrationMarker").ne(CHANGE_MARKER)
            )),
            new Update()
                .set("status", "PENDING_REVIEW")
                .set("migrationMarker", CHANGE_MARKER),
            COLLECTION
        );
    }

    @Rollback
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.updateMulti(
            Query.query(Criteria.where("migrationMarker").is(CHANGE_MARKER)),
            new Update()
                .unset("status")
                .unset("migrationMarker"),
            COLLECTION
        );
    }
}
