package com.killerdev.fighteros_app.dto.evento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class EventoInscripcionResponse {
    private UUID boxeadorId;
    private String boxeadorNombre;
    private String boxeadorFotoUrl;
    private BigDecimal pesoActual;
    private String categoriaNombre;
    private UUID gimnasioId;
    private String gimnasioNombre;
    private OffsetDateTime fecha;
}
