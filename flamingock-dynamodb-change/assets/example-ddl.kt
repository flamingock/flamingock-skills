package com.example.migrations

import io.flamingock.api.annotations.Apply
import io.flamingock.api.annotations.Change
import io.flamingock.api.annotations.Rollback
import io.flamingock.api.annotations.TargetSystem
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-order-archive-table", author = "TODO", transactional = false)
class _ORDER__CreateOrderArchiveTable {

    @Apply
    fun apply(client: DynamoDbClient) {
        try {
            client.createTable(CreateTableRequest.builder().tableName("order_archive").build())
        } catch (_: ResourceInUseException) {
            // already exists
        }
    }

    @Rollback
    fun rollback(client: DynamoDbClient) {
        try {
            client.deleteTable(DeleteTableRequest.builder().tableName("order_archive").build())
        } catch (_: ResourceNotFoundException) {
            // already removed
        }
    }
}
