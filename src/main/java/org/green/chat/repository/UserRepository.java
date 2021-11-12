package org.green.chat.repository;

import org.green.chat.repository.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, Long> {

    Flux<UserEntity> findByIdIn(Collection<Long> id);

    Mono<UserEntity> findByUsername(String username);
}
