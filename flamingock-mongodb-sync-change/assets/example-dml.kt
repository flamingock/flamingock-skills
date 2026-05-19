package com.example.migrations

import com.mongodb.client.ClientSession
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import io.flamingock.api.annotations.Apply
import io.flamingock.api.annotations.Change
import io.flamingock.api.annotations.Rollback
import io.flamingock.api.annotations.TargetSystem

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
class _ORDER__BackfillOrderStatus {

    @Apply
    fun apply(mongoDatabase: MongoDatabase, session: ClientSession) {
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
        )
    }

    @Rollback
    fun rollback(mongoDatabase: MongoDatabase, session: ClientSession) {
        mongoDatabase.getCollection(COLLECTION).updateMany(
            session,
            Filters.eq("migrationMarker", CHANGE_MARKER),
            Updates.combine(
                Updates.unset("status"),
                Updates.unset("migrationMarker")
            )
        )
    }

    private companion object {
        const val COLLECTION = "orders"
        const val CHANGE_MARKER = "backfill-order-status"
    }
}
