package org.green.chat.controller;

import org.green.chat.IntegrationTest;
import org.green.chat.model.LoginRequest;
import org.green.chat.repository.entity.UserEntity;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.green.chat.controller.UserController.USER_LOGIN;

public class UserControllerTest extends IntegrationTest {

    @Test
    void shouldCreateAndReturnUser() {
        StepVerifier.create(requester.route(USER_LOGIN)
                        .data(new LoginRequest("TestUser", "test"))
                        .retrieveMono(UserEntity.class))
                .expectNextMatches(user -> user.getUsername().equals("TestUser"))
                .verifyComplete();
    }
}
