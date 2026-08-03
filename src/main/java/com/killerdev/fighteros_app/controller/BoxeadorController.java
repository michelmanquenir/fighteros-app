package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.boxeador.BoxeadorPerfilResponse;
import com.killerdev.fighteros_app.dto.boxeador.BoxeadorResumenResponse;
import com.killerdev.fighteros_app.dto.boxeador.BoxeadorUpdateRequest;
import com.killerdev.fighteros_app.dto.boxeador.CampeonatoResponse;
import com.killerdev.fighteros_app.dto.boxeador.CopaResponse;
import com.killerdev.fighteros_app.dto.boxeador.EstadisticasResponse;
import com.killerdev.fighteros_app.dto.boxeador.MedallaResponse;
import com.killerdev.fighteros_app.dto.boxeador.MultimediaResponse;
import com.killerdev.fighteros_app.dto.boxeador.PatrocinioResponse;
import com.killerdev.fighteros_app.dto.boxeador.PeleaResumenResponse;
import com.killerdev.fighteros_app.dto.boxeador.PesoPactadoRequest;
import com.killerdev.fighteros_app.dto.boxeador.PesoPactadoResponse;
import com.killerdev.fighteros_app.model.enums.EstadoDeportivoEnum;
import com.killerdev.fighteros_app.model.enums.TipoMultimediaEnum;
import com.killerdev.fighteros_app.security.CustomUserDetails;
import com.killerdev.fighteros_app.service.BoxeadorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/boxeadores")
public class BoxeadorController {

    private final BoxeadorService boxeadorService;

    public BoxeadorController(BoxeadorService boxeadorService) {
        this.boxeadorService = boxeadorService;
    }

    @GetMapping
    public Page<BoxeadorResumenResponse> listar(
            @RequestParam(required = false) UUID gimnasioId,
            @RequestParam(required = false) Short regionId,
            @RequestParam(required = false) UUID categoriaId,
            @RequestParam(required = false) EstadoDeportivoEnum estado,
            Pageable pageable) {
        return boxeadorService.listar(gimnasioId, regionId, categoriaId, estado, pageable);
    }

    @GetMapping("/{id}")
    public BoxeadorPerfilResponse obtenerPerfil(@PathVariable UUID id) {
        return boxeadorService.obtenerPerfil(id);
    }

    @PutMapping("/{id}")
    public BoxeadorPerfilResponse actualizar(@PathVariable UUID id,
                                              @Valid @RequestBody BoxeadorUpdateRequest request,
                                              @AuthenticationPrincipal CustomUserDetails principal) {
        return boxeadorService.actualizar(id, request, principal.getId());
    }

    @GetMapping("/{id}/estadisticas")
    public EstadisticasResponse obtenerEstadisticas(@PathVariable UUID id) {
        return boxeadorService.obtenerEstadisticas(id);
    }

    @GetMapping("/{id}/historial")
    public List<PeleaResumenResponse> obtenerHistorial(@PathVariable UUID id) {
        return boxeadorService.obtenerHistorial(id);
    }

    @GetMapping("/{id}/proximas-peleas")
    public List<PeleaResumenResponse> obtenerProximasPeleas(@PathVariable UUID id) {
        return boxeadorService.obtenerProximasPeleas(id);
    }

    @GetMapping("/{id}/pesos-pactados")
    public List<PesoPactadoResponse> obtenerPesosPactados(@PathVariable UUID id) {
        return boxeadorService.obtenerPesosPactados(id);
    }

    @GetMapping("/{id}/medallas")
    public List<MedallaResponse> obtenerMedallas(@PathVariable UUID id) {
        return boxeadorService.obtenerMedallas(id);
    }

    @GetMapping("/{id}/copas")
    public List<CopaResponse> obtenerCopas(@PathVariable UUID id) {
        return boxeadorService.obtenerCopas(id);
    }

    @GetMapping("/{id}/campeonatos")
    public List<CampeonatoResponse> obtenerCampeonatos(@PathVariable UUID id) {
        return boxeadorService.obtenerCampeonatos(id);
    }

    @GetMapping("/{id}/patrocinios")
    public List<PatrocinioResponse> obtenerPatrocinios(@PathVariable UUID id) {
        return boxeadorService.obtenerPatrocinios(id);
    }

    @GetMapping("/{id}/multimedia")
    public List<MultimediaResponse> obtenerMultimedia(@PathVariable UUID id) {
        return boxeadorService.obtenerMultimedia(id);
    }

    @PostMapping(value = "/{id}/foto", consumes = "multipart/form-data")
    public BoxeadorPerfilResponse subirFoto(@PathVariable UUID id,
                                             @RequestPart("archivo") MultipartFile archivo,
                                             @AuthenticationPrincipal CustomUserDetails principal) {
        return boxeadorService.subirFoto(id, archivo, principal.getId());
    }

    @PostMapping(value = "/{id}/multimedia", consumes = "multipart/form-data")
    public MultimediaResponse agregarMultimedia(@PathVariable UUID id,
                                                 @RequestPart("archivo") MultipartFile archivo,
                                                 @RequestParam TipoMultimediaEnum tipo,
                                                 @AuthenticationPrincipal CustomUserDetails principal) {
        return boxeadorService.agregarMultimedia(id, archivo, tipo, principal.getId());
    }

    @PostMapping("/{id}/pesos-pactados")
    public PesoPactadoResponse registrarPesoPactado(@PathVariable UUID id,
                                                     @Valid @RequestBody PesoPactadoRequest request,
                                                     @AuthenticationPrincipal CustomUserDetails principal) {
        return boxeadorService.registrarPesoPactado(id, request, principal.getId());
    }
}
