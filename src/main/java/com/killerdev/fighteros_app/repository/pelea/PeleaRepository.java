package com.killerdev.fighteros_app.repository.pelea;

import com.killerdev.fighteros_app.model.enums.EstadoPeleaEnum;
import com.killerdev.fighteros_app.model.pelea.Pelea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PeleaRepository extends JpaRepository<Pelea, UUID> {

    @Query("""
            select p from Pelea p
            where (p.boxeadorA.id = :boxeadorId or p.boxeadorB.id = :boxeadorId)
              and p.estado = :estado
            order by p.fecha desc
            """)
    List<Pelea> buscarPorBoxeadorYEstado(@Param("boxeadorId") UUID boxeadorId,
                                          @Param("estado") EstadoPeleaEnum estado);
}
