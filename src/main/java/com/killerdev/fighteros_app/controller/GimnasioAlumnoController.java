package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.gimnasio.AlumnoCreadoResponse;
import com.killerdev.fighteros_app.dto.gimnasio.CrearAlumnoRequest;
import com.killerdev.fighteros_app.dto.gimnasio.GimnasioAlumnoRequest;
import com.killerdev.fighteros_app.security.CustomUserDetails;
import com.killerdev.fighteros_app.service.AuthService;
import com.killerdev.fighteros_app.service.GimnasioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/gimnasios/{gimnasioId}/alumnos")
public class GimnasioAlumnoController {

    private final GimnasioService gimnasioService;
    private final AuthService authService;

    public GimnasioAlumnoController(GimnasioService gimnasioService, AuthService authService) {
        this.gimnasioService = gimnasioService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<Void> agregar(@PathVariable UUID gimnasioId,
                                         @Valid @RequestBody GimnasioAlumnoRequest request,
                                         @AuthenticationPrincipal CustomUserDetails principal) {
        gimnasioService.agregarAlumno(gimnasioId, request.getBoxeadorId(), principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/nuevos")
    public AlumnoCreadoResponse crear(@PathVariable UUID gimnasioId,
                                       @Valid @RequestBody CrearAlumnoRequest request,
                                       @AuthenticationPrincipal CustomUserDetails principal) {
        return authService.registrarAlumno(gimnasioId, request, principal.getId());
    }

    @DeleteMapping("/{boxeadorId}")
    public ResponseEntity<Void> quitar(@PathVariable UUID gimnasioId,
                                        @PathVariable UUID boxeadorId,
                                        @AuthenticationPrincipal CustomUserDetails principal) {
        gimnasioService.quitarAlumno(gimnasioId, boxeadorId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
