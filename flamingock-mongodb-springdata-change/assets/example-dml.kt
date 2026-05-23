package com.example.migrations

import io.flamingock.api.annotations.Apply
import io.flamingock.api.annotations.Change
import io.flamingock.api.annotations.Rollback
import io.flamingock.api.annotations.TargetSystem
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
class _ORDER__BackfillOrderStatus {

    @Apply
    fun apply(mongoTemplate: MongoTemplate) {
        mongoTemplate.updateMulti(
            Query.query(
                Criteria().andOperator(
                    Criteria.where("status").exists(false),
                    Criteria.where("migrationMarker").ne(CHANGE_MARKER)
                )
            ),
            Update().set("status", "PENDING_REVIEW").set("migrationMarker", CHANGE_MARKER),
            COLLECTION
        )
    }

    @Rollback
    fun rollback(mongoTemplate: MongoTemplate) {
        mongoTemplate.updateMulti(
            Query.query(Criteria.where("migrationMarker").`is`(CHANGE_MARKER)),
            Update().unset("status").unset("migrationMarker"),
            COLLECTION
        )
    }

    private companion object {
        const val COLLECTION = "orders"
        const val CHANGE_MARKER = "backfill-order-status"
    }
}
