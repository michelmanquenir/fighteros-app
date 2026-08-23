package com.killerdev.fighteros_app.dto.evento;

import com.killerdev.fighteros_app.model.enums.ModalidadInscripcionEnum;
import com.killerdev.fighteros_app.model.enums.TipoEventoEnum;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class EventoCreateRequest {

    @NotBlank
    private String nombre;

    @NotNull
    private TipoEventoEnum tipo;

    @NotNull
    @FutureOrPresent
    private LocalDate fecha;

    private String lugar;

    private Short regionId;

    private UUID gimnasioId;

    @Positive
    private Integer cuposTotales;

    private ModalidadInscripcionEnum modalidad;

    @Positive
    private Integer cuposPorGimnasio;

    private String reglamentoUrl;

    private String afichePosterUrl;
}
