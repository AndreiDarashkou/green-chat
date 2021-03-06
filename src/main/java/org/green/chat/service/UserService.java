package org.green.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.*;
import org.green.chat.repository.UserRepository;
import org.green.chat.repository.entity.UserEntity;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UserService implements ReactiveUserDetailsService {

    private static final String CURRENT_USER_ID = "current_user_id";
    private final static Set<Long> ONLINE = new HashSet<>();

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Sinks.Many<List<UserEntity>> users = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<List<UserEntity>> usersStream = users.asFlux().share().cache(1);

    @EventListener(ApplicationStartedEvent.class)
    public void subscribeUsers() {
        usersStream.subscribe();
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> AuthUser.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .created(user.getCreated())
                        .color(user.getColor())
                        .build());
    }

    public Mono<UserEntity> login(LoginRequest user) {
        return userRepository.findByUsername(user.getUsername())
                .switchIfEmpty(userRepository
                        .save(UserEntity.of(user.getUsername(), passwordEncoder.encode(user.getPassword()))))
                .doOnNext(u -> log.info("user logged in: " + u));
    }

    public Flux<List<UserEntity>> online(long userId) {
        return usersStream.transformDeferredContextual((userFlux, contextView) -> {
                    ContextHolder<Long> subscribedUserId = contextView.get(CURRENT_USER_ID);
                    return userFlux
                            .doOnCancel(() -> {
                                log.info("users.stream unsubscribed: {}", userId);
                                Long cachedUserId = subscribedUserId.getData();
                                ONLINE.remove(cachedUserId);
                                notifyOnlineUsers();
                            })
                            .doOnSubscribe(sub -> {
                                log.info("users.stream subscribed: {}", userId);
                                subscribedUserId.setData(userId);
                                ONLINE.add(userId);
                                notifyOnlineUsers();
                            });
                })
                .contextWrite(context -> context.put(CURRENT_USER_ID, new ContextHolder<UserEntity>(null)))
                .flatMap(list ->
                        chatService.getAllRelativeIds(userId)
                                .map(relatives -> list.stream().distinct()
                                        .filter(user -> relatives.contains(user.getId()))
                                        .toList()))
                .distinctUntilChanged();
    }

    public Mono<UserDto> getShortInfo(UserRequest request) {
        return userRepository.findById(request.getUserId())
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getColor(), user.getCreated()));
    }

    public Mono<List<UserDto>> searchByUsername(SearchRequest request) {
        PageRequest pageRequest = PageRequest.ofSize(10).withSort(Sort.by(Sort.Direction.ASC, "username"));
        return userRepository.findByUsernameContainingIgnoreCase(request.getSearch(), pageRequest)
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getColor(), user.getCreated()))
                .collectList();
    }

    public Mono<List<UserDto>> getFriends(long userId) {
        return chatService.getAllRelativeIds(userId)
                .flatMapMany(userRepository::findByIdIn)
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getColor(), user.getCreated()))
                .collectList();
    }

    private void notifyOnlineUsers() {
        userRepository.findByIdIn(ONLINE).collectList()
                .subscribe(users::tryEmitNext);
    }
}
