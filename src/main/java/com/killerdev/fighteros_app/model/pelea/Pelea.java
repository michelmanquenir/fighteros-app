package com.killerdev.fighteros_app.model.pelea;

import com.killerdev.fighteros_app.model.deportivo.Arbitro;
import com.killerdev.fighteros_app.model.deportivo.Boxeador;
import com.killerdev.fighteros_app.model.deportivo.CategoriaPeso;
import com.killerdev.fighteros_app.model.enums.EstadoPeleaEnum;
import com.killerdev.fighteros_app.model.enums.EstadoValidacionEnum;
import com.killerdev.fighteros_app.model.enums.MetodoVictoriaEnum;
import com.killerdev.fighteros_app.model.enums.ResultadoPeleaEnum;
import com.killerdev.fighteros_app.model.evento.Evento;
import com.killerdev.fighteros_app.model.evento.EventoRing;
import com.killerdev.fighteros_app.model.evento.EventoTorneo;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "peleas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pelea {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_pelea_id")
    private SolicitudPelea solicitudPelea;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "boxeador_a_id", nullable = false)
    private Boxeador boxeadorA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "boxeador_b_id", nullable = false)
    private Boxeador boxeadorB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoriaPeso categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id")
    private EventoTorneo torneo;

    @Column(nullable = false)
    private Short ronda;

    @Column(name = "peso_pactado")
    private BigDecimal pesoPactado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ring_id")
    private EventoRing ring;

    private OffsetDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arbitro_id")
    private Arbitro arbitro;

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private EstadoPeleaEnum estado;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ResultadoPeleaEnum resultado;

    @Column(name = "metodo_victoria")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private MetodoVictoriaEnum metodoVictoria;

    @Column(name = "round_final")
    private Short roundFinal;

    @Column(name = "estado_validacion", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private EstadoValidacionEnum estadoValidacion;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
