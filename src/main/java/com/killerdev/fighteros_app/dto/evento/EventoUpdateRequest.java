package com.killerdev.fighteros_app.dto.evento;

import com.killerdev.fighteros_app.model.enums.EstadoEventoEnum;
import com.killerdev.fighteros_app.model.enums.ModalidadInscripcionEnum;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class EventoUpdateRequest {

    private String nombre;

    private LocalDate fecha;

    private LocalTime hora;

    private String lugar;

    private Short regionId;

    @Positive
    private Integer cuposTotales;

    private ModalidadInscripcionEnum modalidad;

    @Positive
    private Integer cuposPorGimnasio;

    private EstadoEventoEnum estado;

    private String reglamentoUrl;

    private String afichePosterUrl;

    private String linkEntradas;
}
