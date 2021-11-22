package org.green.chat.repository;

import org.green.chat.repository.entity.Message;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, Long> {

    @Query("select * from green_chat.messages where chat_id = $1 order by created limit $2")
    Flux<Message> findByFilter(long chatId, int limit);
}
