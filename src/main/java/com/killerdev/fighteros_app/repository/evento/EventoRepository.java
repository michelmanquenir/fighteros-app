package com.killerdev.fighteros_app.repository.evento;

import com.killerdev.fighteros_app.model.evento.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface EventoRepository extends JpaRepository<Evento, UUID>, JpaSpecificationExecutor<Evento> {
    Page<Evento> findByOrganizador_Id(UUID organizadorId, Pageable pageable);
}
