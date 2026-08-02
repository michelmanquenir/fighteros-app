package com.killerdev.fighteros_app.security;

import com.killerdev.fighteros_app.model.identidad.Usuario;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRepository;
import com.killerdev.fighteros_app.repository.identidad.UsuarioRolRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository, UsuarioRolRepository usuarioRolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        List<String> roles = usuarioRolRepository.findByUsuario_Id(usuario.getId()).stream()
                .map(ur -> ur.getId().getRol().name())
                .toList();
        return new CustomUserDetails(usuario, roles);
    }
}
