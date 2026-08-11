package com.killerdev.fighteros_app.dto.evento;

import com.killerdev.fighteros_app.model.enums.EstadoEventoEnum;
import com.killerdev.fighteros_app.model.enums.TipoEventoEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class EventoResponse {
    private UUID id;
    private String nombre;
    private TipoEventoEnum tipo;
    private LocalDate fecha;
    private String lugar;
    private Short regionId;
    private String regionNombre;
    private Integer cuposTotales;
    private EstadoEventoEnum estado;
    private String afichePosterUrl;
    private String reglamentoUrl;
    private UUID organizadorId;
    private String organizadorNombre;
    private UUID gimnasioId;
    private String gimnasioNombre;
}
