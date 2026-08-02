package com.killerdev.fighteros_app.repository.deportivo;

import com.killerdev.fighteros_app.model.deportivo.Boxeador;
import com.killerdev.fighteros_app.model.enums.EstadoDeportivoEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BoxeadorRepository extends JpaRepository<Boxeador, UUID> {

    boolean existsByRut(String rut);

    @Query("""
            select b from Boxeador b
            where (:gimnasioId is null or b.gimnasio.id = :gimnasioId)
              and (:regionId is null or b.region.id = :regionId)
              and (:categoriaId is null or b.categoria.id = :categoriaId)
              and (:estado is null or b.estadoDeportivo = :estado)
            """)
    Page<Boxeador> buscar(@Param("gimnasioId") UUID gimnasioId,
                           @Param("regionId") Short regionId,
                           @Param("categoriaId") UUID categoriaId,
                           @Param("estado") EstadoDeportivoEnum estado,
                           Pageable pageable);
}
