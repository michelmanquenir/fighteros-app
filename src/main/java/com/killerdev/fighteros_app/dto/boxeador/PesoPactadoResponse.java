package com.killerdev.fighteros_app.dto.boxeador;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class PesoPactadoResponse {
    private UUID id;
    private BigDecimal pesoPactado;
    private OffsetDateTime createdAt;
}
