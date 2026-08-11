package com.killerdev.fighteros_app.model.evento;

import com.killerdev.fighteros_app.model.enums.EstadoEventoEnum;
import com.killerdev.fighteros_app.model.enums.TipoEventoEnum;
import com.killerdev.fighteros_app.model.identidad.Gimnasio;
import com.killerdev.fighteros_app.model.identidad.Liga;
import com.killerdev.fighteros_app.model.identidad.Region;
import com.killerdev.fighteros_app.model.identidad.Usuario;
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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Evento {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizador_id", nullable = false)
    private Usuario organizador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liga_id")
    private Liga liga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gimnasio_id")
    private Gimnasio gimnasio;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TipoEventoEnum tipo;

    @Column(nullable = false)
    private LocalDate fecha;

    private String lugar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @Column(name = "reglamento_url")
    private String reglamentoUrl;

    @Column(name = "afiche_url")
    private String afichePosterUrl;

    @Column(name = "cupos_totales")
    private Integer cuposTotales;

    @Column(name = "cantidad_publico")
    private Integer cantidadPublico;

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private EstadoEventoEnum estado;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
