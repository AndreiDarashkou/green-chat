package org.green.chat;

import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.green.chat.model.Message;
import org.green.chat.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatControllerTests {

    @Autowired
    private RSocketRequester.Builder builder;

    private RSocketRequester requester;

    @BeforeAll
    private void setup() {
        requester = builder.transport(WebsocketClientTransport.create("localhost", 6565));
    }

    @Test
    void shouldFireAndForget() {
        Mono<Void> result = requester.route("message.send")
                .data(new Message("ASd", "asdasd"))
                .send();

        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void shouldRequestStream() {
        Flux<Message> result = requester.route("messages.stream")
                .data(new User("user"))
                .retrieveFlux(Message.class)
                .doOnNext(System.out::println);

        StepVerifier.create(result)
                .expectNextCount(10)
                .verifyComplete();
    }
}