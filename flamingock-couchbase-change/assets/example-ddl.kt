package com.example.migrations

import com.couchbase.client.core.error.CollectionExistsException
import com.couchbase.client.java.Bucket
import com.couchbase.client.java.Cluster
import com.couchbase.client.java.manager.collection.CollectionSpec
import io.flamingock.api.annotations.Apply
import io.flamingock.api.annotations.Change
import io.flamingock.api.annotations.Rollback
import io.flamingock.api.annotations.TargetSystem

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-order-archive-collection", author = "TODO", transactional = false)
class _ORDER__CreateOrderArchiveCollection {

    @Apply
    fun apply(cluster: Cluster, bucket: Bucket) {
        try {
            bucket.collections().createCollection(CollectionSpec.create("orderArchive", "_default"))
        } catch (_: CollectionExistsException) {
            // already exists
        }
    }

    @Rollback
    fun rollback(cluster: Cluster, bucket: Bucket) {
        bucket.collections().dropCollection(CollectionSpec.create("orderArchive", "_default"))
    }
}
