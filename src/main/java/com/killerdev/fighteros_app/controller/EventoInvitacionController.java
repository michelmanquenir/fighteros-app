package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.evento.InvitacionCreateRequest;
import com.killerdev.fighteros_app.dto.evento.InvitacionResponse;
import com.killerdev.fighteros_app.security.CustomUserDetails;
import com.killerdev.fighteros_app.service.InvitacionEventoService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/eventos/{eventoId}/invitaciones")
public class EventoInvitacionController {

    private final InvitacionEventoService invitacionService;

    public EventoInvitacionController(InvitacionEventoService invitacionService) {
        this.invitacionService = invitacionService;
    }

    @GetMapping
    public List<InvitacionResponse> listar(@PathVariable UUID eventoId,
                                            @AuthenticationPrincipal CustomUserDetails principal) {
        return invitacionService.listarPorEvento(eventoId, principal.getId());
    }

    @PostMapping
    public InvitacionResponse invitar(@PathVariable UUID eventoId,
                                       @Valid @RequestBody InvitacionCreateRequest request,
                                       @AuthenticationPrincipal CustomUserDetails principal) {
        return invitacionService.invitar(eventoId, request, principal.getId());
    }
}
