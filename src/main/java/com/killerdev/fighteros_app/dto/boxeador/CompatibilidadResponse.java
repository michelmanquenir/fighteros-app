package com.killerdev.fighteros_app.dto.boxeador;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CompatibilidadResponse {
    private UUID boxeadorAId;
    private String boxeadorANombre;
    private UUID boxeadorBId;
    private String boxeadorBNombre;

    private int puntajeGeneral;

    private int puntajePeso;
    private BigDecimal pesoA;
    private BigDecimal pesoB;
    private boolean mismaCategoria;
    private String categoriaANombre;
    private String categoriaBNombre;

    private int puntajeExperiencia;
    private long peleasA;
    private long peleasB;

    private int puntajeNivel;
    private String nivelANombre;
    private String nivelBNombre;
}
