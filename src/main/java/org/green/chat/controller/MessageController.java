package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final MessageService messageService;

    @MessageMapping(MESSAGE_STREAM)
    public Flux<Message> messageStream(Long chatId) {
        log.info("called messages.stream");
        return messageService.messageStream(chatId);
    }

    @MessageMapping(MESSAGE_SEND)
    public void sendMessage(Mono<Message> message) {
        log.info("called message.send");
        messageService.sendMessage(message);
    }
}
