package com.example.final_project_17team.global.jwt;

import lombok.Data;

@Data
public class JwtRequestDto {
    private String username;
    private String password;
}