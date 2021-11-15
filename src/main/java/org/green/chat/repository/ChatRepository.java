package org.green.chat.repository;

import org.green.chat.repository.entity.Chat;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatRepository extends ReactiveCrudRepository<Chat, Long> {

    @Query("select * from green_chat.chats where users @> array[$1]::bigint[]")
    Flux<Chat> findAllByUsersContains(long userId);

    @Query("select id from green_chat.chats where users @> array[$1]::bigint[]")
    Flux<Long> findAllIdsByUsersContains(long userId);

    Flux<Chat> findAllByUsersContainsAndGroupIsFalse(long userId);
}
