package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.MessageRequest;
import org.green.chat.model.MessageResponse;
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
    public Flux<MessageResponse> messageStreamNew(String userId) {
        log.info("called messages.stream");
        return messageService.messageStream(userId);
    }

    @MessageMapping("message.send")
    public void sendMessageNew(Mono<MessageRequest> message) {
        log.info("called message.send");
        messageService.sendMessage(message);
    }
}
