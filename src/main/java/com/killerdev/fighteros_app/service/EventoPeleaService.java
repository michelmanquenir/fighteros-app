package com.killerdev.fighteros_app.service;

import com.killerdev.fighteros_app.dto.boxeador.CompatibilidadResponse;
import com.killerdev.fighteros_app.dto.evento.EventoPeleaCreateRequest;
import com.killerdev.fighteros_app.dto.evento.EventoPeleaResponse;
import com.killerdev.fighteros_app.dto.evento.EventoPeleaResultadoRequest;
import com.killerdev.fighteros_app.dto.evento.PeleaPendienteResponse;
import com.killerdev.fighteros_app.exception.AccesoDenegadoException;
import com.killerdev.fighteros_app.exception.OperacionInvalidaException;
import com.killerdev.fighteros_app.exception.ResourceNotFoundException;
import com.killerdev.fighteros_app.model.deportivo.Boxeador;
import com.killerdev.fighteros_app.model.enums.EstadoPeleaEnum;
import com.killerdev.fighteros_app.model.enums.EstadoSolicitudEnum;
import com.killerdev.fighteros_app.model.enums.EstadoValidacionEnum;
import com.killerdev.fighteros_app.model.enums.ResultadoPeleaEnum;
import com.killerdev.fighteros_app.model.enums.RolUsuarioEnum;
import com.killerdev.fighteros_app.model.evento.Evento;
import com.killerdev.fighteros_app.model.evento.EventoInscripcion;
import com.killerdev.fighteros_app.model.evento.EventoTorneo;
import com.killerdev.fighteros_app.model.identidad.Gimnasio;
import com.killerdev.fighteros_app.model.pelea.Pelea;
import com.killerdev.fighteros_app.repository.deportivo.BoxeadorRepository;
import com.killerdev.fighteros_app.repository.evento.EventoInscripcionRepository;
import com.killerdev.fighteros_app.repository.evento.EventoRepository;
import com.killerdev.fighteros_app.repository.evento.EventoTorneoRepository;
import com.killerdev.fighteros_app.repository.identidad.GimnasioRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRolRepository;
import com.killerdev.fighteros_app.repository.pelea.PeleaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EventoPeleaService {

    private static final int MAX_SUGERENCIAS = 3;

    private final PeleaRepository peleaRepository;
    private final EventoRepository eventoRepository;
    private final BoxeadorRepository boxeadorRepository;
    private final EventoTorneoRepository torneoRepository;
    private final EventoInscripcionRepository inscripcionRepository;
    private final GimnasioRepository gimnasioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final BoxeadorService boxeadorService;

    public EventoPeleaService(PeleaRepository peleaRepository,
                               EventoRepository eventoRepository,
                               BoxeadorRepository boxeadorRepository,
                               EventoTorneoRepository torneoRepository,
                               EventoInscripcionRepository inscripcionRepository,
                               GimnasioRepository gimnasioRepository,
                               UsuarioRolRepository usuarioRolRepository,
                               BoxeadorService boxeadorService) {
        this.peleaRepository = peleaRepository;
        this.eventoRepository = eventoRepository;
        this.boxeadorRepository = boxeadorRepository;
        this.torneoRepository = torneoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.gimnasioRepository = gimnasioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.boxeadorService = boxeadorService;
    }

    public List<PeleaPendienteResponse> misPendientes(UUID usuarioAutenticadoId) {
        List<UUID> misGimnasioIds = gimnasioRepository.findAllByUsuarioAdmin_Id(usuarioAutenticadoId).stream()
                .map(Gimnasio::getId)
                .toList();
        if (misGimnasioIds.isEmpty()) {
            return List.of();
        }
        Set<UUID> misGimnasioIdsSet = new HashSet<>(misGimnasioIds);
        return peleaRepository.findByBoxeadorA_Gimnasio_IdInOrBoxeadorB_Gimnasio_IdIn(misGimnasioIds, misGimnasioIds)
                .stream()
                .filter(p -> esPendienteParaMisGimnasios(p, misGimnasioIdsSet))
                .map(this::aPendienteResponse)
                .toList();
    }

    private boolean esPendienteParaMisGimnasios(Pelea p, Set<UUID> misGimnasioIds) {
        Gimnasio gimnasioA = p.getBoxeadorA().getGimnasio();
        Gimnasio gimnasioB = p.getBoxeadorB().getGimnasio();
        boolean pendienteA = gimnasioA != null && misGimnasioIds.contains(gimnasioA.getId())
                && p.getConfirmacionGimnasioA() == EstadoSolicitudEnum.pendiente;
        boolean pendienteB = gimnasioB != null && misGimnasioIds.contains(gimnasioB.getId())
                && p.getConfirmacionGimnasioB() == EstadoSolicitudEnum.pendiente;
        return pendienteA || pendienteB;
    }

    private PeleaPendienteResponse aPendienteResponse(Pelea p) {
        return PeleaPendienteResponse.builder()
                .peleaId(p.getId())
                .eventoId(p.getEvento().getId())
                .eventoNombre(p.getEvento().getNombre())
                .boxeadorANombre(p.getBoxeadorA().getUsuario().getNombre())
                .boxeadorAFotoUrl(p.getBoxeadorA().getFotoUrl())
                .boxeadorBNombre(p.getBoxeadorB().getUsuario().getNombre())
                .boxeadorBFotoUrl(p.getBoxeadorB().getFotoUrl())
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .build();
    }

    public List<EventoPeleaResponse> listar(UUID eventoId) {
        return peleaRepository.findByEvento_IdOrderByRondaAscCreatedAtAsc(eventoId).stream()
                .map(this::aResponse)
                .toList();
    }

    public List<CompatibilidadResponse> sugerirRivales(UUID eventoId, UUID boxeadorId) {
        eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        if (!inscripcionRepository.existsByEvento_IdAndBoxeador_Id(eventoId, boxeadorId)) {
            throw new OperacionInvalidaException("Ese boxeador no está inscrito en este evento");
        }

        Set<UUID> yaEmparejados = obtenerBoxeadoresYaEmparejados(eventoId);
        List<UUID> candidatos = inscripcionRepository.findByEvento_IdOrderByCreatedAtAsc(eventoId).stream()
                .map(i -> i.getBoxeador().getId())
                .filter(id -> !id.equals(boxeadorId) && !yaEmparejados.contains(id))
                .toList();

        return candidatos.stream()
                .map(candidatoId -> boxeadorService.compararBoxeadores(boxeadorId, candidatoId))
                .sorted(Comparator.comparingInt(CompatibilidadResponse::getPuntajeGeneral).reversed())
                .limit(MAX_SUGERENCIAS)
                .toList();
    }

    @Transactional
    public List<EventoPeleaResponse> generarAutomatico(UUID eventoId, UUID torneoId, UUID usuarioAutenticadoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado"));
        verificarOrganizador(evento, usuarioAutenticadoId);

        EventoTorneo torneo = null;
        if (torneoId != null) {
            torneo = torneoRepository.findById(torneoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado"));
            if (!torneo.getEvento().getId().equals(eventoId)) {
                throw new OperacionInvalidaException("Ese torneo no pertenece a este evento");
            }
        }

        Set<UUID> yaEmparejados = obtenerBoxeadoresYaEmparejados(eventoId);
        List<EventoInscripcion> inscripciones = inscripcionRepository.findByEvento_IdOrderByCreatedAtAsc(eventoId);
        List<UUID> disponibles = new ArrayList<>(inscripciones.stream()
                .filter(i -> torneoId == null || (i.getTorneo() != null && i.getTorneo().getId().equals(torneoId)))
                .map(i -> i.getBoxeador().getId())
                .filter(id -> !yaEmparejados.contains(id))
                .distinct()
                .toList());

        record ParCandidato(UUID idA, UUID idB, int puntaje) {
        }
        List<ParCandidato> pares = new ArrayList<>();
        for (int i = 0; i < disponibles.size(); i++) {
            for (int j = i + 1; j < disponibles.size(); j++) {
                UUID idA = disponibles.get(i);
                UUID idB = disponibles.get(j);
                int puntaje = boxeadorService.compararBoxeadores(idA, idB).getPuntajeGeneral();
                pares.add(new ParCandidato(idA, idB, puntaje));
            }
        }
        pares.sort(Comparator.comparingInt(ParCandidato::puntaje).reversed());

        Set<UUID> usados = new HashSet<>();
        List<Pelea> creadas = new ArrayList<>();
        for (ParCandidato par : pares) {
            if (usados.contains(par.idA()) || usados.contains(par.idB())) {
                continue;
            }
            Boxeador boxeadorA = boxeadorRepository.findById(par.idA())
                    .orElseThrow(() -> new ResourceNotFoundException("Boxeador no encontrado"));
            Boxeador boxeadorB = boxeadorRepository.findById(par.idB())
                    .orElseThrow(() -> new ResourceNotFoundException("Boxeador no encontrado"));

            Pelea pelea = Pelea.builder()
                    .evento(evento)
                    .boxeadorA(boxeadorA)
                    .boxeadorB(boxeadorB)
                    .categoria(boxeadorA.getCategoria())
                    .torneo(torneo)
                    .ronda((short) 1)
                    .estado(EstadoPeleaEnum.programada)
                    .estadoValidacion(EstadoValidacionEnum.pendiente)
                    .confirmacionGimnasioA(confirmacionInicial(boxeadorA.getGimnasio(), usuarioAutenticadoId))
                    .confirmacionGimnasioB(confirmacionInicial(boxeadorB.getGimnasio(), usuarioAutenticadoId))
                    .build();
            creadas.add(peleaRepository.save(pelea));
            usados.add(par.idA());
            usados.add(par.idB());
        }

        return creadas.stream().map(this::aResponse).toList();
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
                .confirmacionGimnasioA(confirmacionInicial(boxeadorA.getGimnasio(), usuarioAutenticadoId))
                .confirmacionGimnasioB(confirmacionInicial(boxeadorB.getGimnasio(), usuarioAutenticadoId))
                .build();
        return aResponse(peleaRepository.save(pelea));
    }

    @Transactional
    public EventoPeleaResponse registrarResultado(UUID eventoId, UUID peleaId, EventoPeleaResultadoRequest request,
                                                    UUID usuarioAutenticadoId) {
        Pelea pelea = peleaRepository.findById(peleaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pelea no encontrada"));
        if (!pelea.getEvento().getId().equals(eventoId)) {
            throw new ResourceNotFoundException("Pelea no encontrada");
        }
        verificarOrganizador(pelea.getEvento(), usuarioAutenticadoId);

        UUID ganadorId = request.getGanadorId();
        if (ganadorId == null) {
            pelea.setResultado(null);
            pelea.setEstado(EstadoPeleaEnum.programada);
        } else if (ganadorId.equals(pelea.getBoxeadorA().getId())) {
            pelea.setResultado(ResultadoPeleaEnum.victoria_a);
            pelea.setEstado(EstadoPeleaEnum.realizada);
        } else if (ganadorId.equals(pelea.getBoxeadorB().getId())) {
            pelea.setResultado(ResultadoPeleaEnum.victoria_b);
            pelea.setEstado(EstadoPeleaEnum.realizada);
        } else {
            throw new OperacionInvalidaException("El ganador debe ser uno de los dos peleadores de esta pelea");
        }

        return aResponse(peleaRepository.save(pelea));
    }

    @Transactional
    public EventoPeleaResponse registrarConfirmacion(UUID eventoId, UUID peleaId, boolean aceptar,
                                                       UUID usuarioAutenticadoId) {
        Pelea pelea = peleaRepository.findById(peleaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pelea no encontrada"));
        if (!pelea.getEvento().getId().equals(eventoId)) {
            throw new ResourceNotFoundException("Pelea no encontrada");
        }

        boolean esAdmin = esAdmin(usuarioAutenticadoId);
        boolean esDuenoA = esDueñoDeGimnasio(usuarioAutenticadoId, pelea.getBoxeadorA().getGimnasio());
        boolean esDuenoB = esDueñoDeGimnasio(usuarioAutenticadoId, pelea.getBoxeadorB().getGimnasio());

        if (!esAdmin && !esDuenoA && !esDuenoB) {
            throw new AccesoDenegadoException("Solo el gimnasio de uno de los peleadores puede confirmar esta pelea");
        }

        EstadoSolicitudEnum decision = aceptar ? EstadoSolicitudEnum.aceptada : EstadoSolicitudEnum.rechazada;
        if (esDuenoA || esAdmin) {
            pelea.setConfirmacionGimnasioA(decision);
        }
        if (esDuenoB || esAdmin) {
            pelea.setConfirmacionGimnasioB(decision);
        }

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

    private Set<UUID> obtenerBoxeadoresYaEmparejados(UUID eventoId) {
        Set<UUID> ids = new HashSet<>();
        peleaRepository.findByEvento_IdOrderByRondaAscCreatedAtAsc(eventoId).forEach(p -> {
            ids.add(p.getBoxeadorA().getId());
            ids.add(p.getBoxeadorB().getId());
        });
        return ids;
    }

    private EstadoSolicitudEnum confirmacionInicial(Gimnasio gimnasio, UUID usuarioAutenticadoId) {
        if (gimnasio != null && esDueñoDeGimnasio(usuarioAutenticadoId, gimnasio)) {
            return EstadoSolicitudEnum.aceptada;
        }
        return EstadoSolicitudEnum.pendiente;
    }

    private boolean esDueñoDeGimnasio(UUID usuarioId, Gimnasio gimnasio) {
        if (gimnasio == null) {
            return false;
        }
        return gimnasioRepository.findAllByUsuarioAdmin_Id(usuarioId).stream()
                .anyMatch(g -> g.getId().equals(gimnasio.getId()));
    }

    private void verificarOrganizador(Evento evento, UUID usuarioAutenticadoId) {
        boolean esOrganizador = evento.getOrganizador().getId().equals(usuarioAutenticadoId);
        if (!esOrganizador && !esAdmin(usuarioAutenticadoId)) {
            throw new AccesoDenegadoException("Solo el organizador del evento puede pactar sus peleas");
        }
    }

    private boolean esAdmin(UUID usuarioAutenticadoId) {
        return usuarioRolRepository.findByUsuario_Id(usuarioAutenticadoId).stream()
                .anyMatch(ur -> ur.getId().getRol() == RolUsuarioEnum.admin);
    }

    private EventoPeleaResponse aResponse(Pelea p) {
        EstadoSolicitudEnum confA = p.getConfirmacionGimnasioA();
        EstadoSolicitudEnum confB = p.getConfirmacionGimnasioB();
        EstadoSolicitudEnum estadoConfirmacion;
        if (confA == EstadoSolicitudEnum.rechazada || confB == EstadoSolicitudEnum.rechazada) {
            estadoConfirmacion = EstadoSolicitudEnum.rechazada;
        } else if (confA == EstadoSolicitudEnum.aceptada && confB == EstadoSolicitudEnum.aceptada) {
            estadoConfirmacion = EstadoSolicitudEnum.aceptada;
        } else {
            estadoConfirmacion = EstadoSolicitudEnum.pendiente;
        }

        Gimnasio gimnasioA = p.getBoxeadorA().getGimnasio();
        Gimnasio gimnasioB = p.getBoxeadorB().getGimnasio();

        return EventoPeleaResponse.builder()
                .id(p.getId())
                .boxeadorAId(p.getBoxeadorA().getId())
                .boxeadorANombre(p.getBoxeadorA().getUsuario().getNombre())
                .boxeadorAFotoUrl(p.getBoxeadorA().getFotoUrl())
                .gimnasioAId(gimnasioA != null ? gimnasioA.getId() : null)
                .gimnasioANombre(gimnasioA != null ? gimnasioA.getNombre() : null)
                .boxeadorBId(p.getBoxeadorB().getId())
                .boxeadorBNombre(p.getBoxeadorB().getUsuario().getNombre())
                .boxeadorBFotoUrl(p.getBoxeadorB().getFotoUrl())
                .gimnasioBId(gimnasioB != null ? gimnasioB.getId() : null)
                .gimnasioBNombre(gimnasioB != null ? gimnasioB.getNombre() : null)
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .torneoId(p.getTorneo() != null ? p.getTorneo().getId() : null)
                .torneoNombre(p.getTorneo() != null ? p.getTorneo().getNombre() : null)
                .ronda(p.getRonda())
                .estado(p.getEstado())
                .ganadorId(resolverGanadorId(p))
                .confirmacionGimnasioA(confA)
                .confirmacionGimnasioB(confB)
                .estadoConfirmacion(estadoConfirmacion)
                .build();
    }

    private UUID resolverGanadorId(Pelea p) {
        if (p.getResultado() == ResultadoPeleaEnum.victoria_a) {
            return p.getBoxeadorA().getId();
        }
        if (p.getResultado() == ResultadoPeleaEnum.victoria_b) {
            return p.getBoxeadorB().getId();
        }
        return null;
    }
}
