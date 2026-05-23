package com.example.migrations;

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-order-archive-table", author = "TODO", transactional = false)
public class _ORDER__CreateOrderArchiveTable {

    @Apply
    public void apply(DynamoDbClient client) {
        try {
            client.createTable(CreateTableRequest.builder().tableName("order_archive").build());
        } catch (ResourceInUseException ignored) {
            // already exists
        }
    }

    @Rollback
    public void rollback(DynamoDbClient client) {
        try {
            client.deleteTable(DeleteTableRequest.builder().tableName("order_archive").build());
        } catch (ResourceNotFoundException ignored) {
            // already removed
        }
    }
}
