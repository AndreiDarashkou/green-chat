package org.green.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@SpringBootApplication
public class GreenChatApplication {

    public static void main(String[] args) throws InterruptedException {
        Flux.range(1, 100)
                .map(it -> new Random().nextInt(5))
                .doOnNext(val -> System.out.println("next: " + val))
                .distinctUntilChanged()
                .doOnNext(val -> System.out.println("distinct: " + val))
                .subscribe();

        Thread.sleep(5000);
        //SpringApplication.run(GreenChatApplication.class, args);
    }

}
