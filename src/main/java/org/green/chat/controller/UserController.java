package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.LoginRequest;
import org.green.chat.model.SearchRequest;
import org.green.chat.model.UserRequest;
import org.green.chat.repository.entity.UserEntity;
import org.green.chat.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @MessageMapping("user.login")
    public Mono<UserEntity> usersLogin(LoginRequest request) {
        log.info("called user.login: {}", request);
        return userService.login(request);
    }

    @MessageMapping("user.stream")
    public Flux<List<UserEntity>> usersStream(UserRequest request) {
        log.info("called user.stream: {}", request);
        return userService.online(request);
    }

    @MessageMapping("user.short.info")
    public Mono<UserEntity> shortInfo(UserRequest request) {
        log.info("called user.short.info : {}", request);
        return userService.getShortInfo(request);
    }

    @MessageMapping("user.search")
    public Mono<List<UserEntity>> searchUsers(SearchRequest request) {
        log.info("called user.search: {}", request);
        return userService.searchByUsername(request);
    }
}
