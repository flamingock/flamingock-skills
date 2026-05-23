package com.example.migrations

import io.flamingock.api.annotations.Apply
import io.flamingock.api.annotations.Change
import io.flamingock.api.annotations.Rollback
import io.flamingock.api.annotations.TargetSystem
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-order-archive-collection", author = "TODO", transactional = false)
class _ORDER__CreateOrderArchiveCollection {

    @Apply
    fun apply(mongoTemplate: MongoTemplate) {
        if (!mongoTemplate.collectionExists(COLLECTION)) {
            mongoTemplate.createCollection(COLLECTION)
        }
        mongoTemplate.indexOps(COLLECTION).ensureIndex(Index().on("createdAt", Sort.Direction.DESC))
    }

    @Rollback
    fun rollback(mongoTemplate: MongoTemplate) {
        if (mongoTemplate.collectionExists(COLLECTION)) {
            mongoTemplate.dropCollection(COLLECTION)
        }
    }

    private companion object {
        const val COLLECTION = "orderArchive"
    }
}
