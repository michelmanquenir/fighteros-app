package com.killerdev.fighteros_app.dto.gimnasio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class GimnasioMioResponse {
    private UUID id;
    private String nombre;
    private String direccion;
    private Short regionId;
    private String regionNombre;
    private String telefono;
    private String email;
    private String descripcion;
    private List<String> roles;
}
