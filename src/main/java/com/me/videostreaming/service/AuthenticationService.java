package com.me.videostreaming.service;

import com.me.videostreaming.model.AuthenticationRequest;
import com.me.videostreaming.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class AuthenticationService {

    @Autowired
    ReactiveUserDetailsService userDetailsService;

    public Mono<ResponseEntity<Object>> authenticate(AuthenticationRequest request) {

        return userDetailsService.findByUsername(request.getUsername())
                .filter(userDetails -> request.getPassword().equals(userDetails.getPassword()))
                .map(user -> {
                    try {
                        return ResponseEntity.status(HttpStatus.OK)
                                .header("SET-COOKIE",
                                        "Authentication="+"Bearer_" + JWTUtil.generateToken(user))
                                .build();
                    } catch (IOException e) {
                        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }
}
