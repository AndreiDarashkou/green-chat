package org.green.chat.controller;

import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.green.chat.DatabaseInitializer;
import org.green.chat.model.LoginRequest;
import org.green.chat.repository.entity.UserEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import static org.green.chat.controller.UserController.USER_LOGIN;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = DatabaseInitializer.class)
public class UserControllerTest {

    @Autowired
    private RSocketRequester.Builder builder;

    private RSocketRequester requester;

    @BeforeAll
    private void setup() {
        requester = builder.transport(WebsocketClientTransport.create("localhost", 6565));
    }

    @Test
    void shouldCreateAndReturnUser() {
        StepVerifier.create(requester.route(USER_LOGIN)
                        .data(new LoginRequest("TestUser", "test"))
                        .retrieveMono(UserEntity.class))
                .expectNextMatches(user -> user.getUsername().equals("TestUser") && user.getId() == 1)
                .verifyComplete();
    }

    @AfterAll
    private void cleanup() {
        requester.dispose();
    }

}
