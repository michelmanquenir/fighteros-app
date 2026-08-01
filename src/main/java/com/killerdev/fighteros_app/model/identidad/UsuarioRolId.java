package com.killerdev.fighteros_app.model.identidad;

import com.killerdev.fighteros_app.model.enums.RolUsuarioEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UsuarioRolId implements Serializable {

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "rol")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private RolUsuarioEnum rol;
}
