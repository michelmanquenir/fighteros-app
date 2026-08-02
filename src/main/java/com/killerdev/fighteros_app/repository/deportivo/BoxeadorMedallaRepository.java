package com.killerdev.fighteros_app.repository.deportivo;

import com.killerdev.fighteros_app.model.deportivo.BoxeadorMedalla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoxeadorMedallaRepository extends JpaRepository<BoxeadorMedalla, UUID> {
    List<BoxeadorMedalla> findByBoxeador_IdOrderByFechaDesc(UUID boxeadorId);
}
