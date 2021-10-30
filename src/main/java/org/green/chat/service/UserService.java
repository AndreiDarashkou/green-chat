package org.green.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.LoginRequest;
import org.green.chat.model.User;
import org.green.chat.model.UserRequest;
import org.green.chat.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String CURRENT_USER_ID = "current_user_id";
    private final static Set<String> ONLINE = new HashSet<>();

    private final UserRepository userRepository;

    private final Sinks.Many<Set<User>> users = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<Set<User>> usersStream = users.asFlux().share().cache(1);

    @EventListener(ApplicationStartedEvent.class)
    public void subscribeUsers() {
        usersStream.subscribe();
    }

    public Mono<User> login(LoginRequest user) {
        return userRepository.findByUsername(user.getUsername())
                .switchIfEmpty(userRepository.save(User.of(user.getUsername())))
                .doOnNext(u -> ONLINE.add(u.getId()))
                .doOnNext(u -> notifyOnlineUsers())
                .doOnNext(u -> log.info("user logged in: " + u));
    }

    public Flux<Set<User>> online(UserRequest request) {
        return usersStream.transformDeferredContextual((userFlux, contextView) -> {
                    ContextHolder<String> subscribedUserId = contextView.get(CURRENT_USER_ID);
                    return userFlux
                            .doOnCancel(() -> {
                                log.info("users.stream unsubscribed: {}", request);
                                String cachedUserId = subscribedUserId.getData();
                                ONLINE.remove(cachedUserId);
                                notifyOnlineUsers();
                            })
                            .doOnSubscribe(sub -> {
                                log.info("users.stream subscribed: {}", request);
                                subscribedUserId.setData(request.getUserId());
                                ONLINE.add(request.getUserId());
                                notifyOnlineUsers();
                            });
                })
                .contextWrite(context -> context.put(CURRENT_USER_ID, new ContextHolder<User>(null)));
    }

    private void notifyOnlineUsers() {
        Set<User> onlineUsers = userRepository.findByUserIdIn(ONLINE);
        users.tryEmitNext(onlineUsers);
    }

}
