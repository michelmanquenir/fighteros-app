package com.killerdev.fighteros_app.service;

import com.killerdev.fighteros_app.dto.evento.InvitacionCreateRequest;
import com.killerdev.fighteros_app.dto.evento.InvitacionResponse;
import com.killerdev.fighteros_app.exception.AccesoDenegadoException;
import com.killerdev.fighteros_app.exception.DuplicateResourceException;
import com.killerdev.fighteros_app.exception.OperacionInvalidaException;
import com.killerdev.fighteros_app.exception.ResourceNotFoundException;
import com.killerdev.fighteros_app.model.enums.EstadoSolicitudEnum;
import com.killerdev.fighteros_app.model.enums.ModalidadInscripcionEnum;
import com.killerdev.fighteros_app.model.enums.RolUsuarioEnum;
import com.killerdev.fighteros_app.model.evento.Evento;
import com.killerdev.fighteros_app.model.evento.EventoGimnasioInvitacion;
import com.killerdev.fighteros_app.model.identidad.Gimnasio;
import com.killerdev.fighteros_app.repository.evento.EventoGimnasioInvitacionRepository;
import com.killerdev.fighteros_app.repository.evento.EventoRepository;
import com.killerdev.fighteros_app.repository.identidad.GimnasioRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class InvitacionEventoService {

    private final EventoGimnasioInvitacionRepository invitacionRepository;
    private final EventoRepository eventoRepository;
    private final GimnasioRepository gimnasioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public InvitacionEventoService(EventoGimnasioInvitacionRepository invitacionRepository,
                                    EventoRepository eventoRepository,
                                    GimnasioRepository gimnasioRepository,
                                    UsuarioRolRepository usuarioRolRepository) {
        this.invitacionRepository = invitacionRepository;
        this.eventoRepository = eventoRepository;
        this.gimnasioRepository = gimnasioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    public List<InvitacionResponse> listarPorEvento(UUID eventoId, UUID usuarioAutenticadoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        verificarOrganizador(evento, usuarioAutenticadoId);
        return invitacionRepository.findByEvento_IdOrderByCreatedAtAsc(eventoId).stream()
                .map(this::aResponse)
                .toList();
    }

    public List<InvitacionResponse> misInvitaciones(UUID usuarioAutenticadoId) {
        List<UUID> misGimnasioIds = gimnasioRepository.findAllByUsuarioAdmin_Id(usuarioAutenticadoId).stream()
                .map(Gimnasio::getId)
                .toList();
        if (misGimnasioIds.isEmpty()) {
            return List.of();
        }
        return invitacionRepository.findByGimnasio_IdInOrderByCreatedAtDesc(misGimnasioIds).stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional
    public InvitacionResponse invitar(UUID eventoId, InvitacionCreateRequest request, UUID usuarioAutenticadoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        verificarOrganizador(evento, usuarioAutenticadoId);

        if (evento.getModalidad() != ModalidadInscripcionEnum.cerrada) {
            throw new OperacionInvalidaException("Solo los eventos de modalidad cerrada requieren invitar gimnasios");
        }

        Gimnasio gimnasio = gimnasioRepository.findById(request.getGimnasioId())
                .orElseThrow(() -> new ResourceNotFoundException("Gimnasio no encontrado"));

        if (invitacionRepository.findByEvento_IdAndGimnasio_Id(eventoId, gimnasio.getId()).isPresent()) {
            throw new DuplicateResourceException("Ese gimnasio ya fue invitado a este evento");
        }

        EventoGimnasioInvitacion invitacion = EventoGimnasioInvitacion.builder()
                .evento(evento)
                .gimnasio(gimnasio)
                .estado(EstadoSolicitudEnum.pendiente)
                .build();
        return aResponse(invitacionRepository.save(invitacion));
    }

    @Transactional
    public InvitacionResponse responder(UUID invitacionId, boolean aceptar, UUID usuarioAutenticadoId) {
        EventoGimnasioInvitacion invitacion = invitacionRepository.findById(invitacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitación no encontrada"));

        boolean esDuenoDelGimnasio = gimnasioRepository.findAllByUsuarioAdmin_Id(usuarioAutenticadoId).stream()
                .anyMatch(g -> g.getId().equals(invitacion.getGimnasio().getId()));
        if (!esDuenoDelGimnasio && !tieneRol(usuarioAutenticadoId, RolUsuarioEnum.admin)) {
            throw new AccesoDenegadoException("No puedes responder una invitación que no es de tu gimnasio");
        }

        if (invitacion.getEstado() != EstadoSolicitudEnum.pendiente) {
            throw new OperacionInvalidaException("Esta invitación ya fue respondida");
        }

        invitacion.setEstado(aceptar ? EstadoSolicitudEnum.aceptada : EstadoSolicitudEnum.rechazada);
        return aResponse(invitacionRepository.save(invitacion));
    }

    private void verificarOrganizador(Evento evento, UUID usuarioAutenticadoId) {
        boolean esOrganizador = evento.getOrganizador().getId().equals(usuarioAutenticadoId);
        if (!esOrganizador && !tieneRol(usuarioAutenticadoId, RolUsuarioEnum.admin)) {
            throw new AccesoDenegadoException("Solo el organizador del evento puede gestionar sus invitaciones");
        }
    }

    private boolean tieneRol(UUID usuarioId, RolUsuarioEnum rol) {
        return usuarioRolRepository.findByUsuario_Id(usuarioId).stream()
                .anyMatch(ur -> ur.getId().getRol() == rol);
    }

    private InvitacionResponse aResponse(EventoGimnasioInvitacion i) {
        return InvitacionResponse.builder()
                .id(i.getId())
                .eventoId(i.getEvento().getId())
                .eventoNombre(i.getEvento().getNombre())
                .gimnasioId(i.getGimnasio().getId())
                .gimnasioNombre(i.getGimnasio().getNombre())
                .estado(i.getEstado())
                .fecha(i.getCreatedAt())
                .build();
    }
}
