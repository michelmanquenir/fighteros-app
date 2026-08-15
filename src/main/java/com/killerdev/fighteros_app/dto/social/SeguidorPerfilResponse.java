package com.killerdev.fighteros_app.dto.social;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class SeguidorPerfilResponse {
    private UUID usuarioId;
    private String nombre;
    private String avatarUrl;
    private boolean esBoxeador;
}
