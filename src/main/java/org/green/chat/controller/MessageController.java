package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.ChatRequest;
import org.green.chat.model.UserRequest;
import org.green.chat.repository.entity.Message;
import org.green.chat.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    public static final String MESSAGE_STREAM = "message.stream";
    public static final String MESSAGE_SEND = "message.send";
    public static final String MESSAGE_HISTORY = "message.history";

    private final MessageService messageService;

    @MessageMapping(MESSAGE_STREAM)
    public Flux<Message> messageStream(UserRequest request) {
        log.info("called messages.stream");
        return messageService.messageStream(request.getUserId());
    }

    @MessageMapping(MESSAGE_HISTORY)
    public Mono<Void> messageHistory(ChatRequest request) {
        log.info("called messages.history");
        return messageService.send(request);
    }

    @MessageMapping(MESSAGE_SEND)
    public void sendMessage(Mono<Message> message) {
        log.info("called message.send");
        messageService.sendMessage(message);
    }
}
