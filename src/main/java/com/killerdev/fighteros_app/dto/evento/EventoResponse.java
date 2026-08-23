package com.killerdev.fighteros_app.dto.evento;

import com.killerdev.fighteros_app.model.enums.EstadoEventoEnum;
import com.killerdev.fighteros_app.model.enums.ModalidadInscripcionEnum;
import com.killerdev.fighteros_app.model.enums.TipoEventoEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
public class EventoResponse {
    private UUID id;
    private String nombre;
    private TipoEventoEnum tipo;
    private LocalDate fecha;
    private LocalTime hora;
    private String lugar;
    private Short regionId;
    private String regionNombre;
    private Integer cuposTotales;
    private ModalidadInscripcionEnum modalidad;
    private Integer cuposPorGimnasio;
    private EstadoEventoEnum estado;
    private String afichePosterUrl;
    private String reglamentoUrl;
    private String linkEntradas;
    private boolean carteleraPublicada;
    private boolean inscripcionesCerradas;
    private UUID organizadorId;
    private String organizadorNombre;
    private UUID gimnasioId;
    private String gimnasioNombre;
}
