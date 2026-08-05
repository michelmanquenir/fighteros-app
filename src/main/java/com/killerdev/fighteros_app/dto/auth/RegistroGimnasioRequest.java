package com.killerdev.fighteros_app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroGimnasioRequest {

    @NotBlank
    private String nombreAdmin;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank
    private String nombreGimnasio;

    private String direccion;

    private Short regionId;

    private String telefono;

    private String emailGimnasio;

    private String descripcion;
}
