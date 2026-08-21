package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.evento.EventoPeleaCreateRequest;
import com.killerdev.fighteros_app.dto.evento.EventoPeleaResponse;
import com.killerdev.fighteros_app.dto.evento.EventoPeleaResultadoRequest;
import com.killerdev.fighteros_app.security.CustomUserDetails;
import com.killerdev.fighteros_app.service.EventoPeleaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/eventos/{eventoId}/peleas")
public class EventoPeleaController {

    private final EventoPeleaService peleaService;

    public EventoPeleaController(EventoPeleaService peleaService) {
        this.peleaService = peleaService;
    }

    @GetMapping
    public List<EventoPeleaResponse> listar(@PathVariable UUID eventoId) {
        return peleaService.listar(eventoId);
    }

    @PostMapping
    public EventoPeleaResponse crear(@PathVariable UUID eventoId,
                                      @Valid @RequestBody EventoPeleaCreateRequest request,
                                      @AuthenticationPrincipal CustomUserDetails principal) {
        return peleaService.crear(eventoId, request, principal.getId());
    }

    @PutMapping("/{peleaId}/resultado")
    public EventoPeleaResponse registrarResultado(@PathVariable UUID eventoId,
                                                    @PathVariable UUID peleaId,
                                                    @RequestBody EventoPeleaResultadoRequest request,
                                                    @AuthenticationPrincipal CustomUserDetails principal) {
        return peleaService.registrarResultado(eventoId, peleaId, request, principal.getId());
    }

    @DeleteMapping("/{peleaId}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID eventoId,
                                          @PathVariable UUID peleaId,
                                          @AuthenticationPrincipal CustomUserDetails principal) {
        peleaService.eliminar(eventoId, peleaId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
