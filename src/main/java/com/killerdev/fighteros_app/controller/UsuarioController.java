package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.usuario.UsuarioResponse;
import com.killerdev.fighteros_app.security.CustomUserDetails;
import com.killerdev.fighteros_app.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/me/avatar", consumes = "multipart/form-data")
    public UsuarioResponse subirAvatar(@RequestPart("archivo") MultipartFile archivo,
                                        @AuthenticationPrincipal CustomUserDetails principal) {
        return usuarioService.subirAvatar(principal.getId(), archivo);
    }
}
