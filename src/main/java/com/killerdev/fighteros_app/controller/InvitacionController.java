package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.evento.InvitacionResponderRequest;
import com.killerdev.fighteros_app.dto.evento.InvitacionResponse;
import com.killerdev.fighteros_app.security.CustomUserDetails;
import com.killerdev.fighteros_app.service.InvitacionEventoService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/invitaciones")
public class InvitacionController {

    private final InvitacionEventoService invitacionService;

    public InvitacionController(InvitacionEventoService invitacionService) {
        this.invitacionService = invitacionService;
    }

    @GetMapping("/mias")
    public List<InvitacionResponse> misInvitaciones(@AuthenticationPrincipal CustomUserDetails principal) {
        return invitacionService.misInvitaciones(principal.getId());
    }

    @PutMapping("/{invitacionId}/responder")
    public InvitacionResponse responder(@PathVariable UUID invitacionId,
                                         @Valid @RequestBody InvitacionResponderRequest request,
                                         @AuthenticationPrincipal CustomUserDetails principal) {
        return invitacionService.responder(invitacionId, request.getAceptar(), principal.getId());
    }
}
