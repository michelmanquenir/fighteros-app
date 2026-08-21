package com.killerdev.fighteros_app.dto.evento;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EventoPeleaResultadoRequest {

    // null para quitar el resultado y volver la pelea a "programada".
    private UUID ganadorId;
}
