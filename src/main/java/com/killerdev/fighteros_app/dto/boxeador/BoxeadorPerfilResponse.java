package com.killerdev.fighteros_app.dto.boxeador;

import com.killerdev.fighteros_app.model.enums.EstadoDeportivoEnum;
import com.killerdev.fighteros_app.model.enums.NivelProgresionEnum;
import com.killerdev.fighteros_app.model.enums.SexoEnum;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class BoxeadorPerfilResponse {
    private UUID id;
    private String nombre;
    private String email;
    private String fotoUrl;
    private String rut;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private SexoEnum sexo;
    private BigDecimal pesoActual;
    private BigDecimal pesoHabitual;
    private UUID categoriaId;
    private String categoriaNombre;
    private UUID gimnasioId;
    private String gimnasioNombre;
    private UUID entrenadorId;
    private String entrenadorNombre;
    private Short regionId;
    private String regionNombre;
    private EstadoDeportivoEnum estadoDeportivo;
    private NivelProgresionEnum nivelProgresion;
}
