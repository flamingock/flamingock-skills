package com.example.migrations

import io.flamingock.api.annotations.Apply
import io.flamingock.api.annotations.Change
import io.flamingock.api.annotations.Rollback
import io.flamingock.api.annotations.TargetSystem
import java.sql.Connection

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
class _ORDER__BackfillOrderStatus {

    @Apply
    fun apply(connection: Connection) {
        connection.prepareStatement("UPDATE orders SET status = ? WHERE status IS NULL").use { statement ->
            statement.setString(1, "PENDING_REVIEW")
            statement.executeUpdate()
        }
    }

    @Rollback
    fun rollback(connection: Connection) {
        connection.prepareStatement("UPDATE orders SET status = NULL WHERE status = ?").use { statement ->
            statement.setString(1, "PENDING_REVIEW")
            statement.executeUpdate()
        }
    }
}
