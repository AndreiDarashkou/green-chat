package org.green.chat.repository;

import org.green.chat.repository.entity.Chat;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatRepository extends ReactiveCrudRepository<Chat, Long> {

    Flux<Chat> findAllByUsersContains(long userId);

    Flux<Chat> findAllByUsersContainsAndGroupIsFalse(long userId);
}
