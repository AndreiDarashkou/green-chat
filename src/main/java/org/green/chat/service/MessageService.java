package org.green.chat.service;

import org.green.chat.model.Message;
import org.green.chat.model.MessageRequest;
import org.green.chat.model.MessageResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;

@Service
public class MessageService {

    private final Sinks.Many<Message> messages = Sinks.many().unicast().onBackpressureBuffer();
    private final Flux<Message> messageStream = messages.asFlux().share().cache(10);

    private final Sinks.Many<MessageResponse> messages2 = Sinks.many().unicast().onBackpressureBuffer();
    private final Flux<MessageResponse> messageStream2 = messages2.asFlux().share().cache(10);

    public Flux<Message> messageStream() {
        return messageStream;
    }

    public void sendMessage(Mono<Message> message) {
        message.doOnNext(System.out::println)
                .subscribe(messages::tryEmitNext);
    }

    public void sendMessageNew(Mono<MessageRequest> message) {
        message.doOnNext(System.out::println)
                .subscribe(msg -> {
                    MessageResponse response = new MessageResponse();
                    response.setMessage(msg.getMessage());
                    response.setFromUserId(msg.getFromUserId());
                    response.setToUserId(msg.getToUserId());
                    response.setTimestamp(Instant.now());

                    messages2.tryEmitNext(response);
                });
    }

    public Flux<MessageResponse> messageStreamNew(String userId) {
        return messageStream2
                .filter(msg -> msg.getToUserId() == null || msg.getToUserId().equals(userId));
    }
}
