package com.killerdev.fighteros_app.dto.gimnasio;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GimnasioAlumnoRequest {

    @NotNull
    private UUID boxeadorId;
}
