package org.green.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.model.ChatRequest;
import org.green.chat.repository.MessageRepository;
import org.green.chat.repository.entity.Message;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;

    private final Sinks.Many<Message> messages = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<Message> messageStream = messages.asFlux().share();

    @EventListener(ApplicationStartedEvent.class)
    public void subscribeUsers() {
        messageStream.subscribe(System.out::println);
    }

    public void sendMessage(Mono<Message> message) {
        message.doOnNext(System.out::println)
                .doOnNext(msg -> msg.setCreated(Instant.now()))
                .flatMap(messageRepository::save)
                .subscribe(msg -> messages.emitNext(msg, (m, s) -> {
                    log.warn("error emitting message: " + s);
                    return false;
                }));
    }

    public Flux<Message> messageStream(long userId) {
        return messageStream
                .doOnNext(msg -> log.info("message stream: " + msg))
                .doOnError(err -> log.error("message stream error:", err))
                .doOnComplete(() -> log.info("message stream completed"))
                .filterWhen(msg -> chatService.checkRecipient(msg, userId));
    }

    public Flux<Message> getHistory(ChatRequest request) {
        return messageRepository.findByFilter(request.getChatId(), request.getFrom(), request.getLimit());
    }

    public Mono<Void> sendHistory(ChatRequest request) {
        return messageRepository.findByFilter(request.getChatId(), Instant.now(), request.getLimit())
                .doOnNext(msg -> System.out.println("send message: " + msg))
                .doOnNext(msg -> messages.emitNext(msg, (m, signal) -> {
                    log.warn(signal.toString());
                    return false;
                }))
                .doOnError(e -> log.error(e.getMessage()))
                .then();
    }
}
