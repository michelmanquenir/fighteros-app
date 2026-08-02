package com.killerdev.fighteros_app.repository.deportivo;

import com.killerdev.fighteros_app.model.deportivo.BoxeadorCampeonato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoxeadorCampeonatoRepository extends JpaRepository<BoxeadorCampeonato, UUID> {
    List<BoxeadorCampeonato> findByBoxeador_IdOrderByFechaObtenidoDesc(UUID boxeadorId);
}
