package com.killerdev.fighteros_app.dto.boxeador;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class CopaResponse {
    private UUID id;
    private String nombre;
    private LocalDate fecha;
}
