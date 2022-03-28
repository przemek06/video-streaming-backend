package com.me.videostreaming.model;

import com.me.videostreaming.entity.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = false)
@Data
@ToString
public class UserDetailsModel implements UserDetails, Serializable {
    private UserEntity userEntity;

    public UserDetailsModel(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public Long getId() {
        return userEntity.getId();
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(userEntity.getRoles()
                .split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userEntity.getActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userEntity.getActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userEntity.getActive();
    }

    @Override
    public boolean isEnabled() {
        return userEntity.getActive();
    }

}
