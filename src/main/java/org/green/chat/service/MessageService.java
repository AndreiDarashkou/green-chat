package org.green.chat.service;

import org.green.chat.model.Message;
import org.green.chat.model.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class MessageService {

    private final Sinks.Many<Message> messages = Sinks.many().unicast().onBackpressureBuffer();

    private final Flux<Message> messageStream = messages.asFlux()
            .share()
            .cache(10)
            .doOnSubscribe(sub -> System.out.println("subscribed: " + sub))
            .doOnError(err -> System.out.println("exception " + err.getMessage()))
            .doOnCancel(() -> System.out.println("cancelled"))
            .doOnTerminate(() -> System.out.println("someone terminated"));

    public Flux<Message> messageStream() {
        return messageStream;
    }

    public void sendMessage(Mono<Message> message) {
        message.doOnNext(System.out::println)
                .subscribe(messages::tryEmitNext);
    }
}
