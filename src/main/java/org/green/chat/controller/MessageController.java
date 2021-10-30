package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.Message;
import org.green.chat.model.MessageRequest;
import org.green.chat.model.MessageResponse;
import org.green.chat.model.User;
import org.green.chat.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("messages.stream")
    public Flux<Message> messageStream() {
        log.info("called messages.stream");
        return messageService.messageStream();
    }

    @MessageMapping("message.send")
    public void sendMessage(Mono<Message> message) {
        log.info("called message.send");
        messageService.sendMessage(message);
    }

    @MessageMapping("messages.stream.new")
    public Flux<MessageResponse> messageStreamNew(String userId) {
        log.info("called messages.stream.new");
        return messageService.messageStreamNew(userId);
    }

    @MessageMapping("message.send.new")
    public void sendMessageNew(Mono<MessageRequest> message) {
        log.info("called message.send.new");
        messageService.sendMessageNew(message);
    }
}
