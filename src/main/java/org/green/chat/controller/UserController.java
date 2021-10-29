package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.User;
import org.green.chat.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @MessageMapping("users.login")
    public Mono<User> usersLogin(Mono<User> user) {
        log.info("called users.login");
        return userService.login(user);
    }

    @MessageMapping("users.stream")
    public Flux<Set<User>> usersStream(User user) {
        log.info("called users.stream");
        return userService.online(user);
    }
}
