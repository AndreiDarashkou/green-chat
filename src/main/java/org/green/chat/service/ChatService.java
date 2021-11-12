package org.green.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.repository.ChatRepository;
import org.green.chat.repository.entity.Chat;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public Mono<Chat> create(Chat chat) {
        return chatRepository.save(chat);
    }

    public Flux<Chat> getAll(long userId) {
        return chatRepository.findAllByUsersContains(userId);
    }

    public Mono<List<Long>> getAllRelativeIds(Long userId) {
        return chatRepository.findAllByUsersContainsAndGroupIsFalse(userId)
                .map(Chat::getUsers)
                .flatMap(Flux::fromIterable)
                .collectList();
    }
}
