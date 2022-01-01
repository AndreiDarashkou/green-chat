package org.green.chat.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.controller.dto.ChatDto;
import org.green.chat.model.CreateChatRequest;
import org.green.chat.repository.ChatRepository;
import org.green.chat.repository.MessageRepository;
import org.green.chat.repository.UserRepository;
import org.green.chat.repository.entity.Chat;
import org.green.chat.repository.entity.Message;
import org.green.chat.repository.entity.UserEntity;
import org.springframework.stereotype.Service;
import reactor.cache.CacheFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final Cache<Long, List<Chat>> userChatCache = Caffeine.newBuilder().maximumSize(100).build();
    private final Cache<Long, List<Long>> userChatIdsCache = Caffeine.newBuilder().maximumSize(100).build();

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public Mono<ChatDto> create(long userId, CreateChatRequest request) {
        return chatRepository.save(Chat.of(request))
                .flatMap(saved -> getName(saved, userId)
                        .doOnNext(saved::setName)
                        .flatMap(name -> Mono.just(saved)))
                .doOnNext(saved -> saved.getUsers().forEach(id -> {
                    userChatCache.invalidate(id);
                    userChatIdsCache.invalidate(id);
                }))
                .map(chat -> new ChatDto(chat.getId(), chat.getUsers(), chat.getName(), chat.isGroup(),
                        chat.getColor(), chat.getCreated(), Message.EMPTY));
    }

    public Flux<ChatDto> getAll(long userId, Long chatId) {
        return CacheFlux.lookup(this::lookupChats, userId)
                .onCacheMissResume(() -> chatRepository.findAllByUsersContains(userId))
                .andWriteWith((k, signals) -> Mono.fromRunnable(() -> cacheChats(k, signals)))
                .filter(chat -> chatId == null || chatId.equals(chat.getId()))
                .flatMap(chat -> messageRepository.findFirstByChatIdOrderByIdDesc(chat.getId())
                        .defaultIfEmpty(Message.EMPTY)
                        .map(message ->
                                new ChatDto(chat.getId(), chat.getUsers(), chat.getName(), chat.isGroup(),
                                        chat.getColor(), chat.getCreated(), message)
                        ));
    }

    public Mono<List<Long>> getAllRelativeIds(Long userId) {
        return chatRepository.findAllConnectedUserIds(userId)
                .filter(id -> !Objects.equals(id, userId))
                .collectList();
    }

    public Mono<ChatDto> get(long userId, long chatId) {
        return getAll(userId, chatId).next();
    }

    public Mono<Boolean> checkRecipient(Message msg, long userId) {
        return getAllIds(userId).any(chId -> chId == msg.getChatId());
    }

    private Flux<Long> getAllIds(long userId) {
        return CacheFlux.lookup(this::lookupChatIds, userId)
                .onCacheMissResume(() -> chatRepository.findAllIdsByUsersContains(userId))
                .andWriteWith((k, signals) -> Mono.fromRunnable(() -> cacheChatIds(k, signals)));
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

    private Mono<String> getName(Chat chat, long userId) {
        if (chat.isGroup()) {
            return Mono.just(chat.getName());
        }
        long friendId = chat.getUsers().stream().filter(id -> id != userId).findFirst()
                .orElseThrow(RuntimeException::new);

        return userRepository.findById(friendId).map(UserEntity::getUsername);
    }

    private Mono<List<Signal<Long>>> lookupChatIds(long userId) {
        List<Long> cached = userChatIdsCache.asMap().get(userId);

        return cached == null ? Mono.empty() :
                Mono.just(cached)
                        .flatMapMany(Flux::fromIterable)
                        .map(Signal::next)
                        .collectList();
    }

    private void cacheChatIds(Long userId, List<Signal<Long>> signals) {
        List<Long> chats = signals.stream()
                .filter(sig -> sig.getType() == SignalType.ON_NEXT)
                .map(Signal::get)
                .toList();

        userChatIdsCache.put(userId, chats);
    }
}
