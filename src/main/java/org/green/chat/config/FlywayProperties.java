package org.green.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "flyway")
public class FlywayProperties {
    private String url;
    private String username;
    private String password;
    private String database;
}
