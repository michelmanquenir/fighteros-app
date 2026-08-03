package com.killerdev.fighteros_app.dto.catalogo;

import com.killerdev.fighteros_app.model.enums.SexoEnum;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class CategoriaPesoResponse {
    private UUID id;
    private String nombre;
    private SexoEnum sexo;
    private BigDecimal pesoMin;
    private BigDecimal pesoMax;
}
