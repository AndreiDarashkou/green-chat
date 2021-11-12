package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.UserRequest;
import org.green.chat.repository.entity.Chat;
import org.green.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("chat.create")
    public Mono<Chat> createChat(Chat chat) {
        log.info("called chat.create");
        return chatService.create(chat);
    }

    @MessageMapping("chat.list")
    public Flux<Chat> getChatList(UserRequest request) {
        log.info("called chat.list");
        return chatService.getAll(request.getUserId());
    }
}
