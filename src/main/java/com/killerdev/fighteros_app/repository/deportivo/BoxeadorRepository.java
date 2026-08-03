package com.killerdev.fighteros_app.repository.deportivo;

import com.killerdev.fighteros_app.model.deportivo.Boxeador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BoxeadorRepository extends JpaRepository<Boxeador, UUID>,
        JpaSpecificationExecutor<Boxeador> {

    boolean existsByRut(String rut);
}
