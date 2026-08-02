package com.killerdev.fighteros_app.dto.boxeador;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class PatrocinioResponse {
    private UUID id;
    private String patrocinadorNombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
