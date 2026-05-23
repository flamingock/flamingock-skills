package com.example.migrations;

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Recovery;
import io.flamingock.api.annotations.RecoveryStrategy;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import software.amazon.awssdk.services.s3.S3Client;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Recovery(strategy = RecoveryStrategy.ALWAYS_RETRY) // keep only when the operation is truly idempotent
@Change(order = "TODO", id = "ensure-order-bucket-tag", author = "TODO", transactional = false)
public class _ORDER__EnsureOrderBucketTag {

    @Apply
    public void apply(S3Client s3Client) {
        // read current state, apply only the Flamingock-owned change, and keep the operation safe to re-run
    }

    @Rollback
    public void rollback(S3Client s3Client) {
        // compensate only the tag or setting introduced by apply; never delete unrelated resources
    }
}
