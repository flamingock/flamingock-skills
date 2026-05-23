package com.example.migrations;

import io.flamingock.api.annotations.Apply;
import io.flamingock.api.annotations.Change;
import io.flamingock.api.annotations.Rollback;
import io.flamingock.api.annotations.TargetSystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@TargetSystem(id = "YOUR_TARGET_SYSTEM_ID")
@Change(order = "TODO", id = "backfill-order-status", author = "TODO")
public class _ORDER__BackfillOrderStatus {

    @Apply
    public void apply(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
            "UPDATE orders SET status = ? WHERE status IS NULL"
        )) {
            statement.setString(1, "PENDING_REVIEW");
            statement.executeUpdate();
        }
    }

    @Rollback
    public void rollback(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
            "UPDATE orders SET status = NULL WHERE status = ?"
        )) {
            statement.setString(1, "PENDING_REVIEW");
            statement.executeUpdate();
        }
    }
}
