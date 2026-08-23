package com.killerdev.fighteros_app.dto.evento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PeleaPendienteResponse {
    private UUID peleaId;
    private UUID eventoId;
    private String eventoNombre;
    private String boxeadorANombre;
    private String boxeadorAFotoUrl;
    private String boxeadorBNombre;
    private String boxeadorBFotoUrl;
    private String categoriaNombre;
}
