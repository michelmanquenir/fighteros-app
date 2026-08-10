package com.killerdev.fighteros_app.service;

import com.killerdev.fighteros_app.dto.gimnasio.GimnasioCreateRequest;
import com.killerdev.fighteros_app.dto.gimnasio.GimnasioMioResponse;
import com.killerdev.fighteros_app.exception.DuplicateResourceException;
import com.killerdev.fighteros_app.exception.ResourceNotFoundException;
import com.killerdev.fighteros_app.model.enums.RolUsuarioEnum;
import com.killerdev.fighteros_app.model.identidad.Gimnasio;
import com.killerdev.fighteros_app.model.identidad.Region;
import com.killerdev.fighteros_app.model.identidad.Usuario;
import com.killerdev.fighteros_app.model.identidad.UsuarioRol;
import com.killerdev.fighteros_app.model.identidad.UsuarioRolId;
import com.killerdev.fighteros_app.repository.identidad.GimnasioRepository;
import com.killerdev.fighteros_app.repository.identidad.RegionRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GimnasioService {

    private final GimnasioRepository gimnasioRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RegionRepository regionRepository;

    public GimnasioService(GimnasioRepository gimnasioRepository,
                            UsuarioRepository usuarioRepository,
                            UsuarioRolRepository usuarioRolRepository,
                            RegionRepository regionRepository) {
        this.gimnasioRepository = gimnasioRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.regionRepository = regionRepository;
    }

    @Transactional
    public GimnasioMioResponse crear(GimnasioCreateRequest request, UUID usuarioAutenticadoId) {
        if (gimnasioRepository.findByUsuarioAdmin_Id(usuarioAutenticadoId).isPresent()) {
            throw new DuplicateResourceException("Ya tienes un gimnasio registrado");
        }

        Usuario usuario = usuarioRepository.findById(usuarioAutenticadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Gimnasio gimnasio = Gimnasio.builder()
                .usuarioAdmin(usuario)
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .region(resolverRegion(request.getRegionId()))
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .redesSociales("{}")
                .descripcion(request.getDescripcion())
                .build();
        gimnasioRepository.save(gimnasio);

        agregarRolSiFalta(usuario, usuarioAutenticadoId);

        return aResponse(gimnasio, obtenerRoles(usuarioAutenticadoId));
    }

    @Transactional(readOnly = true)
    public GimnasioMioResponse obtenerMio(UUID usuarioAutenticadoId) {
        Gimnasio gimnasio = gimnasioRepository.findByUsuarioAdmin_Id(usuarioAutenticadoId)
                .orElseThrow(() -> new ResourceNotFoundException("No tienes un gimnasio registrado"));
        return aResponse(gimnasio, obtenerRoles(usuarioAutenticadoId));
    }

    private void agregarRolSiFalta(Usuario usuario, UUID usuarioId) {
        UsuarioRolId rolId = UsuarioRolId.builder()
                .usuarioId(usuarioId)
                .rol(RolUsuarioEnum.gimnasio_admin)
                .build();
        if (usuarioRolRepository.existsById(rolId)) {
            return;
        }
        UsuarioRol usuarioRol = UsuarioRol.builder()
                .id(rolId)
                .usuario(usuario)
                .build();
        usuarioRolRepository.save(usuarioRol);
    }

    private List<String> obtenerRoles(UUID usuarioId) {
        return usuarioRolRepository.findByUsuario_Id(usuarioId).stream()
                .map(ur -> ur.getId().getRol().name())
                .toList();
    }

    private Region resolverRegion(Short regionId) {
        if (regionId == null) {
            return null;
        }
        return regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Región no encontrada"));
    }

    private GimnasioMioResponse aResponse(Gimnasio gimnasio, List<String> roles) {
        return GimnasioMioResponse.builder()
                .id(gimnasio.getId())
                .nombre(gimnasio.getNombre())
                .direccion(gimnasio.getDireccion())
                .regionId(gimnasio.getRegion() != null ? gimnasio.getRegion().getId() : null)
                .regionNombre(gimnasio.getRegion() != null ? gimnasio.getRegion().getNombre() : null)
                .telefono(gimnasio.getTelefono())
                .email(gimnasio.getEmail())
                .descripcion(gimnasio.getDescripcion())
                .roles(roles)
                .build();
    }
}
