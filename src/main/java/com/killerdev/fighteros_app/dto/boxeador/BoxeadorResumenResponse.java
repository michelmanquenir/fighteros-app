package com.killerdev.fighteros_app.dto.boxeador;

import com.killerdev.fighteros_app.model.enums.EstadoDeportivoEnum;
import com.killerdev.fighteros_app.model.enums.SexoEnum;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class BoxeadorResumenResponse {
    private UUID id;
    private String nombre;
    private String fotoUrl;
    private SexoEnum sexo;
    private BigDecimal pesoActual;
    private String categoriaNombre;
    private String gimnasioNombre;
    private EstadoDeportivoEnum estadoDeportivo;
}
