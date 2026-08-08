package com.killerdev.fighteros_app.dto.social;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EstadoSeguimientoResponse {
    private String estado; // "ninguno" | "pendiente" | "aceptado"
}
