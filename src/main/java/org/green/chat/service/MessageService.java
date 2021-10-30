package org.green.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.MessageRequest;
import org.green.chat.model.MessageResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;

@Slf4j
@Service
public class MessageService {

    private final Sinks.Many<MessageResponse> messages = Sinks.many().unicast().onBackpressureBuffer();
    private final Flux<MessageResponse> messageStream = messages.asFlux().share().cache(10);

    public void sendMessage(Mono<MessageRequest> message) {
        message.doOnNext(System.out::println)
                .subscribe(msg -> {
                    MessageResponse response = new MessageResponse();
                    response.setMessage(msg.getMessage());
                    response.setFromUserId(msg.getFromUserId());
                    response.setToUserId(msg.getToUserId());
                    response.setTimestamp(Instant.now());

                    messages.tryEmitNext(response);
                });
    }

    public Flux<MessageResponse> messageStream(String userId) {
        return messageStream
                .filter(msg -> msg.getToUserId() == null || msg.getToUserId().equals(userId));
    }
}
