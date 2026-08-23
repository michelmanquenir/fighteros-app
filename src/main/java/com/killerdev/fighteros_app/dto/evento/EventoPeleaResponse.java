package com.killerdev.fighteros_app.dto.evento;

import com.killerdev.fighteros_app.model.enums.EstadoPeleaEnum;
import com.killerdev.fighteros_app.model.enums.EstadoSolicitudEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class EventoPeleaResponse {
    private UUID id;
    private UUID boxeadorAId;
    private String boxeadorANombre;
    private String boxeadorAFotoUrl;
    private UUID gimnasioAId;
    private String gimnasioANombre;
    private UUID boxeadorBId;
    private String boxeadorBNombre;
    private String boxeadorBFotoUrl;
    private UUID gimnasioBId;
    private String gimnasioBNombre;
    private String categoriaNombre;
    private UUID torneoId;
    private String torneoNombre;
    private short ronda;
    private EstadoPeleaEnum estado;
    private UUID ganadorId;
    private EstadoSolicitudEnum confirmacionGimnasioA;
    private EstadoSolicitudEnum confirmacionGimnasioB;
    private EstadoSolicitudEnum estadoConfirmacion;
}
