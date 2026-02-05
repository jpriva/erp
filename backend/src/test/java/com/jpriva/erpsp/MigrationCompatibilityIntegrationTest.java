package com.jpriva.erpsp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.hibernate.autoconfigure.HibernateProperties;
import org.springframework.boot.hibernate.autoconfigure.HibernateSettings;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Map;

import static org.assertj.core.api.Fail.fail;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none"
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class MigrationCompatibilityIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private HibernateProperties hibernateProperties;

    @Autowired
    private ApplicationContext context;

    @Test
    void schemaShouldBeCompatibleWithJpa() {
        LocalContainerEntityManagerFactoryBean validatorFactory = new LocalContainerEntityManagerFactoryBean();
        try {
            validatorFactory.setDataSource(dataSource);
            validatorFactory.setPackagesToScan("com.jpriva.erpsp");
            validatorFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

            Map<String, Object> props = hibernateProperties.determineHibernateProperties(
                    jpaProperties.getProperties(),
                    new HibernateSettings()
            );
            props.put("hibernate.hbm2ddl.auto", "validate");

            validatorFactory.setJpaPropertyMap(props);
            validatorFactory.setResourceLoader(context);

            validatorFactory.afterPropertiesSet();

            validatorFactory.destroy();
        } catch (Exception e) {
            Throwable rootCause = getRootCause(e);

            if (rootCause.getMessage() != null && rootCause.getMessage().contains("Schema validation:")) {
                System.err.println("\n ==================== COMPATIBILITY ERROR DETECTED:");
                System.err.println(rootCause.getMessage());

                fail("Schema validation failed: " + rootCause.getMessage());
            } else {
                throw e;
            }
        }
    }

    private Throwable getRootCause(Throwable t) {
        Throwable result = t;
        while (result.getCause() != null && result.getCause() != result) {
            result = result.getCause();
        }
        return result;
    }


}
