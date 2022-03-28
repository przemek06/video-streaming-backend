package com.me.videostreaming.controller;

import com.me.videostreaming.model.AuthenticationRequest;
import com.me.videostreaming.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping(value = "/authenticate",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> authenticate(@RequestBody AuthenticationRequest request){
        return authenticationService.authenticate(request);
    }
}
