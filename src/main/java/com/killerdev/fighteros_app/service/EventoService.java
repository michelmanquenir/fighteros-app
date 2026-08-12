package com.killerdev.fighteros_app.service;

import com.killerdev.fighteros_app.dto.evento.EventoArchivoResponse;
import com.killerdev.fighteros_app.dto.evento.EventoCreateRequest;
import com.killerdev.fighteros_app.dto.evento.EventoResponse;
import com.killerdev.fighteros_app.dto.evento.EventoUpdateRequest;
import com.killerdev.fighteros_app.exception.AccesoDenegadoException;
import com.killerdev.fighteros_app.exception.OperacionInvalidaException;
import com.killerdev.fighteros_app.exception.ResourceNotFoundException;
import com.killerdev.fighteros_app.model.enums.EstadoEventoEnum;
import com.killerdev.fighteros_app.model.enums.RolUsuarioEnum;
import com.killerdev.fighteros_app.model.enums.TipoEventoEnum;
import com.killerdev.fighteros_app.model.evento.Evento;
import com.killerdev.fighteros_app.model.identidad.Gimnasio;
import com.killerdev.fighteros_app.model.identidad.Region;
import com.killerdev.fighteros_app.model.identidad.Usuario;
import com.killerdev.fighteros_app.repository.evento.EventoRepository;
import com.killerdev.fighteros_app.repository.identidad.GimnasioRepository;
import com.killerdev.fighteros_app.repository.identidad.RegionRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRolRepository;
import com.killerdev.fighteros_app.storage.StorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EventoService {

    private final EventoRepository eventoRepository;
    private final GimnasioRepository gimnasioRepository;
    private final RegionRepository regionRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final StorageService storageService;

    public EventoService(EventoRepository eventoRepository,
                          GimnasioRepository gimnasioRepository,
                          RegionRepository regionRepository,
                          UsuarioRepository usuarioRepository,
                          UsuarioRolRepository usuarioRolRepository,
                          StorageService storageService) {
        this.eventoRepository = eventoRepository;
        this.gimnasioRepository = gimnasioRepository;
        this.regionRepository = regionRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.storageService = storageService;
    }

    public EventoArchivoResponse subirArchivo(MultipartFile archivo, UUID usuarioAutenticadoId) {
        String url = storageService.subirArchivo(archivo, "eventos/" + usuarioAutenticadoId);
        return EventoArchivoResponse.builder().url(url).build();
    }

    public Page<EventoResponse> listar(Short regionId, TipoEventoEnum tipo, EstadoEventoEnum estado,
                                        Pageable pageable) {
        List<Specification<Evento>> filtros = new ArrayList<>();
        if (regionId != null) {
            filtros.add((root, query, cb) -> cb.equal(root.get("region").get("id"), regionId));
        }
        if (tipo != null) {
            filtros.add((root, query, cb) -> cb.equal(root.get("tipo"), tipo));
        }
        if (estado != null) {
            filtros.add((root, query, cb) -> cb.equal(root.get("estado"), estado));
        }
        Specification<Evento> spec = Specification.allOf(filtros);
        return eventoRepository.findAll(spec, pageable).map(this::aResponse);
    }

    public EventoResponse obtener(UUID id) {
        return aResponse(buscarEvento(id));
    }

    public Page<EventoResponse> misEventos(UUID usuarioAutenticadoId, Pageable pageable) {
        return eventoRepository.findByOrganizador_Id(usuarioAutenticadoId, pageable).map(this::aResponse);
    }

    @Transactional
    public EventoResponse crear(EventoCreateRequest request, UUID usuarioAutenticadoId) {
        Usuario organizador = usuarioRepository.findById(usuarioAutenticadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        boolean esAdmin = tieneRol(usuarioAutenticadoId, RolUsuarioEnum.admin);
        Gimnasio gimnasio = resolverGimnasioOrganizador(usuarioAutenticadoId, esAdmin, request.getGimnasioId());

        Evento evento = Evento.builder()
                .organizador(organizador)
                .gimnasio(gimnasio)
                .nombre(request.getNombre())
                .tipo(request.getTipo())
                .fecha(request.getFecha())
                .lugar(request.getLugar())
                .region(resolverRegion(request.getRegionId()))
                .cuposTotales(request.getCuposTotales())
                .reglamentoUrl(request.getReglamentoUrl())
                .afichePosterUrl(request.getAfichePosterUrl())
                .estado(EstadoEventoEnum.planificado)
                .build();
        evento = eventoRepository.save(evento);

        return aResponse(evento);
    }

    @Transactional
    public EventoResponse actualizar(UUID id, EventoUpdateRequest request, UUID usuarioAutenticadoId) {
        Evento evento = buscarEvento(id);
        boolean esDueno = evento.getOrganizador().getId().equals(usuarioAutenticadoId);
        boolean esAdmin = tieneRol(usuarioAutenticadoId, RolUsuarioEnum.admin);
        if (!esDueno && !esAdmin) {
            throw new AccesoDenegadoException("No puedes modificar un evento que no organizas");
        }

        if (request.getNombre() != null) {
            evento.setNombre(request.getNombre());
        }
        if (request.getFecha() != null) {
            evento.setFecha(request.getFecha());
        }
        if (request.getLugar() != null) {
            evento.setLugar(request.getLugar());
        }
        if (request.getRegionId() != null) {
            evento.setRegion(resolverRegion(request.getRegionId()));
        }
        if (request.getCuposTotales() != null) {
            evento.setCuposTotales(request.getCuposTotales());
        }
        if (request.getEstado() != null) {
            evento.setEstado(request.getEstado());
        }
        if (request.getReglamentoUrl() != null) {
            evento.setReglamentoUrl(request.getReglamentoUrl());
        }
        if (request.getAfichePosterUrl() != null) {
            evento.setAfichePosterUrl(request.getAfichePosterUrl());
        }

        return aResponse(eventoRepository.save(evento));
    }

    private Gimnasio resolverGimnasioOrganizador(UUID usuarioAutenticadoId, boolean esAdmin, UUID gimnasioId) {
        if (esAdmin) {
            if (gimnasioId == null) {
                return null;
            }
            return gimnasioRepository.findById(gimnasioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Gimnasio no encontrado"));
        }

        List<Gimnasio> misGimnasios = gimnasioRepository.findAllByUsuarioAdmin_Id(usuarioAutenticadoId);
        if (misGimnasios.isEmpty()) {
            throw new AccesoDenegadoException("Necesitas un gimnasio registrado para crear eventos");
        }
        if (gimnasioId != null) {
            return misGimnasios.stream()
                    .filter(g -> g.getId().equals(gimnasioId))
                    .findFirst()
                    .orElseThrow(() -> new AccesoDenegadoException("Ese gimnasio no te pertenece"));
        }
        if (misGimnasios.size() == 1) {
            return misGimnasios.get(0);
        }
        throw new OperacionInvalidaException("Tienes más de un gimnasio, indica cuál organiza este evento");
    }

    private boolean tieneRol(UUID usuarioId, RolUsuarioEnum rol) {
        return usuarioRolRepository.findByUsuario_Id(usuarioId).stream()
                .anyMatch(ur -> ur.getId().getRol() == rol);
    }

    private Evento buscarEvento(UUID id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
    }

    private Region resolverRegion(Short regionId) {
        if (regionId == null) {
            return null;
        }
        return regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Región no encontrada"));
    }

    private EventoResponse aResponse(Evento e) {
        return EventoResponse.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .tipo(e.getTipo())
                .fecha(e.getFecha())
                .lugar(e.getLugar())
                .regionId(e.getRegion() != null ? e.getRegion().getId() : null)
                .regionNombre(e.getRegion() != null ? e.getRegion().getNombre() : null)
                .cuposTotales(e.getCuposTotales())
                .estado(e.getEstado())
                .afichePosterUrl(e.getAfichePosterUrl())
                .reglamentoUrl(e.getReglamentoUrl())
                .organizadorId(e.getOrganizador().getId())
                .organizadorNombre(e.getOrganizador().getNombre())
                .gimnasioId(e.getGimnasio() != null ? e.getGimnasio().getId() : null)
                .gimnasioNombre(e.getGimnasio() != null ? e.getGimnasio().getNombre() : null)
                .build();
    }
}
