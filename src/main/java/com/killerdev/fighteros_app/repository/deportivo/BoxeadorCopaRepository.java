package com.killerdev.fighteros_app.repository.deportivo;

import com.killerdev.fighteros_app.model.deportivo.BoxeadorCopa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoxeadorCopaRepository extends JpaRepository<BoxeadorCopa, UUID> {
    List<BoxeadorCopa> findByBoxeador_IdOrderByFechaDesc(UUID boxeadorId);
}
