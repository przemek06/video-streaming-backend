package com.me.videostreaming.security;

import com.me.videostreaming.util.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {
    @SneakyThrows
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        UserDetails user = JWTUtil.getUserFromToken(authToken);
        return Mono.just(JWTUtil.validateToken(authToken))
                .filter(valid->valid)
                .switchIfEmpty(Mono.empty())
                .map(valid->new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }
}
