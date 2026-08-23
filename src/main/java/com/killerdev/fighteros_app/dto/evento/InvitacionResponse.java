package com.killerdev.fighteros_app.dto.evento;

import com.killerdev.fighteros_app.model.enums.EstadoSolicitudEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class InvitacionResponse {
    private UUID id;
    private UUID eventoId;
    private String eventoNombre;
    private UUID gimnasioId;
    private String gimnasioNombre;
    private EstadoSolicitudEnum estado;
    private OffsetDateTime fecha;
}
