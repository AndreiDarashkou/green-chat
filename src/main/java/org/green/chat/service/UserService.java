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

    private final Sinks.Many<List<User>> users = Sinks.many().multicast().onBackpressureBuffer();

    private final Flux<List<User>> usersStream = users.asFlux().share().cache(1)
            .doOnSubscribe(sub -> System.out.println("usersStream subscribed: " + sub))
            .doOnError(err -> System.out.println("usersStream exception " + err.getMessage()))
            .doOnCancel(() -> System.out.println("usersStream cancelled"))
            .doOnTerminate(() -> System.out.println("usersStream someone terminated"));

    @EventListener(ApplicationStartedEvent.class)
    public void subscribeUsers() {
        usersStream.subscribe();
    }

    public Mono<User> login(Mono<User> user) {
        return user.doOnNext(u -> MOCK_DB.putIfAbsent(u.getId(), u))
                .doOnNext(ONLINE::add)
                .doOnNext(u -> users.emitNext(new ArrayList<>(ONLINE), (s, err) -> {
                    log.error("cannot emit user");
                    return false;
                }))
                .map(u -> MOCK_DB.get(u.getId()))
                .doOnNext(v -> log.info("user: " + v + " logged in"));
    }

    public Flux<List<User>> online() {
        return usersStream;
    }

}
