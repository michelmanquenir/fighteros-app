package com.killerdev.fighteros_app.service;

import com.killerdev.fighteros_app.dto.evento.EventoTorneoCreateRequest;
import com.killerdev.fighteros_app.dto.evento.EventoTorneoResponse;
import com.killerdev.fighteros_app.exception.AccesoDenegadoException;
import com.killerdev.fighteros_app.exception.ResourceNotFoundException;
import com.killerdev.fighteros_app.model.deportivo.CategoriaPeso;
import com.killerdev.fighteros_app.model.enums.RolUsuarioEnum;
import com.killerdev.fighteros_app.model.evento.Evento;
import com.killerdev.fighteros_app.model.evento.EventoTorneo;
import com.killerdev.fighteros_app.repository.deportivo.CategoriaPesoRepository;
import com.killerdev.fighteros_app.repository.evento.EventoInscripcionRepository;
import com.killerdev.fighteros_app.repository.evento.EventoRepository;
import com.killerdev.fighteros_app.repository.evento.EventoTorneoRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EventoTorneoService {

    private final EventoTorneoRepository torneoRepository;
    private final EventoRepository eventoRepository;
    private final CategoriaPesoRepository categoriaPesoRepository;
    private final EventoInscripcionRepository inscripcionRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public EventoTorneoService(EventoTorneoRepository torneoRepository,
                                EventoRepository eventoRepository,
                                CategoriaPesoRepository categoriaPesoRepository,
                                EventoInscripcionRepository inscripcionRepository,
                                UsuarioRolRepository usuarioRolRepository) {
        this.torneoRepository = torneoRepository;
        this.eventoRepository = eventoRepository;
        this.categoriaPesoRepository = categoriaPesoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    public List<EventoTorneoResponse> listar(UUID eventoId) {
        return torneoRepository.findByEvento_IdOrderByCreatedAtAsc(eventoId).stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional
    public EventoTorneoResponse crear(UUID eventoId, EventoTorneoCreateRequest request, UUID usuarioAutenticadoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        verificarOrganizador(evento, usuarioAutenticadoId);

        CategoriaPeso categoria = null;
        if (request.getCategoriaId() != null) {
            categoria = categoriaPesoRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría de peso no encontrada"));
        }

        EventoTorneo torneo = EventoTorneo.builder()
                .evento(evento)
                .nombre(request.getNombre())
                .categoria(categoria)
                .build();
        return aResponse(torneoRepository.save(torneo));
    }

    @Transactional
    public void eliminar(UUID eventoId, UUID torneoId, UUID usuarioAutenticadoId) {
        EventoTorneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado"));
        if (!torneo.getEvento().getId().equals(eventoId)) {
            throw new ResourceNotFoundException("Torneo no encontrado");
        }
        verificarOrganizador(torneo.getEvento(), usuarioAutenticadoId);
        torneoRepository.delete(torneo);
    }

    private void verificarOrganizador(Evento evento, UUID usuarioAutenticadoId) {
        boolean esOrganizador = evento.getOrganizador().getId().equals(usuarioAutenticadoId);
        boolean esAdmin = usuarioRolRepository.findByUsuario_Id(usuarioAutenticadoId).stream()
                .anyMatch(ur -> ur.getId().getRol() == RolUsuarioEnum.admin);
        if (!esOrganizador && !esAdmin) {
            throw new AccesoDenegadoException("Solo el organizador del evento puede gestionar sus torneos");
        }
    }

    private EventoTorneoResponse aResponse(EventoTorneo t) {
        return EventoTorneoResponse.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .categoriaId(t.getCategoria() != null ? t.getCategoria().getId() : null)
                .categoriaNombre(t.getCategoria() != null ? t.getCategoria().getNombre() : null)
                .cantidadInscritos(inscripcionRepository.countByTorneo_Id(t.getId()))
                .build();
    }
}
