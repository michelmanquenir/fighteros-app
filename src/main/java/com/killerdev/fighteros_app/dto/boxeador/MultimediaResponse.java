package com.killerdev.fighteros_app.dto.boxeador;

import com.killerdev.fighteros_app.model.enums.TipoMultimediaEnum;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class MultimediaResponse {
    private UUID id;
    private TipoMultimediaEnum tipo;
    private String url;
    private Boolean esOficial;
}
