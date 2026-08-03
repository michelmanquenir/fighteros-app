package com.killerdev.fighteros_app.dto.catalogo;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GimnasioResponse {
    private UUID id;
    private String nombre;
}
