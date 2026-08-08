package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.usuario.UsuarioResponse;
import com.killerdev.fighteros_app.security.CustomUserDetails;
import com.killerdev.fighteros_app.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public UsuarioResponse obtenerMe(@AuthenticationPrincipal CustomUserDetails principal) {
        return usuarioService.obtenerMe(principal.getId());
    }
}
