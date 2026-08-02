package com.killerdev.fighteros_app.repository.deportivo;

import com.killerdev.fighteros_app.model.deportivo.Entrenador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EntrenadorRepository extends JpaRepository<Entrenador, UUID> {
}
