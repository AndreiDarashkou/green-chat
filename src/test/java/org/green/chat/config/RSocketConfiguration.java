package org.green.chat.config;

import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;

@TestConfiguration
public class RSocketConfiguration {

    @Autowired
    protected RSocketRequester.Builder builder;

    @Bean(destroyMethod = "dispose")
    public RSocketRequester rsocketRequester() {
        return builder.transport(WebsocketClientTransport.create("localhost", 6565));
    }

}