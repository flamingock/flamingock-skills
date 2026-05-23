package com.example.migrations

import io.flamingock.api.annotations.Apply
import io.flamingock.api.annotations.Change
import io.flamingock.api.annotations.Rollback
import io.flamingock.api.annotations.TargetSystem
import java.sql.Connection

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-order-archive-table", author = "TODO", transactional = false)
class _ORDER__CreateOrderArchiveTable {

    @Apply
    fun apply(connection: Connection) {
        connection.createStatement().use { statement ->
            statement.execute("CREATE TABLE IF NOT EXISTS order_archive (id BIGINT PRIMARY KEY, created_at TIMESTAMP)")
        }
    }

    @Rollback
    fun rollback(connection: Connection) {
        connection.createStatement().use { statement ->
            statement.execute("DROP TABLE IF EXISTS order_archive")
        }
    }
}
