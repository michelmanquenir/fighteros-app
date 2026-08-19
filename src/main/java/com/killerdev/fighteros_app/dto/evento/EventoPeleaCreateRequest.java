package com.killerdev.fighteros_app.dto.evento;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EventoPeleaCreateRequest {

    @NotNull
    private UUID boxeadorAId;

    @NotNull
    private UUID boxeadorBId;

    private UUID torneoId;

    private Short ronda;
}
