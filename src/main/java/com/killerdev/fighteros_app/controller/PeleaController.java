package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.evento.PeleaPendienteResponse;
import com.killerdev.fighteros_app.security.CustomUserDetails;
import com.killerdev.fighteros_app.service.EventoPeleaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/peleas")
public class PeleaController {

    private final EventoPeleaService peleaService;

    public PeleaController(EventoPeleaService peleaService) {
        this.peleaService = peleaService;
    }

    @GetMapping("/mias-pendientes")
    public List<PeleaPendienteResponse> misPendientes(@AuthenticationPrincipal CustomUserDetails principal) {
        return peleaService.misPendientes(principal.getId());
    }
}
