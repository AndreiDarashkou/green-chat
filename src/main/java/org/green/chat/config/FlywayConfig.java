package org.green.chat.config;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class FlywayConfig {

    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .outOfOrder(true)
                .locations("db/migration")
                .dataSource(dataSource)
                .placeholderReplacement(false)
                .load();
    }

    @Bean
    public FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        return new FlywayMigrationInitializer(flyway, null);
    }

    @Bean
    public DataSource dataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setTcpKeepAlive(true);
        dataSource.setUrl("jdbc:postgresql://localhost:5432/green_chat");
        dataSource.setPassword("");
        dataSource.setUser("postgres");
        dataSource.setDatabaseName("postgres");

        return dataSource;
    }

}
