package com.example.migrations

import com.mongodb.client.MongoDatabase
import io.flamingock.api.annotations.Apply
import io.flamingock.api.annotations.Change
import io.flamingock.api.annotations.Rollback
import io.flamingock.api.annotations.TargetSystem

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-order-archive-collection", author = "TODO", transactional = false)
class _ORDER__CreateOrderArchiveCollection {

    @Apply
    fun apply(mongoDatabase: MongoDatabase) {
        if (!collectionExists(mongoDatabase, COLLECTION)) {
            mongoDatabase.createCollection(COLLECTION)
        }
    }

    @Rollback
    fun rollback(mongoDatabase: MongoDatabase) {
        // Keep this pattern only for schema objects owned exclusively by this change.
        if (collectionExists(mongoDatabase, COLLECTION)) {
            mongoDatabase.getCollection(COLLECTION).drop()
        }
    }

    private fun collectionExists(mongoDatabase: MongoDatabase, collectionName: String): Boolean {
        return mongoDatabase.listCollectionNames().toList().contains(collectionName)
    }

    private companion object {
        const val COLLECTION = "orderArchive"
    }
}
