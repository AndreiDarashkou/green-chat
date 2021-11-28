package org.green.chat.controller;

import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.green.chat.DatabaseInitializer;
import org.green.chat.model.ChatRequest;
import org.green.chat.model.LoginRequest;
import org.green.chat.repository.ChatRepository;
import org.green.chat.repository.entity.Chat;
import org.green.chat.repository.entity.UserEntity;
import org.green.chat.service.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.test.StepVerifier;

import java.util.List;

import static org.green.chat.controller.ChatController.CHAT_INFO;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = DatabaseInitializer.class)
public class ChatControllerTest {

    @Autowired
    private RSocketRequester.Builder builder;
    @Autowired
    private UserService userService;
    @Autowired
    private ChatRepository chatRepository;

    private RSocketRequester requester;

    @BeforeAll
    private void setup() {
        requester = builder.transport(WebsocketClientTransport.create("localhost", 6565));
    }

    @Test
    void shouldReturnChatInfo() {
        UserEntity firstUser = userService.login(new LoginRequest("First", "pass1")).blockOptional().orElseThrow();
        UserEntity secondUser = userService.login(new LoginRequest("Second", "pass2")).blockOptional().orElseThrow();
        Chat chat = chatRepository.save(Chat.privateOf(List.of(firstUser.getId(), secondUser.getId())))
                .blockOptional().orElseThrow();

        UsernamePasswordMetadata user = new UsernamePasswordMetadata("First", "pass1");
        MimeType type = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());

        StepVerifier.create(requester.route(CHAT_INFO)
                        .metadata(user, type)
                        .data(new ChatRequest(chat.getId()))
                        .retrieveMono(Chat.class))
                .expectNextMatches(ch -> ch.getName().equals("Second") && ch.getUsers().size() == 2)
                .verifyComplete();
    }

    @AfterAll
    private void cleanup() {
        requester.dispose();
    }

}
