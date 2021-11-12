package org.green.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.LoginRequest;
import org.green.chat.model.SearchRequest;
import org.green.chat.model.UserRequest;
import org.green.chat.repository.UserRepository;
import org.green.chat.repository.entity.UserEntity;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String CURRENT_USER_ID = "current_user_id";
    private final static Set<Long> ONLINE = new HashSet<>();

    private final ChatService chatService;
    private final UserRepository userRepository;

    private final Sinks.Many<List<UserEntity>> users = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<List<UserEntity>> usersStream = users.asFlux().share().cache(1);

    @EventListener(ApplicationStartedEvent.class)
    public void subscribeUsers() {
        usersStream.subscribe();
    }

    public Mono<UserEntity> login(LoginRequest user) {
        return userRepository.findByUsername(user.getUsername())
                .switchIfEmpty(userRepository.save(UserEntity.of(user.getUsername())))
                .doOnNext(u -> log.info("user logged in: " + u));
    }

    public Flux<List<UserEntity>> online(UserRequest request) {
        return usersStream.transformDeferredContextual((userFlux, contextView) -> {
                    ContextHolder<Long> subscribedUserId = contextView.get(CURRENT_USER_ID);
                    return userFlux
                            .doOnCancel(() -> {
                                log.info("users.stream unsubscribed: {}", request);
                                Long cachedUserId = subscribedUserId.getData();
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
                .contextWrite(context -> context.put(CURRENT_USER_ID, new ContextHolder<UserEntity>(null)))
                .flatMap(list ->
                        chatService.getAllRelativeIds(request.getUserId())
                                .map(relatives -> list.stream().distinct()
                                        .filter(user -> relatives.contains(user.getId()))
                                        .toList()))
                .distinctUntilChanged();
    }

    public Mono<UserEntity> getShortInfo(UserRequest request) {
        return userRepository.findById(request.getUserId());
    }

    public Mono<List<UserEntity>> searchByUsername(SearchRequest request) {
        PageRequest pageRequest = PageRequest.ofSize(10).withSort(Sort.by(Sort.Direction.ASC, "username"));
        return userRepository.findByUsernameContainingIgnoreCase(request.getSearch(), pageRequest).collectList();
    }

    private void notifyOnlineUsers() {
        userRepository.findByIdIn(ONLINE).collectList()
                .subscribe(users::tryEmitNext);
    }
}
