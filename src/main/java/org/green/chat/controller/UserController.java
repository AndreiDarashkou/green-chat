package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.LoginRequest;
import org.green.chat.model.User;
import org.green.chat.model.UserRequest;
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
    public Mono<User> usersLogin(LoginRequest request) {
        log.info("called users.login");
        return userService.login(request);
    }

    @MessageMapping("users.stream")
    public Flux<Set<User>> usersStream(UserRequest request) {
        log.info("called users.stream");
        return userService.online(request);
    }
}
