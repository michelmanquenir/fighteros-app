package com.killerdev.fighteros_app.repository.evento;

import com.killerdev.fighteros_app.model.evento.EventoTorneo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventoTorneoRepository extends JpaRepository<EventoTorneo, UUID> {
    List<EventoTorneo> findByEvento_IdOrderByCreatedAtAsc(UUID eventoId);
}
