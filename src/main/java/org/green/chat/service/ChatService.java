package org.green.chat.service;

import org.green.chat.model.Message;
import org.green.chat.model.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class ChatService {

    private final static Set<User> ONLINE = new ConcurrentSkipListSet<>(Comparator.comparing(User::getUsername));
    private final Sinks.Many<List<User>> users = Sinks.many().unicast().onBackpressureBuffer();
    private final Sinks.Many<Message> messages = Sinks.many().unicast().onBackpressureBuffer();

    private final Flux<Message> messageFlux = messages.asFlux()
            .doOnNext(msg -> System.out.println("before share: " + msg))
            .share()
            .cache(10)
            .doOnNext(msg -> System.out.println("after share: " + msg))
            .doOnSubscribe(sub -> System.out.println("subscribed: " + sub))
            .doOnError(err -> System.out.println("exception " + err.getMessage()))
            .doOnCancel(() -> System.out.println("cancelled"))
            .doOnTerminate(() -> System.out.println("someone terminated"));

    public Flux<Message> messageStream(Mono<User> request) {
        return request.doOnNext(ONLINE::add)
                .doOnNext(user -> users.tryEmitNext(new ArrayList<>(ONLINE)))
                .thenMany(messageFlux);//Flux.from(messageFlux)
    }

    public void sendMessage(Mono<Message> message) {
        message.doOnNext(System.out::println)
                .subscribe(messages::tryEmitNext);
    }

    public Flux<List<User>> usersStream() {
        return users.asFlux();
    }

    private void logout() {

    }
}
