package com.jpriva.erpsp.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Configuration
public class FlywayConfig {

    private final DataSource dataSource;

    public FlywayConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void migrationStrategy() {
        try {
            migrateModule("auth", "db/migration/auth");
        } catch (SQLException e) {
            log.error("Failed to migrate module", e);
        }
    }

    private void migrateModule(String schemaName, String location) throws SQLException {
        log.debug("Starting Flyway migration for schema: [{}] from location: [{}]", schemaName, location);
        Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .createSchemas(true)
                .table("flyway_schema_history")
                .locations("classpath:" + location)
                .baselineOnMigrate(true)
                .load()
                .migrate();
        log.debug("Migration for schema [{}] completed successfully.", schemaName);
    }
}
