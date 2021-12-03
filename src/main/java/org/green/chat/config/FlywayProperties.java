package org.green.chat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "flyway")
public record FlywayProperties(String url, String username, String password, String database, boolean ssl,
                               String sslMode) {
}
