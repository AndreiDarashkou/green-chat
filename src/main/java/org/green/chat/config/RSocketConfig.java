package org.green.chat.config;

import io.rsocket.core.Resume;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.CancellationException;

@Slf4j
@Configuration
public class RSocketConfig {

    public RSocketConfig() {
        Hooks.onErrorDropped(e -> {
            if (e instanceof CancellationException || e.getCause() instanceof CancellationException) {
                log.trace("Operator called default onErrorDropped", e);
            } else {
                log.error("Operator called default onErrorDropped", e);
            }
        });
    }

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
