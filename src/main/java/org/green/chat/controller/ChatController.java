package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import org.green.chat.model.Message;
import org.green.chat.model.User;
import org.green.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("messages.stream")
    public Flux<Message> messageStream(Mono<User> request) {
        System.out.println("CALLED STREAM");
        return chatService.messageStream(request);
    }

    @MessageMapping("users.stream")
    public Flux<List<User>> messageStream() {
        return chatService.usersStream();
    }

    @MessageMapping("message.send")
    public void sendMessage(Mono<Message> message) {
        System.out.println("CALLED SEND");
        chatService.sendMessage(message);
    }

    @ConnectMapping
    public Mono<Void> connect() {
        System.out.println("CONNECTED");
        return Mono.empty();
    }

}
