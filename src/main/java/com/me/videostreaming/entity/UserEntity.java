package com.me.videostreaming.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserEntity implements Serializable {

    public UserEntity(Long id, String username, String roles, Boolean active) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.active = active;
    }

    @Id
    @Column(value = "user_id")
    private Long id;
    private String username;
    private String password;
    @Column(value = "user_roles")
    private String roles;
    private Boolean active;
}
