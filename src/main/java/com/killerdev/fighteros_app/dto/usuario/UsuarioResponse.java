package com.killerdev.fighteros_app.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UsuarioResponse {
    private UUID id;
    private String nombre;
    private String email;
    private String avatarUrl;
    private List<String> roles;
}
