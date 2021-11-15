package org.green.chat.repository;

import org.green.chat.repository.entity.Message;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, Long> {

    Flux<Message> findAllByChatId(long chatId);
}
