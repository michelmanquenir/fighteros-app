package com.killerdev.fighteros_app.repository.deportivo;

import com.killerdev.fighteros_app.model.deportivo.BoxeadorEstadisticas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BoxeadorEstadisticasRepository extends JpaRepository<BoxeadorEstadisticas, UUID> {
}
