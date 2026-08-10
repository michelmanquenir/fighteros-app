package com.killerdev.fighteros_app.dto.gimnasio;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GimnasioCreateRequest {

    @NotBlank
    private String nombre;

    private String direccion;

    private Short regionId;

    private String telefono;

    private String email;

    private String descripcion;
}
