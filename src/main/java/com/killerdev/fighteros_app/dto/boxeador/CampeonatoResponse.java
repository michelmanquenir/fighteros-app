package com.killerdev.fighteros_app.dto.boxeador;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class CampeonatoResponse {
    private UUID id;
    private String ligaNombre;
    private String categoriaNombre;
    private String titulo;
    private LocalDate fechaObtenido;
    private Boolean vigente;
}
