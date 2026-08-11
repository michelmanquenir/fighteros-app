package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.gimnasio.GimnasioCreateRequest;
import com.killerdev.fighteros_app.dto.gimnasio.GimnasioMioResponse;
import com.killerdev.fighteros_app.security.CustomUserDetails;
import com.killerdev.fighteros_app.service.GimnasioService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gimnasios")
public class GimnasioController {

    private final GimnasioService gimnasioService;

    public GimnasioController(GimnasioService gimnasioService) {
        this.gimnasioService = gimnasioService;
    }

    @PostMapping
    public GimnasioMioResponse crear(@Valid @RequestBody GimnasioCreateRequest request,
                                      @AuthenticationPrincipal CustomUserDetails principal) {
        return gimnasioService.crear(request, principal.getId());
    }

    @GetMapping("/mios")
    public List<GimnasioMioResponse> obtenerMios(@AuthenticationPrincipal CustomUserDetails principal) {
        return gimnasioService.obtenerMios(principal.getId());
    }
}
