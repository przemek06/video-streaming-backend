package com.me.videostreaming.repository;

import com.me.videostreaming.entity.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<UserEntity, Long> {

    Mono<UserEntity> getUserEntityByUsername(String username);

}
