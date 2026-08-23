package com.killerdev.fighteros_app.dto.evento;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvitacionResponderRequest {

    @NotNull
    private Boolean aceptar;
}
