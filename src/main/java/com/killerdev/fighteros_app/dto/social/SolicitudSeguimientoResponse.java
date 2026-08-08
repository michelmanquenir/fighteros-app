package com.killerdev.fighteros_app.dto.social;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class SolicitudSeguimientoResponse {
    private UUID seguidorId;
    private String seguidorNombre;
    private String seguidorAvatarUrl;
    private OffsetDateTime fecha;
}
