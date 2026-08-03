package com.killerdev.fighteros_app.dto.catalogo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionResponse {
    private Short id;
    private String nombre;
}
