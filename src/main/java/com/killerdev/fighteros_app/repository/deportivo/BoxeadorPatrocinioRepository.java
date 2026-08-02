package com.killerdev.fighteros_app.repository.deportivo;

import com.killerdev.fighteros_app.model.deportivo.BoxeadorPatrocinio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoxeadorPatrocinioRepository extends JpaRepository<BoxeadorPatrocinio, UUID> {
    List<BoxeadorPatrocinio> findByBoxeador_IdOrderByFechaInicioDesc(UUID boxeadorId);
}
