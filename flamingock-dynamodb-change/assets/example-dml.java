package com.example.migrations;

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
public class _ORDER__BackfillOrderStatus {

    @Apply
    public void apply(DynamoDbClient client, TransactWriteItemsEnhancedRequest.Builder txBuilder) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(client)
            .build();
        DynamoDbTable<OrderRecord> table = enhancedClient.table("orders", TableSchema.fromBean(OrderRecord.class));
        txBuilder.addPutItem(table, new OrderRecord("ORDER#1", "PENDING_REVIEW"));
    }

    @Rollback
    public void rollback(DynamoDbClient client, TransactWriteItemsEnhancedRequest.Builder txBuilder) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(client)
            .build();
        DynamoDbTable<OrderRecord> table = enhancedClient.table("orders", TableSchema.fromBean(OrderRecord.class));
        txBuilder.addDeleteItem(table, Key.builder().partitionValue("ORDER#1").build());
    }
}
