package com.killerdev.fighteros_app.model.identidad;

import com.killerdev.fighteros_app.model.enums.EstadoSeguimientoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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

@Entity
@Table(name = "seguidores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Seguidor {

    @EqualsAndHashCode.Include
    @EmbeddedId
    private SeguidorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("seguidorId")
    @JoinColumn(name = "seguidor_id")
    private Usuario seguidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("seguidoId")
    @JoinColumn(name = "seguido_id")
    private Usuario seguido;

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private EstadoSeguimientoEnum estado;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
