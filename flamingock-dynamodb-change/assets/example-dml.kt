package com.example.migrations

import io.flamingock.api.annotations.Apply
import io.flamingock.api.annotations.Change
import io.flamingock.api.annotations.Rollback
import io.flamingock.api.annotations.TargetSystem
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
class _ORDER__BackfillOrderStatus {

    @Apply
    fun apply(client: DynamoDbClient, txBuilder: TransactWriteItemsEnhancedRequest.Builder) {
        val enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build()
        val table = enhancedClient.table("orders", TableSchema.fromBean(OrderRecord::class.java))
        txBuilder.addPutItem(table, OrderRecord("ORDER#1", "PENDING_REVIEW"))
    }

    @Rollback
    fun rollback(client: DynamoDbClient, txBuilder: TransactWriteItemsEnhancedRequest.Builder) {
        val enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build()
        val table = enhancedClient.table("orders", TableSchema.fromBean(OrderRecord::class.java))
        txBuilder.addDeleteItem(table, Key.builder().partitionValue("ORDER#1").build())
    }
}
