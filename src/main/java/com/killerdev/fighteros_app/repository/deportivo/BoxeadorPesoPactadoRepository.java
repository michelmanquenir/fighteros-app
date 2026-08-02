package com.killerdev.fighteros_app.repository.deportivo;

import com.killerdev.fighteros_app.model.deportivo.BoxeadorPesoPactado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BoxeadorPesoPactadoRepository extends JpaRepository<BoxeadorPesoPactado, UUID> {
    List<BoxeadorPesoPactado> findByBoxeador_IdOrderByCreatedAtDesc(UUID boxeadorId);
}
