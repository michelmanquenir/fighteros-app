package com.killerdev.fighteros_app.service;

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
import com.killerdev.fighteros_app.exception.AccesoDenegadoException;
import com.killerdev.fighteros_app.exception.ResourceNotFoundException;
import com.killerdev.fighteros_app.model.deportivo.Boxeador;
import com.killerdev.fighteros_app.model.deportivo.BoxeadorEstadisticas;
import com.killerdev.fighteros_app.model.deportivo.BoxeadorPesoPactado;
import com.killerdev.fighteros_app.model.deportivo.CategoriaPeso;
import com.killerdev.fighteros_app.model.deportivo.Entrenador;
import com.killerdev.fighteros_app.model.enums.EstadoDeportivoEnum;
import com.killerdev.fighteros_app.model.enums.EstadoPeleaEnum;
import com.killerdev.fighteros_app.model.enums.TipoMultimediaEnum;
import com.killerdev.fighteros_app.model.identidad.Gimnasio;
import com.killerdev.fighteros_app.model.identidad.Region;
import com.killerdev.fighteros_app.model.multimedia.Multimedia;
import com.killerdev.fighteros_app.model.pelea.Pelea;
import com.killerdev.fighteros_app.repository.deportivo.BoxeadorCampeonatoRepository;
import com.killerdev.fighteros_app.repository.deportivo.BoxeadorCopaRepository;
import com.killerdev.fighteros_app.repository.deportivo.BoxeadorEstadisticasRepository;
import com.killerdev.fighteros_app.repository.deportivo.BoxeadorMedallaRepository;
import com.killerdev.fighteros_app.repository.deportivo.BoxeadorPatrocinioRepository;
import com.killerdev.fighteros_app.repository.deportivo.BoxeadorPesoPactadoRepository;
import com.killerdev.fighteros_app.repository.deportivo.BoxeadorRepository;
import com.killerdev.fighteros_app.repository.deportivo.CategoriaPesoRepository;
import com.killerdev.fighteros_app.repository.deportivo.EntrenadorRepository;
import com.killerdev.fighteros_app.repository.identidad.GimnasioRepository;
import com.killerdev.fighteros_app.repository.identidad.RegionRepository;
import com.killerdev.fighteros_app.repository.multimedia.MultimediaRepository;
import com.killerdev.fighteros_app.repository.pelea.PeleaRepository;
import com.killerdev.fighteros_app.storage.StorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BoxeadorService {

    private final BoxeadorRepository boxeadorRepository;
    private final CategoriaPesoRepository categoriaPesoRepository;
    private final GimnasioRepository gimnasioRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final RegionRepository regionRepository;
    private final BoxeadorEstadisticasRepository boxeadorEstadisticasRepository;
    private final BoxeadorMedallaRepository boxeadorMedallaRepository;
    private final BoxeadorCopaRepository boxeadorCopaRepository;
    private final BoxeadorCampeonatoRepository boxeadorCampeonatoRepository;
    private final BoxeadorPatrocinioRepository boxeadorPatrocinioRepository;
    private final BoxeadorPesoPactadoRepository boxeadorPesoPactadoRepository;
    private final PeleaRepository peleaRepository;
    private final MultimediaRepository multimediaRepository;
    private final StorageService storageService;

    public BoxeadorService(BoxeadorRepository boxeadorRepository,
                            CategoriaPesoRepository categoriaPesoRepository,
                            GimnasioRepository gimnasioRepository,
                            EntrenadorRepository entrenadorRepository,
                            RegionRepository regionRepository,
                            BoxeadorEstadisticasRepository boxeadorEstadisticasRepository,
                            BoxeadorMedallaRepository boxeadorMedallaRepository,
                            BoxeadorCopaRepository boxeadorCopaRepository,
                            BoxeadorCampeonatoRepository boxeadorCampeonatoRepository,
                            BoxeadorPatrocinioRepository boxeadorPatrocinioRepository,
                            BoxeadorPesoPactadoRepository boxeadorPesoPactadoRepository,
                            PeleaRepository peleaRepository,
                            MultimediaRepository multimediaRepository,
                            StorageService storageService) {
        this.boxeadorRepository = boxeadorRepository;
        this.categoriaPesoRepository = categoriaPesoRepository;
        this.gimnasioRepository = gimnasioRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.regionRepository = regionRepository;
        this.boxeadorEstadisticasRepository = boxeadorEstadisticasRepository;
        this.boxeadorMedallaRepository = boxeadorMedallaRepository;
        this.boxeadorCopaRepository = boxeadorCopaRepository;
        this.boxeadorCampeonatoRepository = boxeadorCampeonatoRepository;
        this.boxeadorPatrocinioRepository = boxeadorPatrocinioRepository;
        this.boxeadorPesoPactadoRepository = boxeadorPesoPactadoRepository;
        this.peleaRepository = peleaRepository;
        this.multimediaRepository = multimediaRepository;
        this.storageService = storageService;
    }

    public Page<BoxeadorResumenResponse> listar(UUID gimnasioId, Short regionId, UUID categoriaId,
                                                 EstadoDeportivoEnum estado, Pageable pageable) {
        return boxeadorRepository.buscar(gimnasioId, regionId, categoriaId, estado, pageable)
                .map(this::aResumen);
    }

    public BoxeadorPerfilResponse obtenerPerfil(UUID id) {
        return aPerfil(buscarBoxeador(id));
    }

    @Transactional
    public BoxeadorPerfilResponse actualizar(UUID id, BoxeadorUpdateRequest request, UUID usuarioAutenticadoId) {
        Boxeador boxeador = buscarBoxeador(id);
        verificarPropietario(id, usuarioAutenticadoId);

        if (request.getPesoActual() != null) {
            boxeador.setPesoActual(request.getPesoActual());
        }
        if (request.getPesoHabitual() != null) {
            boxeador.setPesoHabitual(request.getPesoHabitual());
        }
        if (request.getCategoriaId() != null) {
            boxeador.setCategoria(resolverCategoria(request.getCategoriaId()));
        }
        if (request.getGimnasioId() != null) {
            boxeador.setGimnasio(resolverGimnasio(request.getGimnasioId()));
        }
        if (request.getEntrenadorId() != null) {
            boxeador.setEntrenador(resolverEntrenador(request.getEntrenadorId()));
        }
        if (request.getRegionId() != null) {
            boxeador.setRegion(resolverRegion(request.getRegionId()));
        }
        if (request.getEstadoDeportivo() != null) {
            boxeador.setEstadoDeportivo(request.getEstadoDeportivo());
        }

        return aPerfil(boxeadorRepository.save(boxeador));
    }

    public EstadisticasResponse obtenerEstadisticas(UUID id) {
        buscarBoxeador(id);
        return boxeadorEstadisticasRepository.findById(id)
                .map(this::aEstadisticas)
                .orElseGet(() -> EstadisticasResponse.builder()
                        .peleasTotales(0L).victorias(0L).derrotas(0L).empates(0L)
                        .victoriasKo(0L).victoriasDecision(0L).build());
    }

    public List<PeleaResumenResponse> obtenerHistorial(UUID id) {
        buscarBoxeador(id);
        return peleaRepository.buscarPorBoxeadorYEstado(id, EstadoPeleaEnum.realizada).stream()
                .map(p -> aPeleaResumen(p, id))
                .toList();
    }

    public List<PeleaResumenResponse> obtenerProximasPeleas(UUID id) {
        buscarBoxeador(id);
        return peleaRepository.buscarPorBoxeadorYEstado(id, EstadoPeleaEnum.programada).stream()
                .map(p -> aPeleaResumen(p, id))
                .toList();
    }

    public List<MedallaResponse> obtenerMedallas(UUID id) {
        buscarBoxeador(id);
        return boxeadorMedallaRepository.findByBoxeador_IdOrderByFechaDesc(id).stream()
                .map(m -> MedallaResponse.builder()
                        .id(m.getId()).tipo(m.getTipo()).nombre(m.getNombre())
                        .fecha(m.getFecha()).descripcion(m.getDescripcion())
                        .build())
                .toList();
    }

    public List<CopaResponse> obtenerCopas(UUID id) {
        buscarBoxeador(id);
        return boxeadorCopaRepository.findByBoxeador_IdOrderByFechaDesc(id).stream()
                .map(c -> CopaResponse.builder().id(c.getId()).nombre(c.getNombre()).fecha(c.getFecha()).build())
                .toList();
    }

    public List<CampeonatoResponse> obtenerCampeonatos(UUID id) {
        buscarBoxeador(id);
        return boxeadorCampeonatoRepository.findByBoxeador_IdOrderByFechaObtenidoDesc(id).stream()
                .map(c -> CampeonatoResponse.builder()
                        .id(c.getId())
                        .ligaNombre(c.getLiga() != null ? c.getLiga().getNombre() : null)
                        .categoriaNombre(c.getCategoria() != null ? c.getCategoria().getNombre() : null)
                        .titulo(c.getTitulo())
                        .fechaObtenido(c.getFechaObtenido())
                        .vigente(c.getVigente())
                        .build())
                .toList();
    }

    public List<PatrocinioResponse> obtenerPatrocinios(UUID id) {
        buscarBoxeador(id);
        return boxeadorPatrocinioRepository.findByBoxeador_IdOrderByFechaInicioDesc(id).stream()
                .map(p -> PatrocinioResponse.builder()
                        .id(p.getId()).patrocinadorNombre(p.getPatrocinadorNombre())
                        .descripcion(p.getDescripcion())
                        .fechaInicio(p.getFechaInicio()).fechaFin(p.getFechaFin())
                        .build())
                .toList();
    }

    public List<MultimediaResponse> obtenerMultimedia(UUID id) {
        buscarBoxeador(id);
        return multimediaRepository.findByBoxeador_IdOrderByCreatedAtDesc(id).stream()
                .map(m -> MultimediaResponse.builder()
                        .id(m.getId()).tipo(m.getTipo()).url(m.getUrl()).esOficial(m.getEsOficial())
                        .build())
                .toList();
    }

    @Transactional
    public BoxeadorPerfilResponse subirFoto(UUID id, MultipartFile archivo, UUID usuarioAutenticadoId) {
        Boxeador boxeador = buscarBoxeador(id);
        verificarPropietario(id, usuarioAutenticadoId);
        String url = storageService.subirArchivo(archivo, "boxeadores/" + id + "/foto");
        boxeador.setFotoUrl(url);
        return aPerfil(boxeadorRepository.save(boxeador));
    }

    @Transactional
    public MultimediaResponse agregarMultimedia(UUID id, MultipartFile archivo, TipoMultimediaEnum tipo,
                                                 UUID usuarioAutenticadoId) {
        Boxeador boxeador = buscarBoxeador(id);
        verificarPropietario(id, usuarioAutenticadoId);
        String url = storageService.subirArchivo(archivo, "boxeadores/" + id + "/multimedia");

        Multimedia multimedia = Multimedia.builder()
                .tipo(tipo)
                .url(url)
                .boxeador(boxeador)
                .esOficial(false)
                .build();
        multimedia = multimediaRepository.save(multimedia);

        return MultimediaResponse.builder()
                .id(multimedia.getId()).tipo(multimedia.getTipo()).url(multimedia.getUrl())
                .esOficial(multimedia.getEsOficial())
                .build();
    }

    @Transactional
    public PesoPactadoResponse registrarPesoPactado(UUID id, PesoPactadoRequest request, UUID usuarioAutenticadoId) {
        Boxeador boxeador = buscarBoxeador(id);
        verificarPropietario(id, usuarioAutenticadoId);

        BoxeadorPesoPactado pesoPactado = BoxeadorPesoPactado.builder()
                .boxeador(boxeador)
                .pesoPactado(request.getPesoPactado())
                .build();
        pesoPactado = boxeadorPesoPactadoRepository.save(pesoPactado);

        return PesoPactadoResponse.builder()
                .id(pesoPactado.getId())
                .pesoPactado(pesoPactado.getPesoPactado())
                .createdAt(pesoPactado.getCreatedAt())
                .build();
    }

    private Boxeador buscarBoxeador(UUID id) {
        return boxeadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boxeador no encontrado"));
    }

    private void verificarPropietario(UUID boxeadorId, UUID usuarioAutenticadoId) {
        if (!boxeadorId.equals(usuarioAutenticadoId)) {
            throw new AccesoDenegadoException("No puedes modificar el perfil de otro boxeador");
        }
    }

    private CategoriaPeso resolverCategoria(UUID categoriaId) {
        return categoriaPesoRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de peso no encontrada"));
    }

    private Gimnasio resolverGimnasio(UUID gimnasioId) {
        return gimnasioRepository.findById(gimnasioId)
                .orElseThrow(() -> new ResourceNotFoundException("Gimnasio no encontrado"));
    }

    private Entrenador resolverEntrenador(UUID entrenadorId) {
        return entrenadorRepository.findById(entrenadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado"));
    }

    private Region resolverRegion(Short regionId) {
        return regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Región no encontrada"));
    }

    private BoxeadorResumenResponse aResumen(Boxeador b) {
        return BoxeadorResumenResponse.builder()
                .id(b.getId())
                .nombre(b.getUsuario().getNombre())
                .fotoUrl(b.getFotoUrl())
                .sexo(b.getSexo())
                .pesoActual(b.getPesoActual())
                .categoriaNombre(b.getCategoria() != null ? b.getCategoria().getNombre() : null)
                .gimnasioNombre(b.getGimnasio() != null ? b.getGimnasio().getNombre() : null)
                .estadoDeportivo(b.getEstadoDeportivo())
                .build();
    }

    private BoxeadorPerfilResponse aPerfil(Boxeador b) {
        Integer edad = Period.between(b.getFechaNacimiento(), LocalDate.now()).getYears();
        return BoxeadorPerfilResponse.builder()
                .id(b.getId())
                .nombre(b.getUsuario().getNombre())
                .email(b.getUsuario().getEmail())
                .fotoUrl(b.getFotoUrl())
                .rut(b.getRut())
                .fechaNacimiento(b.getFechaNacimiento())
                .edad(edad)
                .sexo(b.getSexo())
                .pesoActual(b.getPesoActual())
                .pesoHabitual(b.getPesoHabitual())
                .categoriaId(b.getCategoria() != null ? b.getCategoria().getId() : null)
                .categoriaNombre(b.getCategoria() != null ? b.getCategoria().getNombre() : null)
                .gimnasioId(b.getGimnasio() != null ? b.getGimnasio().getId() : null)
                .gimnasioNombre(b.getGimnasio() != null ? b.getGimnasio().getNombre() : null)
                .entrenadorId(b.getEntrenador() != null ? b.getEntrenador().getId() : null)
                .entrenadorNombre(b.getEntrenador() != null ? b.getEntrenador().getUsuario().getNombre() : null)
                .regionId(b.getRegion() != null ? b.getRegion().getId() : null)
                .regionNombre(b.getRegion() != null ? b.getRegion().getNombre() : null)
                .estadoDeportivo(b.getEstadoDeportivo())
                .nivelProgresion(b.getNivelProgresion())
                .build();
    }

    private EstadisticasResponse aEstadisticas(BoxeadorEstadisticas e) {
        return EstadisticasResponse.builder()
                .peleasTotales(e.getPeleasTotales())
                .victorias(e.getVictorias())
                .derrotas(e.getDerrotas())
                .empates(e.getEmpates())
                .victoriasKo(e.getVictoriasKo())
                .victoriasDecision(e.getVictoriasDecision())
                .ultimaPelea(e.getUltimaPelea())
                .build();
    }

    private PeleaResumenResponse aPeleaResumen(Pelea p, UUID boxeadorId) {
        Boxeador rival = p.getBoxeadorA().getId().equals(boxeadorId) ? p.getBoxeadorB() : p.getBoxeadorA();
        return PeleaResumenResponse.builder()
                .id(p.getId())
                .eventoId(p.getEvento().getId())
                .eventoNombre(p.getEvento().getNombre())
                .rivalId(rival.getId())
                .rivalNombre(rival.getUsuario().getNombre())
                .estado(p.getEstado())
                .resultado(p.getResultado())
                .metodoVictoria(p.getMetodoVictoria())
                .fecha(p.getFecha())
                .build();
    }
}
