package org.green.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.repository.MessageRepository;
import org.green.chat.repository.entity.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    private final Sinks.Many<Message> messages = Sinks.many().unicast().onBackpressureBuffer();
    private final Flux<Message> messageStream = messages.asFlux().share();

    public void sendMessage(Mono<Message> message) {
        message.doOnNext(System.out::println)
                .flatMap(messageRepository::save)
                .subscribe(messages::tryEmitNext);
    }

    public Flux<Message> messageStream(long chatId) {
        return messageStream.filter(msg -> msg.getChatId() == chatId);
    }
}
