package com.killerdev.fighteros_app.controller;

import com.killerdev.fighteros_app.dto.catalogo.CategoriaPesoResponse;
import com.killerdev.fighteros_app.dto.catalogo.GimnasioResponse;
import com.killerdev.fighteros_app.dto.catalogo.RegionResponse;
import com.killerdev.fighteros_app.model.deportivo.CategoriaPeso;
import com.killerdev.fighteros_app.repository.deportivo.CategoriaPesoRepository;
import com.killerdev.fighteros_app.repository.identidad.GimnasioRepository;
import com.killerdev.fighteros_app.repository.identidad.RegionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
public class CatalogoController {

    private final RegionRepository regionRepository;
    private final CategoriaPesoRepository categoriaPesoRepository;
    private final GimnasioRepository gimnasioRepository;

    public CatalogoController(RegionRepository regionRepository,
                               CategoriaPesoRepository categoriaPesoRepository,
                               GimnasioRepository gimnasioRepository) {
        this.regionRepository = regionRepository;
        this.categoriaPesoRepository = categoriaPesoRepository;
        this.gimnasioRepository = gimnasioRepository;
    }

    @GetMapping("/regiones")
    public List<RegionResponse> listarRegiones() {
        return regionRepository.findAll(Sort.by("id")).stream()
                .map(r -> RegionResponse.builder().id(r.getId()).nombre(r.getNombre()).build())
                .toList();
    }

    @GetMapping("/categorias-peso")
    public List<CategoriaPesoResponse> listarCategoriasPeso() {
        return categoriaPesoRepository.findAll().stream()
                .sorted(Comparator.comparing((CategoriaPeso c) -> c.getSexo().name())
                        .thenComparing(CategoriaPeso::getPesoMin))
                .map(c -> CategoriaPesoResponse.builder()
                        .id(c.getId()).nombre(c.getNombre()).sexo(c.getSexo())
                        .pesoMin(c.getPesoMin()).pesoMax(c.getPesoMax())
                        .build())
                .toList();
    }

    @GetMapping("/gimnasios")
    public List<GimnasioResponse> listarGimnasios() {
        return gimnasioRepository.findAll(Sort.by("nombre")).stream()
                .map(g -> GimnasioResponse.builder().id(g.getId()).nombre(g.getNombre()).build())
                .toList();
    }
}
