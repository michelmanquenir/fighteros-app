package com.killerdev.fighteros_app.repository.evento;

import com.killerdev.fighteros_app.model.enums.EstadoSolicitudEnum;
import com.killerdev.fighteros_app.model.evento.EventoGimnasioInvitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventoGimnasioInvitacionRepository extends JpaRepository<EventoGimnasioInvitacion, UUID> {

    List<EventoGimnasioInvitacion> findByEvento_IdOrderByCreatedAtAsc(UUID eventoId);

    List<EventoGimnasioInvitacion> findByGimnasio_IdInOrderByCreatedAtDesc(List<UUID> gimnasioIds);

    Optional<EventoGimnasioInvitacion> findByEvento_IdAndGimnasio_Id(UUID eventoId, UUID gimnasioId);

    boolean existsByEvento_IdAndGimnasio_IdAndEstado(UUID eventoId, UUID gimnasioId, EstadoSolicitudEnum estado);
}
