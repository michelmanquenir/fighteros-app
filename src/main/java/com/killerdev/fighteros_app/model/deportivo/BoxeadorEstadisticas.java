package com.killerdev.fighteros_app.model.deportivo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;
import java.util.UUID;

// Mapea la vista de solo lectura v_boxeador_estadisticas (db/migrations/06_vistas.sql)
@Entity
@Immutable
@Table(name = "v_boxeador_estadisticas")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BoxeadorEstadisticas {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "boxeador_id")
    private UUID boxeadorId;

    @Column(name = "peleas_totales")
    private Long peleasTotales;

    private Long victorias;

    private Long derrotas;

    private Long empates;

    @Column(name = "victorias_ko")
    private Long victoriasKo;

    @Column(name = "victorias_decision")
    private Long victoriasDecision;

    @Column(name = "ultima_pelea")
    private OffsetDateTime ultimaPelea;
}
