package org.green.chat.service;

import org.green.chat.model.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class UserService {

    private final static Set<User> ONLINE = new ConcurrentSkipListSet<>(Comparator.comparing(User::getUsername));
    private static final Map<String, User> MOCK_DB = new HashMap<>();

    private final Sinks.Many<List<User>> users = Sinks.many().unicast().onBackpressureBuffer();

    private final Flux<List<User>> usersStream = users.asFlux().share().cache(1)
            .doOnSubscribe(sub -> System.out.println("usersStream subscribed: " + sub))
            .doOnError(err -> System.out.println("usersStream exception " + err.getMessage()))
            .doOnCancel(() -> System.out.println("usersStream cancelled"))
            .doOnTerminate(() -> System.out.println("usersStream someone terminated"));

    public Mono<User> login(Mono<User> user) {
        return user.doOnNext(u -> MOCK_DB.putIfAbsent(u.getId(), u))
                .doOnNext(ONLINE::add)
                .doOnNext(u -> users.tryEmitNext(new ArrayList<>(ONLINE)))
                .map(u -> MOCK_DB.get(u.getId()));
    }

    public Flux<List<User>> online() {
        return usersStream;
    }

}
