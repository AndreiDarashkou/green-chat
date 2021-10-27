package org.green.chat.config;

import io.rsocket.core.Resume;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class RSocketConfig {

    @Bean
    public RSocketServerCustomizer rSocketServerCustomizer() {
        return c -> c.resume(resumeStrategy());
    }

    public Resume resumeStrategy() {
        return new Resume().sessionDuration(Duration.ofSeconds(60)).retry(retry());
    }

    public Retry retry() {
        return Retry.fixedDelay(100, Duration.ofSeconds(1));
    }
}
