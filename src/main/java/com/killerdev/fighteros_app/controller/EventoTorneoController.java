package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.evento.EventoTorneoCreateRequest;
import com.killerdev.fighteros_app.dto.evento.EventoTorneoResponse;
import com.killerdev.fighteros_app.security.CustomUserDetails;
import com.killerdev.fighteros_app.service.EventoTorneoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/eventos/{eventoId}/torneos")
public class EventoTorneoController {

    private final EventoTorneoService torneoService;

    public EventoTorneoController(EventoTorneoService torneoService) {
        this.torneoService = torneoService;
    }

    @GetMapping
    public List<EventoTorneoResponse> listar(@PathVariable UUID eventoId) {
        return torneoService.listar(eventoId);
    }

    @PostMapping
    public EventoTorneoResponse crear(@PathVariable UUID eventoId,
                                       @Valid @RequestBody EventoTorneoCreateRequest request,
                                       @AuthenticationPrincipal CustomUserDetails principal) {
        return torneoService.crear(eventoId, request, principal.getId());
    }

    @DeleteMapping("/{torneoId}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID eventoId,
                                          @PathVariable UUID torneoId,
                                          @AuthenticationPrincipal CustomUserDetails principal) {
        torneoService.eliminar(eventoId, torneoId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
