package com.example.migrations;

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "create-order-archive-table", author = "TODO", transactional = false)
public class _ORDER__CreateOrderArchiveTable {

    @Apply
    public void apply(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS order_archive (id BIGINT PRIMARY KEY, created_at TIMESTAMP)");
        }
    }

    @Rollback
    public void rollback(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS order_archive");
        }
    }
}
