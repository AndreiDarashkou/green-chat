package org.green.chat.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.green.chat.repository.ChatRepository;
import org.green.chat.repository.MessageRepository;
import org.green.chat.repository.entity.Chat;
import org.green.chat.repository.entity.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;

    private final Sinks.Many<Message> messages = Sinks.many().unicast().onBackpressureBuffer();
    private final Flux<Message> messageStream = messages.asFlux().share();

    public void sendMessage(Mono<Message> message) {
        message.doOnNext(System.out::println)
                .flatMap(messageRepository::save)
                .subscribe(messages::tryEmitNext);
    }

    public Flux<Message> messageStream(long userId) {
        //todo filter each message. potentially slow performance
        return messageStream.filter(msg -> chatService.getAllIds(userId).contains(msg.getChatId()));
    }

    public Flux<Message> messageHistory(Long chatId) {
        return messageRepository.findAllByChatId(chatId);
    }
}
