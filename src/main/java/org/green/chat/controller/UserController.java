package org.green.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.*;
import org.green.chat.repository.entity.UserEntity;
import org.green.chat.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    public static final String USER_LOGIN = "user.login";
    public static final String USER_STREAM = "user.stream";
    public static final String USER_SHORT_INFO = "user.short.info";
    public static final String USER_SEARCH = "user.search";
    public static final String USER_FRIENDS = "user.friends";

    private final UserService userService;

    @MessageMapping(USER_LOGIN)
    public Mono<UserEntity> usersLogin(LoginRequest request) {
        log.info("called user.login: {}", request);
        return userService.login(request);
    }

    @MessageMapping(USER_STREAM)
    public Flux<List<UserEntity>> usersStream(@AuthenticationPrincipal AuthUser user) {
        log.info("called user.stream: {}", user);
        return userService.online(user.getId());
    }

    @MessageMapping(USER_SHORT_INFO)
    public Mono<UserDto> shortInfo(UserRequest request) {
        log.info("called user.short.info : {}", request);
        return userService.getShortInfo(request);
    }

    @MessageMapping(USER_SEARCH)
    public Mono<List<UserDto>> searchUsers(SearchRequest request) {
        log.info("called user.search: {}", request);
        return userService.searchByUsername(request);
    }

    @MessageMapping(USER_FRIENDS)
    public Mono<List<UserDto>> getFriends(@AuthenticationPrincipal AuthUser user) {
        log.info("called user.friends: {}", user);
        return userService.getFriends(user.getId());
    }
}
