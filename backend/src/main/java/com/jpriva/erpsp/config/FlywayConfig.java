package com.jpriva.erpsp.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FlywayConfig implements InitializingBean {
    private final DataSource dataSource;

    public FlywayConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        migrateModule("auth", "db/migration/auth");
    }

    private void migrateModule(String schemaName, String location) {
        Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .table("flyway_schema_history")
                .locations("classpath:" + location)
                .baselineOnMigrate(true)
                .createSchemas(true)
                .load()
                .migrate();
    }
}
