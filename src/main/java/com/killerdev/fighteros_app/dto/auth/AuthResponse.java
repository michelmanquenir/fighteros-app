package com.killerdev.fighteros_app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UUID usuarioId;
    private String nombre;
    private String email;
    private List<String> roles;
}
