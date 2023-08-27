package com.example.final_project_17team.user.dto;

import com.example.final_project_17team.user.entity.User;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Builder
@Slf4j
@AllArgsConstructor
public class UserDto implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private boolean gender;
    private Long age;
    private String img_url;
    private LocalDateTime created_at;
    private LocalDateTime modified_at;

    @Data
    public static class join {
        private String username;
        private String password;
        private String passwordCheck;
        private String email;
        private String phone;
        private boolean gender;
        private Long age;
    }

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .phone(user.getPhone())
                .gender(user.isGender())
                .age(user.getAge())
                .img_url(user.getImg_url())
                .created_at(user.getCreated_at())
                .modified_at(user.getModified_at())
                .build();
    }

    public User newEntity() {
        User entity = new User();
        entity.setId(id);
        entity.setUsername(username);
        entity.setPassword(password);
        entity.setEmail(email);
        entity.setPhone(phone);
        entity.setGender(gender);
        entity.setAge(age);
        entity.setImg_url(img_url);
        entity.setCreated_at(created_at);
        entity.setModified_at(modified_at);
        return entity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}