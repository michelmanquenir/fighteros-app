package com.killerdev.fighteros_app.dto.boxeador;

import com.killerdev.fighteros_app.model.enums.EstadoPeleaEnum;
import com.killerdev.fighteros_app.model.enums.MetodoVictoriaEnum;
import com.killerdev.fighteros_app.model.enums.ResultadoPeleaEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class PeleaResumenResponse {
    private UUID id;
    private UUID eventoId;
    private String eventoNombre;
    private UUID rivalId;
    private String rivalNombre;
    private EstadoPeleaEnum estado;
    private ResultadoPeleaEnum resultado;
    // "victoria" | "derrota" | "empate" | "no_contest" | "cancelada" | null —
    // resultado.resultado ya traducido desde la perspectiva de ESTE boxeador
    // (resultado crudo es victoria_a/victoria_b relativo a la pelea, no al boxeador consultado)
    private String resultadoBoxeador;
    private MetodoVictoriaEnum metodoVictoria;
    private OffsetDateTime fecha;
}
