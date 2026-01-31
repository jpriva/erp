package com.jpriva.erpsp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class FlywayMigrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldExecuteMigrationsInAuthSchema() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            var schemas = connection.getMetaData().getSchemas(null, "auth");
            assertTrue(schemas.next(), "Schema 'auth' must exist");

            var tables = connection.getMetaData().getTables(null, "auth", "flyway_schema_history", null);
            assertTrue(tables.next(), "The 'flyway_schema_history' table should have been created by Flyway");
        }
    }
}