package com.example.migrations;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
public class _ORDER__BackfillOrderStatus {

    private static final String COLLECTION = "orders";
    private static final String CHANGE_MARKER = "backfill-order-status";

    @Apply
    public void apply(MongoDatabase mongoDatabase, ClientSession session) {
        mongoDatabase.getCollection(COLLECTION).updateMany(
            session,
            Filters.and(
                Filters.exists("status", false),
                Filters.ne("migrationMarker", CHANGE_MARKER)
            ),
            Updates.combine(
                Updates.set("status", "PENDING_REVIEW"),
                Updates.set("migrationMarker", CHANGE_MARKER)
            )
        );
    }

    @Rollback
    public void rollback(MongoDatabase mongoDatabase, ClientSession session) {
        mongoDatabase.getCollection(COLLECTION).updateMany(
            session,
            Filters.eq("migrationMarker", CHANGE_MARKER),
            Updates.combine(
                Updates.unset("status"),
                Updates.unset("migrationMarker")
            )
        );
    }
}
