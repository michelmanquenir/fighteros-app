package com.killerdev.fighteros_app.dto.gimnasio;

import com.killerdev.fighteros_app.model.enums.SexoEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CrearAlumnoRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank
    private String rut;

    @NotNull
    @Past
    private LocalDate fechaNacimiento;

    @NotNull
    private SexoEnum sexo;

    private BigDecimal pesoActual;

    private BigDecimal pesoHabitual;

    private UUID categoriaId;

    private Short regionId;
}
