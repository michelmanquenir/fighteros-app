package com.killerdev.fighteros_app.dto.boxeador;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class EstadisticasResponse {
    private Long peleasTotales;
    private Long victorias;
    private Long derrotas;
    private Long empates;
    private Long victoriasKo;
    private Long victoriasDecision;
    private OffsetDateTime ultimaPelea;
}
