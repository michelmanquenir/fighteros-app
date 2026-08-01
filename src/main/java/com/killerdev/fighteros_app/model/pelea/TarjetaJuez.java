package com.killerdev.fighteros_app.model.pelea;

import com.killerdev.fighteros_app.model.deportivo.Juez;
import com.killerdev.fighteros_app.model.enums.ModalidadJuezEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tarjetas_jueces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TarjetaJuez {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pelea_id", nullable = false)
    private Pelea pelea;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "juez_id", nullable = false)
    private Juez juez;

    @Column(nullable = false)
    private Short round;

    @Column(name = "puntos_boxeador_a", nullable = false)
    private Short puntosBoxeadorA;

    @Column(name = "puntos_boxeador_b", nullable = false)
    private Short puntosBoxeadorB;

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ModalidadJuezEnum modalidad;

    @Column(name = "foto_tarjeta_url")
    private String fotoTarjetaUrl;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
