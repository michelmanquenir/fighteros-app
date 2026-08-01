package com.killerdev.fighteros_app.model.multimedia;

import com.killerdev.fighteros_app.model.enums.EntidadPublicidadEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "publicidad_sponsors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PublicidadSponsor {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entidad_tipo", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private EntidadPublicidadEnum entidadTipo;

    @Column(name = "entidad_id")
    private UUID entidadId;

    @Column(name = "nombre_sponsor", nullable = false)
    private String nombreSponsor;

    @Column(name = "logo_url")
    private String logoUrl;

    private BigDecimal monto;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;
}
