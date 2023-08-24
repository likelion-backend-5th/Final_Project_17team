package com.example.final_project_17team.user.controller;

import com.example.final_project_17team.global.exception.ErrorCode;
import com.example.final_project_17team.global.exception.CustomException;
import com.example.final_project_17team.global.jwt.JwtRequestDto;
import com.example.final_project_17team.global.jwt.JwtTokenDto;
import com.example.final_project_17team.user.dto.UserDto;
import com.example.final_project_17team.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("users")
@AllArgsConstructor
public class UserController {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public JwtTokenDto login(@RequestBody JwtRequestDto dto) {
        return service.loginUser(dto);
    }

    @PostMapping("/join")
    public void join(@RequestBody UserDto.join dto) {
        if (!dto.getPasswordCheck().equals(dto.getPassword()))
            throw new CustomException(ErrorCode.DIFF_PASSWORD_CHECK, String.format("Username : ", dto.getUsername()));

        service.createUser(UserDto.builder()
                    .username(dto.getUsername())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .email(dto.getEmail())
                    .phone(dto.getPhone())
                    .gender(dto.isGender())
                    .age(dto.getAge())
                    .created_at(LocalDateTime.now())
                    .build()
        );
    }
}