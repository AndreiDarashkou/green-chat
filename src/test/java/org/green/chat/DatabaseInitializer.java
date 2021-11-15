package org.green.chat;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class DatabaseInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";

    private static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:13")
                .withDatabaseName("green_chat")
                .withUsername(USERNAME)
                .withPassword(PASSWORD)
                .withCommand("postgres", "-c", "max_connections=50", "-c", "max_prepared_transactions=50")
                .withExposedPorts(5432);
        postgres.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues values = TestPropertyValues.of(
                "datasource.url=postgresql://" + postgres.getContainerIpAddress(),
                "datasource.port=" + postgres.getMappedPort(5432),
                "datasource.username=" + USERNAME,
                "datasource.password=" + PASSWORD
        );
        values.applyTo(applicationContext);
    }

}