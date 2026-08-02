package com.killerdev.fighteros_app.dto.boxeador;

import com.killerdev.fighteros_app.model.enums.TipoMedallaEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class MedallaResponse {
    private UUID id;
    private TipoMedallaEnum tipo;
    private String nombre;
    private LocalDate fecha;
    private String descripcion;
}
