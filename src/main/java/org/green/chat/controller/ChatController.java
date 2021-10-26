package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import org.green.chat.model.Message;
import org.green.chat.model.User;
import org.green.chat.service.MessageService;
import org.green.chat.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService chatService;
    private final UserService userService;

    @MessageMapping("messages.stream")
    public Flux<Message> messageStream() {
        System.out.println("called messages.stream");
        return chatService.messageStream();
    }

    @MessageMapping("users.login")
    public Mono<User> usersLogin(Mono<User> user) {
        System.out.println("called users.login");
        return userService.login(user);
    }

    @MessageMapping("users.stream")
    public Flux<List<User>> usersStream() {
        System.out.println("called users.stream");
        return userService.online();
    }

    @MessageMapping("message.send")
    public void sendMessage(Mono<Message> message) {
        System.out.println("called message.send");
        chatService.sendMessage(message);
    }

    @ConnectMapping
    public Mono<Void> connect() {
        System.out.println("connected");
        return Mono.empty();
    }

}
