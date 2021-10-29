package org.green.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.User;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.*;

@Slf4j
@Service
public class UserService {

    private final static Set<User> ONLINE = new HashSet<>();
    private static final Map<String, User> MOCK_DB = new HashMap<>();

    private final Sinks.Many<Set<User>> users = Sinks.many().multicast().onBackpressureBuffer();

    private final Flux<Set<User>> usersStream = users.asFlux().share().cache(1);

    @EventListener(ApplicationStartedEvent.class)
    public void subscribeUsers() {
        usersStream.subscribe();
    }

    public Mono<User> login(Mono<User> user) {
        return user.doOnNext(u -> MOCK_DB.putIfAbsent(u.getId(), u))
                .doOnNext(ONLINE::add)
                .doOnNext(u -> users.tryEmitNext(ONLINE))
                .map(u -> MOCK_DB.get(u.getId()))
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
