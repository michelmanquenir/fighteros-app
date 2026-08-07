package com.killerdev.fighteros_app.service;

import com.killerdev.fighteros_app.dto.usuario.UsuarioResponse;
import com.killerdev.fighteros_app.exception.ResourceNotFoundException;
import com.killerdev.fighteros_app.model.identidad.Usuario;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRolRepository;
import com.killerdev.fighteros_app.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final StorageService storageService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                           UsuarioRolRepository usuarioRolRepository,
                           StorageService storageService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerMe(UUID usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        return aResponse(usuario);
    }

    @Transactional
    public UsuarioResponse subirAvatar(UUID usuarioId, MultipartFile archivo) {
        Usuario usuario = buscarUsuario(usuarioId);
        String url = storageService.subirArchivo(archivo, "usuarios/" + usuarioId + "/avatar");
        usuario.setAvatarUrl(url);
        return aResponse(usuarioRepository.save(usuario));
    }

    private Usuario buscarUsuario(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
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
