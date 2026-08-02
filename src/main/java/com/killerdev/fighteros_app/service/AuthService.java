package com.killerdev.fighteros_app.service;

import com.killerdev.fighteros_app.dto.auth.AuthResponse;
import com.killerdev.fighteros_app.dto.auth.LoginRequest;
import com.killerdev.fighteros_app.dto.auth.RegistroBoxeadorRequest;
import com.killerdev.fighteros_app.exception.DuplicateResourceException;
import com.killerdev.fighteros_app.exception.ResourceNotFoundException;
import com.killerdev.fighteros_app.exception.RutInvalidoException;
import com.killerdev.fighteros_app.model.deportivo.Boxeador;
import com.killerdev.fighteros_app.model.deportivo.CategoriaPeso;
import com.killerdev.fighteros_app.model.deportivo.Entrenador;
import com.killerdev.fighteros_app.model.enums.EstadoDeportivoEnum;
import com.killerdev.fighteros_app.model.enums.NivelProgresionEnum;
import com.killerdev.fighteros_app.model.enums.RolUsuarioEnum;
import com.killerdev.fighteros_app.model.enums.SexoEnum;
import com.killerdev.fighteros_app.model.identidad.Gimnasio;
import com.killerdev.fighteros_app.model.identidad.Region;
import com.killerdev.fighteros_app.model.identidad.Usuario;
import com.killerdev.fighteros_app.model.identidad.UsuarioRol;
import com.killerdev.fighteros_app.model.identidad.UsuarioRolId;
import com.killerdev.fighteros_app.repository.deportivo.BoxeadorRepository;
import com.killerdev.fighteros_app.repository.deportivo.CategoriaPesoRepository;
import com.killerdev.fighteros_app.repository.deportivo.EntrenadorRepository;
import com.killerdev.fighteros_app.repository.identidad.GimnasioRepository;
import com.killerdev.fighteros_app.repository.identidad.RegionRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRolRepository;
import com.killerdev.fighteros_app.security.JwtService;
import com.killerdev.fighteros_app.validation.RutValidator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final BoxeadorRepository boxeadorRepository;
    private final CategoriaPesoRepository categoriaPesoRepository;
    private final GimnasioRepository gimnasioRepository;
    private final EntrenadorRepository entrenadorRepository;
    private final RegionRepository regionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                        UsuarioRolRepository usuarioRolRepository,
                        BoxeadorRepository boxeadorRepository,
                        CategoriaPesoRepository categoriaPesoRepository,
                        GimnasioRepository gimnasioRepository,
                        EntrenadorRepository entrenadorRepository,
                        RegionRepository regionRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.boxeadorRepository = boxeadorRepository;
        this.categoriaPesoRepository = categoriaPesoRepository;
        this.gimnasioRepository = gimnasioRepository;
        this.entrenadorRepository = entrenadorRepository;
        this.regionRepository = regionRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registrarBoxeador(RegistroBoxeadorRequest request) {
        if (!RutValidator.esValido(request.getRut())) {
            throw new RutInvalidoException("El RUT ingresado no es válido");
        }
        String rutNormalizado = RutValidator.normalizar(request.getRut());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Ya existe una cuenta con ese email");
        }
        if (boxeadorRepository.existsByRut(rutNormalizado)) {
            throw new DuplicateResourceException("Ya existe un boxeador registrado con ese RUT");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .activo(true)
                .region(resolverRegion(request.getRegionId()))
                .build();
        usuario = usuarioRepository.save(usuario);

        UsuarioRol usuarioRol = UsuarioRol.builder()
                .id(UsuarioRolId.builder().usuarioId(usuario.getId()).rol(RolUsuarioEnum.boxeador).build())
                .usuario(usuario)
                .build();
        usuarioRolRepository.save(usuarioRol);

        CategoriaPeso categoria = resolverCategoria(request.getCategoriaId(), request.getSexo(), request.getPesoActual());

        Boxeador boxeador = Boxeador.builder()
                .usuario(usuario)
                .rut(rutNormalizado)
                .fechaNacimiento(request.getFechaNacimiento())
                .sexo(request.getSexo())
                .pesoActual(request.getPesoActual())
                .pesoHabitual(request.getPesoHabitual())
                .categoria(categoria)
                .gimnasio(resolverGimnasio(request.getGimnasioId()))
                .entrenador(resolverEntrenador(request.getEntrenadorId()))
                .region(resolverRegion(request.getRegionId()))
                .estadoDeportivo(EstadoDeportivoEnum.activo)
                .nivelProgresion(NivelProgresionEnum.debutante)
                .build();
        boxeadorRepository.save(boxeador);

        String token = jwtService.generarToken(usuario.getEmail(), List.of(RolUsuarioEnum.boxeador.name()));
        return AuthResponse.builder()
                .token(token)
                .usuarioId(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        List<String> roles = usuarioRolRepository.findByUsuario_Id(usuario.getId()).stream()
                .map(ur -> ur.getId().getRol().name())
                .toList();
        String token = jwtService.generarToken(usuario.getEmail(), roles);

        return AuthResponse.builder()
                .token(token)
                .usuarioId(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .build();
    }

    private CategoriaPeso resolverCategoria(UUID categoriaId, SexoEnum sexo, BigDecimal peso) {
        if (categoriaId != null) {
            return categoriaPesoRepository.findById(categoriaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría de peso no encontrada"));
        }
        if (peso == null) {
            return null;
        }
        return categoriaPesoRepository
                .findFirstBySexoAndPesoMinLessThanEqualAndPesoMaxGreaterThanEqual(sexo, peso, peso)
                .orElse(null);
    }

    private Gimnasio resolverGimnasio(UUID gimnasioId) {
        if (gimnasioId == null) {
            return null;
        }
        return gimnasioRepository.findById(gimnasioId)
                .orElseThrow(() -> new ResourceNotFoundException("Gimnasio no encontrado"));
    }

    private Entrenador resolverEntrenador(UUID entrenadorId) {
        if (entrenadorId == null) {
            return null;
        }
        return entrenadorRepository.findById(entrenadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Entrenador no encontrado"));
    }

    private Region resolverRegion(Short regionId) {
        if (regionId == null) {
            return null;
        }
        return regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Región no encontrada"));
    }
}
