package org.green.chat.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.repository.ChatRepository;
import org.green.chat.repository.entity.Chat;
import org.green.chat.util.ColorUtils;
import org.springframework.stereotype.Service;
import reactor.cache.CacheFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final Cache<Long, List<Chat>> userChatCache = Caffeine.newBuilder().maximumSize(100).build();
    private final Cache<Long, List<Long>> userChatIdsCache = Caffeine.newBuilder().maximumSize(100).build();

    private final ChatRepository chatRepository;

    public Mono<Chat> create(Mono<Chat> chat) {
        return chat.doOnNext(ch -> ch.setColor(ColorUtils.randomColor()))
                .flatMap(chatRepository::save)
                .doOnNext(saved -> saved.getUsers().forEach(userId -> {
                    userChatCache.invalidate(userId);
                    userChatIdsCache.invalidate(userId);
                }));
    }

    public Flux<Chat> getAll(long userId) {
        return CacheFlux.lookup(this::lookupChats, userId)
                .onCacheMissResume(() -> chatRepository.findAllByUsersContains(userId))
                .andWriteWith((k, signals) -> Mono.fromRunnable(() -> cacheChats(k, signals)));
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

    private Mono<List<Signal<Chat>>> lookupChats(long userId) {
        List<Chat> cached = userChatCache.asMap().get(userId);

        return cached == null ? Mono.empty() :
                Mono.just(cached)
                        .flatMapMany(Flux::fromIterable)
                        .map(Signal::next)
                        .collectList();
    }

    private void cacheChats(Long userId, List<Signal<Chat>> signals) {
        List<Chat> chats = signals.stream()
                .filter(sig -> sig.getType() == SignalType.ON_NEXT)
                .map(Signal::get)
                .toList();

        userChatCache.put(userId, chats);
    }
}
