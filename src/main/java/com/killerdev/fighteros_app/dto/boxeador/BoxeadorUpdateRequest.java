package com.killerdev.fighteros_app.dto.boxeador;

import com.killerdev.fighteros_app.model.enums.EstadoDeportivoEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class BoxeadorUpdateRequest {
    private BigDecimal pesoActual;
    private BigDecimal pesoHabitual;
    private UUID categoriaId;
    private UUID gimnasioId;
    private UUID entrenadorId;
    private Short regionId;
    private EstadoDeportivoEnum estadoDeportivo;
}
