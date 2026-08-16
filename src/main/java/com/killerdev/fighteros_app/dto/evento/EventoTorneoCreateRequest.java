package com.killerdev.fighteros_app.dto.evento;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EventoTorneoCreateRequest {

    @NotBlank
    private String nombre;

    private UUID categoriaId;
}
