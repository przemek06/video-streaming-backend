package com.me.videostreaming.service;

import com.me.videostreaming.model.UserDetailsModel;
import com.me.videostreaming.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Primary
@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String s) {
        return userRepository.getUserEntityByUsername(s).map(UserDetailsModel::new);
    }

}
