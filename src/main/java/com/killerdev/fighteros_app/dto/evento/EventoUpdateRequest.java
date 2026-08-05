package com.killerdev.fighteros_app.dto.evento;

import com.killerdev.fighteros_app.model.enums.EstadoEventoEnum;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EventoUpdateRequest {

    private String nombre;

    private LocalDate fecha;

    private String lugar;

    private Short regionId;

    @Positive
    private Integer cuposTotales;

    private EstadoEventoEnum estado;

    private String reglamentoUrl;

    private String afichePosterUrl;
}
