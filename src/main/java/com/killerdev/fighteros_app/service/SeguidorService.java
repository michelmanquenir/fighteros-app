package com.killerdev.fighteros_app.service;

import com.killerdev.fighteros_app.dto.social.EstadoSeguimientoResponse;
import com.killerdev.fighteros_app.dto.social.SolicitudSeguimientoResponse;
import com.killerdev.fighteros_app.exception.AccesoDenegadoException;
import com.killerdev.fighteros_app.exception.OperacionInvalidaException;
import com.killerdev.fighteros_app.exception.ResourceNotFoundException;
import com.killerdev.fighteros_app.model.deportivo.Boxeador;
import com.killerdev.fighteros_app.model.enums.EstadoSeguimientoEnum;
import com.killerdev.fighteros_app.model.identidad.Seguidor;
import com.killerdev.fighteros_app.model.identidad.SeguidorId;
import com.killerdev.fighteros_app.model.identidad.Usuario;
import com.killerdev.fighteros_app.repository.deportivo.BoxeadorRepository;
import com.killerdev.fighteros_app.repository.identidad.SeguidorRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SeguidorService {

    private final SeguidorRepository seguidorRepository;
    private final UsuarioRepository usuarioRepository;
    private final BoxeadorRepository boxeadorRepository;

    public SeguidorService(SeguidorRepository seguidorRepository,
                            UsuarioRepository usuarioRepository,
                            BoxeadorRepository boxeadorRepository) {
        this.seguidorRepository = seguidorRepository;
        this.usuarioRepository = usuarioRepository;
        this.boxeadorRepository = boxeadorRepository;
    }

    @Transactional
    public EstadoSeguimientoResponse seguir(UUID seguidorId, UUID seguidoId) {
        if (seguidorId.equals(seguidoId)) {
            throw new OperacionInvalidaException("No puedes seguirte a ti mismo");
        }
        Usuario seguidor = buscarUsuario(seguidorId);
        Usuario seguido = buscarUsuario(seguidoId);

        Seguidor existente = seguidorRepository.findByIdSeguidorIdAndIdSeguidoId(seguidorId, seguidoId)
                .orElse(null);
        if (existente != null) {
            return new EstadoSeguimientoResponse(existente.getEstado().name());
        }

        EstadoSeguimientoEnum estado = esPublico(seguidoId)
                ? EstadoSeguimientoEnum.aceptado
                : EstadoSeguimientoEnum.pendiente;

        Seguidor seguidorEntity = Seguidor.builder()
                .id(SeguidorId.builder().seguidorId(seguidorId).seguidoId(seguidoId).build())
                .seguidor(seguidor)
                .seguido(seguido)
                .estado(estado)
                .build();
        seguidorRepository.save(seguidorEntity);

        return new EstadoSeguimientoResponse(estado.name());
    }

    @Transactional
    public void dejarDeSeguir(UUID seguidorId, UUID seguidoId) {
        seguidorRepository.findByIdSeguidorIdAndIdSeguidoId(seguidorId, seguidoId)
                .ifPresent(seguidorRepository::delete);
    }

    @Transactional(readOnly = true)
    public EstadoSeguimientoResponse obtenerEstado(UUID seguidorId, UUID seguidoId) {
        if (seguidorId == null || seguidorId.equals(seguidoId)) {
            return new EstadoSeguimientoResponse("ninguno");
        }
        String estado = seguidorRepository.findByIdSeguidorIdAndIdSeguidoId(seguidorId, seguidoId)
                .map(s -> s.getEstado().name())
                .orElse("ninguno");
        return new EstadoSeguimientoResponse(estado);
    }

    @Transactional(readOnly = true)
    public List<SolicitudSeguimientoResponse> listarSolicitudesPendientes(UUID usuarioId) {
        return seguidorRepository.findByIdSeguidoIdAndEstado(usuarioId, EstadoSeguimientoEnum.pendiente).stream()
                .map(s -> SolicitudSeguimientoResponse.builder()
                        .seguidorId(s.getSeguidor().getId())
                        .seguidorNombre(s.getSeguidor().getNombre())
                        .seguidorAvatarUrl(s.getSeguidor().getAvatarUrl())
                        .fecha(s.getCreatedAt())
                        .build())
                .toList();
    }

    @Transactional
    public void aceptarSolicitud(UUID usuarioAutenticadoId, UUID seguidorId) {
        Seguidor solicitud = seguidorRepository.findByIdSeguidorIdAndIdSeguidoId(seguidorId, usuarioAutenticadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        if (!solicitud.getId().getSeguidoId().equals(usuarioAutenticadoId)) {
            throw new AccesoDenegadoException("No puedes aceptar solicitudes de otra cuenta");
        }
        solicitud.setEstado(EstadoSeguimientoEnum.aceptado);
        seguidorRepository.save(solicitud);
    }

    @Transactional
    public void rechazarSolicitud(UUID usuarioAutenticadoId, UUID seguidorId) {
        Seguidor solicitud = seguidorRepository.findByIdSeguidorIdAndIdSeguidoId(seguidorId, usuarioAutenticadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        if (!solicitud.getId().getSeguidoId().equals(usuarioAutenticadoId)) {
            throw new AccesoDenegadoException("No puedes rechazar solicitudes de otra cuenta");
        }
        seguidorRepository.delete(solicitud);
    }

    private boolean esPublico(UUID usuarioId) {
        return boxeadorRepository.findById(usuarioId)
                .map(Boxeador::getPerfilPublico)
                .orElse(true);
    }

    private Usuario buscarUsuario(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
