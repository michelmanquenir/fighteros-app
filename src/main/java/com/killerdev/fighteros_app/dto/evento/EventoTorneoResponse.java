package com.killerdev.fighteros_app.dto.evento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class EventoTorneoResponse {
    private UUID id;
    private String nombre;
    private UUID categoriaId;
    private String categoriaNombre;
    private long cantidadInscritos;
}
