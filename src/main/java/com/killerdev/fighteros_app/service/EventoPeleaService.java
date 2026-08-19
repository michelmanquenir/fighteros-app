package com.killerdev.fighteros_app.service;

import com.killerdev.fighteros_app.dto.evento.EventoPeleaCreateRequest;
import com.killerdev.fighteros_app.dto.evento.EventoPeleaResponse;
import com.killerdev.fighteros_app.exception.AccesoDenegadoException;
import com.killerdev.fighteros_app.exception.OperacionInvalidaException;
import com.killerdev.fighteros_app.exception.ResourceNotFoundException;
import com.killerdev.fighteros_app.model.deportivo.Boxeador;
import com.killerdev.fighteros_app.model.enums.EstadoPeleaEnum;
import com.killerdev.fighteros_app.model.enums.EstadoValidacionEnum;
import com.killerdev.fighteros_app.model.enums.RolUsuarioEnum;
import com.killerdev.fighteros_app.model.evento.Evento;
import com.killerdev.fighteros_app.model.evento.EventoTorneo;
import com.killerdev.fighteros_app.model.pelea.Pelea;
import com.killerdev.fighteros_app.repository.deportivo.BoxeadorRepository;
import com.killerdev.fighteros_app.repository.evento.EventoRepository;
import com.killerdev.fighteros_app.repository.evento.EventoTorneoRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRolRepository;
import com.killerdev.fighteros_app.repository.pelea.PeleaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EventoPeleaService {

    private final PeleaRepository peleaRepository;
    private final EventoRepository eventoRepository;
    private final BoxeadorRepository boxeadorRepository;
    private final EventoTorneoRepository torneoRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public EventoPeleaService(PeleaRepository peleaRepository,
                               EventoRepository eventoRepository,
                               BoxeadorRepository boxeadorRepository,
                               EventoTorneoRepository torneoRepository,
                               UsuarioRolRepository usuarioRolRepository) {
        this.peleaRepository = peleaRepository;
        this.eventoRepository = eventoRepository;
        this.boxeadorRepository = boxeadorRepository;
        this.torneoRepository = torneoRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    public List<EventoPeleaResponse> listar(UUID eventoId) {
        return peleaRepository.findByEvento_IdOrderByRondaAscCreatedAtAsc(eventoId).stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional
    public EventoPeleaResponse crear(UUID eventoId, EventoPeleaCreateRequest request, UUID usuarioAutenticadoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        verificarOrganizador(evento, usuarioAutenticadoId);

        if (request.getBoxeadorAId().equals(request.getBoxeadorBId())) {
            throw new OperacionInvalidaException("Un boxeador no puede pelear contra sí mismo");
        }

        Boxeador boxeadorA = boxeadorRepository.findById(request.getBoxeadorAId())
                .orElseThrow(() -> new ResourceNotFoundException("Boxeador no encontrado"));
        Boxeador boxeadorB = boxeadorRepository.findById(request.getBoxeadorBId())
                .orElseThrow(() -> new ResourceNotFoundException("Boxeador no encontrado"));

        EventoTorneo torneo = null;
        if (request.getTorneoId() != null) {
            torneo = torneoRepository.findById(request.getTorneoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado"));
            if (!torneo.getEvento().getId().equals(eventoId)) {
                throw new OperacionInvalidaException("Ese torneo no pertenece a este evento");
            }
        }

        Pelea pelea = Pelea.builder()
                .evento(evento)
                .boxeadorA(boxeadorA)
                .boxeadorB(boxeadorB)
                .categoria(boxeadorA.getCategoria())
                .torneo(torneo)
                .ronda(request.getRonda() != null ? request.getRonda() : (short) 1)
                .estado(EstadoPeleaEnum.programada)
                .estadoValidacion(EstadoValidacionEnum.pendiente)
                .build();
        return aResponse(peleaRepository.save(pelea));
    }

    @Transactional
    public void eliminar(UUID eventoId, UUID peleaId, UUID usuarioAutenticadoId) {
        Pelea pelea = peleaRepository.findById(peleaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pelea no encontrada"));
        if (!pelea.getEvento().getId().equals(eventoId)) {
            throw new ResourceNotFoundException("Pelea no encontrada");
        }
        verificarOrganizador(pelea.getEvento(), usuarioAutenticadoId);
        peleaRepository.delete(pelea);
    }

    private void verificarOrganizador(Evento evento, UUID usuarioAutenticadoId) {
        boolean esOrganizador = evento.getOrganizador().getId().equals(usuarioAutenticadoId);
        boolean esAdmin = usuarioRolRepository.findByUsuario_Id(usuarioAutenticadoId).stream()
                .anyMatch(ur -> ur.getId().getRol() == RolUsuarioEnum.admin);
        if (!esOrganizador && !esAdmin) {
            throw new AccesoDenegadoException("Solo el organizador del evento puede pactar sus peleas");
        }
    }

    private EventoPeleaResponse aResponse(Pelea p) {
        return EventoPeleaResponse.builder()
                .id(p.getId())
                .boxeadorAId(p.getBoxeadorA().getId())
                .boxeadorANombre(p.getBoxeadorA().getUsuario().getNombre())
                .boxeadorAFotoUrl(p.getBoxeadorA().getFotoUrl())
                .boxeadorBId(p.getBoxeadorB().getId())
                .boxeadorBNombre(p.getBoxeadorB().getUsuario().getNombre())
                .boxeadorBFotoUrl(p.getBoxeadorB().getFotoUrl())
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .torneoId(p.getTorneo() != null ? p.getTorneo().getId() : null)
                .torneoNombre(p.getTorneo() != null ? p.getTorneo().getNombre() : null)
                .ronda(p.getRonda())
                .estado(p.getEstado())
                .build();
    }
}
