package org.green.chat.config;

import io.rsocket.core.Resume;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import reactor.core.publisher.Hooks;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.CancellationException;

@Slf4j
@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RSocketStrategiesCustomizer rSocketStrategiesCustomizer() {
        return c -> c.encoders(encoders -> encoders.add(new SimpleAuthenticationEncoder()));
    }

    @Bean
    public PayloadSocketAcceptorInterceptor payloadSocketAcceptorInterceptor(RSocketSecurity rSocketSecurity) {
        return rSocketSecurity
                .simpleAuthentication(Customizer.withDefaults())
                .authorizePayload(authorize ->
                        authorize
                                .setup().permitAll()
                                .anyRequest().permitAll()
                )
                .build();
    }

    @Bean
    public RSocketMessageHandler messageHandler(RSocketStrategies socketStrategies) {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.setRSocketStrategies(socketStrategies);
        handler.getArgumentResolverConfigurer().addCustomResolver(new AuthenticationPrincipalArgumentResolver());

        return handler;
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
