package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.controller.dto.ChatDto;
import org.green.chat.model.AuthUser;
import org.green.chat.model.ChatRequest;
import org.green.chat.model.CreateChatRequest;
import org.green.chat.repository.entity.Chat;
import org.green.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    public static final String CHAT_CREATE = "chat.create";
    public static final String CHAT_LIST = "chat.list";
    public static final String CHAT_INFO = "chat.info";

    private final ChatService chatService;

    @MessageMapping(CHAT_CREATE)
    public Mono<Chat> createChat(@AuthenticationPrincipal AuthUser user, CreateChatRequest chat) {
        log.info("called chat.create");
        return chatService.create(user.getId(), chat);
    }

    @MessageMapping(CHAT_LIST)
    public Flux<ChatDto> getChatList(@AuthenticationPrincipal AuthUser user) {
        log.info("called chat.list");
        return chatService.getAll(user.getId());
    }

    @MessageMapping(CHAT_INFO)
    public Mono<ChatDto> getChatInfo(@AuthenticationPrincipal AuthUser user, ChatRequest request) {
        log.info("called chat.info");
        return chatService.get(user.getId(), request.getChatId());
    }
}
