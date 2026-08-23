package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.evento.AsignarTorneoRequest;
import com.killerdev.fighteros_app.dto.evento.EventoArchivoResponse;
import com.killerdev.fighteros_app.dto.evento.EventoCreateRequest;
import com.killerdev.fighteros_app.dto.evento.EventoInscripcionCreateRequest;
import com.killerdev.fighteros_app.dto.evento.EventoInscripcionResponse;
import com.killerdev.fighteros_app.dto.evento.EventoResponse;
import com.killerdev.fighteros_app.dto.evento.EventoUpdateRequest;
import com.killerdev.fighteros_app.model.enums.EstadoEventoEnum;
import com.killerdev.fighteros_app.model.enums.TipoEventoEnum;
import com.killerdev.fighteros_app.security.CustomUserDetails;
import com.killerdev.fighteros_app.service.EventoInscripcionService;
import com.killerdev.fighteros_app.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final EventoInscripcionService inscripcionService;

    public EventoController(EventoService eventoService, EventoInscripcionService inscripcionService) {
        this.eventoService = eventoService;
        this.inscripcionService = inscripcionService;
    }

    @GetMapping
    public Page<EventoResponse> listar(
            @RequestParam(required = false) Short regionId,
            @RequestParam(required = false) TipoEventoEnum tipo,
            @RequestParam(required = false) EstadoEventoEnum estado,
            Pageable pageable) {
        return eventoService.listar(regionId, tipo, estado, pageable);
    }

    @GetMapping("/mios")
    public Page<EventoResponse> misEventos(@AuthenticationPrincipal CustomUserDetails principal,
                                            Pageable pageable) {
        return eventoService.misEventos(principal.getId(), pageable);
    }

    @GetMapping("/{id}")
    public EventoResponse obtener(@PathVariable UUID id) {
        return eventoService.obtener(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('GIMNASIO_ADMIN') or hasRole('ADMIN')")
    public EventoResponse crear(@Valid @RequestBody EventoCreateRequest request,
                                 @AuthenticationPrincipal CustomUserDetails principal) {
        return eventoService.crear(request, principal.getId());
    }

    @PutMapping("/{id}")
    public EventoResponse actualizar(@PathVariable UUID id,
                                      @Valid @RequestBody EventoUpdateRequest request,
                                      @AuthenticationPrincipal CustomUserDetails principal) {
        return eventoService.actualizar(id, request, principal.getId());
    }

    @PostMapping(value = "/archivos", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('GIMNASIO_ADMIN') or hasRole('ADMIN')")
    public EventoArchivoResponse subirArchivo(@RequestPart("archivo") MultipartFile archivo,
                                               @AuthenticationPrincipal CustomUserDetails principal) {
        return eventoService.subirArchivo(archivo, principal.getId());
    }

    @GetMapping("/{id}/inscripciones")
    public List<EventoInscripcionResponse> listarInscripciones(@PathVariable UUID id) {
        return inscripcionService.listar(id);
    }

    @PostMapping("/{id}/inscripciones")
    @PreAuthorize("hasRole('GIMNASIO_ADMIN') or hasRole('ADMIN')")
    public EventoInscripcionResponse inscribir(@PathVariable UUID id,
                                                @Valid @RequestBody EventoInscripcionCreateRequest request,
                                                @AuthenticationPrincipal CustomUserDetails principal) {
        return inscripcionService.inscribir(id, request, principal.getId());
    }

    @DeleteMapping("/{id}/inscripciones/{boxeadorId}")
    public ResponseEntity<Void> retirarInscripcion(@PathVariable UUID id,
                                                     @PathVariable UUID boxeadorId,
                                                     @AuthenticationPrincipal CustomUserDetails principal) {
        inscripcionService.retirar(id, boxeadorId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/inscripciones/{boxeadorId}/torneo")
    public EventoInscripcionResponse asignarTorneo(@PathVariable UUID id,
                                                     @PathVariable UUID boxeadorId,
                                                     @RequestBody AsignarTorneoRequest request,
                                                     @AuthenticationPrincipal CustomUserDetails principal) {
        return inscripcionService.asignarTorneo(id, boxeadorId, request.getTorneoId(), principal.getId());
    }

    @PostMapping("/{id}/cartelera/publicar")
    public EventoResponse publicarCartelera(@PathVariable UUID id,
                                             @AuthenticationPrincipal CustomUserDetails principal) {
        return eventoService.publicarCartelera(id, principal.getId());
    }
}
