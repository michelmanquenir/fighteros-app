package com.killerdev.fighteros_app.repository.evento;

import com.killerdev.fighteros_app.model.evento.EventoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventoInscripcionRepository extends JpaRepository<EventoInscripcion, UUID> {
    List<EventoInscripcion> findByEvento_IdOrderByCreatedAtAsc(UUID eventoId);

    Optional<EventoInscripcion> findByEvento_IdAndBoxeador_Id(UUID eventoId, UUID boxeadorId);

    boolean existsByEvento_IdAndBoxeador_Id(UUID eventoId, UUID boxeadorId);

    long countByTorneo_Id(UUID torneoId);

    long countByEvento_Id(UUID eventoId);

    long countByEvento_IdAndGimnasio_Id(UUID eventoId, UUID gimnasioId);
}
