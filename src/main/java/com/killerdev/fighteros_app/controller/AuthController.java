package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.auth.AuthResponse;
import com.killerdev.fighteros_app.dto.auth.LoginRequest;
import com.killerdev.fighteros_app.dto.auth.RegistroBoxeadorRequest;
import com.killerdev.fighteros_app.dto.auth.RegistroGimnasioRequest;
import com.killerdev.fighteros_app.dto.auth.RegistroUsuarioRequest;
import com.killerdev.fighteros_app.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registrarUsuario(@Valid @RequestBody RegistroUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrarUsuario(request));
    }

    @PostMapping("/registro/gimnasio")
    public ResponseEntity<AuthResponse> registrarGimnasio(@Valid @RequestBody RegistroGimnasioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrarGimnasio(request));
    }

    @PostMapping("/registro/boxeador")
    public ResponseEntity<AuthResponse> registrarBoxeador(@Valid @RequestBody RegistroBoxeadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrarBoxeador(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
