package com.killerdev.fighteros_app.dto.gimnasio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AlumnoCreadoResponse {
    private UUID id;
    private String nombre;
    private String email;
}
