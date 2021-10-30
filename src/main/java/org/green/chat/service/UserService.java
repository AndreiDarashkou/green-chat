package org.green.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.LoginRequest;
import org.green.chat.model.User;
import org.green.chat.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final static Set<User> ONLINE = new HashSet<>();

    private final Sinks.Many<Set<User>> users = Sinks.many().multicast().onBackpressureBuffer();

    private final Flux<Set<User>> usersStream = users.asFlux().share().cache(1);

    @EventListener(ApplicationStartedEvent.class)
    public void subscribeUsers() {
        usersStream.subscribe();
    }

    public Mono<User> login(LoginRequest user) {
        return userRepository.findByUsername(user.getUsername())
                .switchIfEmpty(userRepository.save(User.of(user.getUsername())))
                .doOnNext(u -> users.tryEmitNext(ONLINE))
                .doOnNext(u -> log.info("user logged in: " + u));
    }

    public Flux<Set<User>> online(User user) {
        return usersStream.transformDeferredContextual((userFlux, contextView) -> {
                    ContextHolder<User> cached = contextView.get("current_user");
                    return userFlux
                            .doOnCancel(() -> {
                                User cachedUser = cached.getData();
                                ONLINE.remove(cachedUser);
                                users.tryEmitNext(ONLINE);
                                System.out.println("users.stream unsubscribed: " + user);
                            })
                            .doOnSubscribe(sub -> {
                                System.out.println("users.stream subscribed: " + user);
                                cached.setData(user);
                            });
                })
                .contextWrite(context -> context.put("current_user", new ContextHolder<User>(null)));
    }

}
