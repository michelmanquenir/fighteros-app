package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.social.EstadoSeguimientoResponse;
import com.killerdev.fighteros_app.dto.social.SeguidorPerfilResponse;
import com.killerdev.fighteros_app.dto.social.SolicitudSeguimientoResponse;
import com.killerdev.fighteros_app.security.CustomUserDetails;
import com.killerdev.fighteros_app.service.SeguidorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/seguidores")
public class SeguidorController {

    private final SeguidorService seguidorService;

    public SeguidorController(SeguidorService seguidorService) {
        this.seguidorService = seguidorService;
    }

    @PostMapping("/{seguidoId}")
    public EstadoSeguimientoResponse seguir(@PathVariable UUID seguidoId,
                                             @AuthenticationPrincipal CustomUserDetails principal) {
        return seguidorService.seguir(principal.getId(), seguidoId);
    }

    @DeleteMapping("/{seguidoId}")
    public ResponseEntity<Void> dejarDeSeguir(@PathVariable UUID seguidoId,
                                               @AuthenticationPrincipal CustomUserDetails principal) {
        seguidorService.dejarDeSeguir(principal.getId(), seguidoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{seguidoId}/estado")
    public EstadoSeguimientoResponse obtenerEstado(@PathVariable UUID seguidoId,
                                                     @AuthenticationPrincipal CustomUserDetails principal) {
        return seguidorService.obtenerEstado(principal != null ? principal.getId() : null, seguidoId);
    }

    @GetMapping("/solicitudes")
    public List<SolicitudSeguimientoResponse> listarSolicitudes(@AuthenticationPrincipal CustomUserDetails principal) {
        return seguidorService.listarSolicitudesPendientes(principal.getId());
    }

    @GetMapping("/mis-seguidores")
    public List<SeguidorPerfilResponse> listarMisSeguidores(@AuthenticationPrincipal CustomUserDetails principal) {
        return seguidorService.listarSeguidores(principal.getId());
    }

    @GetMapping("/mis-seguidos")
    public List<SeguidorPerfilResponse> listarMisSeguidos(@AuthenticationPrincipal CustomUserDetails principal) {
        return seguidorService.listarSeguidos(principal.getId());
    }

    @PostMapping("/solicitudes/{seguidorId}/aceptar")
    public ResponseEntity<Void> aceptarSolicitud(@PathVariable UUID seguidorId,
                                                  @AuthenticationPrincipal CustomUserDetails principal) {
        seguidorService.aceptarSolicitud(principal.getId(), seguidorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/solicitudes/{seguidorId}/rechazar")
    public ResponseEntity<Void> rechazarSolicitud(@PathVariable UUID seguidorId,
                                                   @AuthenticationPrincipal CustomUserDetails principal) {
        seguidorService.rechazarSolicitud(principal.getId(), seguidorId);
        return ResponseEntity.noContent().build();
    }
}
