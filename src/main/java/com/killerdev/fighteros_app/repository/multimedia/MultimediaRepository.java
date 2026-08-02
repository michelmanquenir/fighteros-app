package com.killerdev.fighteros_app.repository.multimedia;

import com.killerdev.fighteros_app.model.multimedia.Multimedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MultimediaRepository extends JpaRepository<Multimedia, UUID> {
    List<Multimedia> findByBoxeador_IdOrderByCreatedAtDesc(UUID boxeadorId);
}
