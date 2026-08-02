package com.killerdev.fighteros_app.repository.identidad;

import com.killerdev.fighteros_app.model.identidad.UsuarioRol;
import com.killerdev.fighteros_app.model.identidad.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {
    List<UsuarioRol> findByUsuario_Id(UUID usuarioId);
}
