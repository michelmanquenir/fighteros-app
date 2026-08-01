package com.killerdev.fighteros_app.model.pelea;

import com.killerdev.fighteros_app.model.enums.RolValidadorEnum;
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

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pelea_validaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PeleaValidacion {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pelea_id", nullable = false)
    private Pelea pelea;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "validador_id", nullable = false)
    private Usuario validador;

    @Column(name = "rol_validador", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private RolValidadorEnum rolValidador;

    private Boolean aprobado;

    private String comentario;

    @Column(nullable = false, insertable = false, updatable = false)
    private OffsetDateTime fecha;
}
