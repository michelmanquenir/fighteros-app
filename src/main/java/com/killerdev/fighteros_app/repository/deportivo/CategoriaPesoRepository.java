package com.killerdev.fighteros_app.repository.deportivo;

import com.killerdev.fighteros_app.model.deportivo.CategoriaPeso;
import com.killerdev.fighteros_app.model.enums.SexoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaPesoRepository extends JpaRepository<CategoriaPeso, UUID> {
    Optional<CategoriaPeso> findFirstBySexoAndPesoMinLessThanEqualAndPesoMaxGreaterThanEqual(
            SexoEnum sexo, BigDecimal pesoMin, BigDecimal pesoMax);
}
