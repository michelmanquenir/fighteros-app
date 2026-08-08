package com.killerdev.fighteros_app.model.identidad;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class SeguidorId implements Serializable {

    @Column(name = "seguidor_id")
    private UUID seguidorId;

    @Column(name = "seguido_id")
    private UUID seguidoId;
}
