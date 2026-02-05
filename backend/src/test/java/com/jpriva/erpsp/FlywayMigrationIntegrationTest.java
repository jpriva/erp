package com.jpriva.erpsp;

import com.jpriva.erpsp.config.FlywayConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FlywayMigrationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldExecuteMigrationsInAuthSchema() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            List<Executable> assertions = new ArrayList<>();

            FlywayConfig.MODULES.keySet().forEach(schemaName -> {

                assertions.add(() -> {
                    try (ResultSet schemas = metaData.getSchemas(null, schemaName)) {
                        assertTrue(schemas.next(),
                                String.format("Schema '%s' must exist in BD", schemaName));
                    }
                });

                assertions.add(() -> {
                    try (ResultSet tables = metaData.getTables(null, schemaName, "flyway_schema_history", null)) {
                        assertTrue(tables.next(),
                                String.format("Schema '%s' most have a table named 'flyway_schema_history'", schemaName));
                    }
                });
            });

            assertAll("Verifying Flyway migrations", assertions);
        }
    }
}