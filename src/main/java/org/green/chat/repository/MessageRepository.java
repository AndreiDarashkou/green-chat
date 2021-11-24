package org.green.chat.repository;

import org.green.chat.repository.entity.Message;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, Long> {

    @Query("""
        select * from green_chat.messages
        where chat_id = $1 and created < $2::timestamp order by created limit $3
    """)
    Flux<Message> findByFilter(long chatId, Instant from, int limit);
}
