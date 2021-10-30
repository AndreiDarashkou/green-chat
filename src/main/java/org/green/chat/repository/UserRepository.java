package org.green.chat.repository;

import org.green.chat.model.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserRepository {
    private final static Set<User> ONLINE = new HashSet<>();
    private static final Map<String, User> MOCK_DB = new HashMap<>();

    public Mono<User> save(User user) {
        MOCK_DB.putIfAbsent(user.getId(), user);
        return Mono.just(user);
    }

    public Set<User> findByUserIdIn(Set<String> userIds) {
        return userIds.stream()
                .map(MOCK_DB::get)
                .collect(Collectors.toSet());
    }

    public Mono<User> findByUsername(String username) {
        return MOCK_DB.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .map(Mono::just)
                .orElse(Mono.empty());
    }

    public Mono<User> findByUserId(String userId) {
        return Optional.ofNullable(MOCK_DB.get(userId))
                .map(Mono::just)
                .orElseGet(Mono::empty);
    }
}
