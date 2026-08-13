package com.killerdev.fighteros_app.service;

import com.killerdev.fighteros_app.dto.evento.EventoInscripcionCreateRequest;
import com.killerdev.fighteros_app.dto.evento.EventoInscripcionResponse;
import com.killerdev.fighteros_app.exception.AccesoDenegadoException;
import com.killerdev.fighteros_app.exception.DuplicateResourceException;
import com.killerdev.fighteros_app.exception.ResourceNotFoundException;
import com.killerdev.fighteros_app.model.deportivo.Boxeador;
import com.killerdev.fighteros_app.model.enums.RolUsuarioEnum;
import com.killerdev.fighteros_app.model.evento.Evento;
import com.killerdev.fighteros_app.model.evento.EventoInscripcion;
import com.killerdev.fighteros_app.model.identidad.Gimnasio;
import com.killerdev.fighteros_app.repository.deportivo.BoxeadorRepository;
import com.killerdev.fighteros_app.repository.evento.EventoInscripcionRepository;
import com.killerdev.fighteros_app.repository.evento.EventoRepository;
import com.killerdev.fighteros_app.repository.identidad.GimnasioRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EventoInscripcionService {

    private final EventoInscripcionRepository inscripcionRepository;
    private final EventoRepository eventoRepository;
    private final BoxeadorRepository boxeadorRepository;
    private final GimnasioRepository gimnasioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public EventoInscripcionService(EventoInscripcionRepository inscripcionRepository,
                                     EventoRepository eventoRepository,
                                     BoxeadorRepository boxeadorRepository,
                                     GimnasioRepository gimnasioRepository,
                                     UsuarioRolRepository usuarioRolRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.eventoRepository = eventoRepository;
        this.boxeadorRepository = boxeadorRepository;
        this.gimnasioRepository = gimnasioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    public List<EventoInscripcionResponse> listar(UUID eventoId) {
        return inscripcionRepository.findByEvento_IdOrderByCreatedAtAsc(eventoId).stream()
                .map(this::aResponse)
                .toList();
    }

    @Transactional
    public EventoInscripcionResponse inscribir(UUID eventoId, EventoInscripcionCreateRequest request,
                                                UUID usuarioAutenticadoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        Boxeador boxeador = boxeadorRepository.findById(request.getBoxeadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Boxeador no encontrado"));

        Gimnasio gimnasioDelBoxeador = boxeador.getGimnasio();
        if (gimnasioDelBoxeador == null) {
            throw new AccesoDenegadoException("Este boxeador no pertenece a ningún gimnasio");
        }

        boolean esOrganizador = evento.getOrganizador().getId().equals(usuarioAutenticadoId);
        boolean esDueñoDelGimnasioDelBoxeador = esDueñoDeGimnasio(usuarioAutenticadoId, gimnasioDelBoxeador.getId());

        // El organizador puede armar el cartel completo con boxeadores de
        // cualquier gimnasio (veladas grandes); un dueño de gimnasio siempre
        // puede inscribir a los suyos, sea o no el organizador del evento.
        if (!esAdmin(usuarioAutenticadoId) && !esOrganizador && !esDueñoDelGimnasioDelBoxeador) {
            throw new AccesoDenegadoException(
                    "Solo el organizador del evento o el dueño del gimnasio del boxeador pueden inscribirlo");
        }

        if (inscripcionRepository.existsByEvento_IdAndBoxeador_Id(eventoId, boxeador.getId())) {
            throw new DuplicateResourceException("Este boxeador ya está inscrito en el evento");
        }

        EventoInscripcion inscripcion = EventoInscripcion.builder()
                .evento(evento)
                .boxeador(boxeador)
                .gimnasio(gimnasioDelBoxeador)
                .build();
        return aResponse(inscripcionRepository.save(inscripcion));
    }

    @Transactional
    public void retirar(UUID eventoId, UUID boxeadorId, UUID usuarioAutenticadoId) {
        EventoInscripcion inscripcion = inscripcionRepository.findByEvento_IdAndBoxeador_Id(eventoId, boxeadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada"));

        boolean esOrganizador = inscripcion.getEvento().getOrganizador().getId().equals(usuarioAutenticadoId);
        boolean esDueñoDelGimnasio = esDueñoDeGimnasio(usuarioAutenticadoId, inscripcion.getGimnasio().getId());

        if (!esAdmin(usuarioAutenticadoId) && !esOrganizador && !esDueñoDelGimnasio) {
            throw new AccesoDenegadoException("No puedes retirar esta inscripción");
        }

        inscripcionRepository.delete(inscripcion);
    }

    private boolean esAdmin(UUID usuarioId) {
        return usuarioRolRepository.findByUsuario_Id(usuarioId).stream()
                .anyMatch(ur -> ur.getId().getRol() == RolUsuarioEnum.admin);
    }

    private boolean esDueñoDeGimnasio(UUID usuarioId, UUID gimnasioId) {
        return gimnasioRepository.findAllByUsuarioAdmin_Id(usuarioId).stream()
                .anyMatch(g -> g.getId().equals(gimnasioId));
    }

    private EventoInscripcionResponse aResponse(EventoInscripcion i) {
        Boxeador b = i.getBoxeador();
        return EventoInscripcionResponse.builder()
                .boxeadorId(b.getId())
                .boxeadorNombre(b.getUsuario().getNombre())
                .boxeadorFotoUrl(b.getFotoUrl())
                .pesoActual(b.getPesoActual())
                .categoriaNombre(b.getCategoria() != null ? b.getCategoria().getNombre() : null)
                .gimnasioId(i.getGimnasio().getId())
                .gimnasioNombre(i.getGimnasio().getNombre())
                .fecha(i.getCreatedAt())
                .build();
    }
}
