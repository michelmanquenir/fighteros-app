package com.killerdev.fighteros_app.service;

import com.killerdev.fighteros_app.dto.usuario.UsuarioResponse;
import com.killerdev.fighteros_app.exception.ResourceNotFoundException;
import com.killerdev.fighteros_app.model.identidad.Usuario;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                           UsuarioRolRepository usuarioRolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerMe(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return aResponse(usuario);
    }

    private UsuarioResponse aResponse(Usuario usuario) {
        List<String> roles = usuarioRolRepository.findByUsuario_Id(usuario.getId()).stream()
                .map(ur -> ur.getId().getRol().name())
                .toList();

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .avatarUrl(usuario.getAvatarUrl())
                .roles(roles)
                .build();
    }
}
