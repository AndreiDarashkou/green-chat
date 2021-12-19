package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.AuthUser;
import org.green.chat.model.MessageHistoryRequest;
import org.green.chat.model.MessageHistoryResponse;
import org.green.chat.model.MessageReadRequest;
import org.green.chat.repository.entity.Message;
import org.green.chat.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    public static final String MESSAGE_STREAM = "message.stream";
    public static final String MESSAGE_SEND = "message.send";
    public static final String MESSAGE_READ = "message.read";
    public static final String MESSAGE_HISTORY = "message.history";

    private final MessageService messageService;

    @MessageMapping(MESSAGE_STREAM)
    public Flux<Message> messageStream(@AuthenticationPrincipal AuthUser user) {
        log.info("called message.stream");
        return messageService.messageStream(user.getId());
    }

    @MessageMapping(MESSAGE_HISTORY)
    public Mono<MessageHistoryResponse> messageHistory(MessageHistoryRequest request) {
        log.info("called message.history: " + request);
        return messageService.getHistory(request);
    }

    @MessageMapping(MESSAGE_SEND)
    public void sendMessage(Mono<Message> message) {
        log.info("called message.send");
        messageService.sendMessage(message);
    }

    @MessageMapping(MESSAGE_READ)
    public Mono<Void> readMessage(MessageReadRequest request) {
        log.info("called message.read");
        return messageService.readMessage(request);
    }
}
