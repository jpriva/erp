package com.jpriva.erpsp;

import com.jpriva.erpsp.config.FlywayConfig;
import org.springframework.boot.jpa.autoconfigure.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;

@Import({AbstractIntegrationTest.MocksConfig.class})
public class AbstractIntegrationTest {
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:latest");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @TestConfiguration
    static class MocksConfig {
        @Bean
        @Primary
        JavaMailSender javaMailSender() {
            return new JavaMailSenderImpl();
        }

        @Bean("flywayConfig")
        public FlywayConfig flywayConfig(DataSource dataSource) {
            return new FlywayConfig(dataSource);
        }

        @Bean
        public EntityManagerFactoryDependsOnPostProcessor forceFlywayBeforeHibernate() {
            return new EntityManagerFactoryDependsOnPostProcessor("flywayConfig");
        }
    }
}
