package com.killerdev.fighteros_app.repository.identidad;

import com.killerdev.fighteros_app.model.enums.EstadoSeguimientoEnum;
import com.killerdev.fighteros_app.model.identidad.Seguidor;
import com.killerdev.fighteros_app.model.identidad.SeguidorId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeguidorRepository extends JpaRepository<Seguidor, SeguidorId> {
    Optional<Seguidor> findByIdSeguidorIdAndIdSeguidoId(UUID seguidorId, UUID seguidoId);

    List<Seguidor> findByIdSeguidoIdAndEstado(UUID seguidoId, EstadoSeguimientoEnum estado);

    long countByIdSeguidoIdAndEstado(UUID seguidoId, EstadoSeguimientoEnum estado);
}
