package com.killerdev.fighteros_app.dto.boxeador;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PesoPactadoRequest {

    @NotNull
    @Positive
    private BigDecimal pesoPactado;
}
