package com.example.migrations;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
public class _ORDER__BackfillOrderStatus {

    @Apply
    public void apply(Cluster cluster, Bucket bucket, TransactionAttemptContext txContext) {
        txContext.insert(bucket.defaultCollection(), "order::1", JsonObject.create().put("status", "PENDING_REVIEW"));
    }

    @Rollback
    public void rollback(Cluster cluster, Bucket bucket, TransactionAttemptContext txContext) {
        txContext.remove(bucket.defaultCollection().get("order::1"));
    }
}
