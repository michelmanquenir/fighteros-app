package com.killerdev.fighteros_app.repository.identidad;

import com.killerdev.fighteros_app.model.identidad.Gimnasio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GimnasioRepository extends JpaRepository<Gimnasio, UUID> {
}
