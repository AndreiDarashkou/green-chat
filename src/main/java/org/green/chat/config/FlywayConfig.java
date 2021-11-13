package org.green.chat.config;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
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

//    @Bean
//    public DataSource dataSource(FlywayProperties properties) {
//        PGSimpleDataSource dataSource = new PGSimpleDataSource();
//        dataSource.setTcpKeepAlive(true);
//        dataSource.setUrl(properties.getUrl());
//        dataSource.setUser(properties.getUsername());
//        dataSource.setPassword(properties.getPassword());
//        dataSource.setDatabaseName(properties.getDatabase());
//
//        return dataSource;
//    }

    @Bean
    @Primary
    public DataSource inMemoryDS() throws Exception {
        DataSource embeddedPostgresDS = EmbeddedPostgres.builder()
                .setPort(5432)
                .start().getPostgresDatabase();

        return embeddedPostgresDS;
    }

}
