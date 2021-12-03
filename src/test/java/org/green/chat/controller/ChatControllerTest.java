package org.green.chat.controller;

import io.rsocket.metadata.WellKnownMimeType;
import org.green.chat.IntegrationTest;
import org.green.chat.model.ChatRequest;
import org.green.chat.model.LoginRequest;
import org.green.chat.repository.ChatRepository;
import org.green.chat.repository.entity.Chat;
import org.green.chat.repository.entity.UserEntity;
import org.green.chat.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.test.StepVerifier;

import java.util.List;

import static org.green.chat.controller.ChatController.CHAT_INFO;

public class ChatControllerTest extends IntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private ChatRepository chatRepository;

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
}
