package org.green.chat.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.repository.ChatRepository;
import org.green.chat.repository.entity.Chat;
import org.springframework.stereotype.Service;
import reactor.cache.CacheFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final Cache<Long, List<Chat>> userChatCache = Caffeine.newBuilder().maximumSize(100).build();
    private final Cache<Long, List<Long>> userChatIdsCache = Caffeine.newBuilder().maximumSize(100).build();

    private final ChatRepository chatRepository;

    public Mono<Chat> create(Chat chat) {
        return chatRepository.save(chat)
                .doOnNext(saved -> saved.getUsers().forEach(userId -> {
                    userChatCache.invalidate(userId);
                    userChatIdsCache.invalidate(userId);
                }));
    }

    public Flux<Chat> getAll(long userId) {
        return CacheFlux.lookup(userChatCache.asMap(), userId, Chat.class)
                .onCacheMissResume(() -> chatRepository.findAllByUsersContains(userId));
    }

    public List<Long> getAllIds(long userId) {
        return userChatIdsCache.get(userId, (id) -> chatRepository.findAllIdsByUsersContains(id).toStream().toList());
    }

    public Mono<List<Long>> getAllRelativeIds(Long userId) {
        return chatRepository.findAllByUsersContainsAndGroupIsFalse(userId)
                .map(Chat::getUsers)
                .flatMap(Flux::fromIterable)
                .collectList();
    }
}
